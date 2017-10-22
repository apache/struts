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
package org.apache.struts2.sitemesh;


import com.opensymphony.module.sitemesh.*;
import com.opensymphony.module.sitemesh.util.OutputConverter;
import com.opensymphony.xwork2.ActionContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.listener.StrutsListener;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.view.VelocityView;
import org.apache.velocity.tools.view.VelocityViewServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.StringWriter;

/**
 * <p>This is a SiteMesh Velocity view servlet.</p>
 *
 * <p>It overrides the SiteMesh servlet to rely on the
 * Velocity Manager in Struts instead of creating it's
 * own manager</p>
 */
public class VelocityDecoratorServlet extends VelocityViewServlet {

    private static final Logger LOG = LogManager.getLogger(VelocityDecoratorServlet.class);
            
    private static final long serialVersionUID = -6731485159371716918L;
    
    protected VelocityManager velocityManager;
    protected String defaultContentType;

    /**
     * <p>Initializes servlet, toolbox and Velocity template engine.
     * Called by the servlet container on loading.</p>
     *
     * <p>NOTE: If no charset is specified in the default.contentType
     * property (in your velocity.properties) and you have specified
     * an output.encoding property, then that will be used as the
     * charset for the default content-type of pages served by this
     * servlet.</p>
     *
     * @param config servlet configuration
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        Dispatcher dispatcher = (Dispatcher) getServletContext().getAttribute(StrutsStatics.SERVLET_DISPATCHER);
        if (dispatcher == null) {
            throw new IllegalStateException("Unable to find the Dispatcher in the Servlet Context. Is '" + StrutsListener.class.getName() + "' missing in web.xml?");
        }
        velocityManager = dispatcher.getContainer().getInstance(VelocityManager.class);
        velocityManager.init(config.getServletContext());

        // do whatever we have to do to init Velocity
        getVelocityView().setVelocityEngine(velocityManager.getVelocityEngine());
        // toolboxManager = velocityManager.getToolboxManager();

        
        // we can get these now that velocity is initialized
        defaultContentType = getVelocityProperty(VelocityView.CONTENT_TYPE_KEY, VelocityView.DEFAULT_CONTENT_TYPE);

        String encoding = getVelocityProperty(RuntimeConstants.OUTPUT_ENCODING, VelocityView.DEFAULT_OUTPUT_ENCODING);

        // For non Latin-1 encodings, ensure that the charset is
        // included in the Content-Type header.
        if (!VelocityView.DEFAULT_OUTPUT_ENCODING.equalsIgnoreCase(encoding)) {
            int index = defaultContentType.lastIndexOf("charset");
            if (index < 0) {
                // the charset specifier is not yet present in header.
                // append character encoding to default content-type
                defaultContentType += "; charset=" + encoding;
            } else {
                // The user may have configuration issues.
                getVelocityView().getVelocityEngine().getLog().warn("VelocityViewServlet: Charset was already " + "specified in the Content-Type property.  " + "Output encoding property will be ignored.");
            }
        }

        getVelocityView().getVelocityEngine().getLog().info("VelocityViewServlet: Default content-type is: " + defaultContentType);
    }

    public Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context context) {
        HTMLPage htmlPage = (HTMLPage) request.getAttribute(RequestConstants.PAGE);
        String template;

        context.put("base", request.getContextPath());
        // For backwards compatibility with apps that used the old VelocityDecoratorServlet
        // that extended VelocityServlet instead of VelocityViewServlet
        context.put("req", request);
        context.put("res", response);

        if (htmlPage == null) {
            context.put("title", "Title?");
            context.put("body", "<p>Body?</p>");
            context.put("head", "<!-- head -->");
            template = request.getServletPath();
        } else {
            try {
                context.put("title", OutputConverter.convert(htmlPage.getTitle()));
                {
                    StringWriter buffer = new StringWriter();
                    htmlPage.writeBody(OutputConverter.getWriter(buffer));
                    context.put("body", buffer.toString());
                }
                {
                    StringWriter buffer = new StringWriter();
                    htmlPage.writeHead(OutputConverter.getWriter(buffer));
                    context.put("head", buffer.toString());
                }
            } catch (IOException e) {
                LOG.error("IOException handle request template", e);
            }
            context.put("page", htmlPage);
            DecoratorMapper decoratorMapper = getDecoratorMapper();
            Decorator decorator = decoratorMapper.getDecorator(request, htmlPage);
            template = decorator.getPage();
        }

        return getTemplate(template);
    }

    private DecoratorMapper getDecoratorMapper() {
        Factory factory = Factory.getInstance(new Config(getServletConfig()));
        return factory.getDecoratorMapper();
    }

    /**
     * <p>Creates and returns an initialized Velocity context.</p>
     *
     * @param request  servlet request from client
     * @param response servlet reponse to client
     */
    protected Context createContext(HttpServletRequest request, HttpServletResponse response) {
        Context context = (Context) request.getAttribute(VelocityManager.KEY_VELOCITY_STRUTS_CONTEXT);
        if (context == null) {
            ActionContext ctx = ServletActionContext.getActionContext(request);
            context = velocityManager.createContext(ctx.getValueStack(), request, response);
        }
        return context;
    }

    /**
     * <p>
     * Sets the content type of the response.  This is available to be overridden
     * by a derived class.
     * </p>
     * <p>The default implementation is:</p>
     * <pre>
     *
     *    response.setContentType(defaultContentType);
     *
     * </pre>
     * <p>
     * where defaultContentType is set to the value of the default.contentType
     * property, or "text/html" if that is not set.</p>
     *
     * @param request  servlet request from client
     * @param response servlet reponse to client
     */
    protected void setContentType(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(defaultContentType);
    }

}
