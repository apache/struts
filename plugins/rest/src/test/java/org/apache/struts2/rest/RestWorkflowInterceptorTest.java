/*
 * $Id: RestWorkflowInterceptor.java 666756 2008-06-11 18:11:00Z hermanns $
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

import com.mockobjects.dynamic.AnyConstraintMatcher;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionSupport;
import junit.framework.TestCase;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import java.util.HashMap;

public class RestWorkflowInterceptorTest extends TestCase {

    public void testCustomValidationFailureStatusCode() throws Exception {
        RestWorkflowInterceptor wf = new RestWorkflowInterceptor();

        ActionSupport action = new ActionSupport();
        action.addActionError("some error");

        wf.setValidationFailureStatusCode("666");
        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        Mock mockActionProxy = new Mock(ActionProxy.class);
        mockActionProxy.expectAndReturn("getConfig", null);
        mockActionInvocation.expectAndReturn("getAction", action);
        Mock mockContentTypeHandlerManager = new Mock(ContentTypeHandlerManager.class);
        mockContentTypeHandlerManager.expectAndReturn("handleResult", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                DefaultHttpHeaders headers = (DefaultHttpHeaders) args[1];
                return 666 == headers.getStatus();
            }
        }, null);
        wf.setContentTypeHandlerManager((ContentTypeHandlerManager) mockContentTypeHandlerManager.proxy());

        ActionContext.setContext(new ActionContext(new HashMap<String, Object>() {{
            put(ServletActionContext.ACTION_MAPPING, new ActionMapping());
        }}));
        wf.doIntercept((ActionInvocation) mockActionInvocation.proxy());
        mockContentTypeHandlerManager.verify();
        mockActionInvocation.verify();
    }
}
