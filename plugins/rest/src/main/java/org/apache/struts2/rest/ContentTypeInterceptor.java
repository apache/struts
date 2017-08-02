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

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.handler.ContentTypeHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Uses the content handler to apply the request body to the action
 */
public class ContentTypeInterceptor implements Interceptor {

    private static final long serialVersionUID = 1L;
    ContentTypeHandlerManager selector;
    
    @Inject
    public void setContentTypeHandlerSelector(ContentTypeHandlerManager sel) {
        this.selector = sel;
    }
    
    public void destroy() {}

    public void init() {}

    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        ContentTypeHandler handler = selector.getHandlerForRequest(request);
        
        Object target = invocation.getAction();
        if (target instanceof ModelDriven) {
            target = ((ModelDriven)target).getModel();
        }
        
        if (request.getContentLength() > 0) {
            InputStream is = request.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            handler.toObject(invocation, reader, target);
        }
        return invocation.invoke();
    }

}
