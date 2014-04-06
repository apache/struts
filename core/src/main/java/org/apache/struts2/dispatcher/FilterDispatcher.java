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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.ng.filter.FilterHostConfig;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Master filter for Struts that handles four distinct
 * responsibilities:
 * <p/>
 * <ul>
 * <p/>
 * <li>Executing actions</li>
 * <p/>
 * <li>Cleaning up the {@link ActionContext} (see note)</li>
 * <p/>
 * <li>Serving static content</li>
 * <p/>
 * <li>Kicking off XWork's interceptor chain for the request lifecycle</li>
 * <p/>
 * </ul>
 * <p/>
 * <p/> <b>IMPORTANT</b>: this filter must be mapped to all requests. Unless you know exactly what you are doing, always
 * map to this URL pattern: /*
 * <p/>
 * <p/> <b>Executing actions</b>
 * <p/>
 * <p/> This filter executes actions by consulting the {@link ActionMapper} and determining if the requested URL should
 * invoke an action. If the mapper indicates it should, <b>the rest of the filter chain is stopped</b> and the action is
 * invoked. This is important, as it means that filters like the SiteMesh filter must be placed <b>before</b> this
 * filter or they will not be able to decorate the output of actions.
 * <p/>
 * <p/> <b>Cleaning up the {@link ActionContext}</b>
 * <p/>
 * <p/> This filter will also automatically clean up the {@link ActionContext} for you, ensuring that no memory leaks
 * take place. However, this can sometimes cause problems integrating with other products like SiteMesh. See {@link
 * ActionContextCleanUp} for more information on how to deal with this.
 * <p/>
 * <p/> <b>Serving static content</b>
 * <p/>
 * <p/> This filter also serves common static content needed when using various parts of Struts, such as JavaScript
 * files, CSS files, etc. It works by looking for requests to /struts/*, and then mapping the value after "/struts/"
 * to common packages in Struts and, optionally, in your class path. By default, the following packages are
 * automatically searched:
 * <p/>
 * <ul>
 * <p/>
 * <li>org.apache.struts2.static</li>
 * <p/>
 * <li>template</li>
 * <p/>
 * </ul>
 * <p/>
 * <p/> This means that you can simply request /struts/xhtml/styles.css and the XHTML UI theme's default stylesheet
 * will be returned. Likewise, many of the AJAX UI components require various JavaScript files, which are found in the
 * org.apache.struts2.static package. If you wish to add additional packages to be searched, you can add a comma
 * separated (space, tab and new line will do as well) list in the filter init parameter named "packages". <b>Be
 * careful</b>, however, to expose any packages that may have sensitive information, such as properties file with
 * database access credentials.
 * <p/>
 * <p/>
 * <p/>
 * <p>
 * <p/>
 * This filter supports the following init-params:
 * <!-- START SNIPPET: params -->
 * <p/>
 * <ul>
 * <p/>
 * <li><b>config</b> - a comma-delimited list of XML configuration files to load.</li>
 * <p/>
 * <li><b>actionPackages</b> - a comma-delimited list of Java packages to scan for Actions.</li>
 * <p/>
 * <li><b>configProviders</b> - a comma-delimited list of Java classes that implement the
 * {@link ConfigurationProvider} interface that should be used for building the {@link Configuration}.</li>
 * <p/>
 * <li><b>loggerFactory</b> - The class name of the {@link LoggerFactory} implementation.</li>
 * <p/>
 * <li><b>*</b> - any other parameters are treated as framework constants.</li>
 * <p/>
 * </ul>
 * <p/>
 * <!-- END SNIPPET: params -->
 * <p/>
 * </p>
 * <p/>
 * To use a custom {@link Dispatcher}, the <code>createDispatcher()</code> method could be overriden by
 * the subclass.
 *
 * @version $Date$ $Id$
 * @deprecated Since Struts 2.1.3, use {@link org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter} instead or
 * {@link org.apache.struts2.dispatcher.ng.filter.StrutsPrepareFilter} and {@link org.apache.struts2.dispatcher.ng.filter.StrutsExecuteFilter}
 * if needing using the {@link ActionContextCleanUp} filter in addition to this one
 *
 * @see ActionMapper
 * @see ActionContextCleanUp
 * @see org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter
 * @see org.apache.struts2.dispatcher.ng.filter.StrutsPrepareFilter
 * @see org.apache.struts2.dispatcher.ng.filter.StrutsExecuteFilter
 */
public class FilterDispatcher implements StrutsStatics, Filter {

    /**
     * Provide a logging instance.
     */
    private Logger log;

    /**
     * Provide ActionMapper instance, set by injection.
     */
    private ActionMapper actionMapper;

    /**
     * Provide FilterConfig instance, set on init.
     */
    private FilterConfig filterConfig;

    /**
     * Expose Dispatcher instance to subclass.
     */
    protected Dispatcher dispatcher;

    /**
     * Loads static resources, set by injection.
     */
    protected StaticContentLoader staticResourceLoader;

    /**
     * Maintains per-request override of devMode configuration.
     */
    private static ThreadLocal<Boolean> devModeOverride = new InheritableThreadLocal<Boolean>();

    /**
     * Initializes the filter by creating a default dispatcher
     * and setting the default packages for static resources.
     *
     * @param filterConfig The filter configuration
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            this.filterConfig = filterConfig;

            initLogging();

            dispatcher = createDispatcher(filterConfig);
            dispatcher.init();
            dispatcher.getContainer().inject(this);

            staticResourceLoader.setHostConfig(new FilterHostConfig(filterConfig));
        } finally {
            ActionContext.setContext(null);
        }
    }

    private void initLogging() {
        String factoryName = filterConfig.getInitParameter("loggerFactory");
        if (factoryName != null) {
            try {
                Class cls = ClassLoaderUtil.loadClass(factoryName, this.getClass());
                LoggerFactory fac = (LoggerFactory) cls.newInstance();
                LoggerFactory.setLoggerFactory(fac);
            } catch (InstantiationException e) {
                System.err.println("Unable to instantiate logger factory: " + factoryName + ", using default");
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                System.err.println("Unable to access logger factory: " + factoryName + ", using default");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.err.println("Unable to locate logger factory class: " + factoryName + ", using default");
                e.printStackTrace();
            }
        }

        log = LoggerFactory.getLogger(FilterDispatcher.class);

    }

    /**
     * Calls dispatcher.cleanup,
     * which in turn releases local threads and destroys any DispatchListeners.
     *
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        if (dispatcher == null) {
            log.warn("something is seriously wrong, Dispatcher is not initialized (null) ");
        } else {
            try {
                dispatcher.cleanup();
            } finally {
                ActionContext.setContext(null);
            }
        }
    }

    /**
     * Set an override of the static devMode value.  Do not set this via a
     * request parameter or any other unprotected method.  Using a signed
     * cookie is one safe way to turn it on per request.
     * 
     * @param devMode   the override value
     */
    public static void overrideDevMode(
        boolean devMode)
    {
        devModeOverride.set(Boolean.valueOf(devMode));
    }

    /**
     * @return Boolean override value, or null if no override
     */
    public static Boolean getDevModeOverride()
    {
        return devModeOverride.get();
    }

    /**
     * Create a default {@link Dispatcher} that subclasses can override
     * with a custom Dispatcher, if needed.
     *
     * @param filterConfig Our FilterConfig
     * @return Initialized Dispatcher
     */
    protected Dispatcher createDispatcher(FilterConfig filterConfig) {
        Map<String, String> params = new HashMap<String, String>();
        for (Enumeration e = filterConfig.getInitParameterNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            String value = filterConfig.getInitParameter(name);
            params.put(name, value);
        }
        return createDispatcher(filterConfig.getServletContext(), params);
    }

    /**
     * Create a default {@link Dispatcher} that subclasses can override
     * with a custom Dispatcher, if needed.  Called by
     * createDispatcher(FilterConfig).
     *
     * @param ctx ServletContext
     * @param params parameters from FilterConfig
     * @return Initialized Dispatcher
     */
    protected Dispatcher createDispatcher(ServletContext ctx, Map<String, String> params) {
        return new Dispatcher(ctx, params);
    }

    /**
     * Modify state of StrutsConstants.STRUTS_STATIC_CONTENT_LOADER setting.
     * @param staticResourceLoader val New setting
     */
    @Inject
    public void setStaticResourceLoader(StaticContentLoader staticResourceLoader) {
        this.staticResourceLoader = staticResourceLoader;
    }

    /**
     * Modify ActionMapper instance.
     * @param mapper New instance
     */
    @Inject
    public void setActionMapper(ActionMapper mapper) {
        actionMapper = mapper;
    }

    /**
     * Provide a workaround for some versions of WebLogic.
     * <p/>
     * Servlet 2.3 specifies that the servlet context can be retrieved from the session. Unfortunately, some versions of
     * WebLogic can only retrieve the servlet context from the filter config. Hence, this method enables subclasses to
     * retrieve the servlet context from other sources.
     *
     * @return the servlet context.
     */
    protected ServletContext getServletContext() {
        return filterConfig.getServletContext();
    }

    /**
     * Expose the FilterConfig instance.
     *
     * @return Our FilterConfit instance
     */
    protected FilterConfig getFilterConfig() {
        return filterConfig;
    }

    /**
     * Wrap and return the given request, if needed, so as to to transparently
     * handle multipart data as a wrapped class around the given request.
     *
     * @param request  Our ServletRequest object
     * @param response Our ServerResponse object
     * @return Wrapped HttpServletRequest object
     * @throws ServletException on any error
     */
    protected HttpServletRequest prepareDispatcherAndWrapRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        Dispatcher du = Dispatcher.getInstance();

        // Prepare and wrap the request if the cleanup filter hasn't already, cleanup filter should be
        // configured first before struts2 dispatcher filter, hence when its cleanup filter's turn,
        // static instance of Dispatcher should be null.
        if (du == null) {

            Dispatcher.setInstance(dispatcher);

            // prepare the request no matter what - this ensures that the proper character encoding
            // is used before invoking the mapper (see WW-9127)
            dispatcher.prepare(request, response);
        } else {
            dispatcher = du;
        }

        try {
            // Wrap request first, just in case it is multipart/form-data
            // parameters might not be accessible through before encoding (ww-1278)
            request = dispatcher.wrapRequest(request);
        } catch (IOException e) {
            String message = "Could not wrap servlet request with MultipartRequestWrapper!";
            log.error(message, e);
            throw new ServletException(message, e);
        }

        return request;
    }

    /**
     * Process an action or handle a request a static resource.
     * <p/>
     * The filter tries to match the request to an action mapping.
     * If mapping is found, the action processes is delegated to the dispatcher's serviceAction method.
     * If action processing fails, doFilter will try to create an error page via the dispatcher.
     * <p/>
     * Otherwise, if the request is for a static resource,
     * the resource is copied directly to the response, with the appropriate caching headers set.
     * <p/>
     * If the request does not match an action mapping, or a static resource page,
     * then it passes through.
     *
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        showDeprecatedWarning();

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        ServletContext servletContext = getServletContext();

        String timerKey = "FilterDispatcher_doFilter: ";
        try {

            // FIXME: this should be refactored better to not duplicate work with the action invocation
            ValueStack stack = dispatcher.getContainer().getInstance(ValueStackFactory.class).createValueStack();
            ActionContext ctx = new ActionContext(stack.getContext());
            ActionContext.setContext(ctx);

            UtilTimerStack.push(timerKey);
            request = prepareDispatcherAndWrapRequest(request, response);
            ActionMapping mapping;
            try {
                mapping = actionMapper.getMapping(request, dispatcher.getConfigurationManager());
            } catch (Exception ex) {
                log.error("error getting ActionMapping", ex);
                dispatcher.sendError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex);
                return;
            }

            if (mapping == null) {
                // there is no action in this request, should we look for a static resource?
                String resourcePath = RequestUtils.getServletPath(request);

                if ("".equals(resourcePath) && null != request.getPathInfo()) {
                    resourcePath = request.getPathInfo();
                }

                if (staticResourceLoader.canHandle(resourcePath)) {
                    staticResourceLoader.findStaticResource(resourcePath, request, response);
                } else {
                    // this is a normal request, let it pass through
                    chain.doFilter(request, response);
                }
                // The framework did its job here
                return;
            }

            dispatcher.serviceAction(request, response, mapping);

        } finally {
            dispatcher.cleanUpRequest(request);
            try {
                ActionContextCleanUp.cleanUp(req);
            } finally {
                UtilTimerStack.pop(timerKey);
            }
            devModeOverride.remove();
        }
    }

    private void showDeprecatedWarning() {
        String msg =
                "\n\n" +
                "***********************************************************************\n" +
                "*                               WARNING!!!                            *\n" +
                "*                                                                     *\n" +
                "* >>> FilterDispatcher <<< is deprecated! Please use the new filters! *\n" +
                "*                                                                     *\n" +
                "*           This can be a source of unpredictable problems!           *\n" +
                "*                                                                     *\n" +
                "*              Please refer to the docs for more details!             *\n" +
                "*            http://struts.apache.org/2.x/docs/webxml.html            *\n" +
                "*                                                                     *\n" +
                "***********************************************************************\n\n";
        System.out.println(msg);
    }
}
