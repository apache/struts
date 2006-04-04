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

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action2.ServletActionContext;
import org.apache.struts.action2.dispatcher.StrutsResultSupport;
import org.apache.struts.action2.portlet.PortletActionConstants;
import org.apache.struts.action2.portlet.context.PortletActionContext;
import org.apache.struts.action2.views.util.ResourceUtil;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.util.OgnlValueStack;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * @author CameronBraid
 */
public class PortletFreemarkerResult extends StrutsResultSupport {

	private static final long serialVersionUID = -5570612389289887543L;

	protected ActionInvocation invocation;

    protected Configuration configuration;

    protected ObjectWrapper wrapper;

    /*
     * Struts results are constructed for each result execeution
     * 
     * the current context is availible to subclasses via these protected fields
     */
    protected String location;

    private String pContentType = "text/html";

    public void setContentType(String aContentType) {
        pContentType = aContentType;
    }

    /**
     * allow parameterization of the contentType the default being text/html
     */
    public String getContentType() {
        return pContentType;
    }

    /**
     * Execute this result, using the specified template location. <p/>The
     * template location has already been interoplated for any variable
     * substitutions <p/>this method obtains the freemarker configuration and
     * the object wrapper from the provided hooks. It them implements the
     * template processing workflow by calling the hooks for preTemplateProcess
     * and postTemplateProcess
     */
    public void doExecute(String location, ActionInvocation invocation)
            throws IOException, TemplateException, PortletException {
        if (PortletActionContext.isEvent()) {
            executeActionResult(location, invocation);
        } else if (PortletActionContext.isRender()) {
            executeRenderResult(location, invocation);
        }
    }

    /**
     * @param location
     * @param invocation
     */
    private void executeActionResult(String location,
                                     ActionInvocation invocation) {
        ActionResponse res = PortletActionContext.getActionResponse();
        // View is rendered outside an action...uh oh...
        res.setRenderParameter(PortletActionConstants.ACTION_PARAM, "freemarkerDirect");
        res.setRenderParameter("location", location);
        res.setRenderParameter(PortletActionConstants.MODE_PARAM, PortletActionContext
                .getRequest().getPortletMode().toString());

    }

    /**
     * @param location
     * @param invocation
     * @throws TemplateException
     * @throws IOException
     * @throws TemplateModelException
     */
    private void executeRenderResult(String location,
                                     ActionInvocation invocation) throws TemplateException, IOException,
            TemplateModelException, PortletException {
        prepareServletActionContext();
        this.location = location;
        this.invocation = invocation;
        this.configuration = getConfiguration();
        this.wrapper = getObjectWrapper();

        HttpServletRequest req = ServletActionContext.getRequest();
        HttpServletResponse res = ServletActionContext.getResponse();

        if (!location.startsWith("/")) {
            ActionContext ctx = invocation.getInvocationContext();
            String base = ResourceUtil.getResourceBase(req);
            location = base + "/" + location;
        }

        Template template = configuration.getTemplate(location, deduceLocale());
        TemplateModel model = createModel();
        // Give subclasses a chance to hook into preprocessing
        if (preTemplateProcess(template, model)) {
            try {
                // Process the template
                PortletActionContext.getRenderResponse().setContentType(pContentType);
                template.process(model, getWriter());
            } finally {
                // Give subclasses a chance to hook into postprocessing
                postTemplateProcess(template, model);
            }
        }
    }

    /**
     *  
     */
    private void prepareServletActionContext() throws PortletException,
            IOException {
        PortletRequestDispatcher disp = PortletActionContext.getPortletConfig()
                .getPortletContext().getNamedDispatcher("preparator");
        disp.include(PortletActionContext.getRenderRequest(),
                PortletActionContext.getRenderResponse());
    }

    /**
     * This method is called from {@link #doExecute(String, ActionInvocation)}
     * to obtain the FreeMarker configuration object that this result will use
     * for template loading. This is a hook that allows you to custom-configure
     * the configuration object in a subclass, or to fetch it from an IoC
     * container. <p/><b>The default implementation obtains the configuration
     * from the ConfigurationManager instance. </b>
     */
    protected Configuration getConfiguration() throws TemplateException {
        return FreemarkerManager.getInstance().getConfiguration(
                ServletActionContext.getServletContext());
    }

    /**
     * This method is called from {@link #doExecute(String, ActionInvocation)}
     * to obtain the FreeMarker object wrapper object that this result will use
     * for adapting objects into template models. This is a hook that allows you
     * to custom-configure the wrapper object in a subclass. <p/><b>The default
     * implementation returns {@link Configuration#getObjectWrapper()}</b>
     */
    protected ObjectWrapper getObjectWrapper() {
        return configuration.getObjectWrapper();
    }

    /**
     * The default writer writes directly to the response writer.
     */
    protected Writer getWriter() throws IOException {
        return PortletActionContext.getRenderResponse().getWriter();
    }

    /**
     * Build the instance of the ScopesHashModel, including JspTagLib support
     * <p/>Objects added to the model are <p/>
     * <ul>
     * <li>Application - servlet context attributes hash model
     * <li>JspTaglibs - jsp tag lib factory model
     * <li>Request - request attributes hash model
     * <li>Session - session attributes hash model
     * <li>req - the HttpServletRequst object for direct access
     * <li>res - the HttpServletResponse object for direct access
     * <li>stack - the OgnLValueStack instance for direct access
     * <li>ognl - the instance of the OgnlTool
     * <li>action - the action itself
     * <li>exception - optional : the JSP or Servlet exception as per the
     * servlet spec (for JSP Exception pages)
     * <li>struts - instance of the StrutsUtil class
     * </ul>
     */
    protected TemplateModel createModel() throws TemplateModelException {
        ServletContext servletContext = ServletActionContext
                .getServletContext();
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        OgnlValueStack stack = ServletActionContext.getContext()
                .getValueStack();
        return FreemarkerManager.getInstance().buildTemplateModel(stack,
                invocation.getAction(), servletContext, request, response,
                wrapper);
    }

    /**
     * Returns the locale used for the
     * {@link Configuration#getTemplate(String, Locale)}call. The base
     * implementation simply returns the locale setting of the configuration.
     * Override this method to provide different behaviour,
     */
    protected Locale deduceLocale() {
        return configuration.getLocale();
    }

    /**
     * the default implementation of postTemplateProcess applies the contentType
     * parameter
     */
    protected void postTemplateProcess(Template template, TemplateModel data)
            throws IOException {
    }

    /**
     * Called before the execution is passed to template.process(). This is a
     * generic hook you might use in subclasses to perform a specific action
     * before the template is processed. By default does nothing. A typical
     * action to perform here is to inject application-specific objects into the
     * model root
     *
     * @return true to process the template, false to suppress template
     *         processing.
     */
    protected boolean preTemplateProcess(Template template, TemplateModel model)
            throws IOException {
        Object attrContentType = template.getCustomAttribute("content_type");

        if (attrContentType != null) {
            ServletActionContext.getResponse().setContentType(
                    attrContentType.toString());
        } else {
            String contentType = getContentType();

            if (contentType == null) {
                contentType = "text/html";
            }

            String encoding = template.getEncoding();

            if (encoding != null) {
                contentType = contentType + "; charset=" + encoding;
            }

            ServletActionContext.getResponse().setContentType(contentType);
        }

        return true;
    }
}

