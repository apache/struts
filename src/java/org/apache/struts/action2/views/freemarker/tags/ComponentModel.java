package com.opensymphony.webwork.views.freemarker.tags;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.GenericUIBean;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see ComponentModel
 */
public class ComponentModel extends TagModel {
    public ComponentModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new GenericUIBean(stack, req, res);
    }
}
