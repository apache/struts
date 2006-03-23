/*
 * Copyright (c) 2005 Opensymphony. All Rights Reserved.
 */
package com.opensymphony.webwork.views.freemarker.tags;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.Tree;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TreeModel
 * <p/>
 * Created : Dec 12, 2005 3:45:44 PM
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class TreeModel extends TagModel {
    public TreeModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new Tree(stack,req,res);
    }
}
