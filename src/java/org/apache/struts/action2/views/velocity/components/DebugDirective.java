/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.velocity.components;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.Debug;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <code>DebugDirective</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id: DebugDirective.java,v 1.1 2006/01/26 17:28:34 rainerh Exp $
 */
public class DebugDirective extends AbstractDirective {
    public String getBeanName() {
        return "debug";
    }

    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Debug(stack, req, res);
    }
}
