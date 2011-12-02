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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import com.opensymphony.xwork2.inject.Inject;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.components.Include;
import org.apache.struts2.components.UIBean;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * JSP based template engine.
 */
public class JspTemplateEngine extends BaseTemplateEngine {
    private static final Logger LOG = LoggerFactory.getLogger(JspTemplateEngine.class);

	String encoding;

	@Inject(StrutsConstants.STRUTS_I18N_ENCODING)
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

    public void renderTemplate(TemplateRenderingContext templateContext) throws Exception {
        Template template = templateContext.getTemplate();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Trying to render template " + template + ", repeating through parents until we succeed");
        }
        UIBean tag = templateContext.getTag();
        ValueStack stack = templateContext.getStack();
        stack.push(tag);
        PageContext pageContext = (PageContext) stack.getContext().get(ServletActionContext.PAGE_CONTEXT);
        List<Template> templates = template.getPossibleTemplates(this);
        Exception exception = null;
        boolean success = false;
        for (Template t : templates) {
            try {
                Include.include(getFinalTemplateName(t), pageContext.getOut(),
                        pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), encoding);
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
