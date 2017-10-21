/*
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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.result.StrutsResultSupport;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

/**
 * Renders a view using the Freemarker template engine.
 */
public class FreemarkerResult extends StrutsResultSupport {

    private static final long serialVersionUID = -3778230771704661631L;

    private static final Logger LOG = LogManager.getLogger(FreemarkerResult.class);

    protected ActionInvocation invocation;
    protected Configuration configuration;
    protected ObjectWrapper wrapper;
    protected FreemarkerManager freemarkerManager;
    private Writer writer;
    private Boolean writeIfCompleted = null;

    /*
     * Struts results are constructed for each result execution
     *
     * the current context is available to subclasses via these protected fields
     */
    protected String location;
    private String pContentType = "text/html";
    private static final String PARENT_TEMPLATE_WRITER = FreemarkerResult.class.getName() +  ".parentWriter";

    public FreemarkerResult() {
        super();
    }

    public FreemarkerResult(String location) {
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
     * allow parameterization of the contentType
     * the default being text/html
     *
     * @return the content type
     */
    public String getContentType() {
        return pContentType;
    }

    /**
     * Execute this result, using the specified template locationArg.
     * <p>
     * The template locationArg has already been interpolated for any variable substitutions
     * </p>
     * <p>
     * this method obtains the freemarker configuration and the object wrapper from the provided hooks.
     * It them implements the template processing workflow by calling the hooks for
     * preTemplateProcess and postTemplateProcess
     * </p>
     *
     * @param locationArg location argument
     * @param invocation the action invocation
     *
     * @throws IOException in case of IO errors
     * @throws TemplateException in case of freemarker template errors
     */
    public void doExecute(String locationArg, ActionInvocation invocation) throws IOException, TemplateException {
        this.location = locationArg;
        this.invocation = invocation;
        this.configuration = getConfiguration();
        this.wrapper = getObjectWrapper();

        ActionContext ctx = invocation.getInvocationContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get(ServletActionContext.HTTP_REQUEST);

        String absoluteLocation;
        if (location.startsWith("/")) {
            absoluteLocation = location; 
        } else { 
            String namespace = invocation.getProxy().getNamespace();
            if (namespace == null || namespace.length() == 0 || namespace.equals("/")) {
                absoluteLocation = "/" + location;
            } else if (namespace.startsWith("/")) {
                absoluteLocation = namespace + "/" + location;
            } else {
                absoluteLocation = "/" + namespace + "/" + location;
            }
        }

        Template template = configuration.getTemplate(absoluteLocation, deduceLocale());
        TemplateModel model = createModel();

        // Give subclasses a chance to hook into preprocessing
        if (preTemplateProcess(template, model)) {
            try {
                final boolean willWriteIfCompleted;
                if (writeIfCompleted != null) {
                    willWriteIfCompleted = isWriteIfCompleted();
                } else {
                    willWriteIfCompleted = template.getTemplateExceptionHandler() == TemplateExceptionHandler.RETHROW_HANDLER;
                }

                // Process the template
                Writer writer = getWriter();
                if (willWriteIfCompleted){
                    CharArrayWriter parentCharArrayWriter = (CharArrayWriter) req.getAttribute(PARENT_TEMPLATE_WRITER);
                    boolean isTopTemplate;
                    if (isTopTemplate = (parentCharArrayWriter == null)) {
                        //this is the top template
                        parentCharArrayWriter = new CharArrayWriter();
                        //set it in the request because when the "action" tag is used a new VS and ActionContext is created
                        req.setAttribute(PARENT_TEMPLATE_WRITER, parentCharArrayWriter);
                    }

                    try {
                        template.process(model, parentCharArrayWriter);

                        if (isTopTemplate) {
                            parentCharArrayWriter.flush();
                            parentCharArrayWriter.writeTo(writer);
                        }
                    } catch (TemplateException | IOException e) {
                        if (LOG.isErrorEnabled()) {
                            LOG.error("Error processing Freemarker result!", e);
                        }
                        throw e;
                    } finally {
                        if (isTopTemplate) {
                            req.removeAttribute(PARENT_TEMPLATE_WRITER);
                            parentCharArrayWriter.close();
                        }
                    }
                } else {
                    template.process(model, writer);
                }
            } finally {
                // Give subclasses a chance to hook into postprocessing
                postTemplateProcess(template, model);
            }
        }
    }

    /**
     * <p>
     * This method is called from {@link #doExecute(String, ActionInvocation)} to obtain the
     * FreeMarker configuration object that this result will use for template loading. This is a
     * hook that allows you to custom-configure the configuration object in a subclass, or to fetch
     * it from an IoC container.
     * </p>
     *
     * <p>
     * <b>
     * The default implementation obtains the configuration from the ConfigurationManager instance.
     * </b>
     * </p>
     *
     * @return the freemarker configuration object
     * @throws TemplateException in case of freemarker configuration errors
     */
    protected Configuration getConfiguration() throws TemplateException {
        return freemarkerManager.getConfiguration(ServletActionContext.getServletContext());
    }

    /**
     * <p>
     * This method is called from {@link #doExecute(String, ActionInvocation)}  to obtain the
     * FreeMarker object wrapper object that this result will use for adapting objects into template
     * models. This is a hook that allows you to custom-configure the wrapper object in a subclass.
     * </p>
     *
     * <p>
     * <b>
     * The default implementation returns {@link Configuration#getObjectWrapper()}
     * </b>
     * </p>
     *
     * @return the object wrapper from configuration
     */
    protected ObjectWrapper getObjectWrapper() {
        return configuration.getObjectWrapper();
    }


    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    /**
     * The default writer writes directly to the response writer.
     *
     * @return Writer the response writer
     * @throws IOException in case of IO errors
     */
    protected Writer getWriter() throws IOException {
        if(writer != null) {
            return writer;
        }
        return ServletActionContext.getResponse().getWriter();
    }

    /**
     * Build the instance of the ScopesHashModel, including JspTagLib support
     * <p>
     * Objects added to the model are
     * </p>
     *
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
     * <li>exception - optional : the JSP or Servlet exception as per the servlet spec (for JSP Exception pages)
     * <li>struts - instance of the StrutsUtil class
     * </ul>
     *
     * @return TemplateModel returns the created template model
     * @throws TemplateModelException in case of errors during creating the model
     */
    protected TemplateModel createModel() throws TemplateModelException {
        ServletContext servletContext = ServletActionContext.getServletContext();
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ValueStack stack = ServletActionContext.getContext().getValueStack();

        Object action = null;
        if(invocation!= null ) action = invocation.getAction(); //Added for NullPointException
        return freemarkerManager.buildTemplateModel(stack, action, servletContext, request, response, wrapper);
    }

    /**
     * Returns the locale used for the {@link Configuration#getTemplate(String, Locale)} call. The base implementation
     * simply returns the locale setting of the action (assuming the action implements {@link LocaleProvider}) or, if
     * the action does not the configuration's locale is returned. Override this method to provide different behaviour,
     *
     * @return the locale from action if action implements the {@link LocaleProvider}) or local from configuration
     */
    protected Locale deduceLocale() {
        if (invocation.getAction() instanceof LocaleProvider) {
            return ((LocaleProvider) invocation.getAction()).getLocale();
        } else {
            return configuration.getLocale();
        }
    }

    /**
     * the default implementation of postTemplateProcess applies the contentType parameter
     *
     * @param template the freemarker template
     * @param model the template model
     *
     * @throws IOException in case of IO errors
     */
    protected void postTemplateProcess(Template template, TemplateModel model) throws IOException {
    }

    /**
     * Called before the execution is passed to template.process().
     * This is a generic hook you might use in subclasses to perform a specific
     * action before the template is processed. By default does nothing.
     * A typical action to perform here is to inject application-specific
     * objects into the model root
     *
     * @param template the freemarker template
     * @param model the template model
     * @return true to process the template, false to suppress template processing.
     * @throws IOException in case of IO errors
     */
    protected boolean preTemplateProcess(Template template, TemplateModel model) throws IOException {
        Object attrContentType = template.getCustomAttribute("content_type");

        HttpServletResponse response = ServletActionContext.getResponse();
        if (response.getContentType() == null) {
            if (attrContentType != null) {
                response.setContentType(attrContentType.toString());
            } else {
                String contentType = getContentType();

                if (contentType == null) {
                    contentType = "text/html";
                }

                String encoding = template.getEncoding();

                if (encoding != null) {
                    contentType = contentType + "; charset=" + encoding;
                }

                response.setContentType(contentType);
            }
        } else if(isInsideActionTag()){
             //trigger com.opensymphony.module.sitemesh.filter.PageResponseWrapper.deactivateSiteMesh()
            response.setContentType(response.getContentType());
        }

        return true;
    }

    private boolean isInsideActionTag() {
        Object attribute = ServletActionContext.getRequest().getAttribute(StrutsStatics.STRUTS_ACTION_TAG_INVOCATION);
        return (Boolean) ObjectUtils.defaultIfNull(attribute, Boolean.FALSE);
    }

    public boolean isWriteIfCompleted() {
        return writeIfCompleted != null && writeIfCompleted;
    }

    /**
     * @param writeIfCompleted template is processed and flushed according to freemarker library policies
     */
    public void setWriteIfCompleted(Boolean writeIfCompleted) {
        this.writeIfCompleted = writeIfCompleted;
    }
}
