package com.opensymphony.webwork.views.freemarker.tags;

import com.opensymphony.webwork.components.ComboBox;
import com.opensymphony.webwork.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see ComboBox
 */
public class ComboBoxModel extends TagModel {
    public ComboBoxModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new ComboBox(stack, req, res);
    }
}
