/*
 * Copyright (c) 2005 Opensymphony. All Rights Reserved.
 */
package org.apache.struts.webwork.views.freemarker.tags;

import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.Debug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Debug
 *
 * @author Rainer Hermanns
 * @version $Id: DebugModel.java,v 1.2 2006/01/24 16:37:52 rainerh Exp $
 */
public class DebugModel extends TagModel {

    public DebugModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new Debug(stack, req, res);
    }

}
