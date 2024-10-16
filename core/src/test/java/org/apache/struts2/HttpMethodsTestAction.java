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

import org.apache.struts2.ActionSupport;
import org.apache.struts2.interceptor.httpmethod.AllowedHttpMethod;
import org.apache.struts2.interceptor.httpmethod.HttpDelete;
import org.apache.struts2.interceptor.httpmethod.HttpGet;
import org.apache.struts2.interceptor.httpmethod.HttpGetOrPost;
import org.apache.struts2.interceptor.httpmethod.HttpMethod;
import org.apache.struts2.interceptor.httpmethod.HttpMethodAware;
import org.apache.struts2.interceptor.httpmethod.HttpPost;
import org.apache.struts2.interceptor.httpmethod.HttpPut;

import static org.apache.struts2.interceptor.httpmethod.HttpMethod.POST;

@AllowedHttpMethod(POST)
public class HttpMethodsTestAction extends ActionSupport implements HttpMethodAware {

    private String resultName = null;
    private HttpMethod httpMethod;

    public HttpMethodsTestAction() {
    }

    public HttpMethodsTestAction(String resultName) {
        this.resultName = resultName;
    }

    @HttpGet
    public String onGetOnly() {
        return "onGetOnly";
    }

    @HttpPost
    public String onPostOnly() {
        return "onPostOnly";
    }

    @HttpGetOrPost
    public String onGetPostOnly() {
        return "onGetPostOnly";
    }

    @HttpPut @HttpPost
    public String onPutOrPost() {
        return "onPutOrPost";
    }

    @HttpDelete
    public String onDelete() {
        return "onDelete";
    }

    @Override
    public void setMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    @Override
    public String getBadRequestResultName() {
        return resultName;
    }
}
