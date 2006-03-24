package com.opensymphony.webwork.views.freemarker.tags;

import com.opensymphony.webwork.components.Checkbox;
import com.opensymphony.webwork.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Checkbox
 */
public class CheckboxModel extends TagModel {
    public CheckboxModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new Checkbox(stack, req, res);
    }
}
