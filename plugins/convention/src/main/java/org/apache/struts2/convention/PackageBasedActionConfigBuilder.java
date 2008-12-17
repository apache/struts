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

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.AnnotationTools;
import org.apache.struts2.convention.annotation.DefaultInterceptorRef;
import org.apache.struts2.convention.annotation.ExceptionMapping;
import org.apache.struts2.convention.annotation.ExceptionMappings;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Namespaces;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.finder.ClassFinder;
import com.opensymphony.xwork2.util.finder.Test;
import com.opensymphony.xwork2.util.finder.UrlSet;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * <p>
 * This class implements the ActionConfigBuilder interface.
 * </p>
 */
public class PackageBasedActionConfigBuilder implements ActionConfigBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(PackageBasedActionConfigBuilder.class);
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
    private String[] excludeJars;
    private String packageLocatorsBasePackage;
    private boolean disableJarScanning = true;
    private boolean disableActionScanning = false;
    private boolean disablePackageLocatorsScanning = false;
    private String actionSuffix = "Action";
    private boolean checkImplementsAction = true;
    private boolean mapAllMatches = false;

    /**
     * Constructs actions based on a list of packages.
     *
     * @param   configuration The XWork configuration that the new package configs and action configs
     *          are added to.
     * @param   actionNameBuilder The action name builder used to convert action class names to action
     *          names.
     * @param   resultMapBuilder The result map builder used to create ResultConfig mappings for each
     *          action.
     * @param   interceptorMapBuilder The interceptor map builder used to create InterceptorConfig mappings for each
     *          action.
     * @param   objectFactory The ObjectFactory used to create the actions and such.
     * @param   redirectToSlash A boolean parameter that controls whether or not this will create an
     *          action for indexes. If this is set to true, index actions are not created because
     *          the unknown handler will redirect from /foo to /foo/. The only action that is created
     *          is to the empty action in the namespace (e.g. the namespace /foo and the action "").
     * @param   defaultParentPackage The default parent package for all the configuration.
     */
    @Inject
    public PackageBasedActionConfigBuilder(Configuration configuration, ActionNameBuilder actionNameBuilder,
            ResultMapBuilder resultMapBuilder, InterceptorMapBuilder interceptorMapBuilder, ObjectFactory objectFactory,
            @Inject("struts.convention.redirect.to.slash") String redirectToSlash,
            @Inject("struts.convention.default.parent.package") String defaultParentPackage) {

        // Validate that the parameters are okay
        this.configuration = configuration;
        this.actionNameBuilder = actionNameBuilder;
        this.resultMapBuilder = resultMapBuilder;
        this.interceptorMapBuilder = interceptorMapBuilder;
        this.objectFactory = objectFactory;
        this.redirectToSlash = Boolean.parseBoolean(redirectToSlash);

        if (LOG.isTraceEnabled()) {
            LOG.trace("Setting action default parent package to [#0]", defaultParentPackage);
        }

        this.defaultParentPackage = defaultParentPackage;
    }

    /**
     * @param disableActionScanning Disable scanning for actions
     */
    @Inject(value = "struts.convention.action.disableScanning", required = false)
    public void setDisableActionScanning(String disableActionScanning) {
        this.disableActionScanning = "true".equals(disableActionScanning);
    }

    /**
     * @param exlcudeJars Comma separated list of regular expressions of jars to be exluded.
     *                    Ignored if "struts.convention.action.disableJarScanning" is true
     */
    @Inject(value = "struts.convention.action.excludeJars", required = false)
    public void setExcludeJars(String excludeJars) {
        this.excludeJars = excludeJars.split("\\s*[,]\\s*");;
    }

    /**
     * @param disableJarScanning Disable scanning jar files for actions
     */
    @Inject(value = "struts.convention.action.disableJarScanning", required = false)
    public void setDisableJarScanning(String disableJarScanning) {
        this.disableJarScanning = "true".equals(disableJarScanning);
    }

    /**
     * @param disableActionScanning If set to true, only the named packages will be scanned
     */
    @Inject(value = "struts.convention.package.locators.disable", required = false)
    public void setDisablePackageLocatorsScanning(String disablePackageLocatorsScanning) {
        this.disablePackageLocatorsScanning = "true".equals(disablePackageLocatorsScanning);
    }

    /**
     * @param   actionPackages (Optional) An optional list of action packages that this should create
     *          configuration for.
     */
    @Inject(value = "struts.convention.action.packages", required = false)
    public void setActionPackages(String actionPackages) {
        if (!StringTools.isTrimmedEmpty(actionPackages)) {
            this.actionPackages = actionPackages.split("\\s*[,]\\s*");
        }
    }

    /**
     * @param   actionPackages (Optional) Map classes that implement com.opensymphony.xwork2.Action
     *          as actions
     */
    @Inject(value = "struts.convention.action.checkImplementsAction", required = false)
    public void setCheckImplementsAction(String checkImplementsAction) {
        this.checkImplementsAction = "true".equals(checkImplementsAction);
    }

    /**
     * @param   actionSuffix (Optional) Classes that end with these value will be mapped as actions
     *          (defaults to "Action")
     */
    @Inject(value = "struts.convention.action.suffix", required = false)
    public void setActionSuffix(String actionSuffix) {
        if (!StringTools.isTrimmedEmpty(actionSuffix)) {
            this.actionSuffix = actionSuffix;
        }
    }

    /**
     * @param   excludePackages (Optional) A  list of packages that should be skipped when building
     *          configuration.
     */
    @Inject(value = "struts.convention.exclude.packages", required = false)
    public void setExcludePackages(String excludePackages) {
        if (!StringTools.isTrimmedEmpty(excludePackages)) {
            this.excludePackages = excludePackages.split("\\s*[,]\\s*");
        }
    }

    /**
     * @param   packageLocators (Optional) A list of names used to find action packages.
     */
    @Inject(value = "struts.convention.package.locators", required = false)
    public void setPackageLocators(String packageLocators) {
        this.packageLocators = packageLocators.split("\\s*[,]\\s*");
    }

    /**
     * @param   packageLocatorsBasePackage (Optional) If set, only packages that start with this
     * name will be scanned for actions.
     */
    @Inject(value = "struts.convention.package.locators.basePackage", required = false)
    public void setPackageLocatorsBase(String packageLocatorsBasePackage) {
        this.packageLocatorsBasePackage = packageLocatorsBasePackage;
    }        

    /**
     * @param   mapAllMatches (Optional) Map actions that match the "*${Suffix}" pattern
     *                          even if they don't have a default method. The mapping from
     *                          the url to the action will be delegated the action mapper.
     */
    @Inject(value = "struts.convention.action.mapAllMatches", required = false)
    public void setMapAllMatches(String mapAllMatches) {
        this.mapAllMatches  = "true".equals(mapAllMatches);
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
        if (!disableActionScanning ) {
            if (actionPackages == null && packageLocators == null) {
                throw new ConfigurationException("At least a list of action packages or action package locators " +
                    "must be given using one of the properties [struts.convention.action.packages] or " +
                    "[struts.convention.package.locators]");
            }

            if (LOG.isTraceEnabled()) {
                LOG.trace("Loading action configurations");
                if (actionPackages != null) {
                    LOG.trace("Actions being loaded from action packages " + Arrays.asList(actionPackages));
                }
                if (packageLocators != null) {
                    LOG.trace("Actions being loaded using package locators " + Arrays.asList(packageLocators));
                }
                if (excludePackages != null) {
                    LOG.trace("Excluding actions from packages " + Arrays.asList(excludePackages));
                }
            }

            Set<Class> classes = findActions();
            buildConfiguration(classes);
        }
    }

    @SuppressWarnings("unchecked")
    protected Set<Class> findActions() {
        Set<Class> classes = new HashSet<Class>();
        try {
            if (actionPackages != null || (packageLocators != null && !disablePackageLocatorsScanning)) {
                ClassFinder finder = new ClassFinder(getClassLoader(), buildUrlSet().getUrls(), true);

                // named packages
                if (actionPackages != null) {
                    for (String packageName : actionPackages) {
                        Test<ClassFinder.ClassInfo> test = getPackageFinderTest(packageName);
                        classes.addAll(finder.findClasses(test));
                    }
                }

                //package locators
                if (packageLocators != null && !disablePackageLocatorsScanning) {
                    for (String packageLocator : packageLocators) {
                        Test<ClassFinder.ClassInfo> test = getPackageLocatorTest(packageLocator);
                        classes.addAll(finder.findClasses(test));
                    }
                }
            }
        } catch (Exception ex) {
            if (LOG.isErrorEnabled())
                LOG.error("Unable to scan named packages", ex);
        }

        return classes;
    }

    private UrlSet buildUrlSet() throws IOException {
        UrlSet urlSet = new UrlSet(getClassLoader());

        urlSet = urlSet.exclude(ClassLoader.getSystemClassLoader().getParent());
        urlSet = urlSet.excludeJavaExtDirs();
        urlSet = urlSet.excludeJavaEndorsedDirs();
        urlSet = urlSet.excludeJavaHome();
        urlSet = urlSet.excludePaths(System.getProperty("sun.boot.class.path", ""));
        urlSet = urlSet.exclude(".*/JavaVM.framework/.*");

        if (disableJarScanning) {
            urlSet = urlSet.exclude(".*?jar(!/)?");
        } else if (excludeJars != null) {
            for (String pattern : excludeJars) {
                urlSet = urlSet.exclude(pattern.trim());
            }
        }

        return urlSet;
    }

    private ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    protected Test<ClassFinder.ClassInfo> getPackageFinderTest(final String packageName) {
        // so "my.package" does not match "my.package2.test"
        final String strictPackageName = packageName + ".";
        return new Test<ClassFinder.ClassInfo>() {
            public boolean test(ClassFinder.ClassInfo classInfo) {
                String classPackageName = classInfo.getPackageName();
                boolean inPackage = classPackageName.equals(packageName) || classPackageName.startsWith(strictPackageName);
                boolean nameMatches = classInfo.getName().endsWith(actionSuffix);

                try {
                    return inPackage && (nameMatches ||  (checkImplementsAction && com.opensymphony.xwork2.Action.class.isAssignableFrom(classInfo.get())));
                } catch (ClassNotFoundException ex) {
                    if (LOG.isErrorEnabled())
                        LOG.error("Unable to load class [#0]", ex, classInfo.getName());
                    return false;
                }
            }
        };
    }

    protected Test<ClassFinder.ClassInfo> getPackageLocatorTest(final String packageLocator) {
        return new Test<ClassFinder.ClassInfo>() {
            public boolean test(ClassFinder.ClassInfo classInfo) {
                String packageName = classInfo.getPackageName();
                if (packageName.length() > 0 && (packageLocatorsBasePackage == null || packageName.startsWith(packageLocatorsBasePackage))) {
                    String[] splitted = packageName.split("\\.");

                    boolean packageMatches = StringTools.contains(splitted, packageLocator, false);
                    boolean nameMatches = classInfo.getName().endsWith(actionSuffix);

                    try {
                        return packageMatches && (nameMatches ||  (checkImplementsAction && com.opensymphony.xwork2.Action.class.isAssignableFrom(classInfo.get())));
                    } catch (ClassNotFoundException ex) {
                        if (LOG.isErrorEnabled())
                            LOG.error("Unable to load class [#0]", ex, classInfo.getName());
                        return false;
                    }
                } else
                    return false;
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected void buildConfiguration(Set<Class> classes) {
        Map<String, PackageConfig.Builder> packageConfigs = new HashMap<String, PackageConfig.Builder>();

        for (Class<?> actionClass : classes) {
            // Skip all interfaces, enums, annotations, and abstract classes
            if (actionClass.isAnnotation() || actionClass.isInterface() || actionClass.isEnum() ||
                    (actionClass.getModifiers() & Modifier.ABSTRACT) != 0) {
                continue;
            }

            // Tell the ObjectFactory about this class
            try {
                objectFactory.getClassInstance(actionClass.getName());
            } catch (ClassNotFoundException e) {
                // Impossible
                new Throwable().printStackTrace();
                System.exit(1);
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
                String defaultActionMethod = "execute";
                PackageConfig.Builder defaultPackageConfig = getPackageConfig(packageConfigs, namespace,
                    actionPackage, actionClass, null);

                // Verify that the annotations have no errors and also determine if the default action
                // configuration should still be built or not.
                Map<String, List<Action>> map = getActionAnnotations(actionClass);
                Set<String> actionNames = new HashSet<String>();
                if (!map.containsKey(defaultActionMethod) && ReflectionTools.containsMethod(actionClass, defaultActionMethod)) {
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
                        createActionConfig(defaultPackageConfig, actionClass, defaultActionName, defaultActionMethod, null);
                    }
                }

                // Build the actions for the annotations
                for (String method : map.keySet()) {
                    List<Action> actions = map.get(method);
                    for (Action action : actions) {
                        PackageConfig.Builder pkgCfg = defaultPackageConfig;
                        if (action.value().contains("/")) {
                            pkgCfg = getPackageConfig(packageConfigs, namespace, actionPackage,
                                actionClass, action);
                        }

                        createActionConfig(pkgCfg, actionClass, defaultActionName, method, action);
                    }
                }

                // some actions will not have any @Action or a default method, like the rest actions
                // where the action mapper is the one that finds the right method at runtime
                if (map.isEmpty() && mapAllMatches) {
                    Action actionAnnotation = actionClass.getAnnotation(Action.class);
                    createActionConfig(defaultPackageConfig, actionClass, defaultActionName, null, actionAnnotation);
                }
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
     * Determines the namespace(s) for the action based on the action class. If there is a {@link Namespace}
     * annotation on the class (including parent classes) or on the package that the class is in, than
     * it is used. Otherwise, the Java package name that the class is in is used in conjunction with
     * either the <b>struts.convention.action.packages</b> or <b>struts.convention.package.locators</b>
     * configuration values. These are used to determine which part of the Java package name should
     * be converted into the namespace for the XWork PackageConfig.
     *
     * @param   actionClass The action class.
     * @return  The namespace or an empty string.
     */
    protected List<String> determineActionNamespace(Class<?> actionClass) {
        List<String> namespaces = new ArrayList<String>();

        // Check if there is a class or package level annotation for the namespace
        //single namespace
        Namespace namespaceAnnotation = AnnotationTools.findAnnotation(actionClass, Namespace.class);
        if (namespaceAnnotation != null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Using non-default action namespace from Namespace annotation of [#0]", namespaceAnnotation.value());
            }

            namespaces.add(namespaceAnnotation.value());
        }

        //multiple annotations
        Namespaces namespacesAnnotation = AnnotationTools.findAnnotation(actionClass, Namespaces.class);
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
                int index = pkg.lastIndexOf(packageLocator);

                // This ensures that the match is at the end, beginning or has a dot on each side of it
                if (index >= 0 && (index + packageLocator.length() == pkg.length() || index == 0 ||
                        (pkg.charAt(index - 1) == '.' && pkg.charAt(index + packageLocator.length()) == '.'))) {
                    pkgPart = actionClass.getName().substring(index + packageLocator.length() + 1);
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
     * @param   actionClass The action class.
     * @return  The action name.
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
     * @param   actionClass The action class.
     * @return  The list of annotations or an empty list if there are none.
     */
    protected Map<String, List<Action>> getActionAnnotations(Class<?> actionClass) {
        Method[] methods = actionClass.getMethods();
        Map<String, List<Action>> map = new HashMap<String, List<Action>>();
        for (Method method : methods) {
            Actions actionsAnnotation = method.getAnnotation(Actions.class);
            if (actionsAnnotation != null) {
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
     * Creates a single ActionConfig object.
     *
     * @param   pkgCfg The package the action configuration instance will belong to.
     * @param   actionClass The action class.
     * @param   actionName The name of the action.
     * @param   actionMethod The method that the annotation was on (if the annotation is not null) or
     *          the default method (execute).
     * @param   annotation The ActionName annotation that might override the action name and possibly
     */
    protected void createActionConfig(PackageConfig.Builder pkgCfg, Class<?> actionClass, String actionName,
            String actionMethod, Action annotation) {
        if (annotation != null) {
            actionName = annotation.value() != null && annotation.value().equals(Action.DEFAULT_VALUE) ?
                actionName : annotation.value();
            actionName = StringTools.lastToken(actionName, "/");
        }

        ActionConfig.Builder actionConfig = new ActionConfig.Builder(pkgCfg.getName(),
            actionName, actionClass.getName());
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
                LOG.warn("Duplicated action definition in package [#0] with name [#1]. First definition was loaded from [#3]", pkgCfg.getName(), actionName, existingActionConfig.getLocation().toString());
        }
    }

    private List<ExceptionMappingConfig> buildExceptionMappings(ExceptionMapping[] exceptions, String actionName) {
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

    private PackageConfig.Builder getPackageConfig(final Map<String, PackageConfig.Builder> packageConfigs,
            String actionNamespace, final String actionPackage, final Class<?> actionClass,
            Action action) {
        if (action != null && !action.value().equals(Action.DEFAULT_VALUE)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Using non-default action namespace from the Action annotation of [#0]", action.value());
            }
            actionNamespace = StringTools.upToLastToken(action.value(), "/");
        }

        // Next grab the parent annotation from the class
        ParentPackage parent = AnnotationTools.findAnnotation(actionClass, ParentPackage.class);
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
            throw new ConfigurationException("Unable to locate parent package [" + parentName + "]");
        }

        // Grab based on package-namespace and if it exists, we need to ensure the existing one has
        // the correct parent package. If not, we need to create a new package config
        String name = actionPackage + "#" + parentPkg.getName() + "#" + actionNamespace;
        PackageConfig.Builder pkgConfig = packageConfigs.get(name);
        if (pkgConfig == null) {
            pkgConfig = new PackageConfig.Builder(name).namespace(actionNamespace).addParent(parentPkg);
            packageConfigs.put(name, pkgConfig);

            //check for @DefaultInterceptorRef in the package
            DefaultInterceptorRef defaultInterceptorRef = AnnotationTools.findAnnotation(actionClass, DefaultInterceptorRef.class);
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
     *    in the parent namespace of the same name as the namespace that points to the index
     *    action in the namespace. e.g. /foo -> /foo/index
     * 3. Create the action in the namespace for empty string if it doesn't exist. e.g. /foo/
     *    the action is "" and the namespace is /foo
     *
     * @param   packageConfigs Used to store the actions.
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
}
