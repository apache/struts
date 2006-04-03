/*
 * Copyright (c) 2005 Opensymphony. All Rights Reserved.
 */
package org.apache.struts.action2.views.freemarker.tags;

import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.Debug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Debug
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public class DebugModel extends TagModel {

    public DebugModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new Debug(stack, req, res);
    }

}
