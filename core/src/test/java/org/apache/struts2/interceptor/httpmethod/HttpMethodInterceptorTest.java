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

import jakarta.servlet.http.HttpServletResponse;
import org.apache.struts2.action.Action;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.result.Result;
import org.apache.struts2.UnknownHandlerManager;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.entities.ResultConfig;
import org.apache.struts2.inject.Container;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.mock.MockActionProxy;
import org.apache.struts2.HttpMethodsTestAction;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.TestAction;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    /**
     * Simulates a wildcard action like {@code <action name="Wild-*" method="{1}">}
     * resolving to method "onPostOnly" (annotated with @HttpPost).
     * With the fix in DefaultActionProxy.resolveMethod(), config-resolved methods
     * set isMethodSpecified()=true, so the interceptor checks method-level annotations.
     * A GET request should be rejected because @HttpPost only allows POST.
     */
    public void testWildcardResolvedMethodWithPostAnnotationRejectsGet() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        prepareActionInvocation(action);
        // Simulate wildcard resolution: Wild-onPostOnly -> method="onPostOnly"
        actionProxy.setMethod("onPostOnly");
        // After the fix, config-resolved methods have methodSpecified=true
        actionProxy.setMethodSpecified(true);

        invocation.setResultCode("onPostOnly");

        prepareRequest("get");

        // when
        String resultName = interceptor.intercept(invocation);

        // then - interceptor checks method-level @HttpPost and rejects GET
        assertEquals("bad-request", resultName);
    }

    /**
     * Counterpart: same wildcard scenario but with POST request — should succeed.
     */
    public void testWildcardResolvedMethodWithPostAnnotationAllowsPost() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        prepareActionInvocation(action);
        actionProxy.setMethod("onPostOnly");
        actionProxy.setMethodSpecified(true);

        invocation.setResultCode("onPostOnly");

        prepareRequest("post");

        // when
        String resultName = interceptor.intercept(invocation);

        // then - interceptor checks method-level @HttpPost and allows POST
        assertEquals("onPostOnly", resultName);
    }

    private void prepareActionInvocation(Object action) {
        prepareActionInvocation(action, actionConfig("bad-request", "custom-bad-request", "action-bad-request"));
    }

    private void prepareActionInvocation(Object action, ActionConfig config) {
        interceptor = new HttpMethodInterceptor();
        invocation = new MockActionInvocation();
        invocation.setAction(action);
        actionProxy = new MockActionProxy();
        actionProxy.setConfig(config);
        invocation.setProxy(actionProxy);
    }

    private ActionConfig actionConfig(String... resultNames) {
        ActionConfig.Builder builder = new ActionConfig.Builder("package", "action", TestAction.class.getName());
        for (String resultName : resultNames) {
            builder.addResultConfig(new ResultConfig.Builder(resultName, "result").build());
        }
        return builder.build();
    }


    /**
     * Regression: wildcard-resolved method with NO method-level annotation on a class
     * that has a class-level @AllowedHttpMethod(POST) — GET must be rejected.
     * The WW-5535 fix introduced an if/else-if that made the class-level check
     * unreachable when isMethodSpecified()=true and the method is unannotated.
     */
    public void testWildcardResolvedUnannotatedMethodRespectsClassLevelAnnotation() throws Exception {
        // given — HttpMethodsTestAction has @AllowedHttpMethod(POST) at class level
        // execute() inherited from ActionSupport has no method-level HTTP annotation
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        prepareActionInvocation(action);
        actionProxy.setMethod("execute");
        actionProxy.setMethodSpecified(true); // simulates wildcard-resolved, not default

        prepareRequest("get");

        // when
        String resultName = interceptor.intercept(invocation);

        // then — class-level @AllowedHttpMethod(POST) must still be enforced
        assertEquals("bad-request", resultName);
    }

    /**
     * Counterpart: POST on wildcard-resolved unannotated method must succeed
     * when the class allows POST via class-level annotation.
     */
    public void testWildcardResolvedUnannotatedMethodAllowsPostWithClassLevelAnnotation() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        prepareActionInvocation(action);
        actionProxy.setMethod("execute");
        actionProxy.setMethodSpecified(true);
        invocation.setResultCode("success");

        prepareRequest("post");

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("success", resultName);
    }

    /**
     * Integration regression for WW-5535: exercise the full wildcard resolution path through
     * a real {@link org.apache.struts2.DefaultActionProxy} (not a MockActionProxy).
     * <p>
     * Config: {@code <action name="Wild-*" class="HttpMethodsTestAction" method="{1}">} —
     * URL {@code Wild-execute} resolves to method {@code execute()} inherited from
     * {@code ActionSupport} (no method-level HTTP annotation). The class carries
     * {@code @AllowedHttpMethod(POST)}, so GET must be rejected end-to-end.
     */
    public void testWildcardResolvedExecuteRejectsGetThroughRealProxy() throws Exception {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(
            "org/apache/struts2/config/providers/xwork-test-allowed-methods.xml"));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/Wild-execute");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Map<String, Object> extraContext = ActionContext.of()
            .withServletRequest(request)
            .withServletResponse(response)
            .getContextMap();

        ActionProxy proxy = actionProxyFactory.createActionProxy("", "Wild-execute", null, extraContext);

        // sanity: confirms the WW-5535 fix in DefaultActionProxy.resolveMethod() is wired up
        assertEquals("execute", proxy.getMethod());
        assertTrue("Wildcard-resolved method must report isMethodSpecified()=true", proxy.isMethodSpecified());

        HttpMethodInterceptor realInterceptor = new HttpMethodInterceptor();
        String result = realInterceptor.intercept(proxy.getInvocation());

        // class-level @AllowedHttpMethod(POST) must still be enforced even though the resolved
        // method carries no method-level annotation — this is what #1690 fixed
        assertEquals(Action.NONE, result);
        assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.getStatus());
        assertEquals("POST", response.getHeader("Allow"));
    }

    public void testAnnotationWithoutMappedResultSends405() throws Exception {
        prepareActionInvocation(new HttpMethodsTestAction(), actionConfig());
        MockHttpServletResponse response = prepareRequest("get");

        String resultName = interceptor.intercept(invocation);

        assertEquals(Action.NONE, resultName);
        assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.getStatus());
        assertEquals("POST", response.getHeader("Allow"));
    }

    public void testConfigAndAnnotationMustBothAllowTheMethod() throws Exception {
        ActionConfig config = new ActionConfig.Builder(actionConfig("bad-request"))
            .addHttpMethods(List.of(HttpMethod.GET))
            .build();
        prepareActionInvocation(new HttpMethodsTestAction(), config);
        actionProxy.setMethod("onGetPostOnly");
        actionProxy.setMethodSpecified(true);
        invocation.setResultCode("onGetPostOnly");

        prepareRequest("get");
        assertEquals("onGetPostOnly", interceptor.intercept(invocation));

        prepareRequest("post");
        assertEquals("bad-request", interceptor.intercept(invocation));
    }

    public void testAnnotationNarrowsConfig() throws Exception {
        ActionConfig config = new ActionConfig.Builder(actionConfig("bad-request"))
            .addHttpMethods(List.of(HttpMethod.GET, HttpMethod.POST))
            .build();
        prepareActionInvocation(new HttpMethodsTestAction(), config);
        invocation.setResultCode("success");

        prepareRequest("post");
        assertEquals("success", interceptor.intercept(invocation));

        prepareRequest("get");
        assertEquals("bad-request", interceptor.intercept(invocation));
    }

    public void testUnknownHandlerResultIsUsedInsteadOf405() throws Exception {
        prepareActionInvocation(new HttpMethodsTestAction(), actionConfig());
        UnknownHandlerManager unknownHandlerManager = mock(UnknownHandlerManager.class);
        when(unknownHandlerManager.hasUnknownHandlers()).thenReturn(true);
        Result unknownResult = mock(Result.class);
        when(unknownHandlerManager.handleUnknownResult(any(), any(), any(), eq("bad-request"))).thenReturn(unknownResult);
        Container mockContainer = mock(Container.class);
        when(mockContainer.getInstance(UnknownHandlerManager.class)).thenReturn(unknownHandlerManager);
        MockHttpServletResponse response = prepareRequest("get");
        ActionContext.getContext().withContainer(mockContainer);

        String resultName = interceptor.intercept(invocation);

        assertEquals("bad-request", resultName);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    public void testConfigRestrictsMethodWithoutAnnotation() throws Exception {
        ActionConfig config = new ActionConfig.Builder(actionConfig("bad-request"))
            .addHttpMethods(List.of(HttpMethod.GET))
            .build();
        prepareActionInvocation(new TestAction(), config);
        invocation.setResultCode("success");

        prepareRequest("get");
        assertEquals("success", interceptor.intercept(invocation));

        prepareRequest("post");
        assertEquals("bad-request", interceptor.intercept(invocation));
    }

    public void testConfigRejectsUnknownRequestMethod() throws Exception {
        ActionConfig config = new ActionConfig.Builder(actionConfig())
            .addHttpMethods(List.of(HttpMethod.GET))
            .build();
        prepareActionInvocation(new TestAction(), config);
        MockHttpServletResponse response = prepareRequest("PROPFIND");

        String resultName = interceptor.intercept(invocation);

        assertEquals(Action.NONE, resultName);
        assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.getStatus());
        assertEquals("GET", response.getHeader("Allow"));
    }

    public void testHttpMethodAwareActionWithUnknownRequestMethod() throws Exception {
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        prepareActionInvocation(action, actionConfig());
        MockHttpServletResponse response = prepareRequest("PROPFIND");

        String resultName = interceptor.intercept(invocation);

        assertEquals(Action.NONE, resultName);
        assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.getStatus());
        assertEquals("POST", response.getHeader("Allow"));
        assertNull(action.getHttpMethod());
    }

    public void testHttpMethodAttributeRejectsWith405AndAllowHeader() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ActionProxy proxy = proxyFor("", "PostOnly", "GET", response);

        String result = new HttpMethodInterceptor().intercept(proxy.getInvocation());

        assertEquals(Action.NONE, result);
        assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.getStatus());
        assertEquals("POST", response.getHeader("Allow"));
    }

    public void testHttpMethodAttributeAllowsListedMethod() throws Exception {
        ActionProxy proxy = proxyFor("", "PostOnly", "POST", new MockHttpServletResponse());

        String result = new HttpMethodInterceptor().intercept(proxy.getInvocation());

        assertEquals(Action.SUCCESS, result);
    }

    public void testHttpMethodAttributeListsAllMethodsInAllowHeader() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ActionProxy proxy = proxyFor("", "GetOrPost", "PUT", response);

        String result = new HttpMethodInterceptor().intercept(proxy.getInvocation());

        assertEquals(Action.NONE, result);
        assertEquals("GET, POST", response.getHeader("Allow"));
    }

    public void testHttpMethodAttributeUsesMappedActionResult() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ActionProxy proxy = proxyFor("", "PostOnlyWithResult", "GET", response);

        String result = new HttpMethodInterceptor().intercept(proxy.getInvocation());

        assertEquals("bad-request", result);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    public void testHttpMethodAttributeUsesMappedGlobalResult() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ActionProxy proxy = proxyFor("/global", "PostOnly", "GET", response);

        String result = new HttpMethodInterceptor().intercept(proxy.getInvocation());

        assertEquals("bad-request", result);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    public void testHttpMethodAttributeUsesCatchAllResult() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ActionProxy proxy = proxyFor("", "PostOnlyWithCatchAllResult", "GET", response);

        String result = new HttpMethodInterceptor().intercept(proxy.getInvocation());

        assertEquals("bad-request", result);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    public void testHttpMethodAttributeOnWildcardAction() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ActionProxy proxy = proxyFor("", "PostWild-execute", "GET", response);

        String result = new HttpMethodInterceptor().intercept(proxy.getInvocation());

        assertEquals(Action.NONE, result);
        assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.getStatus());
    }

    private ActionProxy proxyFor(String namespace, String actionName, String httpMethod, MockHttpServletResponse response) {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(
            "org/apache/struts2/config/providers/struts-http-method.xml"));

        Map<String, Object> extraContext = ActionContext.of()
            .withServletRequest(new MockHttpServletRequest(httpMethod, "/" + actionName))
            .withServletResponse(response)
            .getContextMap();

        return actionProxyFactory.createActionProxy(namespace, actionName, null, extraContext, false, true);
    }

    private MockHttpServletResponse prepareRequest(String httpMethod) {
        MockHttpServletRequest request = new MockHttpServletRequest(httpMethod, "/action");
        MockHttpServletResponse response = new MockHttpServletResponse();
        ActionContext.getContext().withServletRequest(request).withServletResponse(response);
        invocation.setInvocationContext(ActionContext.getContext());
        return response;
    }

}
