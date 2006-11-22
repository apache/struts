/*
 * $Id:  $
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ResolverUtil;
import com.opensymphony.xwork2.util.TextUtils;
import com.opensymphony.xwork2.util.ResolverUtil.Test;
import com.opensymphony.xwork2.util.location.LocatableProperties;

/**
 * ClasspathConfigurationProvider loads the configuration
 * by scanning the classpath or selected packages for Action classes.
 * <p>
 * This provider is only invoked if one or more action packages are passed to the dispatcher,
 * usually from the web.xml.
 * Configurations are created for objects that either implement Action or have classnames that end with "Action".
 */
public class ClasspathConfigurationProvider implements ConfigurationProvider {

    /**
     * The default page prefix (or "path").
     * Some applications may place pages under "/WEB-INF" as an extreme security precaution.
     */
    private static final String DEFAULT_PAGE_PREFIX = "struts.configuration.classpath.defaultPagePrefix";

    /**
     * The default page prefix (none).
     */
    private String defaultPagePrefix = "";

    /**
     * The default page extension,  to use in place of ".jsp".
     */
    private static final String DEFAULT_PAGE_EXTENSION = "struts.configuration.classpath.defaultPageExtension";

    /**
     * The defacto default page extension, usually associated with JavaServer Pages.
     */
    private String defaultPageExtension = ".jsp";

    /**
     * A setting to indicate a custom default parent package,
     * to use in place of "struts-default".
     */
    private static final String DEFAULT_PARENT_PACKAGE = "struts.configuration.classpath.defaultParentPackage";

    /**
     * Name of the framework's default configuration package,
     * that application configuration packages automatically inherit.
     */
    private String defaultParentPackage = "struts-default";

    /**
     * Default suffix that can be used to indicate POJO "Action" classes.
     */
    private static final String ACTION = "Action";

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

    /**
     * The list of packages to scan for Action classes.
     */
    private String[] packages;

    /**
     * The package configurations for scanned Actions.
     *
     * @see #loadPackageConfig
     */
    private Map<String,PackageConfig> loadedPackageConfigs = new HashMap<String,PackageConfig>();

    /**
     * Logging instance for this class.
     */
    private static final Log LOG = LogFactory.getLog(ClasspathConfigurationProvider.class);

    /**
     * The XWork Configuration for this application.
     *
     * @see #init
     */
    private Configuration configuration;

    /**
     * Create instance utilizing a list of packages to scan for Action classes.
     * @param pkgs List of pacaktges to scan for Action Classes.
     */
    public ClasspathConfigurationProvider(String[] pkgs) {
        this.packages = pkgs;

        if (Settings.isSet(DEFAULT_PARENT_PACKAGE)) {
            defaultParentPackage = Settings.get(DEFAULT_PARENT_PACKAGE);
        }

        if (Settings.isSet(DEFAULT_PAGE_EXTENSION)) {
            defaultPageExtension = Settings.get(DEFAULT_PAGE_EXTENSION);
        }

        if (Settings.isSet(DEFAULT_PAGE_PREFIX)) {
            defaultPagePrefix = Settings.get(DEFAULT_PAGE_PREFIX);
        }

    }

    public static interface PageLocator {
        public URL locate(String path);
    }

    public static class ClasspathPageLocator implements PageLocator {
        public URL locate(String path) {
            return ClassLoaderUtil.getResource(path, getClass());
        }
    }

    /**
     * @param defaultParentPackage the defaultParentPackage to set
     */
    public void setDefaultParentPackage(String defaultParentPackage) {
        this.defaultParentPackage = defaultParentPackage;
    }

    /**
     * @param defaultPageExtension the defaultPageExtension to set
     */
    public void setDefaultPageExtension(String defaultPageExtension) {
        this.defaultPageExtension = defaultPageExtension;
    }

    /**
     * @param defaultPagePrefix the defaultPagePrefix to set
     */
    public void setDefaultPagePrefix(String defaultPagePrefix) {
        this.defaultPagePrefix = defaultPagePrefix;
    }

    public void setPageLocator(PageLocator locator) {
        this.pageLocator = locator;
    }

    /**
     * @param pkgs A set of packages to load
     */
    protected void loadPackages(String[] pkgs) {

        ResolverUtil<Class> resolver = new ResolverUtil<Class>();
        resolver.find(new Test() {
            // Match Action implementations and classes ending with "Action"
            public boolean matches(Class type) {
                // TODO: should also find annotated classes
                return (Action.class.isAssignableFrom(type) || 
                        type.getSimpleName().endsWith("Action"));
            }
            
        }, pkgs);
        Set<? extends Class<? extends Class>> actionClasses = resolver.getClasses();
        for (Object obj : actionClasses) {
           Class cls = (Class) obj;
           if (!Modifier.isAbstract(cls.getModifiers())) {
               processActionClass(cls, pkgs);
           }
        }

        for (String key : loadedPackageConfigs.keySet()) {
            configuration.addPackageConfig(key, loadedPackageConfigs.get(key));
        }
    }

    /**
     *
     * @param cls Action or POJO instance to process
     * @param pkgs Set of packages to scan for Actions
     */
    protected void processActionClass(Class cls, String[] pkgs) {
        String name = cls.getName();
        String actionPackage = cls.getPackage().getName();
        String actionNamespace = null;
        String actionName = null;
        for (String pkg : pkgs) {
            if (name.startsWith(pkg)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Processing class "+name);
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

        PackageConfig pkgConfig = loadPackageConfig(actionNamespace, actionPackage, cls);

        Annotation annotation = cls.getAnnotation(ParentPackage.class);
        if (annotation != null) {
            String parent = ((ParentPackage)annotation).value();
            PackageConfig parentPkg = configuration.getPackageConfig(parent);
            if (parentPkg == null) {
                throw new ConfigurationException("Unable to locate parent package "+parent, annotation);
            }
            pkgConfig.addParent(parentPkg);

            if (!TextUtils.stringSet(pkgConfig.getNamespace()) && TextUtils.stringSet(parentPkg.getNamespace())) {
                pkgConfig.setNamespace(parentPkg.getNamespace());
            }
        }

        // Truncate Action suffix if found
        if (actionName.endsWith(ACTION)) {
            actionName = actionName.substring(0, actionName.length() - ACTION.length());
        }

        if (actionName.length() > 1) {
            int lowerPos = actionName.lastIndexOf('/') + 1;
            StringBuilder sb = new StringBuilder();
            sb.append(actionName.substring(0, lowerPos));
            sb.append(Character.toLowerCase(actionName.charAt(lowerPos)));
            sb.append(actionName.substring(lowerPos + 1));
            actionName = sb.toString();
        }

        ActionConfig actionConfig = new ActionConfig();
        actionConfig.setClassName(cls.getName());
        actionConfig.setPackageName(actionPackage);

        actionConfig.setResults(new ResultMap<String,ResultConfig>(cls, actionName, pkgConfig));

        pkgConfig.addActionConfig(actionName, actionConfig);
    }

    /**
     * @param actionPackage The Java package containing our Action classes
     * @return
     */
    protected PackageConfig loadPackageConfig(String actionNamespace, String actionPackage, Class actionClass) {
        PackageConfig parent = null;

        if (actionClass != null) {
            Namespace ns = (Namespace) actionClass.getAnnotation(Namespace.class);
            if (ns != null) {
                parent = loadPackageConfig(actionNamespace, actionPackage, null);
                actionNamespace = ns.value();
                actionPackage = actionClass.getName();
            }
        }

        PackageConfig pkgConfig = loadedPackageConfigs.get(actionPackage);
        if (pkgConfig == null) {
            pkgConfig = new PackageConfig();
            pkgConfig.setName(actionPackage);

            if (parent == null) {
                parent = configuration.getPackageConfig(defaultParentPackage);
            }

            if (parent == null) {
                throw new ConfigurationException("Unable to locate default parent package: " +
                        defaultParentPackage);
            }
            pkgConfig.addParent(parent);

            pkgConfig.setNamespace(actionNamespace);

            loadedPackageConfigs.put(actionPackage, pkgConfig);
        }
        return pkgConfig;
    }

    public void destroy() {

    }
    
    public void init(Configuration config) {
        this.configuration = config;
    }

    public void loadPackages() throws ConfigurationException {
        loadedPackageConfigs.clear();
        loadPackages(packages);
        initialized = true;
    }

    public boolean needsReload() {
        return !initialized;
    }

    /**
     * Creates result configs from result annotations, and if a result isn't found,
     * creates them on the fly.
     */
    class ResultMap<K,V> extends HashMap<K,V> {
        private Class actionClass;
        private String actionName;
        private PackageConfig pkgConfig;

        public ResultMap(Class actionClass, String actionName, PackageConfig pkgConfig) {
            this.actionClass = actionClass;
            this.actionName = actionName;
            this.pkgConfig = pkgConfig;

            // check if any annotations are around
            while (!actionClass.getName().equals(Object.class.getName())) {
                //noinspection unchecked
                Results results = (Results) actionClass.getAnnotation(Results.class);
                if (results != null) {
                    // first check here...
                    for (int i = 0; i < results.value().length; i++) {
                        Result result = results.value()[i];
                        ResultConfig config = createResultConfig(result);
                        put((K)config.getName(), (V)config);
                    }
                }

                // what about a single Result annotation?
                Result result = (Result) actionClass.getAnnotation(Result.class);
                if (result != null) {
                    ResultConfig config = createResultConfig(result);
                    put((K)config.getName(), (V)config);
                }

                actionClass = actionClass.getSuperclass();
            }

        }

        protected ResultConfig createResultConfig(Result result) {
            Class<? extends Object> cls = result.type();
            if (cls == NullResult.class) {
                cls = null;
            }
            return createResultConfig(result.name(), cls, result.value());
        }

        public V get(Object key) {

            V result = super.get(key);
            if (result != null) {
                return result;
            } else {

                // TODO: This code never is actually used, do to how the runtime configuration
                // is created.
                String actionPath = pkgConfig.getNamespace() + "/" + actionName;

                String fileName = actionPath + "-" + key + defaultPageExtension;
                if (pageLocator.locate(defaultPagePrefix + fileName) == null) {
                    fileName = actionPath + defaultPageExtension;
                }

                String location = defaultPagePrefix + fileName;
                return (V) createResultConfig(key, null, location);
            }
        }

        /**
         * @param key
         * @param resultClass
         * @param location
         * @return
         */
        private ResultConfig createResultConfig(Object key, Class<? extends Object> resultClass, String location) {
            Map<? extends Object, ? extends Object> configParams = null;
            if (resultClass == null) {
                String defaultResultType = pkgConfig.getFullDefaultResultType();
                ResultTypeConfig resultType = (ResultTypeConfig) pkgConfig.getAllResultTypeConfigs().get(defaultResultType);
                configParams = resultType.getParams();
                String className = resultType.getClazz();
                try {
                    resultClass = ClassLoaderUtil.loadClass(className, getClass());
                } catch (ClassNotFoundException ex) {
                    throw new ConfigurationException("Unable to locate result class "+className, actionClass);
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
            return new ResultConfig((String) key, resultClass.getName(), params);
        }
    }

    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
        // Nothing
    }
}
