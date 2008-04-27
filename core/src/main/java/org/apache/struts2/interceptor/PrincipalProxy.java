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

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

/**
 * Proxy interface used together with PrincipalAware interface. It allows indirect
 * access to HttpServletRequest or PortletRequest Principal related methods.
 */
public interface PrincipalProxy {

    /**
     * True if the user is in the given role
     *
     * @param role The role
     * @return True if the user is in that role
     */
    boolean isUserInRole(String role);

    /**
     * Gets the user principal
     *
     * @return The principal
     */
    Principal getUserPrincipal();

    /**
     * Gets the user id
     *
     * @return The user id
     */
    String getRemoteUser();

    /**
     * Is the request using https?
     *
     * @return True if using https
     */
    boolean isRequestSecure();

    /**
     * Gets the request.
     *
     * @return The request
     * @deprecated To obtain the HttpServletRequest in your action, use
     *             {@link org.apache.struts2.servlet.ServletRequestAware}, since this method will be dropped in future.
     */
    HttpServletRequest getRequest();
}
