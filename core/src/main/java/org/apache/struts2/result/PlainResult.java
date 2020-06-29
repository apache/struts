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
import org.apache.struts2.StrutsException;
import org.apache.struts2.result.plain.HttpHeader;
import org.apache.struts2.result.plain.ResponseBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public interface PlainResult extends Result {

    @Override
    default void execute(ActionInvocation invocation) throws Exception {
        ResponseBuilder builder = new ResponseBuilder();
        write(builder);

        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();

        if (response.isCommitted()) {
            throw new StrutsException("Http response already committed, cannot modify it!");
        }

        for (HttpHeader<String> header : builder.getStringHeaders()) {
            response.addHeader(header.getName(), header.getValue());
        }
        for (HttpHeader<Long> header : builder.getDateHeaders()) {
            response.addDateHeader(header.getName(), header.getValue());
        }
        for (HttpHeader<Integer> header : builder.getIntHeaders()) {
            response.addIntHeader(header.getName(), header.getValue());
        }

        for (Cookie cookie : builder.getCookies()) {
            response.addCookie(cookie);
        }

        response.getWriter().write(builder.getBody());
        response.flushBuffer();
    }

    void write(ResponseBuilder response);

}

