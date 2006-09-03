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
package org.apache.struts2.views.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.IteratorComponent;

import com.opensymphony.xwork2.util.OgnlValueStack;

/**
 * @see IteratorComponent
 */
public class IteratorTag extends ComponentTagSupport {

	private static final long serialVersionUID = -1827978135193581901L;
	
	protected String statusAttr;
    protected String value;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new IteratorComponent(stack);
    }

    protected void populateParams() {
    	super.populateParams();
    	
        IteratorComponent tag = (IteratorComponent) getComponent();
        tag.setStatus(statusAttr);
        tag.setValue(value);
    }

    public void setStatus(String status) {
        this.statusAttr = status;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int doEndTag() throws JspException {
        component = null;
        return EVAL_PAGE;
    }

    public int doAfterBody() throws JspException {
        boolean again = component.end(pageContext.getOut(), getBody());

        if (again) {
            return EVAL_BODY_AGAIN;
        } else {
            if (bodyContent != null) {
                try {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                } catch (Exception e) {
                    throw new JspException(e.getMessage());
                }
            }
            return SKIP_BODY;
        }
    }

}
