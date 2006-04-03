/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.velocity.components;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.TreeNode;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <code>TreeNodeDirective</code>
 *
 * @author Rainer Hermanns
 * @version $Id: TreeNodeDirective.java,v 1.1 2006/01/26 17:37:42 rainerh Exp $
 */
public class TreeNodeDirective  extends AbstractDirective {
    public String getBeanName() {
        return "treenode";
    }

    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new TreeNode(stack, req, res);
    }
}

