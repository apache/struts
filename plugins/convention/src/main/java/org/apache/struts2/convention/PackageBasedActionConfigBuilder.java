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
package org.apache.struts2.convention;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.AnnotationUtils;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.WildcardHelper;
import com.opensymphony.xwork2.util.classloader.ReloadingClassLoader;
import com.opensymphony.xwork2.util.finder.ClassFinder;
import com.opensymphony.xwork2.util.finder.ClassFinderFactory;
import com.opensymphony.xwork2.util.finder.DefaultClassFinder;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterfaceDelegate;
import com.opensymphony.xwork2.util.finder.Test;
import com.opensymphony.xwork2.util.finder.UrlSet;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.DefaultInterceptorRef;
import org.apache.struts2.convention.annotation.ExceptionMapping;
import org.apache.struts2.convention.annotation.ExceptionMappings;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Namespaces;
import org.apache.struts2.convention.annotation.ParentPackage;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * <p>
 * This class implements the ActionConfigBuilder interface.
 * </p>
 */
public class PackageBasedActionConfigBuilder implements ActionConfigBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(PackageBasedActionConfigBuilder.class);
    private static final boolean EXTRACT_BASE_INTERFACES = true;

    private final Configuration configuration;
    private final ActionNameBuilder actionNameBuilder;
    private final ResultMapBuilder resultMapBuilder;
    private final InterceptorMapBuilder interceptorMapBuilder;
    private final ObjectFactory objectFactory;
    private final String defaultParentPackage;
    private final boolean redirectToSlash;
    private String[] actionPackages;
    private String[] excludePackages;
    private String[] packageLocators;
    private String[] includeJars;
    private String packageLocatorsBasePackage;
    private boolean disableActionScanning = false;
    private boolean disablePackageLocatorsScanning = false;
    private String actionSuffix = "Action";
    private boolean checkImplementsAction = true;
    private boolean mapAllMatches = false;
    private Set<String> loadedFileUrls = new HashSet<String>();
    private boolean devMode;
    private ReloadingClassLoader reloadingClassLoader;
    private boolean reload;
    private Set<String> fileProtocols;
    private boolean alwaysMapExecute;
    private boolean excludeParentClassLoader;
    private boolean slashesInActionNames;

    private static final String DEFAULT_METHOD = "execute";
    private boolean eagerLoading = false;

    private FileManager fileManager;
    private ClassFinderFactory classFinderFactory;

    /**
     * Constructs actions based on a list of packages.
     *
     * @param configuration         The XWork configuration that the new package configs and action configs
     *                              are added to.
     * @param container             Xwork Container
     * @param objectFactory         The ObjectFactory used to create the actions and such.
     * @param redirectToSlash       A boolean parameter that controls whether or not this will create an
     *                              action for indexes. If this is set to true, index actions are not created because
     *                              the unknown handler will redirect from /foo to /foo/. The only action that is created
     *                              is to the empty action in the namespace (e.g. the namespace /foo and the action "").
     * @param defaultParentPackage  The default parent package for all the configuration.
     */
    @Inject
    public PackageBasedActionConfigBuilder(Configuration configuration, Container container, ObjectFactory objectFactory,
                                           @Inject("struts.convention.redirect.to.slash") String redirectToSlash,
                                           @Inject("struts.convention.default.parent.package") String defaultParentPackage) {

        // Validate that the parameters are okay
        this.configuration = configuration;
        this.actionNameBuilder = container.getInstance(ActionNameBuilder.class, container.getInstance(String.class, ConventionConstants.CONVENTION_ACTION_NAME_BUILDER));
        this.resultMapBuilder = container.getInstance(ResultMapBuilder.class, container.getInstance(String.class, ConventionConstants.CONVENTION_RESULT_MAP_BUILDER));
        this.interceptorMapBuilder = container.getInstance(InterceptorMapBuilder.class, container.getInstance(String.class, ConventionConstants.CONVENTION_INTERCEPTOR_MAP_BUILDER));
        this.objectFactory = objectFactory;
        this.redirectToSlash = Boolean.parseBoolean(redirectToSlash);

        if (LOG.isTraceEnabled()) {
            LOG.trace("Setting action default parent package to [#0]", defaultParentPackage);
        }

        this.defaultParentPackage = defaultParentPackage;
    }

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String mode) {
        this.devMode = "true".equals(mode);
    }

    /**
     * Reload configuration when classes change. Defaults to "false" and should not be used
     * in production.
     */
    @Inject("struts.convention.classes.reload")
    public void setReload(String reload) {
        this.reload = "true".equals(reload);
    }


    @Inject(StrutsConstants.STRUTS_ENABLE_SLASHES_IN_ACTION_NAMES)
    public void setSlashesInActionNames(String slashesInActionNames) {
        this.slashesInActionNames = "true".equals(slashesInActionNames);
    }

    /**
     * Exclude URLs found by the parent class loader. Defaults to "true", set to true for JBoss
     */
    @Inject("struts.convention.exclude.parentClassLoader")
    public void setExcludeParentClassLoader(String exclude) {
        this.excludeParentClassLoader = "true".equals(exclude);
    }

    /**
     * If this constant is true, and there is an "execute" method(not annotated), a mapping will be added
     * pointing to it, even if there are other mapping in the class
     */
    @Inject("struts.convention.action.alwaysMapExecute")
    public void setAlwaysMapExecute(String alwaysMapExecute) {
        this.alwaysMapExecute = "true".equals(alwaysMapExecute);
    }

    /**
     * File URLs whose protocol are in these list will be processed as jars containing classes
     * @param fileProtocols Comma separated list of file protocols that will be considered as jar files and scanned
     */
    @Inject("struts.convention.action.fileProtocols")
    public void setFileProtocols(String fileProtocols) {
        if (StringUtils.isNotBlank(fileProtocols)) {
            this.fileProtocols = TextParseUtil.commaDelimitedStringToSet(fileProtocols);
        }
    }

    /**
     * @param disableActionScanning Disable scanning for actions
     */
    @Inject(value = "struts.convention.action.disableScanning", required = false)
    public void setDisableActionScanning(String disableActionScanning) {
        this.disableActionScanning = "true".equals(disableActionScanning);
    }

    /**
     * @param includeJars Comma separated list of regular expressions of jars to be included.
     */
    @Inject(value = "struts.convention.action.includeJars", required = false)
    public void setIncludeJars(String includeJars) {
        if (StringUtils.isNotEmpty(includeJars))
            this.includeJars = includeJars.split("\\s*[,]\\s*");
    }

    /**
     * @param disablePackageLocatorsScanning If set to true, only the named packages will be scanned
     */
    @Inject(value = "struts.convention.package.locators.disable", required = false)
    public void setDisablePackageLocatorsScanning(String disablePackageLocatorsScanning) {
        this.disablePackageLocatorsScanning = "true".equals(disablePackageLocatorsScanning);
    }

    /**
     * @param actionPackages (Optional) An optional list of action packages that this should create
     *                       configuration for.
     */
    @Inject(value = "struts.convention.action.packages", required = false)
    public void setActionPackages(String actionPackages) {
        if (StringUtils.isNotBlank(actionPackages)) {
            this.actionPackages = actionPackages.split("\\s*[,]\\s*");
        }
    }

    /**
     * @param checkImplementsAction (Optional) Map classes that implement com.opensymphony.xwork2.Action
     *                       as actions
     */
    @Inject(value = "struts.convention.action.checkImplementsAction", required = false)
    public void setCheckImplementsAction(String checkImplementsAction) {
        this.checkImplementsAction = "true".equals(checkImplementsAction);
    }

    /**
     * @param actionSuffix (Optional) Classes that end with these value will be mapped as actions
     *                     (defaults to "Action")
     */
    @Inject(value = "struts.convention.action.suffix", required = false)
    public void setActionSuffix(String actionSuffix) {
        if (StringUtils.isNotBlank(actionSuffix)) {
            this.actionSuffix = actionSuffix;
        }
    }

    /**
     * @param excludePackages (Optional) A  list of packages that should be skipped when building
     *                        configuration.
     */
    @Inject(value = "struts.convention.exclude.packages", required = false)
    public void setExcludePackages(String excludePackages) {
        if (StringUtils.isNotBlank(excludePackages)) {
            this.excludePackages = excludePackages.split("\\s*[,]\\s*");
        }
    }

    /**
     * @param packageLocators (Optional) A list of names used to find action packages.
     */
    @Inject(value = "struts.convention.package.locators", required = false)
    public void setPackageLocators(String packageLocators) {
        this.packageLocators = packageLocators.split("\\s*[,]\\s*");
    }

    /**
     * @param packageLocatorsBasePackage (Optional) If set, only packages that start with this
     *                                   name will be scanned for actions.
     */
    @Inject(value = "struts.convention.package.locators.basePackage", required = false)
    public void setPackageLocatorsBase(String packageLocatorsBasePackage) {
        this.packageLocatorsBasePackage = packageLocatorsBasePackage;
    }

    /**
     * @param mapAllMatches (Optional) Map actions that match the "*${Suffix}" pattern
     *                      even if they don't have a default method. The mapping from
     *                      the url to the action will be delegated the action mapper.
     */
    @Inject(value = "struts.convention.action.mapAllMatches", required = false)
    public void setMapAllMatches(String mapAllMatches) {
        this.mapAllMatches = "true".equals(mapAllMatches);
    }

    /**
     * @param eagerLoading (Optional) If set, found action classes will be instantiated by the ObjectFactory to accelerate future use
     *                      setting it up can clash with Spring managed beans
     */
    @Inject(value = "struts.convention.action.eagerLoading", required = false)
    public void setEagerLoading(String eagerLoading) {
        this.eagerLoading = "true".equals(eagerLoading);
    }

    @Inject
    public void setFileManagerFactory(FileManagerFactory fileManagerFactory) {
        this.fileManager = fileManagerFactory.getFileManager();
    }

    @Inject(required = false)
    public void setClassFinderFactory(ClassFinderFactory classFinderFactory) {
        this.classFinderFactory = classFinderFactory;
    }

    protected void initReloadClassLoader() {
        //when the configuration is reloaded, a new classloader will be setup
        if (isReloadEnabled() && reloadingClassLoader == null)
           reloadingClassLoader = new ReloadingClassLoader(getClassLoader());
    }

    protected ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * Builds the action configurations by loading all classes in the packages specified by the
     * property <b>struts.convention.action.packages</b> and then figuring out which classes implement Action
     * or have Action in their name. Next, if this class is in a Java package that hasn't been
     * inspected a new PackageConfig (XWork) is created for that Java package using the Java package
     * name. This will contain all the ActionConfigs for all the Action classes that are discovered
     * within that Java package. Next, each class is inspected for the {@link ParentPackage}
     * annotation which is used to control the parent package for a specific action. Lastly, the
     * {@link ResultMapBuilder} is used to create ResultConfig instances of the action.
     */
    public void buildActionConfigs() {
        //setup reload class loader based on dev settings
        initReloadClassLoader();

        if (!disableActionScanning) {
            if (actionPackages == null && packageLocators == null) {
                throw new ConfigurationException("At least a list of action packages or action package locators " +
                        "must be given using one of the properties [struts.convention.action.packages] or " +
                        "[struts.convention.package.locators]");
            }

            if (LOG.isTraceEnabled()) {
                LOG.trace("Loading action configurations");
                if (actionPackages != null)
                    LOG.trace("Actions being loaded from action packages " + Arrays.asList(actionPackages));
                if (packageLocators != null)
                    LOG.trace("Actions being loaded using package locators " + Arrays.asList(packageLocators));
                if (excludePackages != null)
                    LOG.trace("Excluding actions from packages " + Arrays.asList(excludePackages));
            }

            Set<Class> classes = findActions();
            buildConfiguration(classes);
        }
    }

    protected ClassLoaderInterface getClassLoaderInterface() {
        if (isReloadEnabled())
            return new ClassLoaderInterfaceDelegate(this.reloadingClassLoader);
        else {
            /*
            if there is a ClassLoaderInterface in the context, use it, otherwise
            default to the default ClassLoaderInterface (a wrapper around the current
            thread classloader)
            using this, other plugins (like OSGi) can plugin their own classloader for a while
            and it will be used by Convention (it cannot be a bean, as Convention is likely to be
            called multiple times, and it needs to use the default ClassLoaderInterface during normal startup)
            */
            ClassLoaderInterface classLoaderInterface = null;
            ActionContext ctx = ActionContext.getContext();
            if (ctx != null)
                classLoaderInterface = (ClassLoaderInterface) ctx.get(ClassLoaderInterface.CLASS_LOADER_INTERFACE);

            return ObjectUtils.defaultIfNull(classLoaderInterface, new ClassLoaderInterfaceDelegate(getClassLoader()));
        }
    }

    protected boolean isReloadEnabled() {
        return devMode && reload;
    }

    @SuppressWarnings("unchecked")
    protected Set<Class> findActions() {
        Set<Class> classes = new HashSet<Class>();
        try {
            if (actionPackages != null || (packageLocators != null && !disablePackageLocatorsScanning)) {

                // By default, ClassFinder scans EVERY class in the specified
                // url set, which can produce spurious warnings for non-action
                // classes that can't be loaded. We pass a package filter that
                // only considers classes that match the action packages
                // specified by the user
                Test<String> classPackageTest = getClassPackageTest();
                List<URL> urls = readUrls();
                ClassFinder finder = buildClassFinder(classPackageTest, urls);

                Test<ClassFinder.ClassInfo> test = getActionClassTest();
                classes.addAll(finder.findClasses(test));
            }
        } catch (Exception ex) {
            if (LOG.isErrorEnabled())
                LOG.error("Unable to scan named packages", ex);
        }

        return classes;
    }

    protected ClassFinder buildClassFinder(Test<String> classPackageTest, List<URL> urls) {
        if (classFinderFactory != null) {
            LOG.trace("Using ClassFinderFactory to create instance of ClassFinder!");
            return classFinderFactory.buildClassFinder(getClassLoaderInterface(), urls, EXTRACT_BASE_INTERFACES, fileProtocols, classPackageTest);
        } else {
            LOG.trace("ClassFinderFactory not defined, fallback to default ClassFinder implementation");
            return new DefaultClassFinder(getClassLoaderInterface(), urls, EXTRACT_BASE_INTERFACES, fileProtocols, classPackageTest);
        }
    }

    private List<URL> readUrls() throws IOException {
        List<URL> resourceUrls = new ArrayList<URL>();
        // Usually the "classes" dir.
        ArrayList<URL> classesList = Collections.list(getClassLoaderInterface().getResources(""));
        for (URL url : classesList) {
            resourceUrls.addAll(fileManager.getAllPhysicalUrls(url));
        }
        return buildUrlSet(resourceUrls).getUrls();
    }

    private UrlSet buildUrlSet(List<URL> resourceUrls) throws IOException {
        ClassLoaderInterface classLoaderInterface = getClassLoaderInterface();
        UrlSet urlSet = new UrlSet(resourceUrls);
        urlSet = urlSet.include(new UrlSet(classLoaderInterface, this.fileProtocols));

        //excluding the urls found by the parent class loader is desired, but fails in JBoss (all urls are removed)
        if (excludeParentClassLoader) {
            //exclude parent of classloaders
            ClassLoaderInterface parent = classLoaderInterface.getParent();
            //if reload is enabled, we need to step up one level, otherwise the UrlSet will be empty
            //this happens because the parent of the realoding class loader is the web app classloader
            if (parent != null && isReloadEnabled())
                parent = parent.getParent();

            if (parent != null)
                urlSet = urlSet.exclude(parent);

            try {
                // This may fail in some sandboxes, ie GAE
                ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
                urlSet = urlSet.exclude(new ClassLoaderInterfaceDelegate(systemClassLoader.getParent()));

            } catch (SecurityException e) {
                if (LOG.isWarnEnabled())
                    LOG.warn("Could not get the system classloader due to security constraints, there may be improper urls left to scan");
            }
        }

        //try to find classes dirs inside war files
        urlSet = urlSet.includeClassesUrl(classLoaderInterface, new UrlSet.FileProtocolNormalizer() {
            public URL normalizeToFileProtocol(URL url) {
                return fileManager.normalizeToFileProtocol(url);
            }
        });


        urlSet = urlSet.excludeJavaExtDirs();
        urlSet = urlSet.excludeJavaEndorsedDirs();
        try {
        	urlSet = urlSet.excludeJavaHome();
        } catch (NullPointerException e) {
        	// This happens in GAE since the sandbox contains no java.home directory
            if (LOG.isWarnEnabled())
        	    LOG.warn("Could not exclude JAVA_HOME, is this a sandbox jvm?");
        }
        urlSet = urlSet.excludePaths(System.getProperty("sun.boot.class.path", ""));
        urlSet = urlSet.exclude(".*/JavaVM.framework/.*");

        if (includeJars == null) {
            urlSet = urlSet.exclude(".*?\\.jar(!/|/)?");
        } else {
            LOG.debug("jar urls regexes were specified: #0", Arrays.asList(includeJars));

            List<URL> rawIncludedUrls = urlSet.getUrls();
            Set<URL> includeUrls = new HashSet<URL>();
            boolean[] patternUsed = new boolean[includeJars.length];

            for (URL url : rawIncludedUrls) {
                if (fileProtocols.contains(url.getProtocol())) {
                    //it is a jar file, make sure it macthes at least a url regex
                    for (int i = 0; i < includeJars.length; i++) {
                        String includeJar = includeJars[i];
                        if (Pattern.matches(includeJar, url.toExternalForm())) {
                            includeUrls.add(url);
                            patternUsed[i] = true;
                            break;
                        }
                    }
                } else {
                    LOG.debug("It is not a jar [#0]", url);
                    includeUrls.add(url);
                }
            }

            if (LOG.isWarnEnabled()) {
                for (int i = 0; i < patternUsed.length; i++) {
                    if (!patternUsed[i]) {
                        LOG.warn("The includeJars pattern [#0] did not match any jars in the classpath", includeJars[i]);
                    }
                }
            }
            return new UrlSet(includeUrls);
        }

        return urlSet;
    }

    /**
     * Note that we can't include the test for {@link #actionSuffix} here
     * because a class is included if its name ends in {@link #actionSuffix} OR
     * it implements {@link com.opensymphony.xwork2.Action}. Since the whole
     * goal is to avoid loading the class if we don't have to, the (actionSuffix
     * || implements Action) test will have to remain until later. See
     * {@link #getActionClassTest()} for the test performed on the loaded
     * {@link ClassFinder.ClassInfo} structure.
     *
     * @param className the name of the class to test
     * @return true if the specified class should be included in the
     *         package-based action scan
     */
    protected boolean includeClassNameInActionScan(String className) {
        String classPackageName = StringUtils.substringBeforeLast(className, ".");
        return (checkActionPackages(classPackageName) || checkPackageLocators(classPackageName)) && checkExcludePackages(classPackageName);
    }

    /**
     * Checks if provided class package is on the exclude list
     *
     * @param classPackageName name of class package
     * @return false if class package is on the {@link #excludePackages} list
     */
    protected boolean checkExcludePackages(String classPackageName) {
        if(excludePackages != null && excludePackages.length > 0) {
            WildcardHelper wildcardHelper = new WildcardHelper();

            //we really don't care about the results, just the boolean
            Map<String, String> matchMap = new HashMap<String, String>();

            for(String packageExclude : excludePackages) {
                int[] packagePattern = wildcardHelper.compilePattern(packageExclude);
                if(wildcardHelper.match(matchMap, classPackageName, packagePattern)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if class package match provided list of action packages
     *
     * @param classPackageName name of class package
     * @return true if class package is on the {@link #actionPackages} list
     */
    protected boolean checkActionPackages(String classPackageName) {
        if (actionPackages != null) {
            for (String packageName : actionPackages) {
                String strictPackageName = packageName + ".";
                if (classPackageName.equals(packageName)
                        || classPackageName.startsWith(strictPackageName))
                    return true;
            }
        }
        return false;
    }

    /**
     * Checks if class package match provided list of package locators
     *
     * @param classPackageName name of class package
     * @return true if class package is on the {@link #packageLocators} list
     */
    protected boolean checkPackageLocators(String classPackageName) {
        if (packageLocators != null && !disablePackageLocatorsScanning && classPackageName.length() > 0
                && (packageLocatorsBasePackage == null || classPackageName
                        .startsWith(packageLocatorsBasePackage))) {
            for (String packageLocator : packageLocators) {
                String[] splitted = classPackageName.split("\\.");

                if (StringTools.contains(splitted, packageLocator, false))
                    return true;
            }
        }
        return false;
    }

    /**
     * Construct a {@link Test} object that determines if a specified class name
     * should be included in the package scan based on the clazz's package name.
     * Note that the goal is to avoid loading the class, so the test should only
     * rely on information in the class name itself. The default implementation
     * is to return the result of {@link #includeClassNameInActionScan(String)}.
     *
     * @return a {@link Test} object that returns true if the specified class
     *         name should be included in the package scan
     */
    protected Test<String> getClassPackageTest() {
        return new Test<String>() {
            public boolean test(String className) {
                return includeClassNameInActionScan(className);
            }
        };
    }

    /**
     * Construct a {@link Test} Object that determines if a specified class
     * should be included in the package scan based on the full {@link ClassFinder.ClassInfo}
     * of the class. At this point, the class has been loaded, so it's ok to
     * perform tests such as checking annotations or looking at interfaces or
     * super-classes of the specified class.
     *
     * @return a {@link Test} object that returns true if the specified class
     *         should be included in the package scan
     */
    protected Test<ClassFinder.ClassInfo> getActionClassTest() {
        return new Test<ClassFinder.ClassInfo>() {
            public boolean test(ClassFinder.ClassInfo classInfo) {

                // Why do we call includeClassNameInActionScan here, when it's
                // already been called to in the initial call to ClassFinder?
                // When some action class passes our package filter in that step,
                // ClassFinder automatically includes parent classes of that action,
                // such as com.opensymphony.xwork2.ActionSupport.  We repeat the
                // package filter here to filter out such results.
                boolean inPackage = includeClassNameInActionScan(classInfo.getName());
                boolean nameMatches = classInfo.getName().endsWith(actionSuffix);

                try {
                    return inPackage && (nameMatches || (checkImplementsAction && com.opensymphony.xwork2.Action.class.isAssignableFrom(classInfo.get())));
                } catch (ClassNotFoundException ex) {
                    if (LOG.isErrorEnabled())
                        LOG.error("Unable to load class [#0]", ex, classInfo.getName());
                    return false;
                }
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected void buildConfiguration(Set<Class> classes) {
        Map<String, PackageConfig.Builder> packageConfigs = new HashMap<String, PackageConfig.Builder>();

        for (Class<?> actionClass : classes) {
            Actions actionsAnnotation = actionClass.getAnnotation(Actions.class);
            Action actionAnnotation = actionClass.getAnnotation(Action.class);

            // Skip classes that can't be instantiated
            if (cannotInstantiate(actionClass)) {
                if (LOG.isTraceEnabled())
                    LOG.trace("Class [#0] did not pass the instantiation test and will be ignored", actionClass.getName());
                continue;
            }

            if (eagerLoading) {
                // Tell the ObjectFactory about this class
                try {
                    objectFactory.getClassInstance(actionClass.getName());
                } catch (ClassNotFoundException e) {
                    if (LOG.isErrorEnabled())
                        LOG.error("Object Factory was unable to load class [#0]", e, actionClass.getName());
                    throw new StrutsException("Object Factory was unable to load class " + actionClass.getName(), e);
                }
            }

            // Determine the action package
            String actionPackage = actionClass.getPackage().getName();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Processing class [#0] in package [#1]", actionClass.getName(), actionPackage);
            }

            // Determine the default namespace and action name
            List<String> namespaces = determineActionNamespace(actionClass);
            for (String namespace : namespaces) {
                String defaultActionName = determineActionName(actionClass);
                PackageConfig.Builder defaultPackageConfig = getPackageConfig(packageConfigs, namespace,
                        actionPackage, actionClass, null);

                // Verify that the annotations have no errors and also determine if the default action
                // configuration should still be built or not.
                Map<String, List<Action>> map = getActionAnnotations(actionClass);
                Set<String> actionNames = new HashSet<String>();
                boolean hasDefaultMethod = ReflectionTools.containsMethod(actionClass, DEFAULT_METHOD);
                if (!map.containsKey(DEFAULT_METHOD)
                        && hasDefaultMethod
                        && actionAnnotation == null && actionsAnnotation == null
                        && (alwaysMapExecute || map.isEmpty())) {
                    boolean found = false;
                    for (String method : map.keySet()) {
                        List<Action> actions = map.get(method);
                        for (Action action : actions) {

                            // Check if there are duplicate action names in the annotations.
                            String actionName = action.value().equals(Action.DEFAULT_VALUE) ? defaultActionName : action.value();
                            if (actionNames.contains(actionName)) {
                                throw new ConfigurationException("The action class [" + actionClass +
                                        "] contains two methods with an action name annotation whose value " +
                                        "is the same (they both might be empty as well).");
                            } else {
                                actionNames.add(actionName);
                            }

                            // Check this annotation is the default action
                            if (action.value().equals(Action.DEFAULT_VALUE)) {
                                found = true;
                            }
                        }
                    }

                    // Build the default
                    if (!found) {
                        createActionConfig(defaultPackageConfig, actionClass, defaultActionName, DEFAULT_METHOD, null);
                    }
                }

                // Build the actions for the annotations
                for (String method : map.keySet()) {
                    List<Action> actions = map.get(method);
                    for (Action action : actions) {
                        PackageConfig.Builder pkgCfg = defaultPackageConfig;
                        if (action.value().contains("/") && !slashesInActionNames) {
                            pkgCfg = getPackageConfig(packageConfigs, namespace, actionPackage,
                                    actionClass, action);
                        }

                        createActionConfig(pkgCfg, actionClass, defaultActionName, method, action);
                    }
                }

                // some actions will not have any @Action or a default method, like the rest actions
                // where the action mapper is the one that finds the right method at runtime
                if (map.isEmpty() && mapAllMatches && actionAnnotation == null && actionsAnnotation == null) {
                    createActionConfig(defaultPackageConfig, actionClass, defaultActionName, null, actionAnnotation);
                }

                //if there are @Actions or @Action at the class level, create the mappings for them
                String methodName = hasDefaultMethod ? DEFAULT_METHOD : null;
                if (actionsAnnotation != null) {
                    List<Action> actionAnnotations = checkActionsAnnotation(actionsAnnotation);
                    for (Action actionAnnotation2 : actionAnnotations)
                        createActionConfig(defaultPackageConfig, actionClass, defaultActionName, methodName, actionAnnotation2);
                } else if (actionAnnotation != null)
                    createActionConfig(defaultPackageConfig, actionClass, defaultActionName, methodName, actionAnnotation);
            }
        }

        buildIndexActions(packageConfigs);

        // Add the new actions to the configuration
        Set<String> packageNames = packageConfigs.keySet();
        for (String packageName : packageNames) {
            configuration.addPackageConfig(packageName, packageConfigs.get(packageName).build());
        }
    }

    /**
     * Interfaces, enums, annotations, and abstract classes cannot be instantiated.
     * @param actionClass class to check
     * @return returns true if the class cannot be instantiated or should be ignored
     */
    protected boolean cannotInstantiate(Class<?> actionClass) {
        return actionClass.isAnnotation() || actionClass.isInterface() || actionClass.isEnum() ||
                (actionClass.getModifiers() & Modifier.ABSTRACT) != 0 || actionClass.isAnonymousClass();
    }

    /**
     * Determines the namespace(s) for the action based on the action class. If there is a {@link Namespace}
     * annotation on the class (including parent classes) or on the package that the class is in, than
     * it is used. Otherwise, the Java package name that the class is in is used in conjunction with
     * either the <b>struts.convention.action.packages</b> or <b>struts.convention.package.locators</b>
     * configuration values. These are used to determine which part of the Java package name should
     * be converted into the namespace for the XWork PackageConfig.
     *
     * @param actionClass The action class.
     * @return The namespace or an empty string.
     */
    protected List<String> determineActionNamespace(Class<?> actionClass) {
        List<String> namespaces = new ArrayList<String>();

        // Check if there is a class or package level annotation for the namespace
        //single namespace
        Namespace namespaceAnnotation = AnnotationUtils.findAnnotation(actionClass, Namespace.class);
        if (namespaceAnnotation != null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Using non-default action namespace from Namespace annotation of [#0]", namespaceAnnotation.value());
            }

            namespaces.add(namespaceAnnotation.value());
        }

        //multiple annotations
        Namespaces namespacesAnnotation = AnnotationUtils.findAnnotation(actionClass, Namespaces.class);
        if (namespacesAnnotation != null) {
            if (LOG.isTraceEnabled()) {
                StringBuilder sb = new StringBuilder();
                for (Namespace namespace : namespacesAnnotation.value())
                    sb.append(namespace.value()).append(",");
                sb.deleteCharAt(sb.length() - 1);
                LOG.trace("Using non-default action namespaces from Namespaces annotation of [#0]", sb.toString());
            }

            for (Namespace namespace : namespacesAnnotation.value())
                namespaces.add(namespace.value());
        }

        //don't use default if there are annotations
        if (!namespaces.isEmpty())
            return namespaces;

        String pkg = actionClass.getPackage().getName();
        String pkgPart = null;
        if (actionPackages != null) {
            for (String actionPackage : actionPackages) {
                if (pkg.startsWith(actionPackage)) {
                    pkgPart = actionClass.getName().substring(actionPackage.length() + 1);
                }
            }
        }

        if (pkgPart == null && packageLocators != null) {
            for (String packageLocator : packageLocators) {
                // check subpackage and not a part of package name, eg. actions -> my.actions.transactions - WW-3803
                int index = pkg.lastIndexOf("." + packageLocator + ".");

                // This ensures that the match is at the end, beginning or has a dot on each side of it
                if (index >= 0 && (index + packageLocator.length() == pkg.length() || index == 0 ||
                        (pkg.charAt(index) == '.' && pkg.charAt(index + 1 + packageLocator.length()) == '.'))) {
                    pkgPart = actionClass.getName().substring(index + packageLocator.length() + 2);
                }
            }
        }

        if (pkgPart != null) {
            final int indexOfDot = pkgPart.lastIndexOf('.');
            if (indexOfDot >= 0) {
                String convertedNamespace = actionNameBuilder.build(pkgPart.substring(0, indexOfDot));
                namespaces.add("/" + convertedNamespace.replace('.', '/'));
                return namespaces;
            }
        }

        namespaces.add("");
        return namespaces;
    }

    /**
     * Converts the class name into an action name using the ActionNameBuilder.
     *
     * @param actionClass The action class.
     * @return The action name.
     */
    protected String determineActionName(Class<?> actionClass) {
        String actionName = actionNameBuilder.build(actionClass.getSimpleName());
        if (LOG.isTraceEnabled()) {
            LOG.trace("Got actionName for class [#0] of [#1]", actionClass.toString(), actionName);
        }

        return actionName;
    }

    /**
     * Locates all of the {@link Actions} and {@link Action} annotations on methods within the Action
     * class and its parent classes.
     *
     * @param actionClass The action class.
     * @return The list of annotations or an empty list if there are none.
     */
    protected Map<String, List<Action>> getActionAnnotations(Class<?> actionClass) {
        Method[] methods = actionClass.getMethods();
        Map<String, List<Action>> map = new HashMap<String, List<Action>>();
        for (Method method : methods) {
            Actions actionsAnnotation = method.getAnnotation(Actions.class);
            if (actionsAnnotation != null) {
                List<Action> actions = checkActionsAnnotation(actionsAnnotation);
                map.put(method.getName(), actions);
            } else {
                Action ann = method.getAnnotation(Action.class);
                if (ann != null) {
                    map.put(method.getName(), Arrays.asList(ann));
                }
            }
        }

        return map;
    }

    /**
     *  Builds a list of actions from an @Actions annotation, and check that they are not all empty
     * @param actionsAnnotation Actions annotation
     * @return a list   of Actions
     */
    protected List<Action> checkActionsAnnotation(Actions actionsAnnotation) {
        Action[] actionArray = actionsAnnotation.value();
        boolean valuelessSeen = false;
        List<Action> actions = new ArrayList<Action>();
        for (Action ann : actionArray) {
            if (ann.value().equals(Action.DEFAULT_VALUE) && !valuelessSeen) {
                valuelessSeen = true;
            } else if (ann.value().equals(Action.DEFAULT_VALUE)) {
                throw new ConfigurationException("You may only add a single Action " +
                        "annotation that has no value parameter.");
            }

            actions.add(ann);
        }
        return actions;
    }

    /**
     * Creates a single ActionConfig object.
     *
     * @param pkgCfg       The package the action configuration instance will belong to.
     * @param actionClass  The action class.
     * @param actionName   The name of the action.
     * @param actionMethod The method that the annotation was on (if the annotation is not null) or
     *                     the default method (execute).
     * @param annotation   The ActionName annotation that might override the action name and possibly
     */
    protected void createActionConfig(PackageConfig.Builder pkgCfg, Class<?> actionClass, String actionName,
                                      String actionMethod, Action annotation) {
    	String className = actionClass.getName();
        if (annotation != null) {
            actionName = annotation.value() != null && annotation.value().equals(Action.DEFAULT_VALUE) ? actionName : annotation.value();
            actionName = StringUtils.contains(actionName, "/") && !slashesInActionNames ? StringUtils.substringAfterLast(actionName, "/") : actionName;
            if(!Action.DEFAULT_VALUE.equals(annotation.className())){
            	className = annotation.className();
            }
        }

        ActionConfig.Builder actionConfig = new ActionConfig.Builder(pkgCfg.getName(), actionName, className);
        actionConfig.methodName(actionMethod);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating action config for class [#0], name [#1] and package name [#2] in namespace [#3]",
                    actionClass.toString(), actionName, pkgCfg.getName(), pkgCfg.getNamespace());
        }

        //build interceptors
        List<InterceptorMapping> interceptors = interceptorMapBuilder.build(actionClass, pkgCfg, actionName, annotation);
        actionConfig.addInterceptors(interceptors);

        //build results
        Map<String, ResultConfig> results = resultMapBuilder.build(actionClass, annotation, actionName, pkgCfg.build());
        actionConfig.addResultConfigs(results);

        //add params
        if (annotation != null)
            actionConfig.addParams(StringTools.createParameterMap(annotation.params()));

        //add exception mappings from annotation
        if (annotation != null && annotation.exceptionMappings() != null)
            actionConfig.addExceptionMappings(buildExceptionMappings(annotation.exceptionMappings(), actionName));

        //add exception mapping from class
        ExceptionMappings exceptionMappings = actionClass.getAnnotation(ExceptionMappings.class);
        if (exceptionMappings != null)
            actionConfig.addExceptionMappings(buildExceptionMappings(exceptionMappings.value(), actionName));

        //add
        pkgCfg.addActionConfig(actionName, actionConfig.build());

        //check if an action with the same name exists on that package (from XML config probably)
        PackageConfig existingPkg = configuration.getPackageConfig(pkgCfg.getName());
        if (existingPkg != null) {
            // there is a package already with that name, check action
            ActionConfig existingActionConfig = existingPkg.getActionConfigs().get(actionName);
            if (existingActionConfig != null && LOG.isWarnEnabled())
                LOG.warn("Duplicated action definition in package [#0] with name [#1].", pkgCfg.getName(), actionName);
        }

        //watch class file
        if (isReloadEnabled()) {
            URL classFile = actionClass.getResource(actionClass.getSimpleName() + ".class");
            fileManager.monitorFile(classFile);
            loadedFileUrls.add(classFile.toString());
        }
    }

    protected List<ExceptionMappingConfig> buildExceptionMappings(ExceptionMapping[] exceptions, String actionName) {
        List<ExceptionMappingConfig> exceptionMappings = new ArrayList<ExceptionMappingConfig>();

        for (ExceptionMapping exceptionMapping : exceptions) {
            if (LOG.isTraceEnabled())
                LOG.trace("Mapping exception [#0] to result [#1] for action [#2]", exceptionMapping.exception(),
                        exceptionMapping.result(), actionName);
            ExceptionMappingConfig.Builder builder = new ExceptionMappingConfig.Builder(null, exceptionMapping
                    .exception(), exceptionMapping.result());
            if (exceptionMapping.params() != null)
                builder.addParams(StringTools.createParameterMap(exceptionMapping.params()));
            exceptionMappings.add(builder.build());
        }

        return exceptionMappings;
    }

    protected PackageConfig.Builder getPackageConfig(final Map<String, PackageConfig.Builder> packageConfigs,
                                                   String actionNamespace, final String actionPackage, final Class<?> actionClass,
                                                   Action action) {
        if (action != null && !action.value().equals(Action.DEFAULT_VALUE)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Using non-default action namespace from the Action annotation of [#0]", action.value());
            }
            String actionName = action.value();
            actionNamespace = StringUtils.contains(actionName, "/") ? StringUtils.substringBeforeLast(actionName, "/") : StringUtils.EMPTY;
        }

        // Next grab the parent annotation from the class
        ParentPackage parent = AnnotationUtils.findAnnotation(actionClass, ParentPackage.class);
        String parentName = null;
        if (parent != null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Using non-default parent package from annotation of [#0]", parent.value());
            }

            parentName = parent.value();
        }

        // Finally use the default
        if (parentName == null) {
            parentName = defaultParentPackage;
        }

        if (parentName == null) {
            throw new ConfigurationException("Unable to determine the parent XWork package for the action class [" +
                    actionClass.getName() + "]");
        }

        PackageConfig parentPkg = configuration.getPackageConfig(parentName);
        if (parentPkg == null) {
            throw new ConfigurationException("Unable to locate parent package [" + parentName + "] for [" + actionClass + "]");
        }

        // Grab based on package-namespace and if it exists, we need to ensure the existing one has
        // the correct parent package. If not, we need to create a new package config
        String name = actionPackage + "#" + parentPkg.getName() + "#" + actionNamespace;
        PackageConfig.Builder pkgConfig = packageConfigs.get(name);
        if (pkgConfig == null) {
            pkgConfig = new PackageConfig.Builder(name).namespace(actionNamespace).addParent(parentPkg);
            packageConfigs.put(name, pkgConfig);

            //check for @DefaultInterceptorRef in the package
            DefaultInterceptorRef defaultInterceptorRef = AnnotationUtils.findAnnotation(actionClass, DefaultInterceptorRef.class);
            if (defaultInterceptorRef != null) {
                pkgConfig.defaultInterceptorRef(defaultInterceptorRef.value());

                if (LOG.isTraceEnabled())
                    LOG.trace("Setting [#0] as the default interceptor ref for [#1]", defaultInterceptorRef.value(), pkgConfig.getName());
            }
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Created package config named [#0] with a namespace [#1]", name, actionNamespace);
        }

        return pkgConfig;
    }

    /**
     * Determine all the index handling actions and results based on this logic:
     *
     * 1. Loop over all the namespaces such as /foo and see if it has an action named index
     * 2. If an action doesn't exists in the parent namespace of the same name, create an action
     * in the parent namespace of the same name as the namespace that points to the index
     * action in the namespace. e.g. /foo -> /foo/index
     * 3. Create the action in the namespace for empty string if it doesn't exist. e.g. /foo/
     * the action is "" and the namespace is /foo
     *
     * @param packageConfigs Used to store the actions.
     */
    protected void buildIndexActions(Map<String, PackageConfig.Builder> packageConfigs) {
        Map<String, PackageConfig.Builder> byNamespace = new HashMap<String, PackageConfig.Builder>();
        Collection<PackageConfig.Builder> values = packageConfigs.values();
        for (PackageConfig.Builder packageConfig : values) {
            byNamespace.put(packageConfig.getNamespace(), packageConfig);
        }

        // Step #1
        Set<String> namespaces = byNamespace.keySet();
        for (String namespace : namespaces) {
            // First see if the namespace has an index action
            PackageConfig.Builder pkgConfig = byNamespace.get(namespace);
            ActionConfig indexActionConfig = pkgConfig.build().getAllActionConfigs().get("index");
            if (indexActionConfig == null) {
                continue;
            }

            // Step #2
            if (!redirectToSlash) {
                int lastSlash = namespace.lastIndexOf('/');
                if (lastSlash >= 0) {
                    String parentAction = namespace.substring(lastSlash + 1);
                    String parentNamespace = namespace.substring(0, lastSlash);
                    PackageConfig.Builder parent = byNamespace.get(parentNamespace);
                    if (parent == null || parent.build().getAllActionConfigs().get(parentAction) == null) {
                        if (parent == null) {
                            parent = new PackageConfig.Builder(parentNamespace).namespace(parentNamespace).
                                    addParents(pkgConfig.build().getParents());
                            packageConfigs.put(parentNamespace, parent);
                        }

                        if (parent.build().getAllActionConfigs().get(parentAction) == null) {
                            parent.addActionConfig(parentAction, indexActionConfig);
                        }
                    } else if (LOG.isTraceEnabled()) {
                        LOG.trace("The parent namespace [#0] already contains " +
                                "an action [#1]", parentNamespace, parentAction);
                    }
                }
            }

            // Step #3
            if (pkgConfig.build().getAllActionConfigs().get("") == null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Creating index ActionConfig with an action name of [] for the action " +
                            "class [#0]", indexActionConfig.getClassName());
                }

                pkgConfig.addActionConfig("", indexActionConfig);
            }
        }
    }

    public void destroy() {
        loadedFileUrls.clear();
    }

    public boolean needsReload() {
        if (devMode && reload) {
            for (String url : loadedFileUrls) {
                if (fileManager.fileNeedsReloading(url)) {
                    if (LOG.isDebugEnabled())
                        LOG.debug("File [#0] changed, configuration will be reloaded", url);
                    return true;
                }
            }

            return false;
        } else
            return false;
    }

}
