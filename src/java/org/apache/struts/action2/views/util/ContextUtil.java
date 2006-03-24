/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.util;

import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.webwork.views.jsp.ui.OgnlTool;
import com.opensymphony.webwork.config.Configuration;
import com.opensymphony.webwork.util.WebWorkUtil;
import com.opensymphony.webwork.WebWorkConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Value Stack's Context related Utilities.
 * 
 * @author plightbo
 * @author tm_jee
 * @version $Date: 2006/01/25 17:21:00 $ $Id: ContextUtil.java,v 1.7 2006/01/25 17:21:00 rainerh Exp $
 */
public class ContextUtil {
    public static final String REQUEST = "req";
    public static final String REQUEST2 = "request";
    public static final String RESPONSE = "res";
    public static final String RESPONSE2 = "response";
    public static final String SESSION = "session";
    public static final String BASE = "base";
    public static final String STACK = "stack";
    public static final String OGNL = "ognl";
    public static final String WEBWORK = "webwork";
    public static final String ACTION = "action";

    public static Map getStandardContext(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        HashMap map = new HashMap();
        map.put(REQUEST, req);
        map.put(REQUEST2, req);
        map.put(RESPONSE, res);
        map.put(RESPONSE2, res);
        map.put(SESSION, req.getSession(false));
        map.put(BASE, req.getContextPath());
        map.put(STACK, stack);
        map.put(OGNL, OgnlTool.getInstance());
        map.put(WEBWORK, new WebWorkUtil(stack, req, res));

        ActionInvocation invocation = (ActionInvocation) stack.getContext().get(ActionContext.ACTION_INVOCATION);
        if (invocation != null) {
            map.put(ACTION, invocation.getAction());
        }
        return map;
    }
    
    /**
     * Return true if either Configuration's altSyntax is on or the stack context's useAltSyntax is on
     * @param context stack's context
     * @return boolean
     */
    public static boolean isUseAltSyntax(Map context) {
        // We didn't make altSyntax static cause, if so, webwork.configuration.xml.reload will not work
        // plus the Configuration implementation should cache the properties, which WW's
        // configuration implementation does
        boolean altSyntax = "true".equals(Configuration.getString(WebWorkConstants.WEBWORK_TAG_ALTSYNTAX));
        return altSyntax ||(
                (context.containsKey("useAltSyntax") &&
                        context.get("useAltSyntax") != null &&
                        "true".equals(context.get("useAltSyntax").toString())));
    }

    /**
     * Returns a String for overriding the default templateSuffix if templateSuffix is on the stack
     * @param context stack's context
     * @return String
     */
    public static String getTemplateSuffix(Map context) {
        return context.containsKey("templateSuffix") ? (String) context.get("templateSuffix") : null;
    }
}
