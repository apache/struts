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
package org.apache.struts2.components.template;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.util.ClassLoaderUtil;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Freemarker based template engine.
 */
public class FreemarkerTemplateEngine extends BaseTemplateEngine {
    static Class bodyContent = null;

    static {
        try {
            bodyContent = ClassLoaderUtil.loadClass("javax.servlet.jsp.tagext.BodyContent",
                    FreemarkerTemplateEngine.class);
        } catch (ClassNotFoundException e) {
            // this is OK -- this just means JSP isn't even being used here, which is perfectly fine.
            // we need this class in environments that use JSP to know when to wrap the writer
            // and ignore flush() calls. In JSP, it is illegal for a BodyContent writer to be flushed(),
            // so we have to take caution here.
        }
    }

    private static final Log LOG = LogFactory.getLog(FreemarkerTemplateEngine.class);

    public void renderTemplate(TemplateRenderingContext templateContext) throws Exception {
        // get the various items required from the stack
        OgnlValueStack stack = templateContext.getStack();
        Map context = stack.getContext();
        ServletContext servletContext = (ServletContext) context.get(ServletActionContext.SERVLET_CONTEXT);
        HttpServletRequest req = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse res = (HttpServletResponse) context.get(ServletActionContext.HTTP_RESPONSE);

        // prepare freemarker
        FreemarkerManager freemarkerManager = FreemarkerManager.getInstance();
        Configuration config = freemarkerManager.getConfiguration(servletContext);

        // get the list of templates we can use
        List templates = templateContext.getTemplate().getPossibleTemplates(this);

        // find the right template
        freemarker.template.Template template = null;
        String templateName = null;
        Exception exception = null;
        for (Iterator iterator = templates.iterator(); iterator.hasNext();) {
            Template t = (Template) iterator.next();
            templateName = getFinalTemplateName(t);
            try {
                // try to load, and if it works, stop at the first one
                template = config.getTemplate(templateName);
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

        ActionInvocation ai = ActionContext.getContext().getActionInvocation();

        Object action = (ai == null) ? null : ai.getAction();
        SimpleHash model = freemarkerManager.buildTemplateModel(stack, action, servletContext, req, res, config.getObjectWrapper());

        model.put("tag", templateContext.getTag());
        model.put("themeProperties", getThemeProps(templateContext.getTemplate()));

        // the BodyContent JSP writer doesn't like it when FM flushes automatically --
        // so let's just not do it (it will be flushed eventually anyway)
        Writer writer = templateContext.getWriter();
        if (bodyContent != null && bodyContent.isAssignableFrom(writer.getClass())) {
            final Writer wrapped = writer;
            writer = new Writer() {
                public void write(char cbuf[], int off, int len) throws IOException {
                    wrapped.write(cbuf, off, len);
                }

                public void flush() throws IOException {
                    // nothing!
                }

                public void close() throws IOException {
                    wrapped.close();
                }
            };
        }

        try {
            stack.push(templateContext.getTag());
            template.process(model, writer);
        } finally {
            stack.pop();
        }
    }

    protected String getSuffix() {
        return "ftl";
    }
}
