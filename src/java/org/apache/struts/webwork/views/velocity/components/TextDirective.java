package org.apache.struts.webwork.views.velocity.components;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.Text;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Text
 */
public class TextDirective extends AbstractDirective {
    public String getBeanName() {
        return "text";
    }

    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Text(stack);
    }
}
