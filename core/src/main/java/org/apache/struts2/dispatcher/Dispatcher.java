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

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.*;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.config.StrutsBeanSelectionProvider;
import org.apache.struts2.config.DefaultPropertiesProvider;
import org.apache.struts2.config.PropertiesConfigurationProvider;
import org.apache.struts2.config.StrutsJavaConfiguration;
import org.apache.struts2.config.StrutsJavaConfigurationProvider;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.util.AttributeMap;
import org.apache.struts2.util.ObjectFactoryDestroyable;
import org.apache.struts2.util.fs.JBossFileManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

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

    public static final String MULTIPART_FORM_DATA_REGEX = "^multipart/form-data(?:\\s*;\\s*boundary=[0-9a-zA-Z'()+_,\\-./:=?]{1,70})?(?:\\s*;\\s*charset=[a-zA-Z\\-0-9]{3,14})?";

    /**
     * Provide a thread local instance.
     */
    private static ThreadLocal<Dispatcher> instance = new ThreadLocal<>();

    /**
     * Store list of DispatcherListeners.
     */
    private static List<DispatcherListener> dispatcherListeners = new CopyOnWriteArrayList<>();

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
     * Store state of StrutsConstants.STRUTS_MULTIPART_SAVEDIR setting.
     */
    private String multipartSaveDir;

    /**
     * Stores the value of {@link StrutsConstants#STRUTS_MULTIPART_PARSER} setting
     */
    private String multipartHandlerName;

    /**
     * Stores the value of {@link StrutsConstants#STRUTS_MULTIPART_ENABLED}
     */
    private boolean multipartSupportEnabled = true;

    /**
     * A regular expression used to validate if request is a multipart/form-data request
     */
    private Pattern multipartValidationPattern = Pattern.compile(MULTIPART_FORM_DATA_REGEX);

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
     * @param initParams The set of initialization parameters
     */
    public Dispatcher(ServletContext servletContext, Map<String, String> initParams) {
        this.servletContext = servletContext;
        this.initParams = initParams;
    }

    /**
     * Modify state of StrutsConstants.STRUTS_DEVMODE setting.
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
     * @param disableRequestAttributeValueStackLookup New setting
     */
    @Inject(value=StrutsConstants.STRUTS_DISABLE_REQUEST_ATTRIBUTE_VALUE_STACK_LOOKUP, required=false)
    public void setDisableRequestAttributeValueStackLookup(String disableRequestAttributeValueStackLookup) {
        this.disableRequestAttributeValueStackLookup = BooleanUtils.toBoolean(disableRequestAttributeValueStackLookup);
    }

    /**
     * Modify state of StrutsConstants.STRUTS_LOCALE setting.
     * @param val New setting
     */
    @Inject(value=StrutsConstants.STRUTS_LOCALE, required=false)
    public void setDefaultLocale(String val) {
        defaultLocale = val;
    }

    /**
     * Modify state of StrutsConstants.STRUTS_I18N_ENCODING setting.
     * @param val New setting
     */
    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setDefaultEncoding(String val) {
        defaultEncoding = val;
    }

    /**
     * Modify state of StrutsConstants.STRUTS_MULTIPART_SAVEDIR setting.
     * @param val New setting
     */
    @Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
    public void setMultipartSaveDir(String val) {
        multipartSaveDir = val;
    }

    @Inject(StrutsConstants.STRUTS_MULTIPART_PARSER)
    public void setMultipartHandler(String val) {
        multipartHandlerName = val;
    }

    @Inject(value = StrutsConstants.STRUTS_MULTIPART_ENABLED, required = false)
    public void setMultipartSupportEnabled(String multipartSupportEnabled) {
        this.multipartSupportEnabled = Boolean.parseBoolean(multipartSupportEnabled);
    }

    @Inject(value = StrutsConstants.STRUTS_MULTIPART_VALIDATION_REGEX, required = false)
    public void setMultipartValidationRegex(String multipartValidationRegex) {
        this.multipartValidationPattern = Pattern.compile(multipartValidationRegex);
    }

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    @Inject(StrutsConstants.STRUTS_HANDLE_EXCEPTION)
    public void setHandleException(String handleException) {
        this.handleException = Boolean.parseBoolean(handleException);
    }

    public boolean isHandleException() {
        return handleException;
    }

    @Inject
    public void setDispatcherErrorHandler(DispatcherErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * Releases all instances bound to this dispatcher instance.
     */
    public void cleanup() {

    	// clean up ObjectFactory
        ObjectFactory objectFactory = getContainer().getInstance(ObjectFactory.class);
        if (objectFactory == null) {
        	LOG.warn("Object Factory is null, something is seriously wrong, no clean up will be performed");
        }
        if (objectFactory instanceof ObjectFactoryDestroyable) {
            try {
                ((ObjectFactoryDestroyable)objectFactory).destroy();
            }
            catch(Exception e) {
                // catch any exception that may occurred during destroy() and log it
                LOG.error("Exception occurred while destroying ObjectFactory [{}]", objectFactory.toString(), e);
            }
        }

        // clean up Dispatcher itself for this thread
        instance.set(null);

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
        ActionContext.setContext(null);

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
        String[] files = configPaths.split("\\s*[,]\\s*");
        for (String file : files) {
            if (file.endsWith(".xml")) {
                configurationManager.addContainerProvider(createStrutsXmlConfigurationProvider(file, false, servletContext));
            } else {
                throw new IllegalArgumentException("Invalid configuration file name");
            }
        }
    }

    protected XmlConfigurationProvider createStrutsXmlConfigurationProvider(String filename, boolean errorIfMissing, ServletContext ctx) {
        return new StrutsXmlConfigurationProvider(filename, errorIfMissing, ctx);
    }

    private void init_JavaConfigurations() {
        String configClasses = initParams.get("javaConfigClasses");
        if (configClasses != null) {
            String[] classes = configClasses.split("\\s*[,]\\s*");
            for (String cname : classes) {
                try {
                    Class cls = ClassLoaderUtil.loadClass(cname, this.getClass());
                    StrutsJavaConfiguration config = (StrutsJavaConfiguration) cls.newInstance();
                    configurationManager.addContainerProvider(createJavaConfigurationProvider(config));
                } catch (InstantiationException e) {
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
            String[] classes = configProvs.split("\\s*[,]\\s*");
            for (String cname : classes) {
                try {
                    Class cls = ClassLoaderUtil.loadClass(cname, this.getClass());
                    ConfigurationProvider prov = (ConfigurationProvider)cls.newInstance();
                    if (prov instanceof ServletContextAwareConfigurationProvider) {
                        ((ServletContextAwareConfigurationProvider)prov).initWithContext(servletContext);
                    }
                    configurationManager.addContainerProvider(prov);
                } catch (InstantiationException e) {
                    throw new ConfigurationException("Unable to instantiate provider: "+cname, e);
                } catch (IllegalAccessException e) {
                    throw new ConfigurationException("Unable to access provider: "+cname, e);
                } catch (ClassNotFoundException e) {
                    throw new ConfigurationException("Unable to locate provider class: "+cname, e);
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

    private Container init_PreloadConfiguration() {
        return getContainer();
    }

    private void init_CheckWebLogicWorkaround(Container container) {
        // test whether param-access workaround needs to be enabled
        if (servletContext != null && StringUtils.contains(servletContext.getServerInfo(), "WebLogic")) {
            LOG.info("WebLogic server detected. Enabling Struts parameter access work-around.");
            paramsWorkaroundEnabled = true;
        } else {
            paramsWorkaroundEnabled = "true".equals(container.getInstance(String.class,
                    StrutsConstants.STRUTS_DISPATCHER_PARAMETERSWORKAROUND));
        }
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
            init_FilterInitParameters() ; // [6]
            init_AliasStandardObjects() ; // [7]

            Container container = init_PreloadConfiguration();
            container.inject(this);
            init_CheckWebLogicWorkaround(container);

            if (!dispatcherListeners.isEmpty()) {
                for (DispatcherListener l : dispatcherListeners) {
                    l.dispatcherInitialized(this);
                }
            }
            errorHandler.init(servletContext);

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
     *
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
            extraContext.put(ActionContext.VALUE_STACK, valueStackFactory.createValueStack(stack));
        }

        try {
            String namespace = mapping.getNamespace();
            String name = mapping.getName();
            String method = mapping.getMethod();

            ActionProxy proxy;

            //check if we are probably in an async resuming
            ActionInvocation invocation = ActionContext.getContext().getActionInvocation();
            if (invocation == null || invocation.isExecuted()) {
                proxy = getContainer().getInstance(ActionProxyFactory.class).createActionProxy(namespace, name, method,
                        extraContext, true, false);
            } else {
                proxy = invocation.getProxy();
            }

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

    /**
     * Performs logging of missing action/result configuration exception
     *
     * @param request current {@link HttpServletRequest}
     * @param e {@link ConfigurationException} that occurred
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
     * @param request The servlet request
     * @param response The servlet response
     * @param mapping The action mapping
     * @return A map of context objects
     *
     * @since 2.3.17
     */
    public Map<String,Object> createContextMap(HttpServletRequest request, HttpServletResponse response,
            ActionMapping mapping) {

        // request map wrapping the http request objects
        Map requestMap = new RequestMap(request);

        // parameters map wrapping the http parameters.  ActionMapping parameters are now handled and applied separately
        HttpParameters params = HttpParameters.create(request.getParameterMap()).build();

        // session map wrapping the http session
        Map session = new SessionMap(request);

        // application map wrapping the ServletContext
        Map application = new ApplicationMap(servletContext);

        Map<String,Object> extraContext = createContextMap(requestMap, params, session, application, request, response);

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
     *
     * @since 2.3.17
     */
    public HashMap<String,Object> createContextMap(Map requestMap,
                                    HttpParameters parameters,
                                    Map sessionMap,
                                    Map applicationMap,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        HashMap<String, Object> extraContext = new HashMap<>();
        extraContext.put(ActionContext.PARAMETERS, parameters);
        extraContext.put(ActionContext.SESSION, sessionMap);
        extraContext.put(ActionContext.APPLICATION, applicationMap);

        extraContext.put(ActionContext.LOCALE, getLocale(request));

        extraContext.put(StrutsStatics.HTTP_REQUEST, request);
        extraContext.put(StrutsStatics.HTTP_RESPONSE, response);
        extraContext.put(StrutsStatics.SERVLET_CONTEXT, servletContext);

        // helpers to get access to request/session/application scope
        extraContext.put("request", requestMap);
        extraContext.put("session", sessionMap);
        extraContext.put("application", applicationMap);
        extraContext.put("parameters", parameters);

        AttributeMap attrMap = new AttributeMap(extraContext);
        extraContext.put("attr", attrMap);

        return extraContext;
    }

    protected Locale getLocale(HttpServletRequest request) {
        Locale locale;
        if (defaultLocale != null) {
            try {
                locale = LocaleUtils.toLocale(defaultLocale);
            } catch (IllegalArgumentException e) {
                LOG.warn(new ParameterizedMessage("Cannot convert 'struts.locale' = [{}] to proper locale, defaulting to request locale [{}]",
                                defaultLocale, request.getLocale()), e);
                locale = request.getLocale();
            }
        } else {
            locale = request.getLocale();
        }
        return locale;
    }

    /**
     * Return the path to save uploaded files to (this is configurable).
     *
     * @return the path to save uploaded files to
     */
    protected String getSaveDir() {
        String saveDir = multipartSaveDir.trim();

        if (saveDir.equals("")) {
            File tempdir = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        	LOG.info("Unable to find 'struts.multipart.saveDir' property setting. Defaulting to javax.servlet.context.tempdir");

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
                        logMessage = "Could not find create multipart save directory '" + multipartSaveDir.getCanonicalPath() + "'.";
                    } catch (IOException e) {
                        logMessage = "Could not find create multipart save directory '" + multipartSaveDir.toString() + "'.";
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
     * @param request The request
     * @param response The response
     */
    public void prepare(HttpServletRequest request, HttpServletResponse response) {
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
            LOG.error("Error setting character encoding to '{}' - ignoring.", encoding, e);
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
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper
     * @throws java.io.IOException on any error.
     *
     * @since 2.3.17
     */
    public HttpServletRequest wrapRequest(HttpServletRequest request) throws IOException {
        // don't wrap more than once
        if (request instanceof StrutsRequestWrapper) {
            return request;
        }

        if (isMultipartSupportEnabled(request) && isMultipartRequest(request)) {
            MultiPartRequest multiPartRequest = getMultiPartRequest();
            LocaleProviderFactory localeProviderFactory = getContainer().getInstance(LocaleProviderFactory.class);

            request = new MultiPartRequestWrapper(
                    multiPartRequest,
                    request,
                    getSaveDir(),
                    localeProviderFactory.createLocaleProvider(),
                    disableRequestAttributeValueStackLookup
            );
        } else {
            request = new StrutsRequestWrapper(request, disableRequestAttributeValueStackLookup);
        }

        return request;
    }

    /**
     * Checks if support to parse multipart requests is enabled
     * 
     * @param request current servlet request
     * @return false if disabled
     *
     * @since 2.5.11
     */
    protected boolean isMultipartSupportEnabled(HttpServletRequest request) {
        return multipartSupportEnabled;
    }

    /**
     * Checks if request is a multipart request (a file upload request)
     *
     * @param request current servlet request
     * @return true if it is a multipart request
     *
     * @since 2.5.11
     */
    protected boolean isMultipartRequest(HttpServletRequest request) {
        String httpMethod = request.getMethod();
        String contentType = request.getContentType();

        return REQUEST_POST_METHOD.equalsIgnoreCase(httpMethod) &&
                contentType != null &&
                multipartValidationPattern.matcher(contentType.toLowerCase(Locale.ENGLISH)).matches();
    }

    /**
     * On each request it must return a new instance as implementation could be not thread safe
     * and thus ensure of resource clean up
     *
     * @return a multi part request object
     */
    protected MultiPartRequest getMultiPartRequest() {
        MultiPartRequest mpr = null;
        //check for alternate implementations of MultiPartRequest
        Set<String> multiNames = getContainer().getInstanceNames(MultiPartRequest.class);
        for (String multiName : multiNames) {
            if (multiName.equals(multipartHandlerName)) {
                mpr = getContainer().getInstance(MultiPartRequest.class, multiName);
            }
        }
        if (mpr == null ) {
            mpr = getContainer().getInstance(MultiPartRequest.class);
        }
        return mpr;
    }

    /**
     * Removes all the files created by MultiPartRequestWrapper.
     *
     * @param request the HttpServletRequest object.
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper
     */
    public void cleanUpRequest(HttpServletRequest request) {
        ContainerHolder.clear();
        if (!(request instanceof MultiPartRequestWrapper)) {
            return;
        }
        MultiPartRequestWrapper multiWrapper = (MultiPartRequestWrapper) request;
        multiWrapper.cleanUp();
    }

    /**
     * Send an HTTP error response code.
     *
     * @param request  the HttpServletRequest object.
     * @param response the HttpServletResponse object.
     * @param code     the HttpServletResponse error code (see {@link javax.servlet.http.HttpServletResponse} for possible error codes).
     * @param e        the Exception that is reported.
     *
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
     * Expose the dependency injection container.
     * @return Our dependency injection container
     */
    public Container getContainer() {
        if (ContainerHolder.get() != null) {
            return ContainerHolder.get();
        }
        ConfigurationManager mgr = getConfigurationManager();
        if (mgr == null) {
            throw new IllegalStateException("The configuration manager shouldn't be null");
        } else {
            Configuration config = mgr.getConfiguration();
            if (config == null) {
                throw new IllegalStateException("Unable to load configuration");
            } else {
                Container container = config.getContainer();
                ContainerHolder.store(container);
                return container;
            }
        }
    }

}
