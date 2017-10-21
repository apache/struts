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
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <!-- START SNIPPET: description -->
 * <p>
 * This interceptor ensures that the action will only be executed if the user has the correct role.
 * </p>
 * <!-- END SNIPPET: description -->
 *
 * <p><u>Interceptor parameters:</u></p>
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
 * <p>
 * When both allowedRoles and disallowedRoles are configured, then disallowedRoles
 * takes precedence, applying the following logic: 
 *  (if ((inRole(role1) || inRole(role2) || ... inRole(roleN)) &amp;&amp;
 *       !inRole(roleA) &amp;&amp; !inRole(roleB) &amp;&amp; ... !inRole(roleZ))
 *  { //permit ...
 * </p>
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: extending -->
 * <p>
 * There are three extensions to the existing interceptor:
 * </p>
 *
 * <ul>
 *   <li>isAllowed(HttpServletRequest,Object) - whether or not to allow
 *       the passed action execution with this request</li>
 *   <li>handleRejection(ActionInvocation) - handles an unauthorized
 *       request.</li>
 *   <li>areRolesValid(List&lt;String&gt; roles) - allows subclasses to lookup roles
 *   to ensure they are valid.  If not valid, RolesInterceptor will log the error and 
 *   cease to function.  This helps prevent security misconfiguration flaws.
 *   
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

    private static final Logger LOG = LogManager.getLogger(RolesInterceptor.class);

    private boolean isProperlyConfigured = true;
    
    protected List<String> allowedRoles = Collections.emptyList();
    protected List<String> disallowedRoles = Collections.emptyList();

    public void setAllowedRoles(String roles) {
        allowedRoles = stringToList(roles);
        checkRoles(allowedRoles);
    }

    public void setDisallowedRoles(String roles) {
        disallowedRoles = stringToList(roles);
        checkRoles(disallowedRoles);
    }
    
    private void checkRoles(List<String> roles){
        if (!areRolesValid(roles)){
          LOG.fatal("An unknown Role was configured: {}", roles);
          isProperlyConfigured = false;
          throw new IllegalArgumentException("An unknown role was configured: " + roles);
        }
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        if (!isProperlyConfigured) {
          throw new IllegalArgumentException("RolesInterceptor is misconfigured, check logs for erroneous configuration!");
        }
        if (!isAllowed(request, invocation.getAction())) {
            LOG.debug("Request is NOT allowed. Rejecting.");
            return handleRejection(invocation, response);
        } else {
            LOG.debug("Request is allowed. Invoking.");
            return invocation.invoke();
        }
    }

    /**
     * Splits a string into a List
     * @param val the string to split
     * @return the string list
     */
    protected List<String> stringToList(String val) {
        if (val != null) {
            String[] list = val.split("[ ]*,[ ]*");
            return Arrays.asList(list);
        } else {
            return Collections.emptyList();
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
        for (String role : disallowedRoles) {
            if (request.isUserInRole(role)) {
                LOG.debug("User role '{}' is in the disallowedRoles list.", role);
                return false;
            }
        }
  
        if (allowedRoles.isEmpty()){
            LOG.debug("The allowedRoles list is empty.");
            return true;
        }
        
        for (String role : allowedRoles) {
            if (request.isUserInRole(role)) {
                LOG.debug("User role '{}' is in the allowedRoles list.", role);
                return true;
            }
        }
        
        return false;
    }

    /**
     * Handles a rejection by sending a 403 HTTP error
     *
     * @param invocation The invocation
     * @param response the servlet response object
     * @return The result code
     * @throws Exception in case of any error
     */
    protected String handleRejection(ActionInvocation invocation, HttpServletResponse response) throws Exception {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return null;
    }
    
    /**
     * Extension point for sub-classes to test if configured roles are known valid roles.
     * Implementations are encouraged to implement this method to prevent misconfigured roles.
     * If this method returns false, the RolesInterceptor will be disabled and block all requests.
     * 
     * @param roles allowed and disallowed roles
     * @return whether the roles are valid or not (always true for the default implementation)
     */
    protected boolean areRolesValid(List<String> roles){
        return true;
    }

}
