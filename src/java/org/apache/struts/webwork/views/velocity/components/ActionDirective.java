package org.apache.struts.webwork.views.velocity.components;

import org.apache.struts.webwork.components.ActionComponent;
import org.apache.struts.webwork.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see ActionComponent
 */
public class ActionDirective extends AbstractDirective {
    public String getBeanName() {
        return "action";
    }

    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new ActionComponent(stack, req, res);
    }
}
