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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;

/**
 * Master filter for Struts that handles four distinct
 * responsibilities:
 *
 * <ul>
 *
 * <li>Executing actions</li>
 *
 * <li>Cleaning up the {@link ActionContext} (see note)</li>
 *
 * <li>Serving static content</li>
 *
 * <li>Kicking off XWork's interceptor chain for the request lifecycle</li>
 *
 * </ul>
 *
 * <p/> <b>IMPORTANT</b>: this filter must be mapped to all requests. Unless you know exactly what you are doing, always
 * map to this URL pattern: /*
 *
 * <p/> <b>Executing actions</b>
 *
 * <p/> This filter executes actions by consulting the {@link ActionMapper} and determining if the requested URL should
 * invoke an action. If the mapper indicates it should, <b>the rest of the filter chain is stopped</b> and the action is
 * invoked. This is important, as it means that filters like the SiteMesh filter must be placed <b>before</b> this
 * filter or they will not be able to decorate the output of actions.
 *
 * <p/> <b>Cleaning up the {@link ActionContext}</b>
 *
 * <p/> This filter will also automatically clean up the {@link ActionContext} for you, ensuring that no memory leaks
 * take place. However, this can sometimes cause problems integrating with other products like SiteMesh. See {@link
 * ActionContextCleanUp} for more information on how to deal with this.
 *
 * <p/> <b>Serving static content</b>
 *
 * <p/> This filter also serves common static content needed when using various parts of Struts, such as JavaScript
 * files, CSS files, etc. It works by looking for requests to /struts/*, and then mapping the value after "/struts/"
 * to common packages in Struts and, optionally, in your class path. By default, the following packages are
 * automatically searched:
 *
 * <ul>
 *
 * <li>org.apache.struts2.static</li>
 *
 * <li>template</li>
 *
 * </ul>
 *
 * <p/> This means that you can simply request /struts/xhtml/styles.css and the XHTML UI theme's default stylesheet
 * will be returned. Likewise, many of the AJAX UI components require various JavaScript files, which are found in the
 * org.apache.struts2.static package. If you wish to add additional packages to be searched, you can add a comma
 * separated (space, tab and new line will do as well) list in the filter init parameter named "packages". <b>Be
 * careful</b>, however, to expose any packages that may have sensitive information, such as properties file with
 * database access credentials.
 *
 * <p/>
 *
 * To use a custom {@link Dispatcher}, the <code>createDispatcher()</code> method could be overriden by
 * the subclass.
 *
 * @see org.apache.struts2.lifecycle.LifecycleListener
 * @see ActionMapper
 * @see ActionContextCleanUp
 *
 * @version $Date$ $Id$
 */
public class FilterDispatcher implements StrutsStatics, Filter {

    private static final Log LOG = LogFactory.getLog(FilterDispatcher.class);

    private String[] pathPrefixes;

    private SimpleDateFormat df = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss");
    private final Calendar lastModifiedCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    private final String lastModified = df.format(lastModifiedCal.getTime());
    
    private static boolean serveStatic;
    private static boolean serveStaticBrowserCache;
    private static String encoding;
    private static ActionMapper actionMapper;
    private FilterConfig filterConfig;

    /** Dispatcher instance to be used by subclass. */
    protected Dispatcher dispatcher;


    /**
     * Initializes the filter
     *
     * @param filterConfig The filter configuration
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        dispatcher = createDispatcher(filterConfig);
        this.filterConfig = filterConfig;
        String param = filterConfig.getInitParameter("packages");
        String packages = "org.apache.struts2.static template org.apache.struts2.interceptor.debugging";
        if (param != null) {
            packages = param + " " + packages;
        }
        this.pathPrefixes = parse(packages);
    }
    

    /**
     * Cleans up the dispatcher
     *
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        if (dispatcher == null) {
            LOG.warn("something is seriously wrong, Dispatcher is not initialized (null) ");
        } else {
            dispatcher.cleanup();
        }
    }
    
    /**
     * Create a {@link Dispatcher}, this serves as a hook for subclass to overried
     * such that a custom {@link Dispatcher} could be created.
     *
     * @return Dispatcher
     */
    protected Dispatcher createDispatcher(FilterConfig filterConfig) {
        Map<String,String> params = new HashMap<String,String>();
        for (Enumeration e = filterConfig.getInitParameterNames(); e.hasMoreElements(); ) {
            String name = (String) e.nextElement();
            String value = filterConfig.getInitParameter(name);
            params.put(name, value);
        }
        return new Dispatcher(filterConfig.getServletContext(), params);
    }

    @Inject(StrutsConstants.STRUTS_SERVE_STATIC_CONTENT)
    public static void setServeStaticContent(String val) {
        serveStatic = "true".equals(val);
    }
    
    @Inject(StrutsConstants.STRUTS_SERVE_STATIC_BROWSER_CACHE)
    public static void setServeStaticBrowserCache(String val) {
        serveStaticBrowserCache = "true".equals(val);
    }
    
    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public static void setEncoding(String val) {
        encoding = val;
    }
    
    @Inject
    public static void setActionMapper(ActionMapper mapper) {
        actionMapper = mapper;
    }
    
    /**
     * Servlet 2.3 specifies that the servlet context can be retrieved from the session. Unfortunately, some versions of
     * WebLogic can only retrieve the servlet context from the filter config. Hence, this method enables subclasses to
     * retrieve the servlet context from other sources.
     *
     * @param session the HTTP session where, in Servlet 2.3, the servlet context can be retrieved
     * @return the servlet context.
     */
    protected ServletContext getServletContext() {
        return filterConfig.getServletContext();
    }

    /**
     * Gets this filter's configuration
     *
     * @return The filter config
     */
    protected FilterConfig getFilterConfig() {
        return filterConfig;
    }

    /**
     * Helper method that prepare <code>Dispatcher</code>
     * (by calling <code>Dispatcher.prepare(HttpServletRequest, HttpServletResponse)</code>)
     * following by wrapping and returning  the wrapping <code>HttpServletRequest</code> [ through
     * <code>dispatcher.wrapRequest(HttpServletRequest, ServletContext)</code> ]
     *
     * @param request
     * @param response
     * @return HttpServletRequest
     * @throws ServletException
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

            try {
                // Wrap request first, just in case it is multipart/form-data
                // parameters might not be accessible through before encoding (ww-1278)
                request = dispatcher.wrapRequest(request, getServletContext());
            } catch (IOException e) {
                String message = "Could not wrap servlet request with MultipartRequestWrapper!";
                LOG.error(message, e);
                throw new ServletException(message, e);
            }
        }
        else {
            dispatcher = du;
        }
        return request;
    }

    /**
     * Parses the list of packages
     *
     * @param packages A comma-delimited String
     * @return A string array of packages
     */
    protected String[] parse(String packages) {
        if (packages == null) {
            return null;
        }
        List<String> pathPrefixes = new ArrayList<String>();

        StringTokenizer st = new StringTokenizer(packages, ", \n\t");
        while (st.hasMoreTokens()) {
            String pathPrefix = st.nextToken().replace('.', '/');
            if (!pathPrefix.endsWith("/")) {
                pathPrefix += "/";
            }
            pathPrefixes.add(pathPrefix);
        }

        return (String[]) pathPrefixes.toArray(new String[pathPrefixes.size()]);
    }


    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        ServletContext servletContext = getServletContext();

        String timerKey = "FilterDispatcher_doFilter: ";
        try {
            UtilTimerStack.push(timerKey);

            request = prepareDispatcherAndWrapRequest(request, response);


            ActionMapper mapper = null;
            ActionMapping mapping = null;
            try {
                mapping = actionMapper.getMapping(request, dispatcher.getConfigurationManager());
            } catch (Exception ex) {
                LOG.error("error getting ActionMapping", ex);
                dispatcher.sendError(request, response, servletContext, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex);
                ActionContextCleanUp.cleanUp(req);
                return;
            }

            if (mapping == null) {
                // there is no action in this request, should we look for a static resource?
                String resourcePath = RequestUtils.getServletPath(request);

                if ("".equals(resourcePath) && null != request.getPathInfo()) {
                    resourcePath = request.getPathInfo();
                }

                if (serveStatic && resourcePath.startsWith("/struts")) {
                    String name = resourcePath.substring("/struts".length());
                    findStaticResource(name, response);
                } else {
                    // this is a normal request, let it pass through
                    chain.doFilter(request, response);
                }
                // The framework did its job here
                return;
            }


            try {
                dispatcher.serviceAction(request, response, servletContext, mapping);
            } finally {
                ActionContextCleanUp.cleanUp(req);
            }
        }
        finally {
            UtilTimerStack.pop(timerKey);
        }
    }

    /**
     * Finds a static resource
     *
     * @param name The resource name
     * @param response The request
     * @throws IOException If anything goes wrong
     */
    protected void findStaticResource(String name, HttpServletResponse response) throws IOException {
        if (!name.endsWith(".class")) {
            for (int i = 0; i < pathPrefixes.length; i++) {
                InputStream is = findInputStream(name, pathPrefixes[i]);
                if (is != null) {
                    // set the content-type header
                    String contentType = getContentType(name);
                    if (contentType != null) {
                        response.setContentType(contentType);
                    }

                    if (serveStaticBrowserCache) {
                        // set heading information for caching static content
                        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                        response.setHeader("Date",df.format(cal.getTime())+" GMT");
                        cal.add(Calendar.DAY_OF_MONTH,1);
                        response.setHeader("Expires",df.format(cal.getTime())+" GMT");
                        response.setHeader("Retry-After",df.format(cal.getTime())+" GMT");
                        response.setHeader("Cache-Control","public");
                        response.setHeader("Last-Modified",lastModified+" GMT");
                    }
                    else {
                        response.setHeader("Cache-Control","no-cache");
                        response.setHeader("Pragma","no-cache");
                        response.setHeader("Expires","-1");
                    }

                    try {
                        copy(is, response.getOutputStream());
                    } finally {
                        is.close();
                    }
                    return;
                }
            }
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * Determines the content type for the resource name
     *
     * @param name The resource name
     * @return The mime type
     */
    protected String getContentType(String name) {
        // NOT using the code provided activation.jar to avoid adding yet another dependency
        // this is generally OK, since these are the main files we server up
        if (name.endsWith(".js")) {
            return "text/javascript";
        } else if (name.endsWith(".css")) {
            return "text/css";
        } else if (name.endsWith(".html")) {
            return "text/html";
        } else if (name.endsWith(".txt")) {
            return "text/plain";
        } else if (name.endsWith(".gif")) {
            return "image/gif";
        } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (name.endsWith(".png")) {
            return "image/png";
        } else {
            return null;
        }
    }

    /**
     * Copies the from the input stream to the output stream
     *
     * @param input The input stream
     * @param output The output stream
     * @throws IOException If anything goes wrong
     */
    protected void copy(InputStream input, OutputStream output) throws IOException {
        final byte[] buffer = new byte[4096];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
    }

    /**
     * Looks for a static resource in the classpath
     *
     * @param name The resource name
     * @param packagePrefix The package prefix to use to locate the resource
     * @return The inputstream of the resource
     * @throws IOException If there is a problem locating the resource
     */
    protected InputStream findInputStream(String name, String packagePrefix) throws IOException {
        String resourcePath;
        if (packagePrefix.endsWith("/") && name.startsWith("/")) {
            resourcePath = packagePrefix + name.substring(1);
        } else {
            resourcePath = packagePrefix + name;
        }

        resourcePath = URLDecoder.decode(resourcePath, encoding);

        return ClassLoaderUtil.getResourceAsStream(resourcePath, getClass());
    }
}
