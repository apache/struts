package com.opensymphony.webwork.views.jsp;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

/**
 * User: plightbo
 * Date: Sep 1, 2005
 * Time: 8:44:53 PM
 */
public abstract class ComponentTagSupport extends WebWorkBodyTagSupport {
    protected Component component;

    public abstract Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res);

    public int doEndTag() throws JspException {
        component.end(pageContext.getOut(), getBody());
        component = null;
        return EVAL_PAGE;
    }

    public int doStartTag() throws JspException {
        component = getBean(getStack(), (HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
        populateParams();
        boolean evalBody = component.start(pageContext.getOut());

        if (evalBody) {
            return component.usesBody() ? EVAL_BODY_BUFFERED : EVAL_BODY_INCLUDE;
        } else {
            return SKIP_BODY;
        }
    }

    protected void populateParams() {
        component.setId(id);
    }

    public Component getComponent() {
        return component;
    }
}
