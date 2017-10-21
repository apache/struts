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
package org.apache.struts2;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;
import org.apache.struts2.views.util.UrlHelper;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.HttpJspPage;
import java.util.HashMap;
import java.util.Map;

/**
 * Maintains a cache of jsp locations -&gt; servlet instances for those jsps. When a jsp is requested
 * from the cache, the cache will block if the jsp was not compiled already, and wait for the compilation
 */
public abstract class JSPRuntime {
    //maps from jsp path -> pagelet
    protected static final ServletCache servletCache = new ServletCache();

    public static void clearCache() {
        servletCache.clear();
    }

    public static void handle(String location) throws Exception {
        handle(location, false);
    }

    public static void handle(String location, boolean flush) throws Exception {
        final HttpServletResponse response = ServletActionContext.getResponse();
        final HttpServletRequest request = ServletActionContext.getRequest();
        final UrlHelper urlHelper = ServletActionContext.getContext().getInstance(UrlHelper.class);

        int i = location.indexOf("?");
        if (i > 0) {
            //extract params from the url and add them to the request
            String query = location.substring(i + 1);
            Map<String, Object> queryParams = urlHelper.parseQueryString(query, true);
            if (queryParams != null && !queryParams.isEmpty()) {
                Map<String, Parameter> newParams = new HashMap<>();
                for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                    newParams.put(entry.getKey(), new Parameter.Request(entry.getKey(), entry.getValue()));
                }
                ActionContext.getContext().getParameters().appendAll(newParams);
            }
            location = location.substring(0, i);
        }

        Servlet servlet = servletCache.get(location);
        HttpJspPage page = (HttpJspPage) servlet;

        page._jspService(request, response);
        if (flush)
            response.flushBuffer();
    }
}
