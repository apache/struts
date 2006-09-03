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
package org.apache.struts2.views.velocity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;

import org.apache.struts2.RequestUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.config.Settings;
import org.apache.struts2.views.util.ContextUtil;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.servlet.VelocityServlet;

import com.opensymphony.xwork2.ActionContext;


/**
 * @deprecated please use {@link org.apache.struts2.dispatcher.VelocityResult} instead of direct access
 */
public class StrutsVelocityServlet extends VelocityServlet {
    /**
     * 
     */
    private static final long serialVersionUID = -2078492831396251182L;
    private VelocityManager velocityManager;

    public StrutsVelocityServlet() {
        velocityManager = VelocityManager.getInstance();
    }

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        // initialize our VelocityManager
        velocityManager.init(servletConfig.getServletContext());
    }

    protected Context createContext(HttpServletRequest request, HttpServletResponse response) {
        return velocityManager.createContext(ActionContext.getContext().getValueStack(), request, response);
    }

    protected Template handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Context context) throws Exception {
        String servletPath = (String) httpServletRequest.getAttribute("javax.servlet.include.servlet_path");

        if (servletPath == null) {
            servletPath = RequestUtils.getServletPath(httpServletRequest);
        }

        return getTemplate(servletPath, getEncoding());
    }

    /**
     * This method extends the VelocityServlet's loadConfiguration method by performing the following actions:
     * <ul>
     * <li>invokes VelocityServlet.loadConfiguration to create a properties object</li>
     * <li>alters the RESOURCE_LOADER to include a class loader</li>
     * <li>configures the class loader using the StrutsResourceLoader</li>
     * </ul>
     *
     * @param servletConfig
     * @throws IOException
     * @throws FileNotFoundException
     * @see org.apache.velocity.servlet.VelocityServlet#loadConfiguration
     */
    protected Properties loadConfiguration(ServletConfig servletConfig) throws IOException, FileNotFoundException {
        return velocityManager.loadConfiguration(servletConfig.getServletContext());
    }

    /**
     * create a PageContext and render the template to PageContext.getOut()
     *
     * @see VelocityServlet#mergeTemplate(Template, Context, HttpServletResponse) for additional documentation
     */
    protected void mergeTemplate(Template template, Context context, HttpServletResponse response) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, IOException, UnsupportedEncodingException, Exception {
        // save the old PageContext
        PageContext oldPageContext = ServletActionContext.getPageContext();

        // create a new PageContext
        JspFactory jspFactory = JspFactory.getDefaultFactory();
        HttpServletRequest request = (HttpServletRequest) context.get(ContextUtil.REQUEST);
        PageContext pageContext = jspFactory.getPageContext(this, request, response, null, true, 8192, true);

        // put the new PageContext into ActionContext
        ActionContext actionContext = ActionContext.getContext();
        actionContext.put(ServletActionContext.PAGE_CONTEXT, pageContext);

        try {
            Writer writer = pageContext.getOut();
            template.merge(context, writer);
            writer.flush();
        } finally {
            // perform cleanup
            jspFactory.releasePageContext(pageContext);
            actionContext.put(ServletActionContext.PAGE_CONTEXT, oldPageContext);
        }
    }

    private String getEncoding() {
        // todo look into converting this to using XWork/Struts encoding rules
        try {
            return Settings.get(StrutsConstants.STRUTS_I18N_ENCODING);
        } catch (IllegalArgumentException e) {
            return RuntimeSingleton.getString(RuntimeSingleton.OUTPUT_ENCODING, DEFAULT_OUTPUT_ENCODING);
        }
    }
}
