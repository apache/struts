/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.views.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.util.StrutsUtil;
import org.apache.struts2.views.jsp.ui.OgnlTool;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * Value Stack's Context related Utilities.
 *
 */
public class ContextUtil {
    public static final String REQUEST = "request";
    public static final String REQUEST2 = "request";
    public static final String RESPONSE = "response";
    public static final String RESPONSE2 = "response";
    public static final String SESSION = "session";
    public static final String BASE = "base";
    public static final String STACK = "stack";
    public static final String OGNL = "ognl";
    public static final String STRUTS = "struts";
    public static final String ACTION = "action";
    
    public static Map getStandardContext(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        HashMap map = new HashMap();
        map.put(REQUEST, req);
        map.put(REQUEST2, req);
        map.put(RESPONSE, res);
        map.put(RESPONSE2, res);
        map.put(SESSION, req.getSession(false));
        map.put(BASE, req.getContextPath());
        map.put(STACK, stack);
        map.put(OGNL, ((Container)stack.getContext().get(ActionContext.CONTAINER)).getInstance(OgnlTool.class));
        map.put(STRUTS, new StrutsUtil(stack, req, res));

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
        // We didn't make altSyntax static cause, if so, struts.configuration.xml.reload will not work
        // plus the Configuration implementation should cache the properties, which the framework's
        // configuration implementation does
        return "true".equals(((Container)context.get(ActionContext.CONTAINER)).getInstance(String.class, StrutsConstants.STRUTS_TAG_ALTSYNTAX)) ||(
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
