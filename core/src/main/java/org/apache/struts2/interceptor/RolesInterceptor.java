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

package org.apache.struts2.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import org.apache.struts2.ServletActionContext;

/**
 * <!-- START SNIPPET: description --> This interceptor ensures that the action
 * will only be executed if the user has the correct role. <!--
 * END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>allowedRoles - a comma-separated list of roles to allow</li>
 *
 * <li>disallowedRoles - a comma-separated list of roles to disallow</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: extending --> There are two extensions to the
 * existing interceptor:
 * <ul>
 *   <li>isAllowed(HttpServletRequest,Object) - whether or not to allow
 *       the passed action execution with this request</li>
 *   <li>handleRejection(ActionInvocation) - handles an unauthorized
 *       request.</li>
 * </ul>
 * <!-- END SNIPPET: extending -->
 *
 * <pre>
 *  &lt;!-- START SNIPPET: example --&gt;
 *  &lt;!-- only allows the admin and member roles --&gt;
 *  &lt;action name=&quot;someAction&quot; class=&quot;com.examples.SomeAction&quot;&gt;
 *      &lt;interceptor-ref name=&quot;completeStack&quot;/&gt;
 *      &lt;interceptor-ref name=&quot;roles&quot;&gt;
 *        &lt;param name=&quot;allowedRoles&quot;&gt;admin,member&lt;/param&gt;
 *      &lt;/interceptor-ref&gt;
 *      &lt;result name=&quot;success&quot;&gt;good_result.ftl&lt;/result&gt;
 *  &lt;/action&gt;
 *  &lt;!-- END SNIPPET: example --&gt;
 * </pre>
 */
public class RolesInterceptor extends AbstractInterceptor {

    private List<String> allowedRoles = new ArrayList<String>();
    private List<String> disallowedRoles = new ArrayList<String>();

    public void setAllowedRoles(String roles) {
        this.allowedRoles = stringToList(roles);
    }

    public void setDisallowedRoles(String roles) {
        this.disallowedRoles = stringToList(roles);
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        String result = null;
        if (!isAllowed(request, invocation.getAction())) {
            result = handleRejection(invocation, response);
        } else {
            result = invocation.invoke();
        }
        return result;
    }

    /**
     * Splits a string into a List
     */
    protected List<String> stringToList(String val) {
        if (val != null) {
            String[] list = val.split("[ ]*,[ ]*");
            return Arrays.asList(list);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Determines if the request should be allowed for the action
     *
     * @param request The request
     * @param action The action object
     * @return True if allowed, false otherwise
     */
    protected boolean isAllowed(HttpServletRequest request, Object action) {
        if (allowedRoles.size() > 0) {
            boolean result = false;
            for (String role : allowedRoles) {
                if (request.isUserInRole(role)) {
                    result = true;
                }
            }
            return result;
        } else if (disallowedRoles.size() > 0) {
            for (String role : disallowedRoles) {
                if (request.isUserInRole(role)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Handles a rejection by sending a 403 HTTP error
     *
     * @param invocation The invocation
     * @return The result code
     * @throws Exception
     */
    protected String handleRejection(ActionInvocation invocation,
            HttpServletResponse response)
            throws Exception {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return null;
    }
}
