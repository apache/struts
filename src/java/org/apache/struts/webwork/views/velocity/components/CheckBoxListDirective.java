package org.apache.struts.webwork.views.velocity.components;

import org.apache.struts.webwork.components.CheckboxList;
import org.apache.struts.webwork.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see CheckboxList
 */
public class CheckBoxListDirective extends AbstractDirective {
    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new CheckboxList(stack, req, res);
    }

    public String getBeanName() {
        return "checkboxlist";
    }
}
