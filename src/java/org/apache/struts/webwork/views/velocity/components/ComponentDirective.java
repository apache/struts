package org.apache.struts.webwork.views.velocity.components;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.GenericUIBean;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see GenericUIBean
 */
public class ComponentDirective extends AbstractDirective {
    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new GenericUIBean(stack, req, res);
    }

    public String getBeanName() {
        return "component";
    }

    public int getType() {
        return BLOCK;
    }
}
