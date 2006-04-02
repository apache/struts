/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp.ui;

import com.opensymphony.xwork.util.OgnlUtil;
import ognl.Ognl;
import ognl.OgnlException;


/**
 * @author Matt Ho <a href="mailto:matt@enginegreen.com">&lt;matt@enginegreen.com&gt;</a>
 * @version $Id: OgnlTool.java,v 1.6 2005/09/02 05:14:21 plightbo Exp $
 */
public class OgnlTool {
    private static OgnlTool instance = new OgnlTool();

    private OgnlTool() {
    }

    public static OgnlTool getInstance() {
        return instance;
    }

    public Object findValue(String expr, Object context) {
        try {
            return Ognl.getValue(OgnlUtil.compile(expr), context);
        } catch (OgnlException e) {
            return null;
        }
    }
}
