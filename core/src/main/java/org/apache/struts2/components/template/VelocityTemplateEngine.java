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

package org.apache.struts2.components.template;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * Velocity based template engine.
 */
public class VelocityTemplateEngine extends BaseTemplateEngine {
    private static final Logger LOG = LoggerFactory.getLogger(VelocityTemplateEngine.class);
    
    private VelocityManager velocityManager;
    
    @Inject
    public void setVelocityManager(VelocityManager mgr) {
        this.velocityManager = mgr;
    }

    public void renderTemplate(TemplateRenderingContext templateContext) throws Exception {
        // get the various items required from the stack
        Map actionContext = templateContext.getStack().getContext();
        ServletContext servletContext = (ServletContext) actionContext.get(ServletActionContext.SERVLET_CONTEXT);
        HttpServletRequest req = (HttpServletRequest) actionContext.get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse res = (HttpServletResponse) actionContext.get(ServletActionContext.HTTP_RESPONSE);

        // prepare velocity
        velocityManager.init(servletContext);
        VelocityEngine velocityEngine = velocityManager.getVelocityEngine();

        // get the list of templates we can use
        List<Template> templates = templateContext.getTemplate().getPossibleTemplates(this);

        // find the right template
        org.apache.velocity.Template template = null;
        String templateName = null;
        Exception exception = null;
        for (Template t : templates) {
            templateName = getFinalTemplateName(t);
            try {
                // try to load, and if it works, stop at the first one
                template = velocityEngine.getTemplate(templateName);
                break;
            } catch (IOException e) {
                if (exception == null) {
                    exception = e;
                }
            }
        }

        if (template == null) {
            LOG.error("Could not load template " + templateContext.getTemplate());
            if (exception != null) {
                throw exception;
            } else {
                return;
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Rendering template " + templateName);
        }

        Context context = velocityManager.createContext(templateContext.getStack(), req, res);

        Writer outputWriter = templateContext.getWriter();
        context.put("tag", templateContext.getTag());
        context.put("parameters", templateContext.getParameters());

        template.merge(context, outputWriter);
    }

    protected String getSuffix() {
        return "vm";
    }
}
