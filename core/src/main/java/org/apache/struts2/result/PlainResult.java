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
package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.struts2.StrutsException;
import org.apache.struts2.result.plain.HttpHeader;
import org.apache.struts2.result.plain.ResponseBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * This result can only be used in code, as a result of action's method, eg.:
 * <p>
 * public PlainResult execute() {
 * return response -> response.write("");
 * }
 * <p>
 * Please notice the result type of the method is a PlainResult not a String.
 */
public interface PlainResult extends Result {

    Logger LOG = LogManager.getLogger(PlainResult.class);

    @Override
    default void execute(ActionInvocation invocation) throws Exception {
        if (invocation == null) {
            throw new IllegalArgumentException("Invocation cannot be null!");
        }

        LOG.debug("Executing plain result");
        ResponseBuilder builder = new ResponseBuilder();
        write(builder);

        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();

        if (response.isCommitted()) {
            if (ignoreCommitted()) {
                LOG.warn("Http response already committed, ignoring & skipping!");
                return;
            } else {
                throw new StrutsException("Http response already committed, cannot modify it!");
            }
        }

        for (HttpHeader<String> header : builder.getStringHeaders()) {
            LOG.debug(new ParameterizedMessage("A string header: {} = {}", header.getName(), header.getValue()));
            response.addHeader(header.getName(), header.getValue());
        }
        for (HttpHeader<Long> header : builder.getDateHeaders()) {
            LOG.debug(new ParameterizedMessage("A date header: {} = {}", header.getName(), header.getValue()));
            response.addDateHeader(header.getName(), header.getValue());
        }
        for (HttpHeader<Integer> header : builder.getIntHeaders()) {
            LOG.debug(new ParameterizedMessage("An int header: {} = {}", header.getName(), header.getValue()));
            response.addIntHeader(header.getName(), header.getValue());
        }

        for (Cookie cookie : builder.getCookies()) {
            LOG.debug(new ParameterizedMessage("A cookie: {} = {}", cookie.getName(), cookie.getValue()));
            response.addCookie(cookie);
        }

        response.getWriter().write(builder.getBody());
        response.flushBuffer();
    }

    /**
     * Implement this method in action using lambdas
     *
     * @param response a response builder used to build a Http response
     */
    void write(ResponseBuilder response);

    /**
     * Controls if result should ignore already committed Http response
     * If set to true only a warning will be issued and the rest of the result
     * will be skipped
     *
     * @return boolean false by default which means an exception will be thrown
     */
    default boolean ignoreCommitted() {
        return false;
    }

}

