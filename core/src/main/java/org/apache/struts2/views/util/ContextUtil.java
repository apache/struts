/*
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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.util.StrutsUtil;
import org.apache.struts2.views.jsp.ui.OgnlTool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Value Stack's Context related Utilities.
 */
public class ContextUtil {
    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";
    public static final String SESSION = "session";
    public static final String BASE = "base";
    public static final String STACK = "stack";
    public static final String OGNL = "ognl";
    public static final String STRUTS = "struts";
    public static final String ACTION = "action";

    public static Map<String, Object> getStandardContext(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(REQUEST, req);
        map.put(RESPONSE, res);
        map.put(SESSION, req.getSession(false));
        map.put(BASE, req.getContextPath());
        map.put(STACK, stack);
        map.put(OGNL, stack.getActionContext().getContainer().getInstance(OgnlTool.class));
        map.put(STRUTS, new StrutsUtil(stack, req, res));

        ActionInvocation invocation = stack.getActionContext().getActionInvocation();
        if (invocation != null) {
            map.put(ACTION, invocation.getAction());
        }
        return map;
    }

    /**
     * Returns a String for overriding the default templateSuffix if templateSuffix is on the stack
     *
     * @param context stack's context
     * @return String
     */
    public static String getTemplateSuffix(Map<String, Object> context) {
        return context.containsKey("templateSuffix") ? (String) context.get("templateSuffix") : null;
    }

}
