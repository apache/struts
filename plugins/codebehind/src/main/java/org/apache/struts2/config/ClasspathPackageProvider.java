/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.config;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ResolverUtil;
import com.opensymphony.xwork2.util.ResolverUtil.ClassTest;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletContext;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ClasspathPackageProvider loads the configuration
 * by scanning the classpath or selected packages for Action classes.
 * <p>
 * This provider is only invoked if one or more action packages are passed to the dispatcher,
 * usually from the web.xml.
 * Configurations are created for objects that either implement Action or have classnames that end with "Action".
 */
public class ClasspathPackageProvider implements PackageProvider {

    /**
     * The default page prefix (or "path").
     * Some applications may place pages under "/WEB-INF" as an extreme security precaution.
     */
    protected static final String DEFAULT_PAGE_PREFIX = "struts.configuration.classpath.defaultPagePrefix";

    /**
     * The default page prefix (none).
     */
    private String defaultPagePrefix = "";

    /**
     * The default page extension,  to use in place of ".jsp".
     */
    protected static final String DEFAULT_PAGE_EXTENSION = "struts.configuration.classpath.defaultPageExtension";

    /**
     * The defacto default page extension, usually associated with JavaServer Pages.
     */
    private String defaultPageExtension = ".jsp";

    /**
     * A setting to indicate a custom default parent package,
     * to use in place of "struts-default".
     */
    protected static final String DEFAULT_PARENT_PACKAGE = "struts.configuration.classpath.defaultParentPackage";

    /**
     * A setting to disable action scanning.
     */
    protected static final String DISABLE_ACTION_SCANNING = "struts.configuration.classpath.disableActionScanning";

    /**
     * Name of the framework's default configuration package,
     * that application configuration packages automatically inherit.
     */
    private String defaultParentPackage = "struts-default";

    /**
     * The default page prefix (or "path").
     * Some applications may place pages under "/WEB-INF" as an extreme security precaution.
     */
    protected static final String FORCE_LOWER_CASE = "struts.configuration.classpath.forceLowerCase";

    /**
     * Whether to use a lowercase letter as the initial letter of an action.
     * If false, actions will retain the initial uppercase letter from the Action class.
     * (<code>view.action</code> (true) versus <code>View.action</code> (false)).
     */
    private boolean forceLowerCase = true;

    protected static final String CLASS_SUFFIX = "struts.codebehind.classSuffix";
    /**
     * Default suffix that can be used to indicate POJO "Action" classes.
     */
    protected String classSuffix = "Action";

    protected static final String CHECK_IMPLEMENTS_ACTION = "struts.codebehind.checkImplementsAction";

    /**
     * When testing a class, check that it implements Action
     */
    protected boolean checkImplementsAction = true;

    protected static final String CHECK_ANNOTATION = "struts.codebehind.checkAnnotation";

    /**
     * When testing a class, check that it has an @Action annotation
     */
    protected boolean checkAnnotation = true;

    /**
     * Helper class to scan class path for server pages.
     */
    private PageLocator pageLocator = new ClasspathPageLocator();

    /**
     * Flag to indicate the packages have been loaded.
     *
     * @see #loadPackages
     * @see #needsReload
     */
    private boolean initialized = false;

    private boolean disableActionScanning = false;

    private PackageLoader packageLoader;

    /**
     * Logging instance for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ClasspathPackageProvider.class);

    /**
     * The XWork Configuration for this application.
     *
     * @see #init
     */
    private Configuration configuration;

    private String actionPackages;

    private ServletContext servletContext;

    public ClasspathPackageProvider() {
    }

    /**
     * PageLocator defines a locate method that can be used to discover server pages.
     */
    public static interface PageLocator {
        public URL locate(String path);
    }

    /**
     * ClasspathPathLocator searches the classpath for server pages.
     */
    public static class ClasspathPageLocator implements PageLocator {
        public URL locate(String path) {
            return ClassLoaderUtil.getResource(path, getClass());
        }
    }

    @Inject("actionPackages")
    public void setActionPackages(String packages) {
        this.actionPackages = packages;
    }

    public void setServletContext(ServletContext ctx) {
        this.servletContext = ctx;
    }

    /**
     * Disables action scanning.
     *
     * @param disableActionScanning True to disable
     */
    @Inject(value=DISABLE_ACTION_SCANNING, required=false)
    public void setDisableActionScanning(String disableActionScanning) {
        this.disableActionScanning = "true".equals(disableActionScanning);
    }

    /**
     * Check that the class implements Action
     *
     * @param checkImplementsAction True to check
     */
    @Inject(value=CHECK_IMPLEMENTS_ACTION, required=false)
    public void setCheckImplementsAction(String checkImplementsAction) {
        this.checkImplementsAction = "true".equals(checkImplementsAction);
    }

    /**
     * Check that the class has an @Action annotation
     *
     * @param checkImplementsAction True to check
     */
    @Inject(value=CHECK_ANNOTATION, required=false)
    public void setCheckAnnotation(String checkAnnotation) {
        this.checkAnnotation = "true".equals(checkAnnotation);
    }

    /**
     * Register a default parent package for the actions.
     *
     * @param defaultParentPackage the new defaultParentPackage
     */
    @Inject(value=DEFAULT_PARENT_PACKAGE, required=false)
    public void setDefaultParentPackage(String defaultParentPackage) {
        this.defaultParentPackage = defaultParentPackage;
    }

    /**
     * Register a default page extension to use when locating pages.
     *
     * @param defaultPageExtension the new defaultPageExtension
     */
    @Inject(value=DEFAULT_PAGE_EXTENSION, required=false)
    public void setDefaultPageExtension(String defaultPageExtension) {
        this.defaultPageExtension = defaultPageExtension;
    }

    /**
     * Reigster a default page prefix to use when locating pages.
     *
     * @param defaultPagePrefix the defaultPagePrefix to set
     */
    @Inject(value=DEFAULT_PAGE_PREFIX, required=false)
    public void setDefaultPagePrefix(String defaultPagePrefix) {
        this.defaultPagePrefix = defaultPagePrefix;
    }

    /**
     * Default suffix that can be used to indicate POJO "Action" classes.
     *
     * @param classSuffix the classSuffix to set
     */
    @Inject(value=CLASS_SUFFIX, required=false)
    public void setClassSuffix(String classSuffix) {
        this.classSuffix = classSuffix;
    }

    /**
     * Whether to use a lowercase letter as the initial letter of an action.
     *
     * @param force If false, actions will retain the initial uppercase letter from the Action class.
     * (<code>view.action</code> (true) versus <code>View.action</code> (false)).
     */
    @Inject(value=FORCE_LOWER_CASE, required=false)
    public void setForceLowerCase(String force) {
        this.forceLowerCase = "true".equals(force);
    }

    /**
     * Register a PageLocation to use to scan for server pages.
     *
     * @param locator
     */
    public void setPageLocator(PageLocator locator) {
        this.pageLocator = locator;
    }

    /**
     * Scan a list of packages for Action classes.
     *
     * This method loads classes that implement the Action interface
     * or have a class name that ends with the letters "Action".
     *
     * @param pkgs A list of packages to load
     * @see #processActionClass
     */
    protected void loadPackages(String[] pkgs) {

        packageLoader = new PackageLoader();
        ResolverUtil<Class> resolver = new ResolverUtil<Class>();
        resolver.find(createActionClassTest(), pkgs);

        Set<? extends Class<? extends Class>> actionClasses = resolver.getClasses();
        for (Object obj : actionClasses) {
           Class cls = (Class) obj;
           if (!Modifier.isAbstract(cls.getModifiers())) {
               processActionClass(cls, pkgs);
           }
        }

        for (PackageConfig config : packageLoader.createPackageConfigs()) {
            configuration.addPackageConfig(config.getName(), config);
        }
    }

    protected ClassTest createActionClassTest() {
        return new ClassTest() {
            // Match Action implementations and classes ending with "Action"
            public boolean matches(Class type) {
                // TODO: should also find annotated classes
                return ((checkImplementsAction && Action.class.isAssignableFrom(type)) ||
                        type.getSimpleName().endsWith(getClassSuffix()) ||
                        (checkAnnotation && type.getAnnotation(org.apache.struts2.config.Action.class) != null));
            }

        };
    }

    protected String getClassSuffix() {
        return classSuffix;
    }

    /**
     * Create a default action mapping for a class instance.
     *
     * The namespace annotation is honored, if found, otherwise
     * the Java package is converted into the namespace
     * by changing the dots (".") to slashes ("/").
     *
     * @param cls Action or POJO instance to process
     * @param pkgs List of packages that were scanned for Actions
     */
    protected void processActionClass(Class<?> cls, String[] pkgs) {
        String name = cls.getName();
        String actionPackage = cls.getPackage().getName();
        String actionNamespace = null;
        String actionName = null;

        org.apache.struts2.config.Action actionAnn =
            (org.apache.struts2.config.Action) cls.getAnnotation(org.apache.struts2.config.Action.class);
        if (actionAnn != null) {
            actionName = actionAnn.name();
            if (actionAnn.namespace().equals(org.apache.struts2.config.Action.DEFAULT_NAMESPACE)) {
                actionNamespace = "";
            } else {
                actionNamespace = actionAnn.namespace();
            }
        } else {
            for (String pkg : pkgs) {
                if (name.startsWith(pkg)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("ClasspathPackageProvider: Processing class "+name);
                    }
                    name = name.substring(pkg.length() + 1);

                    actionNamespace = "";
                    actionName = name;
                    int pos = name.lastIndexOf('.');
                    if (pos > -1) {
                        actionNamespace = "/" + name.substring(0, pos).replace('.','/');
                        actionName = name.substring(pos+1);
                    }
                    break;
                }
            }
            // Truncate Action suffix if found
            if (actionName.endsWith(getClassSuffix())) {
                actionName = actionName.substring(0, actionName.length() - getClassSuffix().length());
            }

            // Force initial letter of action to lowercase, if desired
            if ((forceLowerCase) && (actionName.length() > 1)) {
                int lowerPos = actionName.lastIndexOf('/') + 1;
                StringBuilder sb = new StringBuilder();
                sb.append(actionName.substring(0, lowerPos));
                sb.append(Character.toLowerCase(actionName.charAt(lowerPos)));
                sb.append(actionName.substring(lowerPos + 1));
                actionName = sb.toString();
            }
        }

        PackageConfig.Builder pkgConfig = loadPackageConfig(actionNamespace, actionPackage, cls);

        // In case the package changed due to namespace annotation processing
        if (!actionPackage.equals(pkgConfig.getName())) {
            actionPackage = pkgConfig.getName();
        }

        List<PackageConfig> parents = findAllParentPackages(cls);
        if (parents.size() > 0) {
            pkgConfig.addParents(parents);

            // Try to guess the namespace from the first package
            PackageConfig firstParent = parents.get(0);
            if (StringUtils.isEmpty(pkgConfig.getNamespace()) && StringUtils.isNotEmpty(firstParent.getNamespace())) {
                pkgConfig.namespace(firstParent.getNamespace());
            }
        }


        ResultTypeConfig defaultResultType = packageLoader.getDefaultResultType(pkgConfig);
        ActionConfig actionConfig = new ActionConfig.Builder(actionPackage, actionName, cls.getName())
                .addResultConfigs(new ResultMap<String,ResultConfig>(cls, actionName, defaultResultType))
                .build();
        pkgConfig.addActionConfig(actionName, actionConfig);
    }

    /**
     * Finds all parent packages by first looking at the ParentPackage annotation on the package, then the class
     * @param cls The action class
     * @return A list of unique packages to add
     */
    private List<PackageConfig> findAllParentPackages(Class<?> cls) {

        List<PackageConfig> parents = new ArrayList<PackageConfig>();
        // Favor parent package annotations from the package
        Set<String> parentNames = new LinkedHashSet<String>();
        ParentPackage annotation = cls.getPackage().getAnnotation(ParentPackage.class);
        if (annotation != null) {
            parentNames.addAll(Arrays.asList(annotation.value()));
        }
        annotation = cls.getAnnotation(ParentPackage.class);
        if (annotation != null) {
            parentNames.addAll(Arrays.asList(annotation.value()));
        }
        if (parentNames.size() > 0) {
            for (String parent : parentNames) {
                PackageConfig parentPkg = configuration.getPackageConfig(parent);
                if (parentPkg == null) {
                    throw new ConfigurationException("ClasspathPackageProvider: Unable to locate parent package "+parent, annotation);
                }
                parents.add(parentPkg);
            }
        }
        return parents;
    }

    /**
     * Finds or creates the package configuration for an Action class.
     *
     * The namespace annotation is honored, if found,
     * and the namespace is checked for a parent configuration.
     *
     * @param actionNamespace The configuration namespace
     * @param actionPackage The Java package containing our Action classes
     * @param actionClass The Action class instance
     * @return PackageConfig object for the Action class
     */
    protected PackageConfig.Builder loadPackageConfig(String actionNamespace, String actionPackage, Class actionClass) {
        PackageConfig.Builder parent = null;

        // Check for the @Namespace annotation
        if (actionClass != null) {
            Namespace ns = (Namespace) actionClass.getAnnotation(Namespace.class);
            if (ns != null) {
                parent = loadPackageConfig(actionNamespace, actionPackage, null);
                actionNamespace = ns.value();
                actionPackage = actionClass.getName();

            // See if the namespace has been overridden by the @Action annotation
            } else {
                org.apache.struts2.config.Action actionAnn =
                    (org.apache.struts2.config.Action) actionClass.getAnnotation(org.apache.struts2.config.Action.class);
                if (actionAnn != null && !actionAnn.DEFAULT_NAMESPACE.equals(actionAnn.namespace())) {
                    // we pass null as the namespace in case the parent package hasn't been loaded yet
                    parent = loadPackageConfig(null, actionPackage, null);
                    actionPackage = actionClass.getName();
                }
            }
        }


        PackageConfig.Builder pkgConfig = packageLoader.getPackage(actionPackage);
        if (pkgConfig == null) {
            pkgConfig = new PackageConfig.Builder(actionPackage);

            pkgConfig.namespace(actionNamespace);
            if (parent == null) {
                PackageConfig cfg = configuration.getPackageConfig(defaultParentPackage);
                if (cfg != null) {
                    pkgConfig.addParent(cfg);
                } else {
                    throw new ConfigurationException("ClasspathPackageProvider: Unable to locate default parent package: " +
                        defaultParentPackage);
                }
            }

            packageLoader.registerPackage(pkgConfig);

        // if the parent package was first created by a child, ensure the namespace is correct
        } else if (pkgConfig.getNamespace() == null) {
            pkgConfig.namespace(actionNamespace);
        }

        if (parent != null) {
            packageLoader.registerChildToParent(pkgConfig, parent);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("class:"+actionClass+" parent:"+parent+" current:"+(pkgConfig != null ? pkgConfig.getName() : ""));
        }

        return pkgConfig;
    }

    /**
     * Default destructor. Override to provide behavior.
     */
    public void destroy() {

    }

    /**
     * Register this application's configuration.
     *
     * @param config The configuration for this application.
     */
    public void init(Configuration config) {
        this.configuration = config;
    }

    /**
     * Clears and loads the list of packages registered at construction.
     *
     * @throws ConfigurationException
     */
    public void loadPackages() throws ConfigurationException {
        if (actionPackages != null && !disableActionScanning) {
            String[] names = actionPackages.split("\\s*[,]\\s*");
            // Initialize the classloader scanner with the configured packages
            if (names.length > 0) {
                setPageLocator(new ServletContextPageLocator(servletContext));
            }
            loadPackages(names);
        }
        initialized = true;
    }

    /**
     * Indicates whether the packages have been initialized.
     *
     * @return True if the packages have been initialized
     */
    public boolean needsReload() {
        return !initialized;
    }

    /**
     * Creates ResultConfig objects from result annotations,
     * and if a result isn't found, creates it on the fly.
     */
    class ResultMap<K,V> extends HashMap<K,V> {
        private Class actionClass;
        private String actionName;
        private ResultTypeConfig defaultResultType;

        public ResultMap(Class actionClass, String actionName, ResultTypeConfig defaultResultType) {
            this.actionClass = actionClass;
            this.actionName = actionName;
            this.defaultResultType = defaultResultType;

            // check if any annotations are around
            while (!actionClass.getName().equals(Object.class.getName())) {
                //noinspection unchecked
                Results results = (Results) actionClass.getAnnotation(Results.class);
                if (results != null) {
                    // first check here...
                    for (int i = 0; i < results.value().length; i++) {
                        Result result = results.value()[i];
                        ResultConfig config = createResultConfig(result);
						if (!containsKey((K)config.getName())) {
                            put((K)config.getName(), (V)config);
                        }
                    }
                }

                // what about a single Result annotation?
                Result result = (Result) actionClass.getAnnotation(Result.class);
                if (result != null) {
                    ResultConfig config = createResultConfig(result);
                    if (!containsKey((K)config.getName())) {
                        put((K)config.getName(), (V)config);
                    }
                }

                actionClass = actionClass.getSuperclass();
            }
        }

        /**
         * Extracts result name and value and calls {@link #createResultConfig}.
         *
         * @param result Result annotation reference representing result type to create
         * @return New or cached ResultConfig object for result
         */
        protected ResultConfig createResultConfig(Result result) {
            Class<? extends Object> cls = result.type();
            if (cls == NullResult.class) {
                cls = null;
            }
            return createResultConfig(result.name(), cls, result.value(), createParameterMap(result.params()));
        }

        protected Map<String, String> createParameterMap(String[] parms) {
            Map<String, String> map = new HashMap<String, String>();
            int subtract = parms.length % 2;
            if(subtract != 0) {
                LOG.warn("Odd number of result parameters key/values specified.  The final one will be ignored.");
            }
            for (int i = 0; i < parms.length - subtract; i++) {
                String key = parms[i++];
                String value = parms[i];
                map.put(key, value);
                if(LOG.isDebugEnabled()) {
                    LOG.debug("Adding parmeter["+key+":"+value+"] to result.");
                }
            }
            return map;
        }

        /**
         * Creates a default ResultConfig,
         * using either the resultClass or the default ResultType for configuration package
         * associated this ResultMap class.
         *
         * @param key The result type name
         * @param resultClass The class for the result type
         * @param location Path to the resource represented by this type
         * @return A ResultConfig for key mapped to location
         */
        private ResultConfig createResultConfig(Object key, Class<? extends Object> resultClass,
                                                String location,
                                                Map<? extends Object,? extends Object > configParams) {
            if (resultClass == null) {
                configParams = defaultResultType.getParams();
                String className = defaultResultType.getClassName();
                try {
                    resultClass = ClassLoaderUtil.loadClass(className, getClass());
                } catch (ClassNotFoundException ex) {
                    throw new ConfigurationException("ClasspathPackageProvider: Unable to locate result class "+className, actionClass);
                }
            }

            String defaultParam;
            try {
                defaultParam = (String) resultClass.getField("DEFAULT_PARAM").get(null);
            } catch (Exception e) {
                // not sure why this happened, but let's just use a sensible choice
                defaultParam = "location";
            }

            HashMap params = new HashMap();
            if (configParams != null) {
                params.putAll(configParams);
            }

            params.put(defaultParam, location);
            return new ResultConfig.Builder((String) key, resultClass.getName()).addParams(params).build();
        }
    }

    /**
     * Search classpath for a page.
     */
    private final class ServletContextPageLocator implements PageLocator {
        private final ServletContext context;
        private ClasspathPageLocator classpathPageLocator = new ClasspathPageLocator();

        private ServletContextPageLocator(ServletContext context) {
            this.context = context;
        }

        public URL locate(String path) {
            URL url = null;
            try {
                url = context.getResource(path);
                if (url == null) {
                    url = classpathPageLocator.locate(path);
                }
            } catch (MalformedURLException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Unable to resolve path "+path+" against the servlet context");
                }
            }
            return url;
        }
    }

    private static class PackageLoader {

        /**
         * The package configurations for scanned Actions.
         */
        private Map<String,PackageConfig.Builder> packageConfigBuilders = new HashMap<String,PackageConfig.Builder>();

        private Map<PackageConfig.Builder,PackageConfig.Builder> childToParent = new HashMap<PackageConfig.Builder,PackageConfig.Builder>();

        public PackageConfig.Builder getPackage(String name) {
            return packageConfigBuilders.get(name);
        }

        public void registerChildToParent(PackageConfig.Builder child, PackageConfig.Builder parent) {
            childToParent.put(child, parent);
        }

        public void registerPackage(PackageConfig.Builder builder) {
            packageConfigBuilders.put(builder.getName(), builder);
        }

        public Collection<PackageConfig> createPackageConfigs() {
            Map<String, PackageConfig> configs = new HashMap<String, PackageConfig>();

            Set<PackageConfig.Builder> builders;
            while ((builders = findPackagesWithNoParents()).size() > 0) {
                for (PackageConfig.Builder parent : builders) {
                    PackageConfig config = parent.build();
                    configs.put(config.getName(), config);
                    packageConfigBuilders.remove(config.getName());

                    for (Iterator<Map.Entry<PackageConfig.Builder,PackageConfig.Builder>> i = childToParent.entrySet().iterator(); i.hasNext(); ) {
                        Map.Entry<PackageConfig.Builder,PackageConfig.Builder> entry = i.next();
                        if (entry.getValue() == parent) {
                            entry.getKey().addParent(config);
                            i.remove();
                        }
                    }
                }
            }
            return configs.values();
        }

        Set<PackageConfig.Builder> findPackagesWithNoParents() {
            Set<PackageConfig.Builder> builders = new HashSet<PackageConfig.Builder>();
            for (PackageConfig.Builder child : packageConfigBuilders.values()) {
                if (!childToParent.containsKey(child)) {
                    builders.add(child);
                }
            }
            return builders;
        }

        public ResultTypeConfig getDefaultResultType(PackageConfig.Builder pkgConfig) {
            PackageConfig.Builder parent;
            PackageConfig.Builder current = pkgConfig;

            while ((parent = childToParent.get(current)) != null) {
                current = parent;
            }
            return current.getResultType(current.getFullDefaultResultType());
        }
    }
}
