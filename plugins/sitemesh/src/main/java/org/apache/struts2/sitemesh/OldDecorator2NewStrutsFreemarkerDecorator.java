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

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.compatability.Content2HTMLPage;
import com.opensymphony.xwork2.ActionContext;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.freemarker.ScopesHashModel;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Adapts a SiteMesh 2 Freemarker {@link com.opensymphony.module.sitemesh.Decorator} to a
 * SiteMesh 3 {@link com.opensymphony.sitemesh.Decorator}.
 * Extends OldDecorator2NewStrutsDecorator to add Struts functionality  for Freemarker
 * @since SiteMesh 2
 */
public class OldDecorator2NewStrutsFreemarkerDecorator extends OldDecorator2NewStrutsDecorator {
    private static final Logger LOG = LogManager.getLogger(OldDecorator2NewStrutsFreemarkerDecorator.class);

    private static FreemarkerManager freemarkerManager;

    public static void setFreemarkerManager(FreemarkerManager mgr) {
        freemarkerManager = mgr;
    }

    public OldDecorator2NewStrutsFreemarkerDecorator(com.opensymphony.module.sitemesh.Decorator oldDecorator) {
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
        if (freemarkerManager == null) {
            throw new ServletException("Missing freemarker dependency");
        }

        try {
            // get the configuration and template
            Configuration config = freemarkerManager.getConfiguration(servletContext);
            Template template = config.getTemplate(oldDecorator.getPage(), getLocale(ctx.getActionInvocation(), config)); // WW-1181

            // get the main hash
            ScopesHashModel model = (ScopesHashModel) request.getAttribute(freemarkerManager.ATTR_TEMPLATE_MODEL);
            if (model == null) {
                model = freemarkerManager.buildTemplateModel(ctx.getValueStack(), ctx.getActionInvocation().getAction(), servletContext, request, response, config.getObjectWrapper());
            }

            // populate the hash with the page
            HTMLPage htmlPage = new Content2HTMLPage(content, request);
            model.put("page", htmlPage);
            model.put("head", htmlPage.getHead());
            model.put("title", htmlPage.getTitle());
            model.put("body", htmlPage.getBody());
            model.put("page.properties", new SimpleHash(htmlPage.getProperties()));

            // finally, render it
            template.process(model, response.getWriter());
        } catch (Exception e) {
            String msg = "Error applying decorator to request: " + request.getRequestURL() + "?" + request.getQueryString() + " with message:" + e.getMessage();
            LOG.error(msg, e);
            throw new ServletException(msg, e);
        }
    }

}

