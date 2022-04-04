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

package org.apache.struts2.util;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.util.DefaultUrlHelper;
import org.apache.struts2.views.util.UrlHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * A bean that can generate a URL.
 *
 */
public class URLBean {

    HashMap<String, String> params;
    HttpServletRequest request;
    HttpServletResponse response;
    String page;

    private UrlHelper urlHelper;

    public URLBean setPage(String page) {
        this.page = page;
        return this;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
        urlHelper = ServletActionContext.getContext().getInstance(DefaultUrlHelper.class);
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getURL() {
        // all this trickier with maps is to reduce the number of objects created
        Map<String, Object> fullParams = null;

        if (params != null) {
            fullParams = new HashMap<String, Object>();
        }

        if (page == null) {
            // No particular page requested, so go to "same page"
            // Add query params to parameters
            if (fullParams != null) {
                fullParams.putAll(request.getParameterMap());
            } else {
                fullParams = request.getParameterMap();
            }
        }

        // added parameters override, just like in URLTag
        if (params != null) {
            fullParams.putAll(params);
        }

        return urlHelper.buildUrl(page, request, response, fullParams);
    }

    public URLBean addParameter(String name, Object value) {
        if (params == null) {
            params = new HashMap<String, String>();
        }

        if (value == null) {
            params.remove(name);
        } else {
            params.put(name, value.toString());
        }

        return this;
    }

    public String toString() {
        return getURL();
    }
}
