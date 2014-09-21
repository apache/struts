package org.apache.struts2.interceptor.httpmethod;

import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;
import org.apache.struts2.HttpMethodsTestAction;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.TestAction;
import org.springframework.mock.web.MockHttpServletRequest;

public class HttpMethodInterceptorTest extends StrutsInternalTestCase {

    public void testNotAnnotatedAction() throws Exception {
        // given
        TestAction action = new TestAction();
        HttpMethodInterceptor interceptor = new HttpMethodInterceptor();
        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setAction(action);
        invocation.setProxy(new MockActionProxy());

        invocation.setResultCode("success");

        MockHttpServletRequest request = new MockHttpServletRequest("post", "/action");
        ServletActionContext.setRequest(request);

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("success", resultName);
    }

    public void testActionWithPostAllowed() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        HttpMethodInterceptor interceptor = new HttpMethodInterceptor();
        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setAction(action);
        invocation.setProxy(new MockActionProxy());

        invocation.setResultCode("success");

        MockHttpServletRequest request = new MockHttpServletRequest("post", "/action");
        ServletActionContext.setRequest(request);

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("success", resultName);
    }

    public void testGetIsNotAllowed() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        HttpMethodInterceptor interceptor = new HttpMethodInterceptor();
        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setAction(action);
        invocation.setProxy(new MockActionProxy());

        invocation.setResultCode("success");

        MockHttpServletRequest request = new MockHttpServletRequest("get", "/action");
        ServletActionContext.setRequest(request);

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("bad-request", resultName);
    }

    public void testGetIsNotAllowedWithCustomResultName() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        HttpMethodInterceptor interceptor = new HttpMethodInterceptor();
        interceptor.setBadRequestResultName("custom-bad-request");

        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setAction(action);
        invocation.setProxy(new MockActionProxy());

        invocation.setResultCode("success");

        MockHttpServletRequest request = new MockHttpServletRequest("get", "/action");
        ServletActionContext.setRequest(request);

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("custom-bad-request", resultName);
    }

    public void testGetIsNotAllowedWithActionDefinedResultName() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction("action-bad-request");
        HttpMethodInterceptor interceptor = new HttpMethodInterceptor();
        interceptor.setBadRequestResultName("custom-bad-request");

        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setAction(action);
        invocation.setProxy(new MockActionProxy());

        invocation.setResultCode("success");

        MockHttpServletRequest request = new MockHttpServletRequest("get", "/action");
        ServletActionContext.setRequest(request);

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("action-bad-request", resultName);
    }

    public void testGetOnlyOnMethod() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        HttpMethodInterceptor interceptor = new HttpMethodInterceptor();
        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setAction(action);
        MockActionProxy proxy = new MockActionProxy();
        proxy.setMethod("onGetOnly");
        proxy.setMethodSpecified(true);
        invocation.setProxy(proxy);

        invocation.setResultCode("onGetOnly");

        MockHttpServletRequest request = new MockHttpServletRequest("get", "/action");
        ServletActionContext.setRequest(request);

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("onGetOnly", resultName);
    }

    public void testPostOnlyOnMethod() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        HttpMethodInterceptor interceptor = new HttpMethodInterceptor();
        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setAction(action);
        MockActionProxy proxy = new MockActionProxy();
        proxy.setMethod("onPostOnly");
        proxy.setMethodSpecified(true);
        invocation.setProxy(proxy);

        invocation.setResultCode("onPostOnly");

        MockHttpServletRequest request = new MockHttpServletRequest("post", "/action");
        ServletActionContext.setRequest(request);

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("onPostOnly", resultName);
    }

    public void testGetPostOnlyOnMethod() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        HttpMethodInterceptor interceptor = new HttpMethodInterceptor();
        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setAction(action);
        MockActionProxy proxy = new MockActionProxy();
        proxy.setMethod("onGetPostOnly");
        proxy.setMethodSpecified(true);
        invocation.setProxy(proxy);

        invocation.setResultCode("onGetPostOnly");

        MockHttpServletRequest request = new MockHttpServletRequest("post", "/action");
        ServletActionContext.setRequest(request);

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("onGetPostOnly", resultName);
    }

    public void testDeleteOnMethod() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        HttpMethodInterceptor interceptor = new HttpMethodInterceptor();
        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setAction(action);
        MockActionProxy proxy = new MockActionProxy();
        proxy.setMethod("onDelete");
        proxy.setMethodSpecified(true);
        invocation.setProxy(proxy);

        invocation.setResultCode("onDelete");

        MockHttpServletRequest request = new MockHttpServletRequest("DELETE", "/action");
        ServletActionContext.setRequest(request);

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("onDelete", resultName);
    }

    public void testPutOnPutOrPostMethod() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        HttpMethodInterceptor interceptor = new HttpMethodInterceptor();
        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setAction(action);
        MockActionProxy proxy = new MockActionProxy();
        proxy.setMethod("onPutOrPost");
        proxy.setMethodSpecified(true);
        invocation.setProxy(proxy);

        invocation.setResultCode("onPutOrPost");

        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/action");
        ServletActionContext.setRequest(request);

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("onPutOrPost", resultName);
    }

    public void testPostOnPutOrPostMethod() throws Exception {
        // given
        HttpMethodsTestAction action = new HttpMethodsTestAction();
        HttpMethodInterceptor interceptor = new HttpMethodInterceptor();
        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setAction(action);
        MockActionProxy proxy = new MockActionProxy();
        proxy.setMethod("onPutOrPost");
        proxy.setMethodSpecified(true);
        invocation.setProxy(proxy);

        invocation.setResultCode("onPutOrPost");

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action");
        ServletActionContext.setRequest(request);

        // when
        String resultName = interceptor.intercept(invocation);

        // then
        assertEquals("onPutOrPost", resultName);
    }

}
