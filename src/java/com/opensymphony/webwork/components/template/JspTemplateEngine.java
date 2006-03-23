/*
 *  Copyright (c) 2002-2006 by OpenSymphony
 *  All rights reserved.
 */
package com.opensymphony.webwork.components.template;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.components.Include;
import com.opensymphony.webwork.components.UIBean;
import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.util.Iterator;
import java.util.List;

/**
 * JSP based template engine.
 *
 * @author jcarreira
 */
public class JspTemplateEngine extends BaseTemplateEngine {
    private static final Log LOG = LogFactory.getLog(JspTemplateEngine.class);

    public void renderTemplate(TemplateRenderingContext templateContext) throws Exception {
        Template template = templateContext.getTemplate();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Trying to render template " + template + ", repeating through parents until we succeed");
        }
        UIBean tag = templateContext.getTag();
        OgnlValueStack stack = templateContext.getStack();
        stack.push(tag);
        PageContext pageContext = (PageContext) stack.getContext().get(ServletActionContext.PAGE_CONTEXT);
        List templates = template.getPossibleTemplates(this);
        Exception exception = null;
        boolean success = false;
        for (Iterator iterator = templates.iterator(); iterator.hasNext();) {
            Template t = (Template) iterator.next();
            try {
                Include.include(getFinalTemplateName(t), pageContext.getOut(),
                        pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
                success = true;
                break;
            } catch (Exception e) {
                if (exception == null) {
                    exception = e;
                }
            }
        }

        if (!success) {
            LOG.error("Could not render JSP template " + templateContext.getTemplate());

            if (exception != null) {
                throw exception;
            } else {
                return;
            }
        }

        stack.pop();
    }

    protected String getSuffix() {
        return "jsp";
    }
}
