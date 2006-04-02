package org.apache.struts.webwork.views.velocity.components;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.Head;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Head
 */
public class HeadDirective extends AbstractDirective {
    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Head(stack, req, res);
    }

    public String getBeanName() {
        return "head";
    }
}
