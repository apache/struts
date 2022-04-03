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
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.mock.MockActionProxy;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.result.ServletRedirectResult;
import org.easymock.EasyMock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MessageStorePreResultListenerTest extends StrutsInternalTestCase {

    public void testSessionWasInvalidated() {
        // given
        ActionContext actionContext = ActionContext.of(new HashMap<>()).bind();
        actionContext.setParameters(HttpParameters.create().build());

        ActionInvocation mockActionInvocation = EasyMock.createControl().createMock(ActionInvocation.class);

        mockActionInvocation.getInvocationContext();
        EasyMock.expectLastCall().andReturn(actionContext);
        EasyMock.expectLastCall().anyTimes();

        EasyMock.replay(mockActionInvocation);

        HttpServletRequest mockedRequest = EasyMock.createControl().createMock(HttpServletRequest.class);
        mockedRequest.getSession(false);
        EasyMock.expectLastCall().andReturn(null);
        EasyMock.expectLastCall().once();
        ServletActionContext.setRequest(mockedRequest);

        EasyMock.replay(mockedRequest);

        HttpServletResponse mockedResponse = EasyMock.createControl().createMock(HttpServletResponse.class);
        mockedResponse.isCommitted();
        EasyMock.expectLastCall().andReturn(false);
        EasyMock.expectLastCall().once();
        ServletActionContext.setResponse(mockedResponse);

        EasyMock.replay(mockedResponse);

        // when
        MessageStoreInterceptor msi = new MessageStoreInterceptor();
        MessageStorePreResultListener listener = new MessageStorePreResultListener();
        listener.init(msi);
        listener.beforeResult(mockActionInvocation, Action.SUCCESS);

        // then
        EasyMock.verify(mockActionInvocation);
        EasyMock.verify(mockedRequest);
        EasyMock.verify(mockedResponse);
    }

    public void testResponseWasComitted() {
        // given
        ActionContext actionContext = ActionContext.of(new HashMap<>()).bind();
        actionContext.setParameters(HttpParameters.create().build());

        ActionInvocation mockActionInvocation = EasyMock.createControl().createMock(ActionInvocation.class);

        mockActionInvocation.getInvocationContext();
        EasyMock.expectLastCall().andReturn(actionContext);
        EasyMock.expectLastCall().anyTimes();

        EasyMock.replay(mockActionInvocation);

        HttpServletResponse mockedResponse = EasyMock.createControl().createMock(HttpServletResponse.class);
        mockedResponse.isCommitted();
        EasyMock.expectLastCall().andReturn(true);
        EasyMock.expectLastCall().once();
        ServletActionContext.setResponse(mockedResponse);

        EasyMock.replay(mockedResponse);

        // when
        MessageStoreInterceptor msi = new MessageStoreInterceptor();
        MessageStorePreResultListener listener = new MessageStorePreResultListener();
        listener.init(msi);
        listener.beforeResult(mockActionInvocation, Action.SUCCESS);

        // then
        EasyMock.verify(mockActionInvocation);
        EasyMock.verify(mockedResponse);
    }

    public void testAutomatic() {
        MessageStoreInterceptor interceptor = new MessageStoreInterceptor();
        interceptor.setOperationMode(MessageStoreInterceptor.AUTOMATIC_MODE);

        MessageStorePreResultListener listener = new MessageStorePreResultListener();
        listener.init(interceptor);

        Map<String, Object> sessionMap = new LinkedHashMap<>();

        ActionSupport action = new ActionSupport();
        action.addActionError("some action error 1");
        action.addActionError("some action error 2");
        action.addActionMessage("some action message 1");
        action.addActionMessage("some action message 2");
        action.addFieldError("field1", "some field error 1");
        action.addFieldError("field2", "some field error 2");

        ActionContext actionContext = ActionContext.of(new HashMap<>()).bind();
        actionContext.setParameters(HttpParameters.create().build());
        actionContext.setSession(sessionMap);

        HttpSession mockedSession = EasyMock.createControl().createMock(HttpSession.class);
        HttpServletRequest mockedRequest = EasyMock.createControl().createMock(HttpServletRequest.class);
        mockedRequest.getSession(false);
        EasyMock.expectLastCall().andReturn(mockedSession);
        EasyMock.expectLastCall().once();
        ServletActionContext.setRequest(mockedRequest);

        EasyMock.replay(mockedRequest);

        HttpServletResponse mockedResponse = EasyMock.createControl().createMock(HttpServletResponse.class);
        mockedResponse.isCommitted();
        EasyMock.expectLastCall().andReturn(false);
        EasyMock.expectLastCall().once();
        ServletActionContext.setResponse(mockedResponse);

        EasyMock.replay(mockedResponse);

        // Mock (ActionInvocation)
        ActionInvocation mockActionInvocation = EasyMock.createControl().createMock(ActionInvocation.class);
        mockActionInvocation.getInvocationContext();
        EasyMock.expectLastCall().andReturn(actionContext);
        EasyMock.expectLastCall().anyTimes();

        mockActionInvocation.getAction();
        EasyMock.expectLastCall().andReturn(action);
        EasyMock.expectLastCall().anyTimes();

        mockActionInvocation.getProxy();
        MockActionProxy actionProxy = new MockActionProxy();
        ResultConfig resultConfig = new ResultConfig.Builder(Action.SUCCESS, ServletRedirectResult.class.getName()).build();
        ActionConfig actionConfig = new ActionConfig.Builder("", "test", action.getClass().getName()).addResultConfig(resultConfig).build();
        actionProxy.setConfig(actionConfig);
        EasyMock.expectLastCall().andReturn(actionProxy);
        EasyMock.expectLastCall().anyTimes();

        EasyMock.replay(mockActionInvocation);

        interceptor.init();
        listener.beforeResult(mockActionInvocation, Action.SUCCESS);

        List actionErrors = (List) sessionMap.get(MessageStoreInterceptor.actionErrorsSessionKey);
        List actionMessages = (List) sessionMap.get(MessageStoreInterceptor.actionMessagesSessionKey);
        Map fieldErrors = (Map) sessionMap.get(MessageStoreInterceptor.fieldErrorsSessionKey);

        assertEquals(actionErrors.size(), 2);
        assertEquals(actionMessages.size(), 2);
        assertEquals(fieldErrors.size(), 2);
        assertTrue(actionErrors.contains("some action error 1"));
        assertTrue(actionErrors.contains("some action error 2"));
        assertTrue(actionMessages.contains("some action message 1"));
        assertTrue(actionMessages.contains("some action message 2"));
        assertEquals(((List) fieldErrors.get("field1")).size(), 1);
        assertEquals(((List) fieldErrors.get("field2")).size(), 1);
        assertEquals(((List) fieldErrors.get("field1")).get(0), "some field error 1");
        assertEquals(((List) fieldErrors.get("field2")).get(0), "some field error 2");

        EasyMock.verify(mockActionInvocation);
    }

    public void testStoreMessage() {
        MessageStoreInterceptor interceptor = new MessageStoreInterceptor();
        interceptor.setAllowRequestParameterSwitch(true);
        interceptor.setOperationMode(MessageStoreInterceptor.STORE_MODE);

        MessageStorePreResultListener listener = new MessageStorePreResultListener();
        listener.init(interceptor);

        Map<String, Object> sessionMap = new LinkedHashMap<>();

        ActionSupport action = new ActionSupport();
        action.addActionError("some action error 1");
        action.addActionError("some action error 2");
        action.addActionMessage("some action message 1");
        action.addActionMessage("some action message 2");
        action.addFieldError("field1", "some field error 1");
        action.addFieldError("field2", "some field error 2");

        ActionContext actionContext = ActionContext.of(new HashMap<>()).bind();
        actionContext.setParameters(HttpParameters.create().build());
        actionContext.setSession(sessionMap);

        HttpSession mockedSession = EasyMock.createControl().createMock(HttpSession.class);
        HttpServletRequest mockedRequest = EasyMock.createControl().createMock(HttpServletRequest.class);
        mockedRequest.getSession(false);
        EasyMock.expectLastCall().andReturn(mockedSession);
        EasyMock.expectLastCall().once();
        ServletActionContext.setRequest(mockedRequest);

        EasyMock.replay(mockedRequest);

        HttpServletResponse mockedResponse = EasyMock.createControl().createMock(HttpServletResponse.class);
        mockedResponse.isCommitted();
        EasyMock.expectLastCall().andReturn(false);
        EasyMock.expectLastCall().once();
        ServletActionContext.setResponse(mockedResponse);

        EasyMock.replay(mockedResponse);

        // Mock (ActionInvocation)
        ActionInvocation mockActionInvocation = EasyMock.createControl().createMock(ActionInvocation.class);
        mockActionInvocation.getInvocationContext();
        EasyMock.expectLastCall().andReturn(actionContext);
        EasyMock.expectLastCall().anyTimes();

        mockActionInvocation.getAction();
        EasyMock.expectLastCall().andReturn(action);

        mockActionInvocation.getProxy();
        MockActionProxy actionProxy = new MockActionProxy();
        ResultConfig resultConfig = new ResultConfig.Builder(Action.SUCCESS, ServletRedirectResult.class.getName()).build();
        ActionConfig actionConfig = new ActionConfig.Builder("", "test", action.getClass().getName()).addResultConfig(resultConfig).build();
        actionProxy.setConfig(actionConfig);
        EasyMock.expectLastCall().andReturn(actionProxy);
        EasyMock.expectLastCall().anyTimes();

        EasyMock.replay(mockActionInvocation);

        interceptor.init();
        listener.beforeResult(mockActionInvocation, Action.SUCCESS);

        assertEquals(sessionMap.size(), 3);
        assertTrue(sessionMap.containsKey(MessageStoreInterceptor.actionErrorsSessionKey));
        assertTrue(sessionMap.containsKey(MessageStoreInterceptor.actionMessagesSessionKey));
        assertTrue(sessionMap.containsKey(MessageStoreInterceptor.fieldErrorsSessionKey));

        List actionErrors = (List) sessionMap.get(MessageStoreInterceptor.actionErrorsSessionKey);
        List actionMessages = (List) sessionMap.get(MessageStoreInterceptor.actionMessagesSessionKey);
        Map fieldErrors = (Map) sessionMap.get(MessageStoreInterceptor.fieldErrorsSessionKey);

        assertEquals(actionErrors.size(), 2);
        assertEquals(actionMessages.size(), 2);
        assertEquals(fieldErrors.size(), 2);

        assertTrue(actionErrors.contains("some action error 1"));
        assertTrue(actionErrors.contains("some action error 2"));
        assertTrue(actionMessages.contains("some action message 1"));
        assertTrue(actionMessages.contains("some action message 2"));
        assertTrue(fieldErrors.containsKey("field1"));
        assertTrue(fieldErrors.containsKey("field2"));
        assertEquals(((List)fieldErrors.get("field1")).size(), 1);
        assertEquals(((List)fieldErrors.get("field2")).size(), 1);
        assertEquals(((List)fieldErrors.get("field1")).get(0), "some field error 1");
        assertEquals(((List)fieldErrors.get("field2")).get(0), "some field error 2");

        EasyMock.verify(mockActionInvocation);
    }


}