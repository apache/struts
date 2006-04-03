/*
 * Copyright (c) 2005 Opensymphony. All Rights Reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.TreeNode;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see TreeNode
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class TreeNodeTag extends AbstractClosingTag {
	
	private static final long serialVersionUID = 7340746943017900803L;
	
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
