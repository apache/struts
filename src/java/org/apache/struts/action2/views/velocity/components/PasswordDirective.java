package com.opensymphony.webwork.views.velocity.components;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.Password;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Password
 */
public class PasswordDirective extends AbstractDirective {
    public String getBeanName() {
        return "password";
    }

    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Password(stack, req, res);
    }
}
