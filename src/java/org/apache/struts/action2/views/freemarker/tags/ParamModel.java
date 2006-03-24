package com.opensymphony.webwork.views.freemarker.tags;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.Param;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: plightbo
 * Date: Aug 7, 2005
 * Time: 3:18:57 PM
 */
public class ParamModel extends TagModel {
    public ParamModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new Param(stack);
    }
}
