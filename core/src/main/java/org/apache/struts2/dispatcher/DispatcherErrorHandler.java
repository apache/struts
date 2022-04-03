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
package org.apache.struts2.dispatcher;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implementation of this interface is used to handle internal errors or missing resources.
 * Basically it sends back HTTP error codes or error page depends on requirements.
 */
public interface DispatcherErrorHandler {

    /**
     * Init instance after creating {@link org.apache.struts2.dispatcher.Dispatcher}
     * @param ctx current {@link javax.servlet.ServletContext}
     */
    public void init(ServletContext ctx);

    /**
     * Handle passed error code or exception
     *
     * @param request current {@link javax.servlet.http.HttpServletRequest}
     * @param response current {@link javax.servlet.http.HttpServletResponse}
     * @param code HTTP Error Code, see {@link javax.servlet.http.HttpServletResponse} for possible error codes
     * @param e Exception to report
     */
    public void handleError(HttpServletRequest request, HttpServletResponse response, int code, Exception e);

}
