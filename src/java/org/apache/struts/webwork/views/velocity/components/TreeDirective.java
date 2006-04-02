/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.velocity.components;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.Tree;
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
