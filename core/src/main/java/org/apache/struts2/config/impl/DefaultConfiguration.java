/*
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
package org.apache.struts2.config.impl;

import org.apache.struts2.ActionContext;
import org.apache.struts2.locale.DefaultLocaleProviderFactory;
import org.apache.struts2.text.DefaultTextProvider;
import org.apache.struts2.FileManager;
import org.apache.struts2.FileManagerFactory;
import org.apache.struts2.locale.LocaleProviderFactory;
import org.apache.struts2.text.LocalizedTextProvider;
import org.apache.struts2.ObjectFactory;
import org.apache.struts2.text.StrutsTextProviderFactory;
import org.apache.struts2.text.TextProvider;
import org.apache.struts2.text.TextProviderFactory;
import org.apache.struts2.config.Configuration;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.ContainerProvider;
import org.apache.struts2.config.FileManagerFactoryProvider;
import org.apache.struts2.config.FileManagerProvider;
import org.apache.struts2.config.PackageProvider;
import org.apache.struts2.config.RuntimeConfiguration;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.entities.InterceptorMapping;
import org.apache.struts2.config.entities.PackageConfig;
import org.apache.struts2.config.entities.ResultConfig;
import org.apache.struts2.config.entities.ResultTypeConfig;
import org.apache.struts2.config.entities.UnknownHandlerConfig;
import org.apache.struts2.config.providers.EnvsValueSubstitutor;
import org.apache.struts2.config.providers.InterceptorBuilder;
import org.apache.struts2.config.providers.ValueSubstitutor;
import org.apache.struts2.conversion.ConversionAnnotationProcessor;
import org.apache.struts2.conversion.ConversionFileProcessor;
import org.apache.struts2.conversion.ConversionPropertiesProcessor;
import org.apache.struts2.conversion.ObjectTypeDeterminer;
import org.apache.struts2.conversion.TypeConverter;
import org.apache.struts2.conversion.TypeConverterCreator;
import org.apache.struts2.conversion.TypeConverterHolder;
import org.apache.struts2.conversion.impl.ArrayConverter;
import org.apache.struts2.conversion.impl.CollectionConverter;
import org.apache.struts2.conversion.impl.DateConverter;
import org.apache.struts2.conversion.impl.DefaultConversionAnnotationProcessor;
import org.apache.struts2.conversion.impl.DefaultConversionFileProcessor;
import org.apache.struts2.conversion.impl.DefaultObjectTypeDeterminer;
import org.apache.struts2.conversion.impl.NumberConverter;
import org.apache.struts2.conversion.impl.StringConverter;
import org.apache.struts2.conversion.impl.XWorkBasicConverter;
import org.apache.struts2.conversion.impl.XWorkConverter;
import org.apache.struts2.factory.ActionFactory;
import org.apache.struts2.factory.ConverterFactory;
import org.apache.struts2.factory.DefaultActionFactory;
import org.apache.struts2.factory.DefaultInterceptorFactory;
import org.apache.struts2.factory.DefaultUnknownHandlerFactory;
import org.apache.struts2.factory.DefaultValidatorFactory;
import org.apache.struts2.factory.InterceptorFactory;
import org.apache.struts2.factory.ResultFactory;
import org.apache.struts2.factory.StrutsConverterFactory;
import org.apache.struts2.factory.UnknownHandlerFactory;
import org.apache.struts2.factory.ValidatorFactory;
import org.apache.struts2.inject.Container;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.inject.Context;
import org.apache.struts2.inject.Factory;
import org.apache.struts2.inject.Scope;
import org.apache.struts2.ognl.BeanInfoCacheFactory;
import org.apache.struts2.ognl.DefaultOgnlBeanInfoCacheFactory;
import org.apache.struts2.ognl.DefaultOgnlExpressionCacheFactory;
import org.apache.struts2.ognl.ExpressionCacheFactory;
import org.apache.struts2.ognl.OgnlCacheFactory;
import org.apache.struts2.ognl.OgnlReflectionProvider;
import org.apache.struts2.ognl.OgnlUtil;
import org.apache.struts2.ognl.OgnlValueStackFactory;
import org.apache.struts2.ognl.SecurityMemberAccess;
import org.apache.struts2.ognl.accessor.CompoundRootAccessor;
import org.apache.struts2.ognl.accessor.RootAccessor;
import org.apache.struts2.ognl.accessor.XWorkMethodAccessor;
import org.apache.struts2.util.OgnlTextParser;
import org.apache.struts2.util.PatternMatcher;
import org.apache.struts2.text.StrutsLocalizedTextProvider;
import org.apache.struts2.util.TextParser;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;
import org.apache.struts2.util.fs.DefaultFileManager;
import org.apache.struts2.util.fs.DefaultFileManagerFactory;
import org.apache.struts2.util.location.LocatableProperties;
import org.apache.struts2.util.reflection.ReflectionProvider;
import ognl.MethodAccessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.conversion.StrutsConversionPropertiesProcessor;
import org.apache.struts2.conversion.StrutsTypeConverterCreator;
import org.apache.struts2.conversion.StrutsTypeConverterHolder;
import org.apache.struts2.factory.StrutsResultFactory;
import org.apache.struts2.ognl.OgnlGuard;
import org.apache.struts2.ognl.ProviderAllowlist;
import org.apache.struts2.ognl.StrutsOgnlGuard;
import org.apache.struts2.ognl.ThreadAllowlist;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * DefaultConfiguration
 *
 * @author Jason Carreira
 *         Created Feb 24, 2003 7:38:06 AM
 */
public class DefaultConfiguration implements Configuration {

    public static final Map<String, Object> BOOTSTRAP_CONSTANTS;

    static {
        Map<String, Object> constants = new HashMap<>();
        constants.put(StrutsConstants.STRUTS_DEVMODE, Boolean.FALSE);
        constants.put(StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, Boolean.FALSE);
        constants.put(StrutsConstants.STRUTS_MATCHER_APPEND_NAMED_PARAMETERS, Boolean.TRUE);
        constants.put(StrutsConstants.STRUTS_OGNL_EXPRESSION_CACHE_TYPE, OgnlCacheFactory.CacheType.BASIC);
        constants.put(StrutsConstants.STRUTS_OGNL_EXPRESSION_CACHE_MAXSIZE, 10000);
        constants.put(StrutsConstants.STRUTS_OGNL_BEANINFO_CACHE_TYPE, OgnlCacheFactory.CacheType.BASIC);
        constants.put(StrutsConstants.STRUTS_OGNL_BEANINFO_CACHE_MAXSIZE, 10000);
        constants.put(StrutsConstants.STRUTS_ENABLE_DYNAMIC_METHOD_INVOCATION, Boolean.FALSE);
        BOOTSTRAP_CONSTANTS = Collections.unmodifiableMap(constants);
    }

    protected static final Logger LOG = LogManager.getLogger(DefaultConfiguration.class);

    // Programmatic Action Configurations
    protected Map<String, PackageConfig> packageContexts = new LinkedHashMap<>();
    protected RuntimeConfiguration runtimeConfiguration;
    protected Container container;
    protected String defaultFrameworkBeanName;
    protected Set<String> loadedFileNames = new TreeSet<>();
    protected List<UnknownHandlerConfig> unknownHandlerStack;


    ObjectFactory objectFactory;

    public DefaultConfiguration() {
        this(Container.DEFAULT_NAME);
    }

    public DefaultConfiguration(String defaultBeanName) {
        this.defaultFrameworkBeanName = defaultBeanName;
    }


    @Override
    public PackageConfig getPackageConfig(String name) {
        return packageContexts.get(name);
    }

    @Override
    public List<UnknownHandlerConfig> getUnknownHandlerStack() {
        return unknownHandlerStack;
    }

    @Override
    public void setUnknownHandlerStack(List<UnknownHandlerConfig> unknownHandlerStack) {
        this.unknownHandlerStack = unknownHandlerStack;
    }

    @Override
    public Set<String> getPackageConfigNames() {
        return packageContexts.keySet();
    }

    @Override
    public Map<String, PackageConfig> getPackageConfigs() {
        return packageContexts;
    }

    @Override
    public Set<String> getLoadedFileNames() {
        return loadedFileNames;
    }

    @Override
    public RuntimeConfiguration getRuntimeConfiguration() {
        return runtimeConfiguration;
    }

    /**
     * @return the container
     */
    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void addPackageConfig(String name, PackageConfig packageContext) {
        PackageConfig check = packageContexts.get(name);
        if (check != null) {
            if (check.getLocation() != null && packageContext.getLocation() != null
                    && check.getLocation().equals(packageContext.getLocation())) {
                LOG.debug("The package name '{}' is already been loaded by the same location and could be removed: {}",
                        name, packageContext.getLocation());
            } else {
                throw new ConfigurationException("The package name '" + name
                        + "' at location "+packageContext.getLocation()
                        + " is already been used by another package at location " + check.getLocation(),
                        packageContext);
            }
        }
        packageContexts.put(name, packageContext);
    }

    @Override
    public PackageConfig removePackageConfig(String packageName) {
        return packageContexts.remove(packageName);
    }

    /**
     * Allows the configuration to clean up any resources used
     */
    @Override
    public void destroy() {
        packageContexts.clear();
        loadedFileNames.clear();
    }

    @Override
    public void rebuildRuntimeConfiguration() {
        runtimeConfiguration = buildRuntimeConfiguration();
    }

    /**
     * Calls the ConfigurationProviderFactory.getConfig() to tell it to reload the configuration and then calls
     * buildRuntimeConfiguration().
     *
     * @param providers list of ContainerProvider
     * @return list of package providers
     *
     * @throws ConfigurationException in case of any configuration errors
     */
    @Override
    public synchronized List<PackageProvider> reloadContainer(List<ContainerProvider> providers) throws ConfigurationException {
        packageContexts.clear();
        loadedFileNames.clear();
        List<PackageProvider> packageProviders = new ArrayList<>();

        ContainerProperties props = new ContainerProperties();
        ContainerBuilder builder = new ContainerBuilder();
        Container bootstrap = createBootstrapContainer(providers);
        for (final ContainerProvider containerProvider : providers)
        {
            bootstrap.inject(containerProvider);
            containerProvider.init(this);
            containerProvider.register(builder, props);
        }
        props.setConstants(builder);

        builder.factory(Configuration.class, new Factory<Configuration>() {
            @Override
            public Configuration create(Context context) throws Exception {
                return DefaultConfiguration.this;
            }

            @Override
            public Class<? extends Configuration> type() {
                return DefaultConfiguration.this.getClass();
            }
        });

        ActionContext oldContext = ActionContext.getContext();
        try {
            // Set the bootstrap container for the purposes of factory creation

            setContext(bootstrap);
            container = builder.create(false);
            setContext(container);
            objectFactory = container.getInstance(ObjectFactory.class);

            // Process the configuration providers first
            for (final ContainerProvider containerProvider : providers)
            {
                if (containerProvider instanceof PackageProvider) {
                    container.inject(containerProvider);
                    ((PackageProvider)containerProvider).loadPackages();
                    packageProviders.add((PackageProvider)containerProvider);
                }
            }

            // Then process any package providers from the plugins
            Set<String> packageProviderNames = container.getInstanceNames(PackageProvider.class);
            for (String name : packageProviderNames) {
                PackageProvider provider = container.getInstance(PackageProvider.class, name);
                provider.init(this);
                provider.loadPackages();
                packageProviders.add(provider);
            }

            rebuildRuntimeConfiguration();
        } finally {
            if (oldContext == null) {
                ActionContext.clear();
            }
        }
        return packageProviders;
    }

    protected ActionContext setContext(Container cont) {
        ValueStack vs = cont.getInstance(ValueStackFactory.class).createValueStack();
        return ActionContext.of(vs.getContext()).bind();
    }

    protected Container createBootstrapContainer(List<ContainerProvider> providers) {
        ContainerBuilder builder = new ContainerBuilder();
        boolean fmFactoryRegistered = false;
        for (ContainerProvider provider : providers) {
            if (provider instanceof FileManagerProvider) {
                provider.register(builder, null);
            }
            if (provider instanceof FileManagerFactoryProvider) {
                provider.register(builder, null);
                fmFactoryRegistered = true;
            }
        }

        bootstrapFactories(builder);
        bootstrapTypeConverters(builder);

        if (!fmFactoryRegistered) {
            builder.factory(FileManagerFactory.class, DefaultFileManagerFactory.class, Scope.SINGLETON);
        }

        for (Map.Entry<String, Object> entry : BOOTSTRAP_CONSTANTS.entrySet()) {
            builder.constant(entry.getKey(), String.valueOf(entry.getValue()));
        }

        return builder.create(true);
    }

    public static ContainerBuilder bootstrapFactories(ContainerBuilder builder) {
        return builder
                // TODO: SpringObjectFactoryTest fails when these are SINGLETON
                .factory(ObjectFactory.class, Scope.PROTOTYPE)
                .factory(ActionFactory.class, DefaultActionFactory.class, Scope.PROTOTYPE)
                .factory(ResultFactory.class, StrutsResultFactory.class, Scope.PROTOTYPE)
                .factory(InterceptorFactory.class, DefaultInterceptorFactory.class, Scope.PROTOTYPE)
                .factory(ValidatorFactory.class, DefaultValidatorFactory.class, Scope.PROTOTYPE)
                .factory(ConverterFactory.class, StrutsConverterFactory.class, Scope.PROTOTYPE)
                .factory(UnknownHandlerFactory.class, DefaultUnknownHandlerFactory.class, Scope.PROTOTYPE)

                .factory(FileManager.class, "system", DefaultFileManager.class, Scope.SINGLETON)
                .factory(ReflectionProvider.class, OgnlReflectionProvider.class, Scope.SINGLETON)
                .factory(ValueStackFactory.class, OgnlValueStackFactory.class, Scope.SINGLETON)

                .factory(XWorkConverter.class, Scope.SINGLETON)
                .factory(XWorkBasicConverter.class, Scope.SINGLETON)
                .factory(ConversionPropertiesProcessor.class, StrutsConversionPropertiesProcessor.class, Scope.SINGLETON)
                .factory(ConversionFileProcessor.class, DefaultConversionFileProcessor.class, Scope.SINGLETON)
                .factory(ConversionAnnotationProcessor.class, DefaultConversionAnnotationProcessor.class, Scope.SINGLETON)
                .factory(TypeConverterCreator.class, StrutsTypeConverterCreator.class, Scope.SINGLETON)
                .factory(TypeConverterHolder.class, StrutsTypeConverterHolder.class, Scope.SINGLETON)

                .factory(TextProvider.class, "system", DefaultTextProvider.class, Scope.SINGLETON)
                .factory(LocalizedTextProvider.class, StrutsLocalizedTextProvider.class, Scope.SINGLETON)
                .factory(TextProviderFactory.class, StrutsTextProviderFactory.class, Scope.SINGLETON)
                .factory(LocaleProviderFactory.class, DefaultLocaleProviderFactory.class, Scope.SINGLETON)
                .factory(TextParser.class, OgnlTextParser.class, Scope.SINGLETON)

                .factory(ObjectTypeDeterminer.class, DefaultObjectTypeDeterminer.class, Scope.SINGLETON)
                .factory(RootAccessor.class, CompoundRootAccessor.class, Scope.SINGLETON)
                .factory(MethodAccessor.class, XWorkMethodAccessor.class, Scope.SINGLETON)

                .factory(ExpressionCacheFactory.class, DefaultOgnlExpressionCacheFactory.class, Scope.SINGLETON)
                .factory(BeanInfoCacheFactory.class, DefaultOgnlBeanInfoCacheFactory.class, Scope.SINGLETON)
                .factory(OgnlUtil.class, Scope.SINGLETON)
                .factory(SecurityMemberAccess.class, Scope.PROTOTYPE)
                .factory(OgnlGuard.class, StrutsOgnlGuard.class, Scope.SINGLETON)
                .factory(ProviderAllowlist.class, Scope.SINGLETON)
                .factory(ThreadAllowlist.class, Scope.SINGLETON)

                .factory(ValueSubstitutor.class, EnvsValueSubstitutor.class, Scope.SINGLETON);
    }

    public static ContainerBuilder bootstrapTypeConverters(ContainerBuilder builder) {
        return builder
                .factory(TypeConverter.class, StrutsConstants.STRUTS_CONVERTER_COLLECTION, CollectionConverter.class, Scope.SINGLETON)
                .factory(TypeConverter.class, StrutsConstants.STRUTS_CONVERTER_ARRAY, ArrayConverter.class, Scope.SINGLETON)
                .factory(TypeConverter.class, StrutsConstants.STRUTS_CONVERTER_DATE, DateConverter.class, Scope.SINGLETON)
                .factory(TypeConverter.class, StrutsConstants.STRUTS_CONVERTER_NUMBER, NumberConverter.class, Scope.SINGLETON)
                .factory(TypeConverter.class, StrutsConstants.STRUTS_CONVERTER_STRING, StringConverter.class, Scope.SINGLETON);
    }

    /**
     * <p>
     * This builds the internal runtime configuration used by Xwork for finding and configuring Actions from the
     * programmatic configuration data structures. All of the old runtime configuration will be discarded and rebuilt.
     * </p>
     *
     * <p>
     * It basically flattens the data structures to make the information easier to access.  It will take
     * an {@link ActionConfig} and combine its data with all inherited dast.  For example, if the {@link ActionConfig}
     * is in a package that contains a global result and it also contains a result, the resulting {@link ActionConfig}
     * will have two results.
     * </p>
     *
     * @return runtime configuration
     * @throws ConfigurationException in case of any configuration errors
     */
    protected synchronized RuntimeConfiguration buildRuntimeConfiguration() throws ConfigurationException {
        Map<String, Map<String, ActionConfig>> namespaceActionConfigs = new LinkedHashMap<>();
        Map<String, String> namespaceConfigs = new LinkedHashMap<>();

        for (PackageConfig packageConfig : packageContexts.values()) {

            if (!packageConfig.isAbstract()) {
                String namespace = packageConfig.getNamespace();
                Map<String, ActionConfig> configs = namespaceActionConfigs.get(namespace);

                if (configs == null) {
                    configs = new LinkedHashMap<>();
                }

                Map<String, ActionConfig> actionConfigs = packageConfig.getAllActionConfigs();

                for (Object o : actionConfigs.keySet()) {
                    String actionName = (String) o;
                    ActionConfig baseConfig = actionConfigs.get(actionName);
                    configs.put(actionName, buildFullActionConfig(packageConfig, baseConfig));
                }

                namespaceActionConfigs.put(namespace, configs);
                if (packageConfig.getFullDefaultActionRef() != null) {
                    namespaceConfigs.put(namespace, packageConfig.getFullDefaultActionRef());
                }
            }
        }

        PatternMatcher<int[]> matcher = container.getInstance(PatternMatcher.class);
        boolean appendNamedParameters = Boolean.parseBoolean(
                container.getInstance(String.class, StrutsConstants.STRUTS_MATCHER_APPEND_NAMED_PARAMETERS)
        );
        boolean fallbackToEmptyNamespace = Boolean.parseBoolean(
                Optional.ofNullable(container.getInstance(String.class, StrutsConstants.STRUTS_ACTION_CONFIG_FALLBACK_TO_EMPTY_NAMESPACE)).orElse("true")
        );

        return new RuntimeConfigurationImpl(Collections.unmodifiableMap(namespaceActionConfigs),
                Collections.unmodifiableMap(namespaceConfigs), matcher, appendNamedParameters, fallbackToEmptyNamespace);
    }

    private void setDefaultResults(Map<String, ResultConfig> results, PackageConfig packageContext) {
        String defaultResult = packageContext.getFullDefaultResultType();

        for (Map.Entry<String, ResultConfig> entry : results.entrySet()) {

            if (entry.getValue() == null) {
                ResultTypeConfig resultTypeConfig = packageContext.getAllResultTypeConfigs().get(defaultResult);
                entry.setValue(new ResultConfig.Builder(null, resultTypeConfig.getClassName()).build());
            }
        }
    }

    /**
     * Builds the full runtime actionconfig with all of the defaults and inheritance
     *
     * @param packageContext the PackageConfig which holds the base config we're building from
     * @param baseConfig     the ActionConfig which holds only the configuration specific to itself, without the defaults
     *                       and inheritance
     * @return a full ActionConfig for runtime configuration with all of the inherited and default params
     * @throws org.apache.struts2.config.ConfigurationException
     *
     */
    private ActionConfig buildFullActionConfig(PackageConfig packageContext, ActionConfig baseConfig) throws ConfigurationException {
        Map<String, String> params = new TreeMap<>(baseConfig.getParams());
        Map<String, ResultConfig> results = new TreeMap<>();

        if (!baseConfig.getPackageName().equals(packageContext.getName()) && packageContexts.containsKey(baseConfig.getPackageName())) {
            results.putAll(packageContexts.get(baseConfig.getPackageName()).getAllGlobalResults());
        } else {
            results.putAll(packageContext.getAllGlobalResults());
        }

       	results.putAll(baseConfig.getResults());

        setDefaultResults(results, packageContext);

        List<InterceptorMapping> interceptors = new ArrayList<>(baseConfig.getInterceptors());

        if (interceptors.size() <= 0) {
            String defaultInterceptorRefName = packageContext.getFullDefaultInterceptorRef();

            if (defaultInterceptorRefName != null) {
                interceptors.addAll(InterceptorBuilder.constructInterceptorReference(new PackageConfig.Builder(packageContext), defaultInterceptorRefName,
                        new LinkedHashMap<String, String>(), packageContext.getLocation(), objectFactory));
            }
        }

        String methodRegex = container.getInstance(String.class, StrutsConstants.STRUTS_SMI_METHOD_REGEX);
        if (methodRegex == null) {
            methodRegex = ActionConfig.DEFAULT_METHOD_REGEX;
        }

        LOG.debug("Using pattern [{}] to match allowed methods when SMI is disabled!", methodRegex);

        return new ActionConfig.Builder(baseConfig)
            .addParams(params)
            .addResultConfigs(results)
            .defaultClassName(packageContext.getDefaultClassRef())  // fill in default if non class has been provided
            .interceptors(interceptors)
            .setStrictMethodInvocation(packageContext.isStrictMethodInvocation())
            .setDefaultMethodRegex(methodRegex)
            .addExceptionMappings(packageContext.getAllExceptionMappingConfigs())
            .build();
    }


    private static class RuntimeConfigurationImpl implements RuntimeConfiguration {

        private final Map<String, Map<String, ActionConfig>> namespaceActionConfigs;
        private final Map<String, ActionConfigMatcher> namespaceActionConfigMatchers;
        private final NamespaceMatcher namespaceMatcher;
        private final Map<String, String> namespaceConfigs;
        private final boolean fallbackToEmptyNamespace;

        public RuntimeConfigurationImpl(Map<String, Map<String, ActionConfig>> namespaceActionConfigs,
                                        Map<String, String> namespaceConfigs,
                                        PatternMatcher<int[]> matcher,
                                        boolean appendNamedParameters,
                                        boolean fallbackToEmptyNamespace)
        {
            this.namespaceActionConfigs = namespaceActionConfigs;
            this.namespaceConfigs = namespaceConfigs;
            this.fallbackToEmptyNamespace = fallbackToEmptyNamespace;

            this.namespaceActionConfigMatchers = new LinkedHashMap<>();
            this.namespaceMatcher = new NamespaceMatcher(matcher, namespaceActionConfigs.keySet(), appendNamedParameters);

            for (Map.Entry<String, Map<String, ActionConfig>> entry : namespaceActionConfigs.entrySet()) {
                ActionConfigMatcher configMatcher = new ActionConfigMatcher(matcher, entry.getValue(), true, appendNamedParameters);
                namespaceActionConfigMatchers.put(entry.getKey(), configMatcher);
            }
        }


        /**
         * Gets the configuration information for an action name, or returns null if the
         * name is not recognized.
         *
         * @param name      the name of the action
         * @param namespace the namespace for the action or null for the empty namespace, ""
         * @return the configuration information for action requested
         */
        @Override
        public ActionConfig getActionConfig(String namespace, String name) {
            ActionConfig config = findActionConfigInNamespace(namespace, name);

            // try wildcarded namespaces
            if (config == null) {
                NamespaceMatch match = namespaceMatcher.match(namespace);
                if (match != null) {
                    config = findActionConfigInNamespace(match.getPattern(), name);

                    // If config found, place all the matches found in the namespace processing in the action's parameters
                    if (config != null) {
                        config = new ActionConfig.Builder(config)
                                .addParams(match.getVariables())
                                .build();
                    }
                }
            }

            // fail over to empty namespace
            if (config == null && shouldFallbackToEmptyNamespace(namespace)) {
                config = findActionConfigInNamespace("", name);
            }

            return config;
        }

        private boolean shouldFallbackToEmptyNamespace(String namespace) {
            return StringUtils.isNotBlank(namespace) && ("/".equals(namespace) || fallbackToEmptyNamespace);
        }

        private ActionConfig findActionConfigInNamespace(String namespace, String name) {
            ActionConfig config = null;
            if (namespace == null) {
                namespace = "";
            }
            Map<String, ActionConfig> actions = namespaceActionConfigs.get(namespace);
            if (actions != null) {
                config = actions.get(name);
                // Check wildcards
                if (config == null) {
                    config = namespaceActionConfigMatchers.get(namespace).match(name);
                    // fail over to default action
                    if (config == null) {
                        String defaultActionRef = namespaceConfigs.get(namespace);
                        if (defaultActionRef != null) {
                            config = actions.get(defaultActionRef);
                        }
                    }
                }
            }
            return config;
        }

        /**
         * Gets the configuration settings for every action.
         *
         * @return a Map of namespace - > Map of ActionConfig objects, with the key being the action name
         */
        @Override
        public Map<String, Map<String, ActionConfig>>  getActionConfigs() {
            return namespaceActionConfigs;
        }

        @Override
        public String toString() {
            StringBuilder buff = new StringBuilder("RuntimeConfiguration - actions are\n");

            for (Map.Entry<String, Map<String, ActionConfig>> entry : namespaceActionConfigs.entrySet()) {
                Map<String, ActionConfig> actionConfigs = entry.getValue();

                for (String s : actionConfigs.keySet()) {
                    buff.append(entry.getKey()).append("/").append(s).append("\n");
                }
            }

            return buff.toString();
        }
    }

    class ContainerProperties extends LocatableProperties {
        @Serial
        private static final long serialVersionUID = -7320625750836896089L;

        @Override
        public Object setProperty(String key, String value) {
            String oldValue = getProperty(key);
            if (LOG.isInfoEnabled() && oldValue != null && !oldValue.equals(value) && !defaultFrameworkBeanName.equals(oldValue)) {
                LOG.info("Overriding property {} - old value: {} new value: {}", key, oldValue, value);
            }
            return super.setProperty(key, value);
        }

        public void setConstants(ContainerBuilder builder) {
            for (Object keyobj : keySet()) {
                String key = (String)keyobj;
                builder.factory(String.class, key, new LocatableConstantFactory<>(getProperty(key), getPropertyLocation(key)));
            }
        }
    }
}
