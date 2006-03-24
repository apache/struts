/*
 * Copyright (c) 2005 Opensymphony. All Rights Reserved.
 */
package com.opensymphony.webwork.views.freemarker.tags;

import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.TreeNode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TreeNodeModel
 * <p/>
 * Created : Dec 12, 2005 3:54:41 PM
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class TreeNodeModel extends TagModel {
    public TreeNodeModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new TreeNode(stack,req,res);
    }
}
