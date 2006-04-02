package org.apache.struts.webwork.views.velocity.components;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.Property;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Patrick Lightbody (plightbo at gmail dot com)
 */
public class PropertyDirective extends AbstractDirective {
    public String getBeanName() {
        return "property";
    }

    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Property(stack);
    }
}
