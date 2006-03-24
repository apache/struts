/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.velocity.components;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.Tree;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <code>TreeDirective</code>
 *
 * @author Rainer Hermanns
 * @version $Id: TreeDirective.java,v 1.1 2006/01/26 17:37:41 rainerh Exp $
 */
public class TreeDirective  extends AbstractDirective {
    public String getBeanName() {
        return "tree";
    }

    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Tree(stack, req, res);
    }
}
