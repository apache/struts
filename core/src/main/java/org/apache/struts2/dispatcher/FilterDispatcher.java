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
 * @see ActionMapper
 * @see ActionContextCleanUp
 *
 * @version $Date$ $Id$
 */
public class FilterDispatcher extends AbstractFilter implements StrutsStatics, Filter {

    /**
     * Provide a logging instance.
     */
    private static final Log LOG = LogFactory.getLog(FilterDispatcher.class);

    /**
     * Store set of path prefixes to use with static resources.
     */
    private String[] pathPrefixes;

    /**
     * Provide a formatted date for setting heading information when caching static content.
     */
    private SimpleDateFormat df = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss");

    /**
     * Provide a formatted date for setting heading information when caching static content.
     */
    private final Calendar lastModifiedCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

    /**
     * Provide a formatted date for setting heading information when caching static content.
     */
    private final String lastModified = df.format(lastModifiedCal.getTime());

    /**
     * Store state of StrutsConstants.STRUTS_SERVE_STATIC_CONTENT setting.
     */
    private static boolean serveStatic;

    /**
     * Store state of StrutsConstants.STRUTS_SERVE_STATIC_BROWSER_CACHE setting.
     */
    private static boolean serveStaticBrowserCache;

    /**
     * Store state of StrutsConstants.STRUTS_I18N_ENCODING setting.
     */
    private static String encoding;

    /**
     * Provide ActionMapper instance, set by injection.
     */
    private static ActionMapper actionMapper;


    /**
     * Initializes the filter by creating a default dispatcher
     * and setting the default packages for static resources.
     *
     * @param filterConfig The filter configuration
     */
    public void postInit(FilterConfig filterConfig) throws ServletException {
        String param = filterConfig.getInitParameter("packages");
        String packages = "org.apache.struts2.static template org.apache.struts2.interceptor.debugging";
        if (param != null) {
            packages = param + " " + packages;
        }
        this.pathPrefixes = parse(packages);
    }


    /**
     * Modify state of StrutsConstants.STRUTS_SERVE_STATIC_CONTENT setting.
     * @param val New setting
     */
    @Inject(StrutsConstants.STRUTS_SERVE_STATIC_CONTENT)
    public static void setServeStaticContent(String val) {
        serveStatic = "true".equals(val);
    }
    
    /**
     * Modify state of StrutsConstants.STRUTS_SERVE_STATIC_BROWSER_CACHE setting.
     * @param val New setting
     */
    @Inject(StrutsConstants.STRUTS_SERVE_STATIC_BROWSER_CACHE)
    public static void setServeStaticBrowserCache(String val) {
        serveStaticBrowserCache = "true".equals(val);
    }
    
    /**
     * Modify state of StrutsConstants.STRUTS_I18N_ENCODING setting.
     * @param val New setting
     */
    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public static void setEncoding(String val) {
        encoding = val;
    }
    
    /**
     * Modify ActionMapper instance.
     * @param mapper New instance
     */
    @Inject
    public static void setActionMapper(ActionMapper mapper) {
        actionMapper = mapper;
    }
    
    /**
     * Create a string array from a comma-delimited list of packages.
     *
     * @param packages A comma-delimited String listing packages
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

        return pathPrefixes.toArray(new String[pathPrefixes.size()]);
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

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        ServletContext servletContext = getServletContext();
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("doFilter");
        }

        String timerKey = "FilterDispatcher_doFilter: ";
        try {
            UtilTimerStack.push(timerKey);
            request = prepareDispatcherAndWrapRequest(request, response);
            ActionMapping mapping;
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
     * Locate a static resource and copy directly to the response,
     * setting the appropriate caching headers. 
     *
     * @param name The resource name
     * @param response The request
     * @throws IOException If anything goes wrong
     */
    protected void findStaticResource(String name, HttpServletResponse response) throws IOException {
        if (!name.endsWith(".class")) {
            for (String pathPrefix : pathPrefixes) {
                InputStream is = findInputStream(name, pathPrefix);
                if (is != null) {
                    // set the content-type header
                    String contentType = getContentType(name);
                    if (contentType != null) {
                        response.setContentType(contentType);
                    }

                    if (serveStaticBrowserCache) {
                        // set heading information for caching static content
                        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                        response.setHeader("Date", df.format(cal.getTime()) + " GMT");
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        response.setHeader("Expires", df.format(cal.getTime()) + " GMT");
                        response.setHeader("Retry-After", df.format(cal.getTime()) + " GMT");
                        response.setHeader("Cache-Control", "public");
                        response.setHeader("Last-Modified", lastModified + " GMT");
                    } else {
                        response.setHeader("Cache-Control", "no-cache");
                        response.setHeader("Pragma", "no-cache");
                        response.setHeader("Expires", "-1");
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
     * Determine the content type for the resource name.
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
     * Copy bytes from the input stream to the output stream.
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
     * Look for a static resource in the classpath.
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
