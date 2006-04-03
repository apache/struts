package org.apache.struts.action2.views.velocity.components;

import org.apache.struts.action2.components.Checkbox;
import org.apache.struts.action2.components.Component;
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
