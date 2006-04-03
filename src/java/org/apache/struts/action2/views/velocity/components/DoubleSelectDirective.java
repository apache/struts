package org.apache.struts.action2.views.velocity.components;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.DoubleSelect;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Rainer Hermanns
 * @see DoubleSelect
 */
public class DoubleSelectDirective extends AbstractDirective {
    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new DoubleSelect(stack, req, res);
    }

    public String getBeanName() {
        return "doubleselect";
    }
}
