package org.apache.struts2.dojo.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.dojo.components.TextArea;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * @see TextArea
 */
public class TextAreaDirective extends DojoAbstractDirective {
    public String getBeanName() {
        return "textarea";
    }

    protected Component getBean(ValueStack stack, HttpServletRequest req,
        HttpServletResponse res) {
        return new TextArea(stack, req, res);
    }

    public int getType() {
        return BLOCK;
    }
}
