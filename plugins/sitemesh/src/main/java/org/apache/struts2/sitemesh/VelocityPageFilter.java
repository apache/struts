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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;

import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Inject;


/**
 *  Applies Velocity-based decorators
 *
 */
public class VelocityPageFilter extends TemplatePageFilter {
    private static final Log LOG = LogFactory.getLog(VelocityPageFilter.class);

    private static VelocityManager velocityManager;
    
    @Inject
    public static void setVelocityManager(VelocityManager mgr) {
        velocityManager = mgr;
    }
        
    /**
     *  Applies the decorator, using the relevent contexts
     *
     * @param page The page
     * @param decorator The decorator
     * @param req The servlet request
     * @param res The servlet response
     * @param servletContext The servlet context
     * @param ctx The action context for this request, populated with the server state
     */
    protected void applyDecorator(Page page, Decorator decorator,
                                  HttpServletRequest req, HttpServletResponse res,
                                  ServletContext servletContext, ActionContext ctx)
            throws ServletException, IOException {
        try {

            // init (if needed)
            velocityManager.init(servletContext);

            // get encoding
            String encoding = getEncoding();

            // get the template and context
            Template template = velocityManager.getVelocityEngine().getTemplate(decorator.getPage(), encoding);
            Context context = velocityManager.createContext(ctx.getValueStack(), req, res);

            // put the page in the context
            context.put("page", page);
            if (page instanceof HTMLPage) {
                HTMLPage htmlPage = ((HTMLPage) page);
                context.put("head", htmlPage.getHead());
            }
            context.put("title",page.getTitle());
            context.put("body",page.getBody());

            // finally, render it
            PrintWriter writer = res.getWriter();
            template.merge(context, writer);
            writer.flush();
        } catch (Exception e) {
            String msg = "Error applying decorator: " + e.getMessage();
            LOG.error(msg, e);
            throw new ServletException(msg, e);
        }
    }
}
