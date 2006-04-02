package org.apache.struts.webwork.views.velocity.components;

import org.apache.struts.webwork.components.Checkbox;
import org.apache.struts.webwork.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Checkbox
 */
public class CheckBoxDirective extends AbstractDirective {
    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Checkbox(stack, req, res);
    }

    public String getBeanName() {
        return "checkbox";
    }
}
