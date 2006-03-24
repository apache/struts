/*
 * Copyright (c) 2005 Opensymphony. All Rights Reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.TreeNode;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see TreeNode
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class TreeNodeTag extends AbstractClosingTag {
    private String label;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new TreeNode(stack,req,res);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    protected void populateParams() {
        if (label != null) {
            TreeNode treeNode = (TreeNode)component;
            treeNode.setLabel(label);
        }
        super.populateParams();
    }
}
