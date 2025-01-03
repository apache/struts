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
package org.apache.struts2.dispatcher;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.ActionProxyFactory;
import org.apache.struts2.FileManager;
import org.apache.struts2.FileManagerFactory;
import org.apache.struts2.locale.LocaleProviderFactory;
import org.apache.struts2.ObjectFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.config.Configuration;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.ConfigurationManager;
import org.apache.struts2.config.ConfigurationProvider;
import org.apache.struts2.config.DefaultPropertiesProvider;
import org.apache.struts2.config.FileManagerFactoryProvider;
import org.apache.struts2.config.FileManagerProvider;
import org.apache.struts2.config.PropertiesConfigurationProvider;
import org.apache.struts2.config.ServletContextAwareConfigurationProvider;
import org.apache.struts2.config.StrutsBeanSelectionProvider;
import org.apache.struts2.config.StrutsJavaConfiguration;
import org.apache.struts2.config.StrutsJavaConfigurationProvider;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.config.entities.InterceptorMapping;
import org.apache.struts2.config.entities.InterceptorStackConfig;
import org.apache.struts2.config.entities.PackageConfig;
import org.apache.struts2.config.providers.XmlConfigurationProvider;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.inject.Container;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.interceptor.Interceptor;
import org.apache.struts2.ognl.ThreadAllowlist;
import org.apache.struts2.result.Result;
import org.apache.struts2.util.ClassLoaderUtil;
import org.apache.struts2.util.ObjectFactoryDestroyable;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;
import org.apache.struts2.util.fs.JBossFileManager;
import org.apache.struts2.util.location.LocatableProperties;
import org.apache.struts2.util.location.Location;
import org.apache.struts2.util.location.LocationUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;

/**
 * A utility class the actual dispatcher delegates most of its tasks to. Each instance
 * of the primary dispatcher holds an instance of this dispatcher to be shared for
 * all requests.
 *
 * @see InitOperations
 */
public class Dispatcher {

    /**
     * Provide a logging instance.
     */
    private static final Logger LOG = LogManager.getLogger(Dispatcher.class);

    /**
     * {@link HttpServletRequest#getMethod()}
     */
    public static final String REQUEST_POST_METHOD = "POST";

    public static final String MULTIPART_FORM_DATA_REGEX = "^multipart/form-data(?:\\s*;\\s*boundary=[0-9a-zA-Z'\"()+_,\\-./:=?]{1,70})?(?:\\s*;\\s*charset=[a-zA-Z\\-0-9]{3,14})?";

    private static final String CONFIG_SPLIT_REGEX = "\\s*,\\s*";

    /**
     * Provide a thread local instance.
     */
    private static final ThreadLocal<Dispatcher> instance = new ThreadLocal<>();

    /**
     * Store list of DispatcherListeners.
     */
    private static final List<DispatcherListener> dispatcherListeners = new CopyOnWriteArrayList<>();

    /**
     * This field exists so {@link #getContainer()} can determine whether to (re-)inject this instance in the case of
     * a {@link ConfigurationManager} reload.
     */
    private Container injectedContainer;

    /**
     * Store state of StrutsConstants.STRUTS_DEVMODE setting.
     */
    private boolean devMode;

    /**
     * Store state of StrutsConstants.DISABLE_REQUEST_ATTRIBUTE_VALUE_STACK_LOOKUP setting.
     */
    private boolean disableRequestAttributeValueStackLookup;

    /**
     * Store state of StrutsConstants.STRUTS_I18N_ENCODING setting.
     */
    private String defaultEncoding;

    /**
     * Store state of StrutsConstants.STRUTS_LOCALE setting.
     */
    private String defaultLocale;

    /**
     * Store state of {@link StrutsConstants#STRUTS_MULTIPART_SAVE_DIR} setting.
     */
    private String multipartSaveDir;

    /**
     * Stores the value of {@link StrutsConstants#STRUTS_MULTIPART_ENABLED}
     */
    private boolean multipartSupportEnabled = true;

    /**
     * A regular expression used to validate if request is a multipart/form-data request
     */
    private Pattern multipartValidationPattern = Pattern.compile(MULTIPART_FORM_DATA_REGEX);

    private String actionExcludedPatternsStr;
    private String actionExcludedPatternsSeparator = ",";
    private List<Pattern> actionExcludedPatterns;

    /**
     * Provide list of default configuration files.
     */
    private static final String DEFAULT_CONFIGURATION_PATHS = "struts-default.xml,struts-plugin.xml,struts.xml";

    /**
     * <p>
     * Store state of STRUTS_DISPATCHER_PARAMETERSWORKAROUND.
     * </p>
     *
     * <p>
     * The workaround is for WebLogic.
     * We try to autodetect WebLogic on Dispatcher init.
     * The workaround can also be enabled manually.
     * </p>
     */
    private boolean paramsWorkaroundEnabled = false;

    /**
     * Indicates if Dispatcher should handle exception and call sendError()
     * Introduced to allow integration with other frameworks like Spring Security
     */
    private boolean handleException;

    /**
     * Interface used to handle internal errors or missing resources
     */
    private DispatcherErrorHandler errorHandler;

    /**
     * Store ConfigurationManager instance, set on init.
     */
    protected ConfigurationManager configurationManager;
    private ObjectFactory objectFactory;
    private ActionProxyFactory actionProxyFactory;
    private LocaleProviderFactory localeProviderFactory;
    private StaticContentLoader staticContentLoader;
    private ActionMapper actionMapper;
    private ThreadAllowlist threadAllowlist;

    /**
     * Provide the dispatcher instance for the current thread.
     *
     * @return The dispatcher instance
     */
    public static Dispatcher getInstance() {
        return instance.get();
    }

    /**
     * Store the dispatcher instance for this thread.
     *
     * @param instance The instance
     */
    public static void setInstance(Dispatcher instance) {
        Dispatcher.instance.set(instance);
    }

    /**
     * Removes the dispatcher instance for this thread.
     */
    public static void clearInstance() {
        Dispatcher.instance.remove();
    }

    /**
     * Add a dispatcher lifecycle listener.
     *
     * @param listener The listener to add
     */
    public static void addDispatcherListener(DispatcherListener listener) {
        dispatcherListeners.add(listener);
    }

    /**
     * Remove a specific dispatcher lifecycle listener.
     *
     * @param listener The listener
     */
    public static void removeDispatcherListener(DispatcherListener listener) {
        dispatcherListeners.remove(listener);
    }

    private ValueStackFactory valueStackFactory;

    /**
     * Keeps current reference to external world and must be protected to support class inheritance
     */
    protected ServletContext servletContext;
    protected Map<String, String> initParams;

    /**
     * Create the Dispatcher instance for a given ServletContext and set of initialization parameters.
     *
     * @param servletContext Our servlet context
     * @param initParams     The set of initialization parameters
     */
    public Dispatcher(ServletContext servletContext, Map<String, String> initParams) {
        this.servletContext = servletContext;
        this.initParams = initParams;
    }

    public static Dispatcher getInstance(ServletContext servletContext) {
        return (Dispatcher) servletContext.getAttribute(StrutsStatics.SERVLET_DISPATCHER);
    }

    /**
     * Modify state of StrutsConstants.STRUTS_DEVMODE setting.
     *
     * @param mode New setting
     */
    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String mode) {
        devMode = Boolean.parseBoolean(mode);
    }

    public boolean isDevMode() {
        return devMode;
    }

    /**
     * Modify state of StrutsConstants.DISABLE_REQUEST_ATTRIBUTE_VALUE_STACK_LOOKUP setting.
     *
     * @param disableRequestAttributeValueStackLookup New setting
     */
    @Inject(value = StrutsConstants.STRUTS_DISABLE_REQUEST_ATTRIBUTE_VALUE_STACK_LOOKUP, required = false)
    public void setDisableRequestAttributeValueStackLookup(String disableRequestAttributeValueStackLookup) {
        this.disableRequestAttributeValueStackLookup = BooleanUtils.toBoolean(disableRequestAttributeValueStackLookup);
    }

    /**
     * Modify state of StrutsConstants.STRUTS_LOCALE setting.
     *
     * @param val New setting
     */
    @Inject(value = StrutsConstants.STRUTS_LOCALE, required = false)
    public void setDefaultLocale(String val) {
        defaultLocale = val;
    }

    /**
     * Modify state of StrutsConstants.STRUTS_I18N_ENCODING setting.
     *
     * @param val New setting
     */
    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setDefaultEncoding(String val) {
        defaultEncoding = val;
    }

    /**
     * Modify state of StrutsConstants.STRUTS_MULTIPART_SAVEDIR setting.
     *
     * @param val New setting
     */
    @Inject(StrutsConstants.STRUTS_MULTIPART_SAVE_DIR)
    public void setMultipartSaveDir(String val) {
        multipartSaveDir = val;
    }

    @Inject(value = StrutsConstants.STRUTS_MULTIPART_ENABLED, required = false)
    public void setMultipartSupportEnabled(String multipartSupportEnabled) {
        this.multipartSupportEnabled = Boolean.parseBoolean(multipartSupportEnabled);
    }

    @Inject(value = StrutsConstants.STRUTS_MULTIPART_VALIDATION_REGEX, required = false)
    public void setMultipartValidationRegex(String multipartValidationRegex) {
        this.multipartValidationPattern = Pattern.compile(multipartValidationRegex);
    }

    @Inject(value = StrutsConstants.STRUTS_ACTION_EXCLUDE_PATTERN_SEPARATOR, required = false)
    public void setActionExcludedPatternsSeparator(String separator) {
        this.actionExcludedPatternsSeparator = separator;
    }

    @Inject(value = StrutsConstants.STRUTS_ACTION_EXCLUDE_PATTERN, required = false)
    public void setActionExcludedPatterns(String excludedPatterns) {
        this.actionExcludedPatternsStr = excludedPatterns;
    }

    public List<Pattern> getActionExcludedPatterns() {
        if (actionExcludedPatterns == null) {
            initActionExcludedPatterns();
        }
        return actionExcludedPatterns;
    }

    private void initActionExcludedPatterns() {
        if (actionExcludedPatternsStr == null || actionExcludedPatternsStr.trim().isEmpty()) {
            actionExcludedPatterns = emptyList();
            return;
        }
        actionExcludedPatterns = Arrays.stream(actionExcludedPatternsStr.split(actionExcludedPatternsSeparator))
                .map(String::trim).map(Pattern::compile).toList();
    }

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    public ValueStackFactory getValueStackFactory() {
        return valueStackFactory;
    }

    @Inject(StrutsConstants.STRUTS_HANDLE_EXCEPTION)
    public void setHandleException(String handleException) {
        this.handleException = Boolean.parseBoolean(handleException);
    }

    @Inject(StrutsConstants.STRUTS_DISPATCHER_PARAMETERSWORKAROUND)
    public void setDispatchersParametersWorkaround(String dispatchersParametersWorkaround) {
        this.paramsWorkaroundEnabled = Boolean.parseBoolean(dispatchersParametersWorkaround)
                || (servletContext != null && StringUtils.contains(servletContext.getServerInfo(), "WebLogic"));
    }

    public boolean isHandleException() {
        return handleException;
    }

    @Inject
    public void setDispatcherErrorHandler(DispatcherErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Inject
    public void setActionProxyFactory(ActionProxyFactory actionProxyFactory) {
        this.actionProxyFactory = actionProxyFactory;
    }

    public ActionProxyFactory getActionProxyFactory() {
        return actionProxyFactory;
    }

    @Inject
    public void setLocaleProviderFactory(LocaleProviderFactory localeProviderFactory) {
        this.localeProviderFactory = localeProviderFactory;
    }

    @Inject
    public void setStaticContentLoader(StaticContentLoader staticContentLoader) {
        this.staticContentLoader = staticContentLoader;
    }

    public StaticContentLoader getStaticContentLoader() {
        return staticContentLoader;
    }

    @Inject
    public void setActionMapper(ActionMapper actionMapper) {
        this.actionMapper = actionMapper;
    }

    public ActionMapper getActionMapper() {
        return actionMapper;
    }

    @Inject
    public void setThreadAllowlist(ThreadAllowlist threadAllowlist) {
        this.threadAllowlist = threadAllowlist;
    }

    /**
     * Releases all instances bound to this dispatcher instance.
     */
    public void cleanup() {
        // clean up ObjectFactory
        if (objectFactory == null) {
            LOG.warn("Object Factory is null, something is seriously wrong, no clean up will be performed");
        }
        if (objectFactory instanceof ObjectFactoryDestroyable) {
            try {
                ((ObjectFactoryDestroyable) objectFactory).destroy();
            } catch (Exception e) {
                // catch any exception that may occur during destroy() and log it
                LOG.error("Exception occurred while destroying ObjectFactory [{}]", objectFactory.toString(), e);
            }
        }

        // clean up Dispatcher itself for this thread
        instance.remove();
        servletContext.setAttribute(StrutsStatics.SERVLET_DISPATCHER, null);

        // clean up DispatcherListeners
        if (!dispatcherListeners.isEmpty()) {
            for (DispatcherListener l : dispatcherListeners) {
                l.dispatcherDestroyed(this);
            }
        }

        // clean up all interceptors by calling their destroy() method
        Set<Interceptor> interceptors = new HashSet<>();
        Collection<PackageConfig> packageConfigs = configurationManager.getConfiguration().getPackageConfigs().values();
        for (PackageConfig packageConfig : packageConfigs) {
            for (Object config : packageConfig.getAllInterceptorConfigs().values()) {
                if (config instanceof InterceptorStackConfig) {
                    for (InterceptorMapping interceptorMapping : ((InterceptorStackConfig) config).getInterceptors()) {
                        interceptors.add(interceptorMapping.getInterceptor());
                    }
                }
            }
        }
        for (Interceptor interceptor : interceptors) {
            interceptor.destroy();
        }

        // Clear container holder when application is unloaded / server shutdown
        ContainerHolder.clear();

        //cleanup action context
        ActionContext.clear();

        // clean up configuration
        configurationManager.destroyConfiguration();
        configurationManager = null;
    }

    private void init_FileManager() throws ClassNotFoundException {
        if (initParams.containsKey(StrutsConstants.STRUTS_FILE_MANAGER)) {
            final String fileManagerClassName = initParams.get(StrutsConstants.STRUTS_FILE_MANAGER);
            final Class<FileManager> fileManagerClass = (Class<FileManager>) Class.forName(fileManagerClassName);
            LOG.info("Custom FileManager specified: {}", fileManagerClassName);
            configurationManager.addContainerProvider(new FileManagerProvider(fileManagerClass, fileManagerClass.getSimpleName()));
        } else {
            // add any other Struts 2 provided implementations of FileManager
            configurationManager.addContainerProvider(new FileManagerProvider(JBossFileManager.class, "jboss"));
        }
        if (initParams.containsKey(StrutsConstants.STRUTS_FILE_MANAGER_FACTORY)) {
            final String fileManagerFactoryClassName = initParams.get(StrutsConstants.STRUTS_FILE_MANAGER_FACTORY);
            final Class<FileManagerFactory> fileManagerFactoryClass = (Class<FileManagerFactory>) Class.forName(fileManagerFactoryClassName);
            LOG.info("Custom FileManagerFactory specified: {}", fileManagerFactoryClassName);
            configurationManager.addContainerProvider(new FileManagerFactoryProvider(fileManagerFactoryClass));
        }
    }

    private void init_DefaultProperties() {
        configurationManager.addContainerProvider(new DefaultPropertiesProvider());
    }

    private void init_LegacyStrutsProperties() {
        configurationManager.addContainerProvider(new PropertiesConfigurationProvider());
    }

    private void init_TraditionalXmlConfigurations() {
        String configPaths = initParams.get("config");
        if (configPaths == null) {
            configPaths = DEFAULT_CONFIGURATION_PATHS;
        }
        loadConfigPaths(configPaths);
    }

    private void loadConfigPaths(String configPaths) {
        String[] files = configPaths.split(CONFIG_SPLIT_REGEX);
        for (String file : files) {
            if (file.endsWith(".xml")) {
                configurationManager.addContainerProvider(createStrutsXmlConfigurationProvider(file, servletContext));
            } else {
                throw new IllegalArgumentException("Invalid configuration file name");
            }
        }
    }

    protected XmlConfigurationProvider createStrutsXmlConfigurationProvider(String filename, ServletContext ctx) {
        return new StrutsXmlConfigurationProvider(filename, ctx);
    }

    private void init_JavaConfigurations() {
        String configClasses = initParams.get("javaConfigClasses");
        if (configClasses != null) {
            String[] classes = configClasses.split(CONFIG_SPLIT_REGEX);
            for (String cname : classes) {
                try {
                    Class<?> cls = ClassLoaderUtil.loadClass(cname, this.getClass());
                    StrutsJavaConfiguration config = (StrutsJavaConfiguration) cls.getDeclaredConstructor().newInstance();
                    configurationManager.addContainerProvider(createJavaConfigurationProvider(config));
                } catch (InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                    throw new ConfigurationException("Unable to instantiate java configuration: " + cname, e);
                } catch (IllegalAccessException e) {
                    throw new ConfigurationException("Unable to access java configuration: " + cname, e);
                } catch (ClassNotFoundException e) {
                    throw new ConfigurationException("Unable to locate java configuration class: " + cname, e);
                }
            }
        }
    }

    protected StrutsJavaConfigurationProvider createJavaConfigurationProvider(StrutsJavaConfiguration config) {
        return new StrutsJavaConfigurationProvider(config);
    }

    private void init_CustomConfigurationProviders() {
        String configProvs = initParams.get("configProviders");
        if (configProvs != null) {
            String[] classes = configProvs.split(CONFIG_SPLIT_REGEX);
            for (String cname : classes) {
                try {
                    Class cls = ClassLoaderUtil.loadClass(cname, this.getClass());
                    ConfigurationProvider prov = (ConfigurationProvider) cls.getDeclaredConstructor().newInstance();
                    if (prov instanceof ServletContextAwareConfigurationProvider) {
                        ((ServletContextAwareConfigurationProvider) prov).initWithContext(servletContext);
                    }
                    configurationManager.addContainerProvider(prov);
                } catch (InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                    throw new ConfigurationException("Unable to instantiate provider: " + cname, e);
                } catch (IllegalAccessException e) {
                    throw new ConfigurationException("Unable to access provider: " + cname, e);
                } catch (ClassNotFoundException e) {
                    throw new ConfigurationException("Unable to locate provider class: " + cname, e);
                }
            }
        }
    }

    private void init_FilterInitParameters() {
        configurationManager.addContainerProvider(new ConfigurationProvider() {
            public void destroy() {
            }

            public void init(Configuration configuration) throws ConfigurationException {
            }

            public void loadPackages() throws ConfigurationException {
            }

            public boolean needsReload() {
                return false;
            }

            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                props.putAll(initParams);
            }
        });
    }

    private void init_AliasStandardObjects() {
        configurationManager.addContainerProvider(new StrutsBeanSelectionProvider());
    }

    /**
     * `struts-deferred.xml` can be used to load configuration which is sensitive to loading order such as 'bean-selection' elements
     */
    private void init_DeferredXmlConfigurations() {
        loadConfigPaths("struts-deferred.xml");
    }

    /**
     * Load configurations, including both XML and zero-configuration strategies,
     * and update optional settings, including whether to reload configurations and resource files.
     */
    public void init() {
        if (configurationManager == null) {
            configurationManager = createConfigurationManager(Container.DEFAULT_NAME);
        }

        try {
            init_FileManager();
            init_DefaultProperties(); // [1]
            init_TraditionalXmlConfigurations(); // [2]
            init_JavaConfigurations();
            init_LegacyStrutsProperties(); // [3]
            init_CustomConfigurationProviders(); // [5]
            init_FilterInitParameters(); // [6]
            init_AliasStandardObjects(); // [7]
            init_DeferredXmlConfigurations();

            getContainer(); // Inject this instance

            if (!dispatcherListeners.isEmpty()) {
                for (DispatcherListener l : dispatcherListeners) {
                    l.dispatcherInitialized(this);
                }
            }
            errorHandler.init(servletContext);

            if (servletContext.getAttribute(StrutsStatics.SERVLET_DISPATCHER) == null) {
                servletContext.setAttribute(StrutsStatics.SERVLET_DISPATCHER, this);
            }
        } catch (Exception ex) {
            LOG.error("Dispatcher initialization failed", ex);
            throw new StrutsException(ex);
        }
    }

    protected ConfigurationManager createConfigurationManager(String name) {
        return new ConfigurationManager(name);
    }

    /**
     * <p>
     * Load Action class for mapping and invoke the appropriate Action method, or go directly to the Result.
     * </p>
     *
     * <p>
     * This method first creates the action context from the given parameters,
     * and then loads an <tt>ActionProxy</tt> from the given action name and namespace.
     * After that, the Action method is executed and output channels through the response object.
     * Actions not found are sent back to the user via the {@link Dispatcher#sendError} method,
     * using the 404 return code.
     * All other errors are reported by throwing a ServletException.
     * </p>
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @param mapping  the action mapping object
     * @throws ServletException when an unknown error occurs (not a 404, but typically something that
     *                          would end up as a 5xx by the servlet container)
     * @since 2.3.17
     */
    public void serviceAction(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping)
        throws ServletException {

        Map<String, Object> extraContext = createContextMap(request, response, mapping);

        // If there was a previous value stack, then create a new copy and pass it in to be used by the new Action
        ValueStack stack = (ValueStack) request.getAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY);
        boolean nullStack = stack == null;
        if (nullStack) {
            ActionContext ctx = ActionContext.getContext();
            if (ctx != null) {
                stack = ctx.getValueStack();
            }
        }
        if (stack != null) {
            extraContext = ActionContext.of(extraContext)
                .withValueStack(valueStackFactory.createValueStack(stack))
                .getContextMap();
        }

        try {
            String actionNamespace = mapping.getNamespace();
            String actionName = mapping.getName();
            String actionMethod = mapping.getMethod();

            LOG.trace("Processing action, namespace: {}, name: {}, method: {}", actionNamespace, actionName, actionMethod);
            ActionProxy proxy = prepareActionProxy(extraContext, actionNamespace, actionName, actionMethod);

            request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, proxy.getInvocation().getStack());

            // if the ActionMapping says to go straight to a result, do it!
            if (mapping.getResult() != null) {
                Result result = mapping.getResult();
                result.execute(proxy.getInvocation());
            } else {
                proxy.execute();
            }

            // If there was a previous value stack then set it back onto the request
            if (!nullStack) {
                request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);
            }
        } catch (ConfigurationException e) {
            logConfigurationException(request, e);
            sendError(request, response, HttpServletResponse.SC_NOT_FOUND, e);
        } catch (Exception e) {
            if (handleException || devMode) {
                if (devMode) {
                    LOG.debug("Dispatcher serviceAction failed", e);
                }
                sendError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            } else {
                throw new ServletException(e);
            }
        }
    }

    protected ActionProxy prepareActionProxy(Map<String, Object> extraContext, String actionNamespace, String actionName, String actionMethod) {
        ActionProxy proxy;
        //check if we are probably in an async resuming
        ActionInvocation invocation = ActionContext.getContext().getActionInvocation();
        if (invocation == null || invocation.isExecuted()) {
            LOG.trace("Creating a new action, namespace: {}, name: {}, method: {}", actionNamespace, actionName, actionMethod);
            proxy = createActionProxy(actionNamespace, actionName, actionMethod, extraContext);
        } else {
            proxy = invocation.getProxy();
            if (isSameAction(proxy, actionNamespace, actionName, actionMethod)) {
                LOG.trace("Proxy: {} matches requested action, namespace: {}, name: {}, method: {} - reusing proxy", proxy, actionNamespace, actionName, actionMethod);
            } else {
                LOG.trace("Proxy: {} doesn't match action namespace: {}, name: {}, method: {} - creating new proxy", proxy, actionNamespace, actionName, actionMethod);
                proxy = createActionProxy(actionNamespace, actionName, actionMethod, extraContext);
            }
        }
        return proxy;
    }

    protected ActionProxy createActionProxy(String namespace, String name, String method, Map<String, Object> extraContext) {
        return actionProxyFactory.createActionProxy(namespace, name, method, extraContext, true, false);
    }

    protected boolean isSameAction(ActionProxy actionProxy, String namespace, String actionName, String method) {
        return Objects.equals(namespace, actionProxy.getNamespace())
            && Objects.equals(actionName, actionProxy.getActionName())
            && Objects.equals(method, actionProxy.getMethod());
    }

    /**
     * Performs logging of missing action/result configuration exception
     *
     * @param request current {@link HttpServletRequest}
     * @param e       {@link ConfigurationException} that occurred
     */
    protected void logConfigurationException(HttpServletRequest request, ConfigurationException e) {
        // WW-2874 Only log error if in devMode
        String uri = request.getRequestURI();
        if (request.getQueryString() != null) {
            uri = uri + "?" + request.getQueryString();
        }
        if (devMode) {
            LOG.error("Could not find action or result: {}", uri, e);
        } else if (LOG.isWarnEnabled()) {
            LOG.warn("Could not find action or result: {}", uri, e);
        }
    }

    /**
     * Create a context map containing all the wrapped request objects
     *
     * @param request  The servlet request
     * @param response The servlet response
     * @param mapping  The action mapping
     * @return A map of context objects
     * @since 2.3.17
     */
    public Map<String, Object> createContextMap(HttpServletRequest request, HttpServletResponse response,
                                                ActionMapping mapping) {

        // request map wrapping the http request objects
        Map requestMap = new RequestMap(request);

        // parameters map wrapping the http parameters.  ActionMapping parameters are now handled and applied separately
        HttpParameters params = HttpParameters.create(request.getParameterMap()).build();

        // session map wrapping the http session
        Map session = new SessionMap(request);

        // application map wrapping the ServletContext
        Map application = new ApplicationMap(servletContext);

        Map<String, Object> extraContext = createContextMap(requestMap, params, session, application, request, response);

        if (mapping != null) {
            extraContext.put(ServletActionContext.ACTION_MAPPING, mapping);
        }
        return extraContext;
    }

    /**
     * Merge all application and servlet attributes into a single <tt>HashMap</tt> to represent the entire
     * <tt>Action</tt> context.
     *
     * @param requestMap     a Map of all request attributes.
     * @param parameters     an Object of all request parameters.
     * @param sessionMap     a Map of all session attributes.
     * @param applicationMap a Map of all servlet context attributes.
     * @param request        the HttpServletRequest object.
     * @param response       the HttpServletResponse object.
     * @return a HashMap representing the <tt>Action</tt> context.
     * @since 2.3.17
     */
    public Map<String, Object> createContextMap(Map<String, Object> requestMap,
                                                HttpParameters parameters,
                                                Map<String, Object> sessionMap,
                                                Map<String, Object> applicationMap,
                                                HttpServletRequest request,
                                                HttpServletResponse response) {
        Map<String, Object> extraContext = ActionContext.of()
            .withParameters(parameters)
            .withSession(sessionMap)
            .withApplication(applicationMap)
            .withLocale(getLocale(request))
            .withServletRequest(request)
            .withServletResponse(response)
            .withServletContext(servletContext)
            // helpers to get access to request/session/application scope
            .with(DispatcherConstants.REQUEST, requestMap)
            .with(DispatcherConstants.SESSION, sessionMap)
            .with(DispatcherConstants.APPLICATION, applicationMap)
            .getContextMap();

        AttributeMap attrMap = new AttributeMap(extraContext);
        extraContext.put(DispatcherConstants.ATTRIBUTES, attrMap);

        return extraContext;
    }

    protected Locale getLocale(HttpServletRequest request) {
        Locale locale;
        if (defaultLocale != null) {
            try {
                locale = LocaleUtils.toLocale(defaultLocale);
            } catch (IllegalArgumentException e) {
                try {
                    locale = request.getLocale();
                    LOG.warn(new ParameterizedMessage("Cannot convert 'struts.locale' = [{}] to proper locale, defaulting to request locale [{}]",
                                    defaultLocale, locale), e);
                } catch (RuntimeException rex) {
                    LOG.warn(new ParameterizedMessage("Cannot convert 'struts.locale' = [{}] to proper locale, and cannot get locale from HTTP Request, falling back to system default locale",
                                    defaultLocale), rex);
                    locale = Locale.getDefault();
                }
            }
        } else {
            try {
                locale = request.getLocale();
            } catch (RuntimeException rex) {
                LOG.warn("Cannot get locale from HTTP Request, falling back to system default locale", rex);
                locale = Locale.getDefault();
            }
        }
        return locale;
    }

    /**
     * Return the path to save uploaded files to (this is configurable).
     *
     * @return the path to save uploaded files to
     */
    protected String getSaveDir() {
        String saveDir = Objects.toString(multipartSaveDir, "").trim();

        if (saveDir.isEmpty()) {
            File tempdir = (File) servletContext.getAttribute(ServletContext.TEMPDIR);
            LOG.info("Unable to find: {} property setting. Defaulting to: {}",
                    StrutsConstants.STRUTS_MULTIPART_SAVE_DIR, ServletContext.TEMPDIR);

            if (tempdir != null) {
                saveDir = tempdir.toString();
                setMultipartSaveDir(saveDir);
            }
        } else {
            File multipartSaveDir = new File(saveDir);

            if (!multipartSaveDir.exists()) {
                if (!multipartSaveDir.mkdirs()) {
                    String logMessage;
                    try {
                        logMessage = "Could not create multipart save directory '" + multipartSaveDir.getCanonicalPath() + "'.";
                    } catch (IOException e) {
                        logMessage = "Could not create multipart save directory '" + multipartSaveDir + "'.";
                    }
                    if (devMode) {
                        LOG.error(logMessage);
                    } else {
                        LOG.warn(logMessage);
                    }
                }
            }
        }

        LOG.debug("saveDir={}", saveDir);

        return saveDir;
    }

    /**
     * Prepare a request, including setting the encoding and locale.
     *
     * @param request  The request
     * @param response The response
     */
    public void prepare(HttpServletRequest request, HttpServletResponse response) {
        getContainer(); // Init ContainerHolder and reinject this instance IF ConfigurationManager was reloaded
        String encoding = null;
        if (defaultEncoding != null) {
            encoding = defaultEncoding;
        }
        // check for Ajax request to use UTF-8 encoding strictly http://www.w3.org/TR/XMLHttpRequest/#the-send-method
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            encoding = "UTF-8";
        }

        Locale locale = getLocale(request);

        if (encoding != null) {
            applyEncoding(request, encoding);
            applyEncoding(response, encoding);
        }

        if (locale != null) {
            response.setLocale(locale);
        }

        if (paramsWorkaroundEnabled) {
            request.getParameter("foo"); // simply read any parameter (existing or not) to "prime" the request
        }
    }

    private void applyEncoding(HttpServletRequest request, String encoding) {
        try {
            if (!encoding.equals(request.getCharacterEncoding())) {
                // if the encoding is already correctly set and the parameters have been already read
                // do not try to set encoding because it is useless and will cause an error
                request.setCharacterEncoding(encoding);
            }
        } catch (Exception e) {
            LOG.error(new ParameterizedMessage("Error setting character encoding to '{}' on request - ignoring.", encoding), e);
        }
    }

    private void applyEncoding(HttpServletResponse response, String encoding) {
        try {
            if (!encoding.equals(response.getCharacterEncoding())) {
                response.setCharacterEncoding(encoding);
            }
        } catch (Exception e) {
            LOG.error(new ParameterizedMessage("Error setting character encoding to '{}' on response - ignoring.", encoding), e);
        }
    }

    /**
     * <p>
     * Wrap and return the given request or return the original request object.
     * </p>
     *
     * <p>
     * This method transparently handles multipart data as a wrapped class around the given request.
     * Override this method to handle multipart requests in a special way or to handle other types of requests.
     * Note, {@link org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper} is
     * flexible - look first to that object before overriding this method to handle multipart data.
     * </p>
     *
     * @param request the HttpServletRequest object.
     * @return a wrapped request or original request.
     * @throws java.io.IOException on any error.
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper
     * @since 2.3.17
     */
    public HttpServletRequest wrapRequest(HttpServletRequest request) throws IOException {
        // don't wrap more than once
        if (request instanceof StrutsRequestWrapper) {
            LOG.debug("Request already wrapped with: {}", StrutsRequestWrapper.class.getSimpleName());
            return request;
        }

        if (isMultipartSupportEnabled(request) && isMultipartRequest(request)) {
            LOG.debug("Wrapping multipart request with: {}", MultiPartRequestWrapper.class.getSimpleName());
            request = new MultiPartRequestWrapper(
                    getMultiPartRequest(),
                    request,
                    getSaveDir(),
                    localeProviderFactory.createLocaleProvider(),
                    disableRequestAttributeValueStackLookup
            );
        } else {
            LOG.debug("Wrapping request using: {}", StrutsRequestWrapper.class.getSimpleName());
            request = new StrutsRequestWrapper(request, disableRequestAttributeValueStackLookup);
        }

        return request;
    }

    /**
     * Checks if support to parse multipart requests is enabled
     *
     * @param request current servlet request
     * @return false if disabled
     * @since 2.5.11
     */
    protected boolean isMultipartSupportEnabled(HttpServletRequest request) {
        LOG.debug("Support for multipart request is enabled: {}", multipartSupportEnabled);
        return multipartSupportEnabled;
    }

    /**
     * Checks if request is a multipart request (a file upload request)
     *
     * @param request current servlet request
     * @return true if it is a multipart request
     * @since 2.5.11
     */
    protected boolean isMultipartRequest(HttpServletRequest request) {
        String httpMethod = request.getMethod();
        String contentType = request.getContentType();

        boolean isPostRequest = REQUEST_POST_METHOD.equalsIgnoreCase(httpMethod);
        boolean isProperContentType = contentType != null && multipartValidationPattern.matcher(contentType.toLowerCase(Locale.ENGLISH)).matches();

        LOG.debug("Validating if this is a proper Multipart request. Request is POST: {} and ContentType matches pattern ({}): {}",
                isPostRequest, multipartValidationPattern, isProperContentType);
        return isPostRequest && isProperContentType;
    }

    /**
     * On each request it must return a new instance as implementation could be not thread safe
     * and thus ensure of resource clean up
     *
     * @return a multi part request object
     */
    protected MultiPartRequest getMultiPartRequest() {
        return getContainer().getInstance(MultiPartRequest.class);
    }

    /**
     * Removes all the files created by MultiPartRequestWrapper.
     *
     * @param request the HttpServletRequest object.
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper
     */
    public void cleanUpRequest(HttpServletRequest request) {
        ContainerHolder.clear();
        threadAllowlist.clearAllowlist();
        if (!(request instanceof MultiPartRequestWrapper multiWrapper)) {
            return;
        }
        multiWrapper.cleanUp();
    }

    /**
     * Send an HTTP error response code.
     *
     * @param request  the HttpServletRequest object.
     * @param response the HttpServletResponse object.
     * @param code     the HttpServletResponse error code (see {@link jakarta.servlet.http.HttpServletResponse} for possible error codes).
     * @param e        the Exception that is reported.
     * @since 2.3.17
     */
    public void sendError(HttpServletRequest request, HttpServletResponse response, int code, Exception e) {
        errorHandler.handleError(request, response, code, e);
    }

    /**
     * Cleanup any resources used to initialise Dispatcher
     */
    public void cleanUpAfterInit() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Cleaning up resources used to init Dispatcher");
        }
        ContainerHolder.clear();
    }

    /**
     * Provide an accessor class for static XWork utility.
     */
    public static class Locator {
        public Location getLocation(Object obj) {
            Location loc = LocationUtils.getLocation(obj);
            if (loc == null) {
                return Location.UNKNOWN;
            }
            return loc;
        }
    }

    /**
     * Expose the ConfigurationManager instance.
     *
     * @return The instance
     */
    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    /**
     * Exposes a thread-cached reference of the dependency injection container. If the container is found to have
     * changed since the last time it was cached, this Dispatcher instance is re-injected to ensure no stale
     * configuration/dependencies persist.
     * <p>
     * A non-cached reference can be obtained by calling {@link #getConfigurationManager()}.
     *
     * @return Our dependency injection container
     */
    public Container getContainer() {
        if (ContainerHolder.get() == null) {
            try {
                ContainerHolder.store(getConfigurationManager().getConfiguration().getContainer());
            } catch (NullPointerException e) {
                throw new IllegalStateException("ConfigurationManager and/or Configuration should not be null", e);
            }
        }
        if (injectedContainer != ContainerHolder.get()) {
            injectedContainer = ContainerHolder.get();
            injectedContainer.inject(this);
        }
        return ContainerHolder.get();
    }
}
