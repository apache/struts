package org.apache.struts.webwork.views.velocity.components;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.Form;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Form
 */
public class FormDirective extends AbstractDirective {
    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Form(stack, req, res);
    }

    public String getBeanName() {
        return "form";
    }

    public int getType() {
        return BLOCK;
    }
}
