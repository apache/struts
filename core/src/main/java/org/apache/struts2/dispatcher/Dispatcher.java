/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.dispatcher;

import java.io.File;
import java.io.IOException;
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
import org.apache.struts2.config.Settings;
import org.apache.struts2.config.StrutsXMLConfigurationProvider;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.impl.StrutsActionProxyFactory;
import org.apache.struts2.impl.StrutsObjectFactory;
import org.apache.struts2.util.AttributeMap;
import org.apache.struts2.util.ObjectFactoryDestroyable;
import org.apache.struts2.util.ObjectFactoryInitializable;
import org.apache.struts2.views.freemarker.FreemarkerManager;

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.FileManager;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.ObjectTypeDeterminer;
import com.opensymphony.xwork2.util.ObjectTypeDeterminerFactory;
import com.opensymphony.xwork2.util.OgnlValueStack;
import com.opensymphony.xwork2.util.XWorkContinuationConfig;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationUtils;

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

    // Set Struts-specific factories.
    static {
        ObjectFactory.setObjectFactory(new StrutsObjectFactory());
        ActionProxyFactory.setFactory(new StrutsActionProxyFactory());
    }

    private static final Log LOG = LogFactory.getLog(Dispatcher.class);

    private static ThreadLocal<Dispatcher> instance = new ThreadLocal<Dispatcher>();
    private static List<DispatcherListener> dispatcherListeners = 
        new ArrayList<DispatcherListener>();
    
    private ConfigurationManager configurationManager;
    private boolean portletSupportActive;
    private boolean devMode = false;

    // used to get WebLogic to play nice
    private boolean paramsWorkaroundEnabled = false;

    /**
     * Gets the current instance for this thread
     * 
     * @return The dispatcher instance
     */
    public static Dispatcher getInstance() {
        return (Dispatcher) instance.get();
    }

    /** 
     * Sets the dispatcher instance for this thread
     * 
     * @param instance The instance
     */
    public static void setInstance(Dispatcher instance) {
        Dispatcher.instance.set(instance);
    }
    
    /**
     * Adds a dispatcher lifecycle listener
     * 
     * @param l The listener
     */
    public static synchronized void addDispatcherListener(DispatcherListener l) {
        dispatcherListeners.add(l);
    }
    
    /** 
     * Removes a dispatcher lifecycle listener
     * 
     * @param l The listener
     */
    public static synchronized void removeDispatcherListener(DispatcherListener l) {
        dispatcherListeners.remove(l);
    }

    /**
     * The constructor with its servlet context instance (optional)
     * 
     * @param servletContext The servlet context
     */
    public Dispatcher(ServletContext servletContext) {
        init(servletContext);
    }

    /** 
     * Cleans up thread local variables
     */
    public void cleanup() {
        ObjectFactory objectFactory = ObjectFactory.getObjectFactory();
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
        instance.set(null);
        synchronized(Dispatcher.class) {
            if (dispatcherListeners.size() > 0) {
                for (DispatcherListener l : dispatcherListeners) {
                    l.dispatcherDestroyed(this);
                }
            }
        }
    }

    /**
     * Initializes the instance
     * 
     * @param servletContext The servlet context
     */
    private void init(ServletContext servletContext) {
        boolean reloadi18n = Boolean.valueOf((String) Settings.get(StrutsConstants.STRUTS_I18N_RELOAD)).booleanValue();
        LocalizedTextUtil.setReloadBundles(reloadi18n);

        if (Settings.isSet(StrutsConstants.STRUTS_OBJECTFACTORY)) {
            String className = (String) Settings.get(StrutsConstants.STRUTS_OBJECTFACTORY);
            if (className.equals("spring")) {
                // note: this class name needs to be in string form so we don't put hard
                //       dependencies on spring, since it isn't technically required.
                className = "org.apache.struts2.spring.StrutsSpringObjectFactory";
            } else if (className.equals("plexus")) {
                // note: this class name needs to be in string form so we don't put hard
                //       dependencies on spring, since it isn't technically required.
                className = "org.apache.struts2.plexus.PlexusObjectFactory";
            }

            try {
                Class clazz = ClassLoaderUtil.loadClass(className, Dispatcher.class);
                ObjectFactory objectFactory = (ObjectFactory) clazz.newInstance();
                if (servletContext != null) {
                    if (objectFactory instanceof ObjectFactoryInitializable) {
                        ((ObjectFactoryInitializable) objectFactory).init(servletContext);
                    }
                }
                ObjectFactory.setObjectFactory(objectFactory);
            } catch (Exception e) {
                LOG.error("Could not load ObjectFactory named " + className + ". Using default ObjectFactory.", e);
            }
        }

        if (Settings.isSet(StrutsConstants.STRUTS_OBJECTTYPEDETERMINER)) {
            String className = (String) Settings.get(StrutsConstants.STRUTS_OBJECTTYPEDETERMINER);
            if (className.equals("tiger")) {
                // note: this class name needs to be in string form so we don't put hard
                //       dependencies on xwork-tiger, since it isn't technically required.
                className = "com.opensymphony.xwork2.util.GenericsObjectTypeDeterminer";
            }
            else if (className.equals("notiger")) {
                className = "com.opensymphony.xwork2.util.DefaultObjectTypeDeterminer";
            }

            try {
                Class clazz = ClassLoaderUtil.loadClass(className, Dispatcher.class);
                ObjectTypeDeterminer objectTypeDeterminer = (ObjectTypeDeterminer) clazz.newInstance();
                ObjectTypeDeterminerFactory.setInstance(objectTypeDeterminer);
            } catch (Exception e) {
                LOG.error("Could not load ObjectTypeDeterminer named " + className + ". Using default DefaultObjectTypeDeterminer.", e);
            }
        }

        if ("true".equals(Settings.get(StrutsConstants.STRUTS_DEVMODE))) {
            devMode = true;
            Settings.set(StrutsConstants.STRUTS_I18N_RELOAD, "true");
            Settings.set(StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, "true");
        }

        //check for configuration reloading
        if ("true".equalsIgnoreCase(Settings.get(StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD))) {
            FileManager.setReloadingConfigs(true);
        }

        if (Settings.isSet(StrutsConstants.STRUTS_CONTINUATIONS_PACKAGE)) {
            String pkg = Settings.get(StrutsConstants.STRUTS_CONTINUATIONS_PACKAGE);
            ObjectFactory.setContinuationPackage(pkg);
        }

        // test wether param-access workaround needs to be enabled
        if (servletContext != null && servletContext.getServerInfo() != null 
                && servletContext.getServerInfo().indexOf("WebLogic") >= 0) {
            LOG.info("WebLogic server detected. Enabling Struts parameter access work-around.");
            paramsWorkaroundEnabled = true;
        } else if (Settings.isSet(StrutsConstants.STRUTS_DISPATCHER_PARAMETERSWORKAROUND)) {
            paramsWorkaroundEnabled = "true".equals(Settings.get(StrutsConstants.STRUTS_DISPATCHER_PARAMETERSWORKAROUND));
        } else {
            LOG.debug("Parameter access work-around disabled.");
        }

        // Check whether portlet support is active or not by trying to get "javax.portlet.PortletRequest"
        try {
            ClassLoaderUtil.loadClass("javax.portlet.PortletRequest", Dispatcher.class);
            portletSupportActive = true;
            if (LOG.isInfoEnabled()) {
                LOG.info("Found portlet-api. Activating Struts's portlet support");
            }
        } catch (ClassNotFoundException e) {
            LOG.debug("Unable to locate the portlet libraries.  Disabling portlet support.");
        }
        
        configurationManager = new ConfigurationManager();
        
        // Load old xwork files
        configurationManager.addConfigurationProvider(new XmlConfigurationProvider("xwork.xml", false));
        
        // Load Struts config files
        configurationManager.addConfigurationProvider(new StrutsXMLConfigurationProvider(false));
        
        synchronized(Dispatcher.class) {
            if (dispatcherListeners.size() > 0) {
                for (DispatcherListener l : dispatcherListeners) {
                    l.dispatcherInitialized(this);
                }
            }
        }
    }

    /**
     * Loads the action and executes it. This method first creates the action context from the given
     * parameters then loads an <tt>ActionProxy</tt> from the given action name and namespace. After that,
     * the action is executed and output channels throught the response object. Actions not found are
     * sent back to the user via the {@link Dispatcher#sendError} method, using the 404 return code.
     * All other errors are reported by throwing a ServletException.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @param mapping  the action mapping object
     * @throws ServletException when an unknown error occurs (not a 404, but typically something that
     *                          would end up as a 5xx by the servlet container)
     */
    public void serviceAction(HttpServletRequest request, HttpServletResponse response, ServletContext context, ActionMapping mapping) throws ServletException {
        Map<String, Object> extraContext = createContextMap(request, response, mapping, context);

        // If there was a previous value stack, then create a new copy and pass it in to be used by the new Action
        OgnlValueStack stack = (OgnlValueStack) request.getAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY);
        if (stack != null) {
            extraContext.put(ActionContext.VALUE_STACK, new OgnlValueStack(stack));
        }

        try {
            String namespace = mapping.getNamespace();
            String name = mapping.getName();
            String method = mapping.getMethod();

            String id = request.getParameter(XWorkContinuationConfig.CONTINUE_PARAM);
            if (id != null) {
                // remove the continue key from the params - we don't want to bother setting
                // on the value stack since we know it won't work. Besides, this breaks devMode!
                Map params = (Map) extraContext.get(ActionContext.PARAMETERS);
                params.remove(XWorkContinuationConfig.CONTINUE_PARAM);

                // and now put the key in the context to be picked up later by XWork
                extraContext.put(XWorkContinuationConfig.CONTINUE_KEY, id);
            }

            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(
                    configurationManager.getConfiguration(), namespace, name, extraContext, true, false);
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
            LOG.error("Could not find action", e);
            sendError(request, response, context, HttpServletResponse.SC_NOT_FOUND, e);
        } catch (Exception e) {
            LOG.error("Could not execute action", e);
            sendError(request, response, context, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }
    }

    /**
     * Creates a context map containing all the wrapped request objects
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

        return createContextMap(requestMap, params, session, application, request, response, context);
    }

    /**
     * Merges all application and servlet attributes into a single <tt>HashMap</tt> to represent the entire
     * <tt>Action</tt> context.
     *
     * @param requestMap     a Map of all request attributes.
     * @param parameterMap   a Map of all request parameters.
     * @param sessionMap     a Map of all session attributes.
     * @param applicationMap a Map of all servlet context attributes.
     * @param request        the HttpServletRequest object.
     * @param response       the HttpServletResponse object.
     * @param servletContext the ServletContext object.
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

        Locale locale = null;
        if (Settings.isSet(StrutsConstants.STRUTS_LOCALE)) {
            locale = LocalizedTextUtil.localeFromString(Settings.get(StrutsConstants.STRUTS_LOCALE), request.getLocale());
        } else {
            locale = request.getLocale();
        }

        extraContext.put(ActionContext.LOCALE, locale);
        extraContext.put(ActionContext.DEV_MODE, Boolean.valueOf(devMode));

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
     * Returns the maximum upload size allowed for multipart requests (this is configurable).
     *
     * @return the maximum upload size allowed for multipart requests
     */
    private static int getMaxSize() {
        Integer maxSize = new Integer(Integer.MAX_VALUE);
        try {
            String maxSizeStr = Settings.get(StrutsConstants.STRUTS_MULTIPART_MAXSIZE);

            if (maxSizeStr != null) {
                try {
                    maxSize = new Integer(maxSizeStr);
                } catch (NumberFormatException e) {
                    LOG.warn("Unable to format 'struts.multipart.maxSize' property setting. Defaulting to Integer.MAX_VALUE");
                }
            } else {
                LOG.warn("Unable to format 'struts.multipart.maxSize' property setting. Defaulting to Integer.MAX_VALUE");
            }
        } catch (IllegalArgumentException e1) {
            LOG.warn("Unable to format 'struts.multipart.maxSize' property setting. Defaulting to Integer.MAX_VALUE");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("maxSize=" + maxSize);
        }

        return maxSize.intValue();
    }

    /**
     * Returns the path to save uploaded files to (this is configurable).
     *
     * @return the path to save uploaded files to
     */
    private String getSaveDir(ServletContext servletContext) {
        String saveDir = Settings.get(StrutsConstants.STRUTS_MULTIPART_SAVEDIR).trim();

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
     * Prepares a request, including setting the encoding and locale
     * 
     * @param request The request
     * @param response The response
     */
    public void prepare(HttpServletRequest request, HttpServletResponse response) {
        String encoding = null;
        if (Settings.isSet(StrutsConstants.STRUTS_I18N_ENCODING)) {
            encoding = Settings.get(StrutsConstants.STRUTS_I18N_ENCODING);
        }

        Locale locale = null;
        if (Settings.isSet(StrutsConstants.STRUTS_LOCALE)) {
            locale = LocalizedTextUtil.localeFromString(Settings.get(StrutsConstants.STRUTS_LOCALE), request.getLocale());
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
     * Wraps and returns the given response or returns the original response object. This is used to transparently
     * handle multipart data as a wrapped class around the given request. Override this method to handle multipart
     * requests in a special way or to handle other types of requests. Note, {@link org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper} is
     * flexible - you should look to that first before overriding this method to handle multipart data.
     *
     * @param request the HttpServletRequest object.
     * @return a wrapped request or original request.
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper
     */
    public HttpServletRequest wrapRequest(HttpServletRequest request, ServletContext servletContext) throws IOException {
        // don't wrap more than once
        if (request instanceof StrutsRequestWrapper) {
            return request;
        }

        if (MultiPartRequest.isMultiPart(request)) {
            request = new MultiPartRequestWrapper(request, getSaveDir(servletContext), getMaxSize());
        } else {
            request = new StrutsRequestWrapper(request);
        }

        return request;
    }

    /**
     * Sends an HTTP error response code.
     *
     * @param request  the HttpServletRequest object.
     * @param response the HttpServletResponse object.
     * @param code     the HttpServletResponse error code (see {@link javax.servlet.http.HttpServletResponse} for possible error codes).
     * @param e        the Exception that is reported.
     */
    public void sendError(HttpServletRequest request, HttpServletResponse response, 
            ServletContext ctx, int code, Exception e) {
        if (devMode) {
            response.setContentType("text/html");
            
            try {
                freemarker.template.Configuration config = FreemarkerManager.getInstance().getConfiguration(ctx);
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
     * Returns <tt>true</tt>, if portlet support is active, <tt>false</tt> otherwise.
     *
     * @return <tt>true</tt>, if portlet support is active, <tt>false</tt> otherwise.
     */
    public boolean isPortletSupportActive() {
        return portletSupportActive;
    }
    
    /** Simple accessor for a static method */
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
     * Gets the current configuration manager instance
     * 
     * @return The instance
     */
    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    /**
     * Sets the current configuration manager instance
     * 
     * @param mgr The configuration manager
     */
    public void setConfigurationManager(ConfigurationManager mgr) {
        this.configurationManager = mgr;
    }
}
