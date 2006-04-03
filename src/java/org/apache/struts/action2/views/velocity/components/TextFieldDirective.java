package org.apache.struts.action2.views.velocity.components;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.TextField;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see TextField
 */
public class TextFieldDirective extends AbstractDirective {
    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new TextField(stack, req, res);
    }

    public String getBeanName() {
        return "textfield";
    }
}
