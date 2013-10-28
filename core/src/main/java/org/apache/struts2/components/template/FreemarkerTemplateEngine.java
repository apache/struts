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
import java.util.Locale;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.freemarker.FreemarkerManager;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.core.ParseException;

/**
 * Freemarker based template engine.
 */
public class FreemarkerTemplateEngine extends BaseTemplateEngine {
    static Class bodyContent = null;
    protected FreemarkerManager freemarkerManager;

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

    private static final Logger LOG = LoggerFactory.getLogger(FreemarkerTemplateEngine.class);

    @Inject
    public void setFreemarkerManager(FreemarkerManager mgr) {
        this.freemarkerManager = mgr;
    }
    
    public void renderTemplate(TemplateRenderingContext templateContext) throws Exception {
    	// get the various items required from the stack
        ValueStack stack = templateContext.getStack();
        Map context = stack.getContext();
        ServletContext servletContext = (ServletContext) context.get(ServletActionContext.SERVLET_CONTEXT);
        HttpServletRequest req = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse res = (HttpServletResponse) context.get(ServletActionContext.HTTP_RESPONSE);

        // prepare freemarker
        Configuration config = freemarkerManager.getConfiguration(servletContext);

        // get the list of templates we can use
        List<Template> templates = templateContext.getTemplate().getPossibleTemplates(this);

        // find the right template
        freemarker.template.Template template = null;
        String templateName = null;
        Exception exception = null;
        for (Template t : templates) {
            templateName = getFinalTemplateName(t);
                try {
                    // try to load, and if it works, stop at the first one
                    template = config.getTemplate(templateName);
                    break;
                } catch (ParseException e) {
                    // template was found but was invalid - always report this.
                    exception = e;
                    break;
                } catch (IOException e) {
                    // FileNotFoundException is anticipated - report the first IOException if no template found
                    if (exception == null) {
                        exception = e;
                    }
                }
        }

        if (template == null) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Could not load the FreeMarker template named '" + templateContext.getTemplate().getName() +"':");
                for (Template t : templates) {
                    LOG.error("Attempted: " + getFinalTemplateName(t));
                }
                LOG.error("The TemplateLoader provided by the FreeMarker Configuration was a: "+config.getTemplateLoader().getClass().getName());
            }
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
