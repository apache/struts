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

import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.RequestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Contains execution operations for filters
 */
public class ExecuteOperations {

    private Dispatcher dispatcher;

    public ExecuteOperations(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * Tries to execute a request for a static resource
     *
     * @param request servlet request
     * @param response servlet response
     * @return True if it was handled, false if the filter should fall through
     * @throws IOException on any io error
     * @throws ServletException on any servlet related error
     */
    public boolean executeStaticResourceRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // there is no action in this request, should we look for a static resource?
        String resourcePath = RequestUtils.getServletPath(request);

        if ("".equals(resourcePath) && null != request.getPathInfo()) {
            resourcePath = request.getPathInfo();
        }

        StaticContentLoader staticResourceLoader = dispatcher.getContainer().getInstance(StaticContentLoader.class);
        if (staticResourceLoader.canHandle(resourcePath)) {
            staticResourceLoader.findStaticResource(resourcePath, request, response);
            // The framework did its job here
            return true;

        } else {
            // this is a normal request, let it pass through
            return false;
        }
    }

    /**
     * Executes an action
     *
     * @param request servlet request
     * @param response servlet response
     * @param mapping action mapping
     *
     * @throws ServletException  on any servlet related error
     */
    public void executeAction(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ServletException {
        dispatcher.serviceAction(request, response, mapping);
    }
}
