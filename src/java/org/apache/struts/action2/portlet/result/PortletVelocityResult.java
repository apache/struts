/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.portlet.result;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.WebWorkConstants;
import com.opensymphony.webwork.config.Configuration;
import com.opensymphony.webwork.dispatcher.WebWorkResultSupport;
import com.opensymphony.webwork.portlet.PortletActionConstants;
import com.opensymphony.webwork.portlet.context.PortletActionContext;
import com.opensymphony.webwork.views.JspSupportServlet;
import com.opensymphony.webwork.views.velocity.VelocityManager;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.util.OgnlValueStack;

import freemarker.template.TemplateException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * <!-- START SNIPPET: description -->
 * 
 * Using the Servlet container's {@link JspFactory}, this result mocks a JSP
 * execution environment and then displays a Velocity template that will be
 * streamed directly to the servlet output.
 * 
 * <!-- END SNIPPET: description --> <p/><b>This result type takes the
 * following parameters: </b>
 * 
 * <!-- START SNIPPET: params -->
 * 
 * <ul>
 * 
 * <li><b>location (default) </b>- the location of the template to process.
 * </li>
 * 
 * <li><b>parse </b>- true by default. If set to false, the location param
 * will not be parsed for Ognl expressions.</li>
 * 
 * </ul>
 * <p>
 * This result follows the same rules from {@link WebWorkResultSupport}.
 * </p>
 * 
 * <!-- END SNIPPET: params -->
 * 
 * <b>Example: </b>
 * 
 * <pre>
 * &lt;!-- START SNIPPET: example --&gt;
 *  &lt;result name=&quot;success&quot; type=&quot;velocity&quot;&gt;
 *    &lt;param name=&quot;location&quot;&gt;foo.vm&lt;/param&gt;
 *  &lt;/result&gt;
 *  &lt;!-- END SNIPPET: example --&gt;
 * </pre>
 * 
 * @author <a href="mailto:matt@indigoegg.com">Matt Ho </a>
 */
public class PortletVelocityResult extends WebWorkResultSupport {

    private static final Log log = LogFactory
            .getLog(PortletVelocityResult.class);

    public void doExecute(String location, ActionInvocation invocation)
            throws Exception {
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
        res.setRenderParameter(PortletActionConstants.ACTION_PARAM,
                "freemarkerDirect");
        res.setRenderParameter("location", location);
        res.setRenderParameter(PortletActionConstants.MODE_PARAM, PortletActionContext
                .getRequest().getPortletMode().toString());

    }

    /**
     * Creates a Velocity context from the action, loads a Velocity template and
     * executes the template. Output is written to the servlet output stream.
     * 
     * @param finalLocation the location of the Velocity template
     * @param invocation an encapsulation of the action execution state.
     * @throws Exception if an error occurs when creating the Velocity context,
     *         loading or executing the template or writing output to the
     *         servlet response stream.
     */
    public void executeRenderResult(String finalLocation,
            ActionInvocation invocation) throws Exception {
        prepareServletActionContext();
        OgnlValueStack stack = ActionContext.getContext().getValueStack();

        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        JspFactory jspFactory = null;
        ServletContext servletContext = ServletActionContext
                .getServletContext();
        Servlet servlet = JspSupportServlet.jspSupportServlet;

        VelocityManager.getInstance().init(servletContext);

        boolean usedJspFactory = false;
        PageContext pageContext = (PageContext) ActionContext.getContext().get(
                ServletActionContext.PAGE_CONTEXT);

        if (pageContext == null && servlet != null) {
            jspFactory = JspFactory.getDefaultFactory();
            pageContext = jspFactory.getPageContext(servlet, request, response,
                    null, true, 8192, true);
            ActionContext.getContext().put(ServletActionContext.PAGE_CONTEXT,
                    pageContext);
            usedJspFactory = true;
        }

        try {
            String encoding = getEncoding(finalLocation);
            String contentType = getContentType(finalLocation);

            if (encoding != null) {
                contentType = contentType + ";charset=" + encoding;
            }

            VelocityManager velocityManager = VelocityManager.getInstance();
            Template t = getTemplate(stack,
                    velocityManager.getVelocityEngine(), invocation,
                    finalLocation, encoding);

            Context context = createContext(velocityManager, stack, request,
                    response, finalLocation);
            Writer writer = new OutputStreamWriter(response.getOutputStream(),
                    encoding);

            response.setContentType(contentType);

            t.merge(context, writer);

            // always flush the writer (we used to only flush it if this was a
            // jspWriter, but someone asked
            // to do it all the time (WW-829). Since Velocity support is being
            // deprecated, we'll oblige :)
            writer.flush();
        } catch (Exception e) {
            log.error("Unable to render Velocity Template, '" + finalLocation
                    + "'", e);
            throw e;
        } finally {
            if (usedJspFactory) {
                jspFactory.releasePageContext(pageContext);
            }
        }

        return;
    }

    /**
     * Retrieve the content type for this template. <p/>People can override
     * this method if they want to provide specific content types for specific
     * templates (eg text/xml).
     * 
     * @return The content type associated with this template (default
     *         "text/html")
     */
    protected String getContentType(String templateLocation) {
        return "text/html";
    }

    /**
     * Retrieve the encoding for this template. <p/>People can override this
     * method if they want to provide specific encodings for specific templates.
     * 
     * @return The encoding associated with this template (defaults to the value
     *         of 'webwork.i18n.encoding' property)
     */
    protected String getEncoding(String templateLocation) {
        String encoding = (String) Configuration
                .get(WebWorkConstants.WEBWORK_I18N_ENCODING);
        if (encoding == null) {
            encoding = System.getProperty("file.encoding");
        }
        if (encoding == null) {
            encoding = "UTF-8";
        }
        return encoding;
    }

    /**
     * Given a value stack, a Velocity engine, and an action invocation, this
     * method returns the appropriate Velocity template to render.
     * 
     * @param stack the value stack to resolve the location again (when parse
     *        equals true)
     * @param velocity the velocity engine to process the request against
     * @param invocation an encapsulation of the action execution state.
     * @param location the location of the template
     * @param encoding the charset encoding of the template
     * @return the template to render
     * @throws Exception when the requested template could not be found
     */
    protected Template getTemplate(OgnlValueStack stack,
            VelocityEngine velocity, ActionInvocation invocation,
            String location, String encoding) throws Exception {
        if (!location.startsWith("/")) {
            location = invocation.getProxy().getNamespace() + "/" + location;
        }

        Template template = velocity.getTemplate(location, encoding);

        return template;
    }

    /**
     * Creates the VelocityContext that we'll use to render this page.
     * 
     * @param velocityManager a reference to the velocityManager to use
     * @param stack the value stack to resolve the location against (when parse
     *        equals true)
     * @param location the name of the template that is being used
     * @return the a minted Velocity context.
     */
    protected Context createContext(VelocityManager velocityManager,
            OgnlValueStack stack, HttpServletRequest request,
            HttpServletResponse response, String location) {
        return velocityManager.createContext(stack, request, response);
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
}
