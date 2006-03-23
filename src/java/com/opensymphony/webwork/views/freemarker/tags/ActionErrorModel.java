package com.opensymphony.webwork.views.freemarker.tags;

import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.ActionError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see ActionError
 */
public class ActionErrorModel extends TagModel {
    public ActionErrorModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new ActionError(stack, req, res);
    }
}
