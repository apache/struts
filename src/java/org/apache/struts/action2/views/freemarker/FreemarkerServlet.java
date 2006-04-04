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
package org.apache.struts.action2.views.freemarker;

import org.apache.struts.action2.RequestUtils;
import org.apache.struts.action2.ServletActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;
import freemarker.template.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;


/**
 * @author CameronBraid
 * @deprecated Please use the FreemarkerResult result type instead
 */
public class FreemarkerServlet extends HttpServlet {

    protected Configuration configuration;


    /**
     *
     */
    public FreemarkerServlet() {
        super();
    }


    final public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("struts.freemarker.servlet", this);
        process(request, response);
    }

    final public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("struts.freemarker.servlet", this);
        process(request, response);
    }

    public void init() throws ServletException {
        try {
            configuration = createConfiguration();
        } catch (TemplateException e) {
            throw new ServletException("could not configure Freemarker", e);
        }
    }

    /**
     * This method is called from process() to obtain the
     * FreeMarker object wrapper object that this result will use
     * for adapting objects into
     * template models.. This is a hook that allows you
     * to custom-configure the wrapper object in a subclass.
     * <p/>
     * <b>
     * The default implementation returns @see Configuration#getObjectWrapper()
     * </b>
     */
    protected ObjectWrapper getObjectWrapper() {
        return configuration.getObjectWrapper();
    }

    protected Configuration createConfiguration() throws TemplateException {
        return FreemarkerManager.getInstance().getConfiguration(getServletContext());
    }

    protected TemplateModel createModel(ObjectWrapper wrapper, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) throws TemplateModelException {

        OgnlValueStack stack = ServletActionContext.getContext().getValueStack();
        Object action = null;
        if (ServletActionContext.getContext().getActionInvocation() != null) {
            action = ServletActionContext.getContext().getActionInvocation().getAction();
        }
        TemplateModel model = FreemarkerManager.getInstance().buildTemplateModel(stack, action, servletContext, request, response, wrapper);
        return model;
    }

    /**
     * Returns the locale used for the
     * {@link Configuration#getTemplate(String, Locale)} call.
     * The base implementation simply returns the locale setting of the
     * configuration. Override this method to provide different behaviour, i.e.
     * to use the locale indicated in the request.
     */
    protected Locale deduceLocale(String templatePath, HttpServletRequest request, HttpServletResponse response) {
        return configuration.getLocale();
    }

    /**
     * Called after the execution returns from template.process().
     * This is a generic hook you might use in subclasses to perform a specific
     * action after the template is processed. It will be invoked even if the
     * template processing throws an exception. By default does nothing.
     *
     * @param request  the actual HTTP request
     * @param response the actual HTTP response
     * @param template the template that was executed
     * @param data     the data that was passed to the template
     */
    protected void postTemplateProcess(HttpServletRequest request, HttpServletResponse response, Template template, TemplateModel data) throws ServletException, IOException {
    }

    //    /**
    //     * If the parameter "nocache" was set to true, generate a set of headers
    //     * that will advise the HTTP client not to cache the returned page.
    //     */
    //    private void setBrowserCachingPolicy(HttpServletResponse response)
    //    {
    //        if (nocache)
    //        {
    //            // HTTP 1.1 browsers should defeat caching on this header
    //            response.setHeader("Cache-Control", "no-cache");
    //            // HTTP 1.0 browsers should defeat caching on this header
    //            response.setHeader("Pragma", "no-cache");
    //            // Last resort for those that ignore all of the above
    //            response.setHeader("Expires", EXPIRATION_DATE);
    //        }
    //    }

    /**
     * Called before the execution is passed to template.process().
     * This is a generic hook you might use in subclasses to perform a specific
     * action before the template is processed. By default does nothing.
     * A typical action to perform here is to inject application-specific
     * objects into the model root
     *
     * @param request  the actual HTTP request
     * @param response the actual HTTP response
     * @param template the template that will get executed
     * @param data     the data that will be passed to the template
     * @return true to process the template, false to suppress template processing.
     */
    protected boolean preTemplateProcess(HttpServletRequest request, HttpServletResponse response, Template template, TemplateModel data) throws ServletException, IOException {
        return true;
    }

    /**
     * Maps the request URL to a template path that is passed to
     * {@link Configuration#getTemplate(String, Locale)}. You can override it
     * (i.e. to provide advanced rewriting capabilities), but you are strongly
     * encouraged to call the overridden method first, then only modify its
     * return value.
     *
     * @param request the currently processed request
     * @return a String representing the template path
     */
    protected String requestUrlToTemplatePath(HttpServletRequest request) {
        // First, see if it is an included request
        String includeServletPath = (String) request.getAttribute("javax.servlet.include.servlet_path");

        if (includeServletPath != null) {
            // Try path info; only if that's null (servlet is mapped to an
            // URL extension instead of to prefix) use servlet path.
            String includePathInfo = (String) request.getAttribute("javax.servlet.include.path_info");

            return (includePathInfo == null) ? includeServletPath : includePathInfo;
        }

        // Seems that the servlet was not called as the result of a
        // RequestDispatcher.include(...). Try pathInfo then servletPath again,
        // only now directly on the request object:
        String path = request.getPathInfo();

        if (path != null) {
            return path;
        }

        path = RequestUtils.getServletPath(request);

        if (path != null) {
            return path;
        }

        // Seems that it is a servlet mapped with prefix, and there was no extra path info.
        return "";
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = requestUrlToTemplatePath(request);

        Template template = null;

        try {
            template = configuration.getTemplate(path, deduceLocale(path, request, response));
        } catch (FileNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        }

        Object attrContentType = template.getCustomAttribute("content_type");

        if (attrContentType != null) {
            response.setContentType(attrContentType.toString());
        } else {
            response.setContentType("text/html; charset=" + template.getEncoding());
        }

        //        // Set cache policy
        //        setBrowserCachingPolicy(response);
        ServletContext servletContext = getServletContext();

        try {
            TemplateModel model = createModel(getObjectWrapper(), servletContext, request, response);

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
        } catch (TemplateException te) {
            // only throw a servlet exception if not a debug handler
            // this is what the original freemarker.ext.servlet.FreemarkerServlet does
            if ((configuration.getTemplateExceptionHandler() != freemarker.template.TemplateExceptionHandler.HTML_DEBUG_HANDLER) && (configuration.getTemplateExceptionHandler() != freemarker.template.TemplateExceptionHandler.DEBUG_HANDLER))
            {
                throw new ServletException(te);
            }
        }
    }
}
