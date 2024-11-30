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
package org.apache.struts2.interceptor.httpmethod;

import org.apache.struts2.ActionContext;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.mock.MockActionProxy;
import org.apache.struts2.HttpMethodsTestAction;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.TestAction;
import org.springframework.mock.web.MockHttpServletRequest;

public class HttpMethodInterceptorTest extends StrutsInternalTestCase {

    private HttpMethodInterceptor interceptor;
    private MockActionInvocation invocation;
    private MockActionProxy actionProxy;

    public void testNotAnnotatedAction() throws Exception {
        // given
        prepareActionInvocation(new TestAction());
        invocation.setResultCode("success");

        prepareRequest("post");

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("success", resultName);
    }

    public void testActionWithPostAllowed() throws Exception {
        // given
        prepareActionInvocation(new HttpMethodsTestAction());
        invocation.setResultCode("success");

        prepareRequest("post");

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("success", resultName);
    }

    public void testGetIsNotAllowed() throws Exception {
        // given
        prepareActionInvocation(new HttpMethodsTestAction());
        invocation.setResultCode("success");

        prepareRequest("get");

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("bad-request", resultName);
    }

    public void testGetIsNotAllowedWithCustomResultName() throws Exception {
        // given
        prepareActionInvocation(new HttpMethodsTestAction());
        interceptor.setBadRequestResultName("custom-bad-request");
        invocation.setResultCode("success");

        prepareRequest("get");

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("custom-bad-request", resultName);
    }

    public void testGetIsNotAllowedWithActionDefinedResultName() throws Exception {
        // given
        prepareActionInvocation(new HttpMethodsTestAction("action-bad-request"));
        interceptor.setBadRequestResultName("custom-bad-request");
        invocation.setResultCode("success");

        prepareRequest("get");

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("action-bad-request", resultName);
    }

    public void testGetOnlyOnMethod() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        prepareActionInvocation(action);
        actionProxy.setMethod("onGetOnly");
        actionProxy.setMethodSpecified(true);
        invocation.setResultCode("onGetOnly");

        prepareRequest("get");

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("onGetOnly", resultName);
        assertEquals(HttpMethod.GET, action.getHttpMethod());
    }

    public void testPostOnlyOnMethod() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        prepareActionInvocation(action);
        actionProxy.setMethod("onPostOnly");
        actionProxy.setMethodSpecified(true);

        invocation.setResultCode("onPostOnly");

        prepareRequest("post");

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("onPostOnly", resultName);
        assertEquals(HttpMethod.POST, action.getHttpMethod());
    }

    public void testGetPostOnlyOnMethod() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        prepareActionInvocation(action);
        actionProxy.setMethod("onGetPostOnly");
        actionProxy.setMethodSpecified(true);

        invocation.setResultCode("onGetPostOnly");

        prepareRequest("post");

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("onGetPostOnly", resultName);
        assertEquals(HttpMethod.POST, action.getHttpMethod());
    }

    public void testDeleteOnMethod() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        prepareActionInvocation(action);
        actionProxy.setMethod("onDelete");
        actionProxy.setMethodSpecified(true);

        invocation.setResultCode("onDelete");

        prepareRequest("DELETE");

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("onDelete", resultName);
        assertEquals(HttpMethod.DELETE, action.getHttpMethod());
    }

    public void testPutOnPutOrPostMethod() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        prepareActionInvocation(action);
        actionProxy.setMethod("onPutOrPost");
        actionProxy.setMethodSpecified(true);

        invocation.setResultCode("onPutOrPost");

        prepareRequest("PUT");

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("onPutOrPost", resultName);
        assertEquals(HttpMethod.PUT, action.getHttpMethod());
    }

    public void testPostOnPutOrPostMethod() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        prepareActionInvocation(action);
        actionProxy.setMethod("onPutOrPost");
        actionProxy.setMethodSpecified(true);

        invocation.setResultCode("onPutOrPost");

        prepareRequest("POST");

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("onPutOrPost", resultName);
        assertEquals(HttpMethod.POST, action.getHttpMethod());
    }

    private void prepareActionInvocation(Object action) {
        interceptor = new HttpMethodInterceptor();
        invocation = new MockActionInvocation();
        invocation.setAction(action);
        actionProxy = new MockActionProxy();
        invocation.setProxy(actionProxy);
    }

    private void prepareRequest(String httpMethod) {
        MockHttpServletRequest request = new MockHttpServletRequest(httpMethod, "/action");
        ActionContext.getContext().withServletRequest(request);
        invocation.setInvocationContext(ActionContext.getContext());
    }

}
