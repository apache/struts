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
package org.apache.struts2.dispatcher;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.config.*;
import org.apache.struts2.config.ClasspathConfigurationProvider.ClasspathPageLocator;
import org.apache.struts2.config.ClasspathConfigurationProvider.PageLocator;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.util.AttributeMap;
import org.apache.struts2.util.ClassLoaderUtils;
import org.apache.struts2.util.ObjectFactoryDestroyable;
import org.apache.struts2.views.freemarker.FreemarkerManager;

import com.opensymphony.xwork2.util.FileManager;
import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.ObjectTypeDeterminer;
import com.opensymphony.xwork2.util.ObjectTypeDeterminerFactory;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationUtils;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;

import freemarker.template.Template;

/**
 * A utility class the actual dispatcher delegates most of its tasks to. Each instance
 * of the primary dispatcher holds an instance of this dispatcher to be shared for
 * all requests.
 *
 * @see org.apache.struts2.dispatcher.FilterDispatcher
 * @see org.apache.struts2.portlet.dispatcher.Jsr168Dispatcher
 */
public class Dispatcher {

    /**
     * Provide a logging instance.
     */
    private static final Log LOG = LogFactory.getLog(Dispatcher.class);

    /**
     * Provide a thread local instance.
     */
    private static ThreadLocal<Dispatcher> instance = new ThreadLocal<Dispatcher>();

    /**
     * Store list of DispatcherListeners.
     */
    private static List<DispatcherListener> dispatcherListeners =
        new ArrayList<DispatcherListener>();

    /**
     * Store ConfigurationManager instance, set on init.
     */
    private ConfigurationManager configurationManager;

    /**
     * Store whether portlet support is active
     * (set to true by Jsr168Dispatcher).
     */
    private static boolean portletSupportActive;

    /**
     * Store state of  StrutsConstants.STRUTS_DEVMODE setting.
     */
    private static boolean devMode;

    /**
     * Store state of StrutsConstants.STRUTS_I18N_ENCODING setting.
     */
    private static String defaultEncoding;

    /**
     * Store state of StrutsConstants.STRUTS_LOCALE setting.
     */
    private static String defaultLocale;

    /**
     * Store state of StrutsConstants.STRUTS_MULTIPART_SAVEDIR setting.
     */
    private static String multipartSaveDir;

    /**
     * Provide list of default configuration files.
     */
    private static final String DEFAULT_CONFIGURATION_PATHS = "struts-default.xml,struts-plugin.xml,struts.xml";

    /**
     * Store state of STRUTS_DISPATCHER_PARAMETERSWORKAROUND.
     * <p/>
     * The workaround is for WebLogic.
     * We try to autodect WebLogic on Dispatcher init.
     * The workaround can also be enabled manually.
     */
    private boolean paramsWorkaroundEnabled = false;

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

        // Tie the ObjectFactory threadlocal instance to this Dispatcher instance
        if (instance != null) {
            Container cont = instance.getContainer();
            if (cont != null) {
                ObjectFactory.setObjectFactory(cont.getInstance(ObjectFactory.class));
            } else {
                LOG.warn("This dispatcher instance doesn't have a container, so the object factory won't be set.");
            }
        } else {
            ObjectFactory.setObjectFactory(null);
        }
    }

    /**
     * Add a dispatcher lifecycle listener.
     *
     * @param listener The listener to add
     */
    public static synchronized void addDispatcherListener(DispatcherListener listener) {
        dispatcherListeners.add(listener);
    }

    /**
     * Remove a specific dispatcher lifecycle listener.
     *
     * @param listener The listener
     */
    public static synchronized void removeDispatcherListener(DispatcherListener listener) {
        dispatcherListeners.remove(listener);
    }

    private ServletContext servletContext;
    private Map<String, String> initParams;


    /**
     * Create the Dispatcher instance for a given ServletContext and set of initialization parameters.
     *
     * @param servletContext Our servlet context
     * @param initParams The set of initialization parameters
     */
    public  Dispatcher(ServletContext servletContext, Map<String, String> initParams) {
        this.servletContext = servletContext;
        this.initParams = initParams;
    }

    /**
     * Modify state of StrutsConstants.STRUTS_DEVMODE setting.
     * @param mode New setting
     */
    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public static void setDevMode(String mode) {
        devMode = "true".equals(mode);
    }

    /**
     * Modify state of StrutsConstants.STRUTS_LOCALE setting.
     * @param val New setting
     */
    @Inject(value=StrutsConstants.STRUTS_LOCALE, required=false)
    public static void setDefaultLocale(String val) {
        defaultLocale = val;
    }

    /**
     * Modify state of StrutsConstants.STRUTS_I18N_ENCODING setting.
     * @param val New setting
     */
    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public static void setDefaultEncoding(String val) {
        defaultEncoding = val;
    }

    /**
     * Modify state of StrutsConstants.STRUTS_MULTIPART_SAVEDIR setting.
     * @param val New setting
     */
    @Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
    public static void setMultipartSaveDir(String val) {
        multipartSaveDir = val;
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
                // catch any exception that may occured during destroy() and log it
                LOG.error("exception occurred while destroying ObjectFactory ["+objectFactory+"]", e);
            }
        }

        // clean up Dispatcher itself for this thread
        instance.set(null);

        // clean up DispatcherListeners
        synchronized(Dispatcher.class) {
            if (dispatcherListeners.size() > 0) {
                for (DispatcherListener l : dispatcherListeners) {
                    l.dispatcherDestroyed(this);
                }
            }
        }

        // clean up configuration
    	configurationManager.destroyConfiguration();
    	configurationManager = null;
    }

    private void init_DefaultProperties() {
        configurationManager.addConfigurationProvider(new DefaultPropertiesProvider());
    }
    
    private void init_LegacyStrutsProperties() {
        configurationManager.addConfigurationProvider(new LegacyPropertiesConfigurationProvider());
    }

    private void init_TraditionalXmlConfigurations() {
        String configPaths = initParams.get("config");
        if (configPaths == null) {
            configPaths = DEFAULT_CONFIGURATION_PATHS;
        }
        String[] files = configPaths.split("\\s*[,]\\s*");
        for (String file : files) {
            if (file.endsWith(".xml")) {
                if ("xwork.xml".equals(file)) {
                    configurationManager.addConfigurationProvider(new XmlConfigurationProvider(file, false));
                } else {
                    configurationManager.addConfigurationProvider(new StrutsXmlConfigurationProvider(file, false, servletContext));
                }
            } else {
                throw new IllegalArgumentException("Invalid configuration file name");
            }
        }
    }

    private void init_ZeroConfiguration() {
        String packages = initParams.get("actionPackages");
        if (packages != null) {
            String[] names = packages.split("\\s*[,]\\s*");
            // Initialize the classloader scanner with the configured packages
            if (names.length > 0) {
                ClasspathConfigurationProvider provider = new ClasspathConfigurationProvider(names);
                provider.setPageLocator(new ServletContextPageLocator(servletContext));
                configurationManager.addConfigurationProvider(provider);
            }
        }
    }

    private void init_CustomConfigurationProviders() {
        String configProvs = initParams.get("configProviders");
        if (configProvs != null) {
            String[] classes = configProvs.split("\\s*[,]\\s*");
            for (String cname : classes) {
                try {
                    Class cls = ClassLoaderUtils.loadClass(cname, this.getClass());
                    ConfigurationProvider prov = (ConfigurationProvider)cls.newInstance();
                    configurationManager.addConfigurationProvider(prov);
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

    private void init_MethodConfigurationProvider() {
        // See https://issues.apache.org/struts/browse/WW-1522
    /*
    com.opensymphony.xwork2.inject.DependencyException: com.opensymphony.xwork2.inject.ContainerImpl$MissingDependencyException: No mapping found for dependency [type=org.apache.struts2.dispatcher.mapper.ActionMapper, name='default'] in public static void org.apache.struts2.dispatcher.FilterDispatcher.setActionMapper(org.apache.struts2.dispatcher.mapper.ActionMapper).
	at com.opensymphony.xwork2.inject.ContainerImpl.addInjectorsForMembers(ContainerImpl.java:135)
	at com.opensymphony.xwork2.inject.ContainerImpl.addInjectorsForMethods(ContainerImpl.java:104)
	at com.opensymphony.xwork2.inject.ContainerImpl.injectStatics(ContainerImpl.java:89)
	at com.opensymphony.xwork2.inject.ContainerBuilder.create(ContainerBuilder.java:494)
	at com.opensymphony.xwork2.config.impl.DefaultConfiguration.reload(DefaultConfiguration.java:140)
	at com.opensymphony.xwork2.config.ConfigurationManager.getConfiguration(ConfigurationManager.java:52)
	at org.apache.struts2.dispatcher.Dispatcher.init_MethodConfigurationProvider(Dispatcher.java:347)
	at org.apache.struts2.dispatcher.Dispatcher.init(Dispatcher.java:421)
	at org.apache.struts2.config.MethodConfigurationProviderTest.setUp(MethodConfigurationProviderTest.java:68)
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:40)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
	at com.intellij.rt.execution.application.AppMain.main(AppMain.java:90)
Caused by: com.opensymphony.xwork2.inject.ContainerImpl$MissingDependencyException: No mapping found for dependency [type=org.apache.struts2.dispatcher.mapper.ActionMapper, name='default'] in public static void org.apache.struts2.dispatcher.FilterDispatcher.setActionMapper(org.apache.struts2.dispatcher.mapper.ActionMapper).
	at com.opensymphony.xwork2.inject.ContainerImpl.createParameterInjector(ContainerImpl.java:217)
	at com.opensymphony.xwork2.inject.ContainerImpl.getParametersInjectors(ContainerImpl.java:207)
	at com.opensymphony.xwork2.inject.ContainerImpl$MethodInjector.<init>(ContainerImpl.java:260)
	at com.opensymphony.xwork2.inject.ContainerImpl$3.create(ContainerImpl.java:108)
	at com.opensymphony.xwork2.inject.ContainerImpl$3.create(ContainerImpl.java:106)
	at com.opensymphony.xwork2.inject.ContainerImpl.addInjectorsForMembers(ContainerImpl.java:132)
	... 26 more

        MethodConfigurationProvider provider = new MethodConfigurationProvider();
        provider.init(configurationManager.getConfiguration());
        provider.loadPackages();
   */
    }

    private void init_FilterInitParameters() {
        configurationManager.addConfigurationProvider(new ConfigurationProvider() {
            public void destroy() {}
            public void init(Configuration configuration) throws ConfigurationException {}
            public void loadPackages() throws ConfigurationException {}
            public boolean needsReload() { return false; }

            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                props.putAll(initParams);
            }
        });
    }

    private void init_AliasStandardObjects() {
        configurationManager.addConfigurationProvider(new BeanSelectionProvider());
    }

    private Container init_PreloadConfiguration() {
        Configuration config = configurationManager.getConfiguration();
        Container container = config.getContainer();

        boolean reloadi18n = Boolean.valueOf(container.getInstance(String.class, StrutsConstants.STRUTS_I18N_RELOAD));
        LocalizedTextUtil.setReloadBundles(reloadi18n);

        ObjectTypeDeterminer objectTypeDeterminer = container.getInstance(ObjectTypeDeterminer.class);
        ObjectTypeDeterminerFactory.setInstance(objectTypeDeterminer);

        TextProvider textProvider = container.getInstance(TextProvider.class);
        TextProviderFactory.setInstance(textProvider);

        return container;
    }

    private void init_CheckConfigurationReloading(Container container) {
        FileManager.setReloadingConfigs("true".equals(container.getInstance(String.class,
                StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD)));
    }

    private void init_CheckWebLogicWorkaround(Container container) {
        // test whether param-access workaround needs to be enabled
        if (servletContext != null && servletContext.getServerInfo() != null
                && servletContext.getServerInfo().indexOf("WebLogic") >= 0) {
            LOG.info("WebLogic server detected. Enabling Struts parameter access work-around.");
            paramsWorkaroundEnabled = true;
        } else {
            paramsWorkaroundEnabled = "true".equals(container.getInstance(String.class,
                    StrutsConstants.STRUTS_DISPATCHER_PARAMETERSWORKAROUND));
        }

        synchronized(Dispatcher.class) {
            if (dispatcherListeners.size() > 0) {
                for (DispatcherListener l : dispatcherListeners) {
                    l.dispatcherInitialized(this);
                }
            }
        }

    }

    /**
     * Load configurations, including both XML and zero-configuration strategies,
     * and update optional settings, including whether to reload configurations and resource files.
     */
    public void init() {

    	if (configurationManager == null) {
    		configurationManager = new ConfigurationManager(BeanSelectionProvider.DEFAULT_BEAN_NAME);
    	}

    	init_DefaultProperties(); // [1]
        init_TraditionalXmlConfigurations(); // [2]
        init_LegacyStrutsProperties(); // [3]
        init_ZeroConfiguration(); // [4]
        init_CustomConfigurationProviders(); // [5]
        init_MethodConfigurationProvider();
        init_FilterInitParameters() ; // [6]
        init_AliasStandardObjects() ; // [7]

        Container container = init_PreloadConfiguration();
        init_CheckConfigurationReloading(container);
        init_CheckWebLogicWorkaround(container);

    }

    /**
     * Load Action class for mapping and invoke the appropriate Action method, or go directly to the Result.
     * <p/>
     * This method first creates the action context from the given parameters,
     * and then loads an <tt>ActionProxy</tt> from the given action name and namespace.
     * After that, the Action method is executed and output channels through the response object.
     * Actions not found are sent back to the user via the {@link Dispatcher#sendError} method,
     * using the 404 return code.
     * All other errors are reported by throwing a ServletException.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @param mapping  the action mapping object
     * @throws ServletException when an unknown error occurs (not a 404, but typically something that
     *                          would end up as a 5xx by the servlet container)
     * @param context Our ServletContext object
     */
    public void serviceAction(HttpServletRequest request, HttpServletResponse response, ServletContext context,
                              ActionMapping mapping) throws ServletException {

        Map<String, Object> extraContext = createContextMap(request, response, mapping, context);

        // If there was a previous value stack, then create a new copy and pass it in to be used by the new Action
        ValueStack stack = (ValueStack) request.getAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY);
        if (stack != null) {
            extraContext.put(ActionContext.VALUE_STACK, ValueStackFactory.getFactory().createValueStack(stack));
        }

        String timerKey = "Handling request from Dispatcher";
        try {
            UtilTimerStack.push(timerKey);
            String namespace = mapping.getNamespace();
            String name = mapping.getName();
            String method = mapping.getMethod();

            Configuration config = configurationManager.getConfiguration();
            ActionProxy proxy = config.getContainer().getInstance(ActionProxyFactory.class).createActionProxy(
                    namespace, name, extraContext, true, false);
            proxy.setMethod(method);
            request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, proxy.getInvocation().getStack());

            // if the ActionMapping says to go straight to a result, do it!
            if (mapping.getResult() != null) {
                Result result = mapping.getResult();
                result.execute(proxy.getInvocation());
            } else {
                proxy.execute();
            }

            // If there was a previous value stack then set it back onto the request
            if (stack != null) {
                request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);
            }
        } catch (ConfigurationException e) {
            LOG.error("Could not find action or result", e);
            sendError(request, response, context, HttpServletResponse.SC_NOT_FOUND, e);
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            UtilTimerStack.pop(timerKey);
        }
    }

    /**
     * Create a context map containing all the wrapped request objects
     *
     * @param request The servlet request
     * @param response The servlet response
     * @param mapping The action mapping
     * @param context The servlet context
     * @return A map of context objects
     */
    public Map<String,Object> createContextMap(HttpServletRequest request, HttpServletResponse response,
            ActionMapping mapping, ServletContext context) {

        // request map wrapping the http request objects
        Map requestMap = new RequestMap(request);

        // parameters map wrapping the http paraneters.
        Map params = null;
        if (mapping != null) {
            params = mapping.getParams();
        }
        Map requestParams = new HashMap(request.getParameterMap());
        if (params != null) {
            params.putAll(requestParams);
        } else {
            params = requestParams;
        }

        // session map wrapping the http session
        Map session = new SessionMap(request);

        // application map wrapping the ServletContext
        Map application = new ApplicationMap(context);

        Map<String,Object> extraContext = createContextMap(requestMap, params, session, application, request, response, context);
        extraContext.put(ServletActionContext.ACTION_MAPPING, mapping);
        return extraContext;
    }

    /**
     * Merge all application and servlet attributes into a single <tt>HashMap</tt> to represent the entire
     * <tt>Action</tt> context.
     *
     * @param requestMap     a Map of all request attributes.
     * @param parameterMap   a Map of all request parameters.
     * @param sessionMap     a Map of all session attributes.
     * @param applicationMap a Map of all servlet context attributes.
     * @param request        the HttpServletRequest object.
     * @param response       the HttpServletResponse object.
     * @param servletContext the ServletContextmapping object.
     * @return a HashMap representing the <tt>Action</tt> context.
     */
    public HashMap<String,Object> createContextMap(Map requestMap,
                                    Map parameterMap,
                                    Map sessionMap,
                                    Map applicationMap,
                                    HttpServletRequest request,
                                    HttpServletResponse response,
                                    ServletContext servletContext) {
        HashMap<String,Object> extraContext = new HashMap<String,Object>();
        extraContext.put(ActionContext.PARAMETERS, new HashMap(parameterMap));
        extraContext.put(ActionContext.SESSION, sessionMap);
        extraContext.put(ActionContext.APPLICATION, applicationMap);

        Locale locale;
        if (defaultLocale != null) {
            locale = LocalizedTextUtil.localeFromString(defaultLocale, request.getLocale());
        } else {
            locale = request.getLocale();
        }

        extraContext.put(ActionContext.LOCALE, locale);
        //extraContext.put(ActionContext.DEV_MODE, Boolean.valueOf(devMode));

        extraContext.put(StrutsStatics.HTTP_REQUEST, request);
        extraContext.put(StrutsStatics.HTTP_RESPONSE, response);
        extraContext.put(StrutsStatics.SERVLET_CONTEXT, servletContext);

        // helpers to get access to request/session/application scope
        extraContext.put("request", requestMap);
        extraContext.put("session", sessionMap);
        extraContext.put("application", applicationMap);
        extraContext.put("parameters", parameterMap);

        AttributeMap attrMap = new AttributeMap(extraContext);
        extraContext.put("attr", attrMap);

        return extraContext;
    }

    /**
     * Return the path to save uploaded files to (this is configurable).
     *
     * @return the path to save uploaded files to
     * @param servletContext Our ServletContext
     */
    private String getSaveDir(ServletContext servletContext) {
        String saveDir = multipartSaveDir.trim();

        if (saveDir.equals("")) {
            File tempdir = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
            LOG.info("Unable to find 'struts.multipart.saveDir' property setting. Defaulting to javax.servlet.context.tempdir");

            if (tempdir != null) {
                saveDir = tempdir.toString();
            }
        } else {
            File multipartSaveDir = new File(saveDir);

            if (!multipartSaveDir.exists()) {
                multipartSaveDir.mkdir();
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("saveDir=" + saveDir);
        }

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

        Locale locale = null;
        if (defaultLocale != null) {
            locale = LocalizedTextUtil.localeFromString(defaultLocale, request.getLocale());
        }

        if (encoding != null) {
            try {
                request.setCharacterEncoding(encoding);
            } catch (Exception e) {
                LOG.error("Error setting character encoding to '" + encoding + "' - ignoring.", e);
            }
        }

        if (locale != null) {
            response.setLocale(locale);
        }

        if (paramsWorkaroundEnabled) {
            request.getParameter("foo"); // simply read any parameter (existing or not) to "prime" the request
        }
    }

    /**
     * Wrap and return the given request or return the original request object.
     * </p>
     * This method transparently handles multipart data as a wrapped class around the given request.
     * Override this method to handle multipart requests in a special way or to handle other types of requests.
     * Note, {@link org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper} is
     * flexible - look first to that object before overriding this method to handle multipart data.
     *
     * @param request the HttpServletRequest object.
     * @param servletContext Our ServletContext object
     * @return a wrapped request or original request.
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper
     * @throws java.io.IOException on any error.
     */
    public HttpServletRequest wrapRequest(HttpServletRequest request, ServletContext servletContext) throws IOException {
        // don't wrap more than once
        if (request instanceof StrutsRequestWrapper) {
            return request;
        }

        String content_type = request.getContentType();
        if (content_type != null && content_type.indexOf("multipart/form-data") != -1) {
            MultiPartRequest multi = getContainer().getInstance(MultiPartRequest.class);
            request = new MultiPartRequestWrapper(multi, request, getSaveDir(servletContext));
        } else {
            request = new StrutsRequestWrapper(request);
        }

        return request;
    }

    /**
     * Send an HTTP error response code.
     *
     * @param request  the HttpServletRequest object.
     * @param response the HttpServletResponse object.
     * @param code     the HttpServletResponse error code (see {@link javax.servlet.http.HttpServletResponse} for possible error codes).
     * @param e        the Exception that is reported.
     * @param ctx      the ServletContext object.
     */
    public void sendError(HttpServletRequest request, HttpServletResponse response,
            ServletContext ctx, int code, Exception e) {
        if (devMode) {
            response.setContentType("text/html");

            try {
                FreemarkerManager mgr = getContainer().getInstance(FreemarkerManager.class);

                freemarker.template.Configuration config = mgr.getConfiguration(ctx);
                Template template = config.getTemplate("/org/apache/struts2/dispatcher/error.ftl");

                List<Throwable> chain = new ArrayList<Throwable>();
                Throwable cur = e;
                chain.add(cur);
                while ((cur = cur.getCause()) != null) {
                    chain.add(cur);
                }

                HashMap<String,Object> data = new HashMap<String,Object>();
                data.put("exception", e);
                data.put("unknown", Location.UNKNOWN);
                data.put("chain", chain);
                data.put("locator", new Locator());
                template.process(data, response.getWriter());
                response.getWriter().close();
            } catch (Exception exp) {
                try {
                    response.sendError(code, "Unable to show problem report: " + exp);
                } catch (IOException ex) {
                    // we're already sending an error, not much else we can do if more stuff breaks
                }
            }
        } else {
            try {
                // send a http error response to use the servlet defined error handler
                // make the exception availible to the web.xml defined error page
                request.setAttribute("javax.servlet.error.exception", e);

                // for compatibility
                request.setAttribute("javax.servlet.jsp.jspException", e);

                // send the error response
                response.sendError(code, e.getMessage());
            } catch (IOException e1) {
                // we're already sending an error, not much else we can do if more stuff breaks
            }
        }
    }

    /**
     * Return <tt>true</tt>, if portlet support is active, <tt>false</tt> otherwise.
     *
     * @return <tt>true</tt>, if portlet support is active, <tt>false</tt> otherwise.
     */
    public boolean isPortletSupportActive() {
        return portletSupportActive;
    }

    /**
     * Modify the portlet support mode.
     * @param portletSupportActive <tt>true</tt> or <tt>false</tt>
     */
    public static void setPortletSupportActive(boolean portletSupportActive) {
        Dispatcher.portletSupportActive = portletSupportActive;
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

    /**
     * Provide an accessor class for static XWork utility.
     */
    public class Locator {
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
     * Modify the ConfigurationManager instance
     *
     * @param mgr The configuration manager
     */
    public void setConfigurationManager(ConfigurationManager mgr) {
        this.configurationManager = mgr;
    }

    /**
     * Expose the dependency injection container.
     * @return Our dependency injection container
     */
    public Container getContainer() {
        ConfigurationManager mgr = getConfigurationManager();
        if (mgr == null) {
            throw new IllegalStateException("The configuration manager shouldn't be null");
        } else {
            Configuration config = mgr.getConfiguration();
            if (config == null) {
                throw new IllegalStateException("Unable to load configuration");
            } else {
                return config.getContainer();
            }
        }
    }
}
