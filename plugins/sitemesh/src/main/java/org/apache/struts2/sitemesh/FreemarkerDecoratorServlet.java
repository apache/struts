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
package org.apache.struts2.sitemesh;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import freemarker.core.InvalidReferenceException;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.StrutsRequestWrapper;
import org.apache.struts2.dispatcher.ng.listener.StrutsListener;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.freemarker.ScopesHashModel;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Locale;

/**
 * <p>This is a SiteMesh FreeMarker view servlet.</p>
 * <p/>
 * <p>It overrides the SiteMesh servlet to rely on the
 * Freemarker Manager in Struts instead of creating it's
 * own manager</p>
 */
public class FreemarkerDecoratorServlet extends freemarker.ext.servlet.FreemarkerServlet {
    private static final com.opensymphony.xwork2.util.logging.Logger LOG = LoggerFactory.getLogger(FreemarkerDecoratorServlet.class);


    protected FreemarkerManager freemarkerManager;


    public static final long serialVersionUID = -2440216393145762479L;


/*
    private static final String EXPIRATION_DATE;

    static {
        // Generate expiration date that is one year from now in the past
        GregorianCalendar expiration = new GregorianCalendar();
        expiration.roll(Calendar.YEAR, -1);
        SimpleDateFormat httpDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", java.util.Locale.US);
        EXPIRATION_DATE = httpDate.format(expiration.getTime());
    }
*/
    protected String templatePath;
    protected boolean nocache;
    protected boolean debug;
    protected Configuration config;
    private ObjectWrapper wrapper;
    private String contentType;
    private boolean noCharsetInContentType;

    public void init() throws ServletException {
        try {
            Dispatcher dispatcher = (Dispatcher) getServletContext().getAttribute(StrutsStatics.SERVLET_DISPATCHER);
            if (dispatcher == null)
                throw new IllegalStateException("Unable to find the Dispatcher in the Servlet Context. Is '" + StrutsListener.class.getName() + "' missing in web.xml?");

            freemarkerManager = dispatcher.getContainer().getInstance(FreemarkerManager.class);
            config = createConfiguration();

            // Process object_wrapper init-param out of order:
            wrapper = config.getObjectWrapper();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Using object wrapper of class " + wrapper.getClass().getName());
            }

            // Process all other init-params:
            Enumeration initpnames = getServletConfig().getInitParameterNames();
            while (initpnames.hasMoreElements()) {
                String name = (String) initpnames.nextElement();
                String value = getInitParameter(name);
                if (name == null) {
                    throw new ServletException("init-param without param-name. " + "Maybe the web.xml is not well-formed?");
                }
                if (value == null) {
                    throw new ServletException("init-param without param-value. " + "Maybe the web.xml is not well-formed?");
                }

                // template path is already handled!
                if (!FreemarkerManager.INITPARAM_TEMPLATE_PATH.equals(name)) freemarkerManager.addSetting(name, value);
            }
            nocache = freemarkerManager.getNocache();
            debug = freemarkerManager.getDebug();
            contentType = freemarkerManager.getContentType();
            noCharsetInContentType = freemarkerManager.getNoCharsetInContentType();
        } catch (ServletException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Give chance to subclasses to perform preprocessing
        if (preprocessRequest(request, response)) {
            return;
        }

        String path = requestUrlToTemplatePath(request);

        if (debug) {
            log("Requested template: " + path);
        }

        Template template = null;
        try {
            template = config.getTemplate(path, deduceLocale(path, request, response));
        } catch (FileNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Object attrContentType = template.getCustomAttribute("content_type");
        if (attrContentType != null) {
            response.setContentType(attrContentType.toString());
        } else {
            if (noCharsetInContentType) {
                response.setContentType(contentType + "; charset=" + template.getEncoding());
            } else {
                response.setContentType(contentType);
            }
        }

        // Set cache policy
        setBrowserCachingPolicy(response);

        ServletContext servletContext = getServletContext();
        ScopesHashModel model = (ScopesHashModel) request.getAttribute(freemarkerManager.ATTR_TEMPLATE_MODEL);
        try {
            if (model == null) {
                ActionContext ctx = ServletActionContext.getActionContext(request);
                model = freemarkerManager.buildTemplateModel(ctx.getValueStack(), ctx.getActionInvocation().getAction(), servletContext, request, response, wrapper);
            }

            // Give subclasses a chance to hook into preprocessing
            if (preTemplateProcess(request, response, template, model)) {
                try {
                    // Process the template
                    template.process(model, response.getWriter());
                } finally {
                    // Give subclasses a chance to hook into postprocessing
                    postTemplateProcess(request, response, template, model);
                }
            }
        } catch (InvalidReferenceException x) {
            // this exception is thrown if there is an error processing a reference.  We want to report these!
            HttpServletRequest req = ((StrutsRequestWrapper) ActionContext.getContext().get("com.opensymphony.xwork2.dispatcher.HttpServletRequest"));
            String resultCode = ActionContext.getContext().getActionInvocation().getResultCode();
            if (req == null) req = request;

            StringBuilder msgBuf = new StringBuilder("Error applying freemarker template to\n       request: ");
            msgBuf.append(req.getRequestURL());
            if (req.getQueryString() != null) msgBuf.append("?").append(req.getQueryString());
            msgBuf.append(" with resultCode: ").append(resultCode).append(".\n\n").append(x.getMessage());
            String msg = msgBuf.toString();
            LOG.error(msg, x);

            ServletException e = new ServletException(msg, x);
            // Attempt to set init cause, but don't freak out if the method
            // is not available (i.e. pre-1.4 JRE). This is required as the
            // constructor-passed throwable won't show up automatically in
            // stack traces.
            try {
                e.getClass().getMethod("initCause", new Class[]{Throwable.class}).invoke(e, new Object[]{x});
            } catch (Exception ex) {
                // Can't set init cause, we're probably running on a pre-1.4
                // JDK, oh well...
            }
            throw e;
        } catch (TemplateException te) {
            if (config.getTemplateExceptionHandler().getClass().getName().indexOf("Debug") != -1) {
                this.log("Error executing FreeMarker template", te);
            } else {
                ServletException e = new ServletException("Error executing FreeMarker template", te);
                // Attempt to set init cause, but don't freak out if the method
                // is not available (i.e. pre-1.4 JRE). This is required as the
                // constructor-passed throwable won't show up automatically in
                // stack traces.
                try {
                    e.getClass().getMethod("initCause", new Class[]{Throwable.class}).invoke(e, new Object[]{te});
                } catch (Exception ex) {
                    // Can't set init cause, we're probably running on a pre-1.4
                    // JDK, oh well...
                }
                throw e;
            }
        }
    }


    /**
     * Returns the locale used for the
     * {@link Configuration#getTemplate(String, Locale)} call.
     * The base implementation simply returns the locale setting of the
     * configuration. Override this method to provide different behaviour, i.e.
     * to use the locale indicated in the request.
     */
    protected Locale deduceLocale(String templatePath, HttpServletRequest request, HttpServletResponse response) {
        return config.getLocale();
    }


    /**
     * Create the instance of the freemarker Configuration object.
     * <p/>
     * this implementation
     * <ul>
     * <li>obtains the default configuration from Configuration.getDefaultConfiguration()
     * <li>sets up template loading from a ClassTemplateLoader and a WebappTemplateLoader
     * <li>sets up the object wrapper to be the BeansWrapper
     * <li>loads settings from the classpath file /freemarker.properties
     * </ul>
     */
    protected freemarker.template.Configuration createConfiguration() {
        return freemarkerManager.getConfiguration(this.getServletContext());
    }

    /**
     * Called before the execution is passed to template.process().
     * This is a generic hook you might use in subclasses to perform a specific
     * action before the template is processed. By default does nothing.
     * A typical action to perform here is to inject application-specific
     * objects into the model root
     * <p/>
     * <p>Example: Expose the Serlvet context path as "baseDir" for all templates:
     * <p/>
     * <pre>
     *    ((SimpleHash) data).put("baseDir", request.getContextPath() + "/");
     *    return true;
     * </pre>
     *
     * @param request  the actual HTTP request
     * @param response the actual HTTP response
     * @param template the template that will get executed
     * @return true to process the template, false to suppress template processing.
     * @see freemarker.ext.servlet.FreemarkerServlet#preTemplateProcess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, freemarker.template.Template, freemarker.template.TemplateModel)
     */
    protected boolean preTemplateProcess(HttpServletRequest request, HttpServletResponse response, Template template, TemplateModel templateModel) throws ServletException, IOException {
        boolean result = super.preTemplateProcess(request, response, template, templateModel);

        SimpleHash hash = (SimpleHash) templateModel;

        HTMLPage htmlPage = (HTMLPage) request.getAttribute(RequestConstants.PAGE);

        String title, body, head;

        if (htmlPage == null) {
            title = "No Title";
            body = "No Body";
            head = "<!-- No head -->";
        } else {
            title = htmlPage.getTitle();

            StringWriter buffer = new StringWriter();
            htmlPage.writeBody(buffer);
            body = buffer.toString();

            buffer = new StringWriter();
            htmlPage.writeHead(buffer);
            head = buffer.toString();

            hash.put("page", htmlPage);
        }

        hash.put("title", title);
        hash.put("body", body);
        hash.put("head", head);
        hash.put("base", request.getContextPath());

        /*
          Factory factory = Factory.getInstance(new Config(getServletConfig()));
          Decorator decorator = factory.getDecoratorMapper().getDecorator(request, htmlPage);
          -> decorator.getPage()
          */

        return result;
    }

    /**
     * If the parameter "nocache" was set to true, generate a set of headers
     * that will advise the HTTP client not to cache the returned page.
     */
    private void setBrowserCachingPolicy(HttpServletResponse res) {
        if (nocache) {
            // HTTP/1.1 + IE extensions
            res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, " + "post-check=0, pre-check=0");
            // HTTP/1.0
            res.setHeader("Pragma", "no-cache");
            // Last resort for those that ignore all of the above
            res.setHeader("Expires", freemarkerManager.EXPIRATION_DATE);
        }
    }
}
