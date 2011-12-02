/**
 * Adapts a SiteMesh 2 Velocity {@link com.opensymphony.module.sitemesh.Decorator}
 * to a SiteMesh 3 {@link com.opensymphony.sitemesh.Decorator}.
 *
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
 * @since SiteMesh 2
 */

package org.apache.struts2.sitemesh;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.compatability.Content2HTMLPage;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.context.Context;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Extends OldDecorator2NewStrutsDecorator to add Struts functionality for Velocity
 */
public class OldDecorator2NewStrutsVelocityDecorator extends OldDecorator2NewStrutsDecorator {
    private static final Logger LOG = LoggerFactory.getLogger(OldDecorator2NewStrutsFreemarkerDecorator.class);

    private static VelocityManager velocityManager;

    @Inject(required = false)
    public static void setVelocityManager(VelocityManager mgr) {
        velocityManager = mgr;
    }

    public OldDecorator2NewStrutsVelocityDecorator(com.opensymphony.module.sitemesh.Decorator oldDecorator) {
        this.oldDecorator = oldDecorator;
    }

    /**
     * Applies the decorator, using the relevent contexts
     *
     * @param content        The content
     * @param request        The servlet request
     * @param response       The servlet response
     * @param servletContext The servlet context
     * @param ctx            The action context for this request, populated with the server state
     */
    protected void render(Content content, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, ActionContext ctx) throws ServletException, IOException {
        String timerKey = "FreemarkerPageFilter_applyDecorator: ";
        if (velocityManager == null) {
            throw new ServletException("Missing freemarker dependency");
        }

        try {

            // init (if needed)
            velocityManager.init(servletContext);

            // get encoding
            String encoding = getEncoding();

            HTMLPage htmlPage = new Content2HTMLPage(content, request);

            // get the template and context
            org.apache.velocity.Template template = velocityManager.getVelocityEngine().getTemplate(oldDecorator.getPage(), encoding);
            Context context = velocityManager.createContext(ctx.getValueStack(), request, response);

            // put the page in the context
            context.put("page", htmlPage);
            context.put("head", htmlPage.getHead());
            context.put("title", htmlPage.getTitle());
            context.put("body", htmlPage.getBody());

            // finally, render it
            PrintWriter writer = response.getWriter();
            template.merge(context, writer);
            writer.flush();
        } catch (Exception e) {
            String msg = "Error applying decorator to request: " + request.getRequestURL() + "?" + request.getQueryString() + " with message:" + e.getMessage();
            LOG.error(msg, e);
            throw new ServletException(msg, e);
        }
    }

}

