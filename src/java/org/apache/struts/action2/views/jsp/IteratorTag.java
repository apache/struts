/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.IteratorComponent;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

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
