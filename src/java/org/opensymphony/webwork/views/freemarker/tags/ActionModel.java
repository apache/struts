package com.opensymphony.webwork.views.freemarker.tags;

import com.opensymphony.webwork.components.ActionComponent;
import com.opensymphony.webwork.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see ActionComponent
 */
public class ActionModel extends TagModel {
    public ActionModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new ActionComponent(stack, req, res);
    }
}
