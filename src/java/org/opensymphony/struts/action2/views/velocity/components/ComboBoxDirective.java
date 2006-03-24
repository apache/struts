package com.opensymphony.webwork.views.velocity.components;

import com.opensymphony.webwork.components.ComboBox;
import com.opensymphony.webwork.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see ComboBox
 */
public class ComboBoxDirective extends AbstractDirective {
    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new ComboBox(stack, req, res);
    }

    public String getBeanName() {
        return "combobox";
    }
}
