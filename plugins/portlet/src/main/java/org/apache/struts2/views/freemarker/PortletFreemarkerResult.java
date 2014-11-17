/*
 * $Id: PortletFreemarkerResult.java 564599 2007-08-10 14:05:17Z nilsga $
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
package org.apache.struts2.views.freemarker;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.StrutsResultSupport;
import org.apache.struts2.portlet.PortletConstants;
import org.apache.struts2.portlet.PortletPhase;
import org.apache.struts2.portlet.context.PortletActionContext;
import org.apache.struts2.views.util.ResourceUtil;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.ResourceResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

/**
 */
public class PortletFreemarkerResult extends StrutsResultSupport {

    private static final long serialVersionUID = -5570612389289887543L;

    protected ActionInvocation invocation;

    protected Configuration configuration;

    protected ObjectWrapper wrapper;
    protected FreemarkerManager freemarkerManager;

    /*
     * Struts results are constructed for each result execeution
     *
     * the current context is availible to subclasses via these protected fields
     */
    protected String location;

    private String pContentType = "text/html";

    public PortletFreemarkerResult() {
        super();
    }

    public PortletFreemarkerResult(String location) {
        super(location);
    }
    
    @Inject
    public void setFreemarkerManager(FreemarkerManager mgr) {
        this.freemarkerManager = mgr;
    }

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
        PortletPhase phase = PortletActionContext.getPhase();
        if (phase.isAction()) {
           executeActionResult(location, invocation);
        } else if (phase.isRender()) {
           executeRenderResult(location, invocation);
        } else if (phase.isResource()){
           executeResourceResult(location, invocation);
        }
    }

    private void executeActionResult(String location, ActionInvocation invocation) {
        ActionResponse res = PortletActionContext.getActionResponse();
        // View is rendered outside an action...uh oh...
        invocation.getInvocationContext().getSession().put(PortletConstants.RENDER_DIRECT_LOCATION, location);
        res.setRenderParameter(PortletConstants.ACTION_PARAM, "freemarkerDirect");
        res.setRenderParameter("location", location);
        res.setRenderParameter(PortletConstants.MODE_PARAM, PortletActionContext.getRequest().getPortletMode().toString());

    }

    private void executeRenderResult(String location, ActionInvocation invocation)
            throws TemplateException, IOException, PortletException {
        this.location = location;
        this.invocation = invocation;
        this.configuration = getConfiguration();
        this.wrapper = getObjectWrapper();

        HttpServletRequest req = ServletActionContext.getRequest();

        if (!location.startsWith("/")) {
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
    
    private void executeResourceResult(String location, ActionInvocation invocation)
             throws TemplateException, IOException, PortletException {
         this.location = location;
         this.invocation = invocation;
         this.configuration = getConfiguration();
         this.wrapper = getObjectWrapper();

         HttpServletRequest req = ServletActionContext.getRequest();

         if (!location.startsWith("/")) {
             String base = ResourceUtil.getResourceBase(req);
             location = base + "/" + location;
         }

         Template template = configuration.getTemplate(location, deduceLocale());
         TemplateModel model = createModel();
         // Give subclasses a chance to hook into preprocessing
         if (preTemplateProcess(template, model)) {
             try {
                 // Process the template
                 ResourceResponse response = PortletActionContext.getResourceResponse();
                 response.setContentType(pContentType);
                 template.process(model, response.getWriter());
             } finally {
                 // Give subclasses a chance to hook into postprocessing
                 postTemplateProcess(template, model);
             }
         }
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
        return freemarkerManager.getConfiguration(ServletActionContext.getServletContext());
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
     * <li>request - the HttpServletRequst object for direct access
     * <li>response - the HttpServletResponse object for direct access
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
        ValueStack stack = ServletActionContext.getContext().getValueStack();
        return freemarkerManager.buildTemplateModel(stack, invocation.getAction(), servletContext, request, response, wrapper);
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
    protected void postTemplateProcess(Template template, TemplateModel data) throws IOException {
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
    protected boolean preTemplateProcess(Template template, TemplateModel model) throws IOException {
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

