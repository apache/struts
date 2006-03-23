/*
 *  Copyright (c) 2002-2006 by OpenSymphony
 *  All rights reserved.
 */
package com.opensymphony.webwork.components.template;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.views.velocity.VelocityManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Velocity based template engine.
 *
 * @author jcarreira
 */
public class VelocityTemplateEngine extends BaseTemplateEngine {
    private static final Log LOG = LogFactory.getLog(VelocityTemplateEngine.class);

    public void renderTemplate(TemplateRenderingContext templateContext) throws Exception {
        // get the various items required from the stack
        Map actionContext = templateContext.getStack().getContext();
        ServletContext servletContext = (ServletContext) actionContext.get(ServletActionContext.SERVLET_CONTEXT);
        HttpServletRequest req = (HttpServletRequest) actionContext.get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse res = (HttpServletResponse) actionContext.get(ServletActionContext.HTTP_RESPONSE);

        // prepare velocity
        VelocityManager velocityManager = VelocityManager.getInstance();
        velocityManager.init(servletContext);
        VelocityEngine velocityEngine = velocityManager.getVelocityEngine();

        // get the list of templates we can use
        List templates = templateContext.getTemplate().getPossibleTemplates(this);

        // find the right template
        org.apache.velocity.Template template = null;
        String templateName = null;
        Exception exception = null;
        for (Iterator iterator = templates.iterator(); iterator.hasNext();) {
            Template t = (Template) iterator.next();
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
