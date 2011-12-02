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

package org.apache.struts2.rest;

import org.apache.struts2.rest.handler.ContentTypeHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.config.entities.ActionConfig;

import java.io.IOException;

/**
 * Manages content type handlers
 */
public interface ContentTypeHandlerManager {
    String STRUTS_REST_HANDLER_OVERRIDE_PREFIX = "struts.rest.handlerOverride.";

    /**
     * Gets the handler for the request by looking at the request content type and extension
     * @param req The request
     * @return The appropriate handler
     */
    ContentTypeHandler getHandlerForRequest(HttpServletRequest req);

    /**
     * Gets the handler for the response by looking at the extension of the request
     * @param req The request
     * @return The appropriate handler
     */
    ContentTypeHandler getHandlerForResponse(HttpServletRequest req, HttpServletResponse res);

    /**
     * Handles the result using handlers to generate content type-specific content
     *
     * @param actionConfig The action config for the current request
     * @param methodResult The object returned from the action method
     * @param target The object to return, usually the action object
     * @return The new result code to process
     * @throws IOException If unable to write to the response
     */
    String handleResult(ActionConfig actionConfig, Object methodResult, Object target)
            throws IOException;
    
    /**
     * Finds the extension in the url
     * 
     * @param url The url
     * @return The extension
     */
    String findExtension(String url);
}
