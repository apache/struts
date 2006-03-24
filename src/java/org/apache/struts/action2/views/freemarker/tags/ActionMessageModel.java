package com.opensymphony.webwork.views.freemarker.tags;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.ActionMessage;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see ActionMessage
 */
public class ActionMessageModel extends TagModel {
    public ActionMessageModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new ActionMessage(stack, req, res);
    }
}
