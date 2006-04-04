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
package org.apache.struts.action2.dispatcher;

import com.opensymphony.util.ClassLoaderUtil;
import org.apache.struts.action2.RequestUtils;
import org.apache.struts.action2.StrutsConstants;
import org.apache.struts.action2.StrutsStatics;
import org.apache.struts.action2.config.Configuration;
import org.apache.struts.action2.dispatcher.mapper.ActionMapper;
import org.apache.struts.action2.dispatcher.mapper.ActionMapperFactory;
import org.apache.struts.action2.dispatcher.mapper.ActionMapping;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.interceptor.component.ComponentConfiguration;
import com.opensymphony.xwork.interceptor.component.ComponentManager;
import com.opensymphony.xwork.interceptor.component.DefaultComponentManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.text.SimpleDateFormat;

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
 * <li>Kicking off XWork's IoC for the request lifecycle</li>
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
 * <li>org.apache.struts.action2.static</li>
 *
 * <li>template</li>
 *
 * </ul>
 *
 * <p/> This means that you can simply request /struts/xhtml/styles.css and the XHTML UI theme's default stylesheet
 * will be returned. Likewise, many of the AJAX UI components require various JavaScript files, which are found in the
 * org.apache.struts.action2.static package. If you wish to add additional packages to be searched, you can add a comma
 * separated (space, tab and new line will do as well) list in the filter init parameter named "packages". <b>Be
 * careful</b>, however, to expose any packages that may have sensitive information, such as properties file with
 * database access credentials.
 *
 * <p/> <b>Kicking off XWork's IoC for the request lifecycle</b>
 *
 * <p/> This filter also kicks off the XWork IoC request scope, provided that you are using XWork's IoC. All you have to
 * do to get started with XWork's IoC is add a components.xml file to WEB-INF/classes and properly set up the {@link
 * org.apache.struts.action2.lifecycle.LifecycleListener} in web.xml. See the IoC docs for more information. <p/>
 *
 * @author Patrick Lightbody
 * @see org.apache.struts.action2.lifecycle.LifecycleListener
 * @see ActionMapper
 * @see ActionContextCleanUp
 * @since 2.2
 */
public class FilterDispatcher implements Filter, StrutsStatics {
    private static final Log LOG = LogFactory.getLog(FilterDispatcher.class);

    protected FilterConfig filterConfig;
    protected String[] pathPrefixes;

    private SimpleDateFormat df = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss");
    private final Calendar lastModifiedCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    private final String lastModified = df.format(lastModifiedCal.getTime());

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    public void destroy() {
    	DispatcherUtils du = DispatcherUtils.getInstance(); // should not be null as it is initialized in init(FilterConfig)
    	if (du == null) {
    		LOG.warn("something is seriously wrong, DispatcherUtil is not initialized (null) ");
    	}
    	du.cleanup();
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        String param = filterConfig.getInitParameter("packages");
        String packages = "org.apache.struts.action2.static template org.apache.struts.action2.interceptor.debugging";
        if (param != null) {
            packages = param + " " + packages;
        }
        this.pathPrefixes = parse(packages);
        DispatcherUtils.initialize(filterConfig.getServletContext());
    }

    protected String[] parse(String packages) {
        if (packages == null) {
            return null;
        }
        List pathPrefixes = new ArrayList();

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


    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // prepare the request no matter what - this ensures that the proper character encoding
        // is used before invoking the mapper (see WW-9127)
        DispatcherUtils du = DispatcherUtils.getInstance();
        du.prepare(request, response);

        ActionMapper mapper = ActionMapperFactory.getMapper();
        ActionMapping mapping = mapper.getMapping(request);

        if (mapping == null) {
            // there is no action in this request, should we look for a static resource?
            String resourcePath = RequestUtils.getServletPath(request);

            if ("".equals(resourcePath) && null != request.getPathInfo()) {
                resourcePath = request.getPathInfo();
            }

            if ("true".equals(Configuration.get(StrutsConstants.STRUTS_SERVE_STATIC_CONTENT)) 
                    && resourcePath.startsWith("/struts")) {
                String name = resourcePath.substring("/struts".length());
                findStaticResource(name, response);
            } else {
                // this is a normal request, let it pass through
                chain.doFilter(request, response);
            }
            // WW did its job here
            return;
        }


        Object o = null;
        ServletContext servletContext = filterConfig.getServletContext();
        try {

            setupContainer(request);
            o = beforeActionInvocation(request, servletContext);

            try {
                request = du.wrapRequest(request, servletContext);
            } catch (IOException e) {
                String message = "Could not wrap servlet request with MultipartRequestWrapper!";
                LOG.error(message, e);
                throw new ServletException(message, e);
            }
            du.serviceAction(request, response, servletContext, mapping);
        } finally {
            afterActionInvocation(request, servletContext, o);
            ActionContextCleanUp.cleanUp(req);
        }
    }

    protected void afterActionInvocation(HttpServletRequest request, Object o, Object o1) {
        // nothing by default, but a good hook for scoped ioc integration
    }

    protected Object beforeActionInvocation(HttpServletRequest request, ServletContext servletContext) {
        // nothing by default, but a good hook for scoped ioc integration
        return null;
    }

    protected void setupContainer(HttpServletRequest request) {
        ComponentManager container = null;
        HttpSession session = request.getSession(false);
        ComponentManager fallback = null;
        if (session != null) {
            fallback = (ComponentManager) session.getAttribute(ComponentManager.COMPONENT_MANAGER_KEY);
        }

        ServletContext servletContext = getServletContext(session);
        if (fallback == null) {
            fallback = (ComponentManager) servletContext.getAttribute(ComponentManager.COMPONENT_MANAGER_KEY);
        }

        if (fallback != null) {
            container = createComponentManager();
            container.setFallback(fallback);
        }

        ComponentConfiguration config = (ComponentConfiguration) servletContext.getAttribute("ComponentConfiguration");
        if (config != null) {
            if (container == null) {
                container = createComponentManager();
            }

            config.configure(container, "request");
            request.setAttribute(ComponentManager.COMPONENT_MANAGER_KEY, container);
        }
    }

    /**
     * Servlet 2.3 specifies that the servlet context can be retrieved from the session. Unfortunately, some versions of
     * WebLogic can only retrieve the servlet context from the filter config. Hence, this method enables subclasses to
     * retrieve the servlet context from other sources.
     *
     * @param session the HTTP session where, in Servlet 2.3, the servlet context can be retrieved
     * @return the servlet context.
     */
    protected ServletContext getServletContext(HttpSession session) {
        return filterConfig.getServletContext();
    }

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

                    // set heading information for caching static content
                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                    response.setHeader("Date",df.format(cal.getTime())+" GMT");
                    cal.add(Calendar.DAY_OF_MONTH,1);
                    response.setHeader("Expires",df.format(cal.getTime())+" GMT");
                    response.setHeader("Retry-After",df.format(cal.getTime())+" GMT");
                    response.setHeader("Cache-Control","public");
                    response.setHeader("Last-Modified",lastModified+" GMT");

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

    private String getContentType(String name) {
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

    protected void copy(InputStream input, OutputStream output) throws IOException {
        final byte[] buffer = new byte[4096];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
    }

    protected InputStream findInputStream(String name, String packagePrefix) throws IOException {
        String resourcePath;
        if (packagePrefix.endsWith("/") && name.startsWith("/")) {
            resourcePath = packagePrefix + name.substring(1);
        } else {
            resourcePath = packagePrefix + name;
        }

        String enc = (String) Configuration.get(StrutsConstants.STRUTS_I18N_ENCODING);
        resourcePath = URLDecoder.decode(resourcePath, enc);

        return ClassLoaderUtil.getResourceAsStream(resourcePath, getClass());
    }

    /**
     * handle .. chars here and other URL hacks
     */
    protected boolean checkUrl(URL url, String rawResourcePath) {

        // ignore folder resources - they provide streams too ! dunno why :)
        if (url.getPath().endsWith("/")) {
            return false;
        }

        // check for parent path access
        // NOTE : most servlet containers shoudl resolve .. chars in the request url anyway
        if (url.toExternalForm().indexOf(rawResourcePath) == -1) {
            return false;
        }

        return true;
    }

    /**
     * Returns a new <tt>DefaultComponentManager</tt> instance. This method is useful for developers wishing to subclass
     * this class and provide a different implementation of <tt>DefaultComponentManager</tt>.
     *
     * @return a new <tt>DefaultComponentManager</tt> instance.
     */
    protected DefaultComponentManager createComponentManager() {
        return new DefaultComponentManager();
    }
}
