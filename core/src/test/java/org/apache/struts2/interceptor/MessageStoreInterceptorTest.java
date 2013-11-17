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

package org.apache.struts2.interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.dispatcher.ServletActionRedirectResult;
import org.easymock.EasyMock;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;


/**
 * Test case for MessageStoreInterceptor.
 *
 * @version $Date$ $Id$
 */
public class MessageStoreInterceptorTest extends StrutsInternalTestCase {

    public void testStoreMessage() throws Exception {
        MessageStoreInterceptor interceptor = new MessageStoreInterceptor();
        interceptor.setAllowRequestParameterSwitch(true);
        interceptor.setOperationMode(MessageStoreInterceptor.STORE_MODE);


        Map paramMap = new LinkedHashMap();
        Map sessionMap = new LinkedHashMap();

        ActionSupport action = new ActionSupport();
        action.addActionError("some action error 1");
        action.addActionError("some action error 2");
        action.addActionMessage("some action message 1");
        action.addActionMessage("some action message 2");
        action.addFieldError("field1", "some field error 1");
        action.addFieldError("field2", "some field error 2");

        ActionContext actionContext = new ActionContext(new HashMap());
        actionContext.put(ActionContext.PARAMETERS, paramMap);
        actionContext.put(ActionContext.SESSION, sessionMap);

        // Mock (ActionInvocation)
        ActionInvocation mockActionInvocation = EasyMock.createControl().createMock(ActionInvocation.class);
        mockActionInvocation.getInvocationContext();
        EasyMock.expectLastCall().andReturn(actionContext);
        EasyMock.expectLastCall().anyTimes();

        mockActionInvocation.invoke();
        EasyMock.expectLastCall().andReturn(Action.SUCCESS);

        mockActionInvocation.getAction();
        EasyMock.expectLastCall().andReturn(action);
        
        mockActionInvocation.getResult();
        EasyMock.expectLastCall().andReturn(new ServletActionRedirectResult());

        EasyMock.replay(mockActionInvocation);

        interceptor.init();
        interceptor.intercept(mockActionInvocation);
        interceptor.destroy();

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

    public void testIgnoreMessageWithoutSession() throws Exception {
        MessageStoreInterceptor interceptor = new MessageStoreInterceptor();
        interceptor.setAllowRequestParameterSwitch(true);
        interceptor.setOperationMode(MessageStoreInterceptor.STORE_MODE);

        Map paramMap = new LinkedHashMap();

        ActionSupport action = new ActionSupport();
        action.addActionError("some action error 1");
        action.addActionMessage("some action message 1");
        action.addFieldError("field2", "some field error 2");

        ActionContext actionContext = new ActionContext(new HashMap());
        actionContext.put(ActionContext.PARAMETERS, paramMap);

        // Mock (ActionInvocation)
        ActionInvocation mockActionInvocation = EasyMock.createControl().createMock(ActionInvocation.class);
        mockActionInvocation.getInvocationContext();
        EasyMock.expectLastCall().andReturn(actionContext);
        EasyMock.expectLastCall().anyTimes();

        mockActionInvocation.invoke();
        EasyMock.expectLastCall().andReturn(Action.SUCCESS);

        mockActionInvocation.getAction();
        EasyMock.expectLastCall().andReturn(action);

        mockActionInvocation.getResult();
        EasyMock.expectLastCall().andReturn(new ServletActionRedirectResult());

        EasyMock.replay(mockActionInvocation);

        interceptor.init();
        interceptor.intercept(mockActionInvocation);
        interceptor.destroy();

        EasyMock.verify(mockActionInvocation);
    }

    public void testRetrieveMessage() throws Exception {
        MessageStoreInterceptor interceptor = new MessageStoreInterceptor();
        interceptor.setOperationMode(MessageStoreInterceptor.RETRIEVE_MODE);
        interceptor.setAllowRequestParameterSwitch(true);


        ActionSupport action = new ActionSupport();

        ActionInvocation mockActionInvocation = EasyMock.createControl().createMock(ActionInvocation.class);
        mockActionInvocation.invoke();
        EasyMock.expectLastCall().andReturn(Action.SUCCESS);

        Map paramsMap = new LinkedHashMap();
        Map sessionMap = new LinkedHashMap();

        List actionErrors = new ArrayList();
        List actionMessages = new ArrayList();
        Map fieldErrors = new LinkedHashMap();

        actionErrors.add("some action error 1");
        actionErrors.add("some action error 2");
        actionMessages.add("some action messages 1");
        actionMessages.add("some action messages 2");
        List field1Errors = new ArrayList();
        field1Errors.add("some field error 1");
        List field2Errors = new ArrayList();
        field2Errors.add("some field error 2");
        fieldErrors.put("field1", field1Errors);
        fieldErrors.put("field2", field2Errors);

        sessionMap.put(MessageStoreInterceptor.actionErrorsSessionKey, actionErrors);
        sessionMap.put(MessageStoreInterceptor.actionMessagesSessionKey, actionMessages);
        sessionMap.put(MessageStoreInterceptor.fieldErrorsSessionKey, fieldErrors);


        ActionContext actionContext = new ActionContext(new HashMap());
        actionContext.put(ActionContext.PARAMETERS, paramsMap);
        actionContext.put(ActionContext.SESSION, sessionMap);

        mockActionInvocation.getInvocationContext();
        EasyMock.expectLastCall().andReturn(actionContext);
        EasyMock.expectLastCall().anyTimes();

        mockActionInvocation.getAction();
        EasyMock.expectLastCall().andReturn(action);
        EasyMock.expectLastCall().anyTimes();
        
        mockActionInvocation.getResult();
        EasyMock.expectLastCall().andReturn(new ServletActionRedirectResult());

        EasyMock.replay(mockActionInvocation);

        interceptor.init();
        interceptor.intercept(mockActionInvocation);
        interceptor.destroy();

        assertEquals(action.getActionErrors().size(), 2);
        assertEquals(action.getActionMessages().size(), 2);
        assertEquals(action.getFieldErrors().size(), 2);
        assertTrue(action.getActionErrors().contains("some action error 1"));
        assertTrue(action.getActionErrors().contains("some action error 2"));
        assertTrue(action.getActionMessages().contains("some action messages 1"));
        assertTrue(action.getActionMessages().contains("some action messages 2"));
        assertEquals(((List)action.getFieldErrors().get("field1")).size(), 1);
        assertEquals(((List)action.getFieldErrors().get("field2")).size(), 1);
        assertEquals(((List)action.getFieldErrors().get("field1")).get(0), "some field error 1");
        assertEquals(((List)action.getFieldErrors().get("field2")).get(0), "some field error 2");

        EasyMock.verify(mockActionInvocation);
    }
    
    public void testAutomatic() throws Exception {
        MessageStoreInterceptor interceptor = new MessageStoreInterceptor();
        interceptor.setAllowRequestParameterSwitch(true);
        interceptor.setOperationMode(MessageStoreInterceptor.AUTOMATIC_MODE);


        Map paramMap = new LinkedHashMap();
        Map sessionMap = new LinkedHashMap();

        ActionSupport action = new ActionSupport();
        action.addActionError("some action error 1");
        action.addActionError("some action error 2");
        action.addActionMessage("some action message 1");
        action.addActionMessage("some action message 2");
        action.addFieldError("field1", "some field error 1");
        action.addFieldError("field2", "some field error 2");

        ActionContext actionContext = new ActionContext(new HashMap());
        actionContext.put(ActionContext.PARAMETERS, paramMap);
        actionContext.put(ActionContext.SESSION, sessionMap);

        // Mock (ActionInvocation)
        ActionInvocation mockActionInvocation = EasyMock.createControl().createMock(ActionInvocation.class);
        mockActionInvocation.getInvocationContext();
        EasyMock.expectLastCall().andReturn(actionContext);
        EasyMock.expectLastCall().anyTimes();

        mockActionInvocation.invoke();
        EasyMock.expectLastCall().andReturn(Action.SUCCESS);

        mockActionInvocation.getAction();
        EasyMock.expectLastCall().andReturn(action);
        EasyMock.expectLastCall().anyTimes();

        mockActionInvocation.getResult();
        EasyMock.expectLastCall().andReturn(new ServletActionRedirectResult());
        
        EasyMock.replay(mockActionInvocation);

        interceptor.init();
        interceptor.intercept(mockActionInvocation);
        interceptor.destroy();

        assertEquals(3, sessionMap.size());
        assertTrue(sessionMap.containsKey(MessageStoreInterceptor.actionErrorsSessionKey));
        assertTrue(sessionMap.containsKey(MessageStoreInterceptor.actionMessagesSessionKey));
        assertTrue(sessionMap.containsKey(MessageStoreInterceptor.fieldErrorsSessionKey));

        List actionErrors = (List) sessionMap.get(MessageStoreInterceptor.actionErrorsSessionKey);
        List actionMessages = (List) sessionMap.get(MessageStoreInterceptor.actionMessagesSessionKey);
        Map fieldErrors = (Map) sessionMap.get(MessageStoreInterceptor.fieldErrorsSessionKey);

        assertEquals(2, actionErrors.size());
        assertEquals(2, actionMessages.size());
        assertEquals(2, fieldErrors.size());

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
        
        action = new ActionSupport();

        mockActionInvocation = EasyMock.createControl().createMock(ActionInvocation.class);
        mockActionInvocation.invoke();
        EasyMock.expectLastCall().andReturn(Action.SUCCESS);

        sessionMap.put(MessageStoreInterceptor.actionErrorsSessionKey, actionErrors);
        sessionMap.put(MessageStoreInterceptor.actionMessagesSessionKey, actionMessages);
        sessionMap.put(MessageStoreInterceptor.fieldErrorsSessionKey, fieldErrors);


        actionContext = new ActionContext(new HashMap());
        actionContext.put(ActionContext.PARAMETERS, paramMap);
        actionContext.put(ActionContext.SESSION, sessionMap);

        mockActionInvocation.getInvocationContext();
        EasyMock.expectLastCall().andReturn(actionContext);
        EasyMock.expectLastCall().anyTimes();

        mockActionInvocation.getAction();
        EasyMock.expectLastCall().andReturn(action);
        EasyMock.expectLastCall().anyTimes();
        
        mockActionInvocation.getResult();
        EasyMock.expectLastCall().andReturn(new ServletActionRedirectResult());

        EasyMock.replay(mockActionInvocation);

        interceptor.init();
        interceptor.intercept(mockActionInvocation);
        interceptor.destroy();

        assertEquals(action.getActionErrors().size(), 2);
        assertEquals(action.getActionMessages().size(), 2);
        assertEquals(action.getFieldErrors().size(), 2);
        assertTrue(action.getActionErrors().contains("some action error 1"));
        assertTrue(action.getActionErrors().contains("some action error 2"));
        assertTrue(action.getActionMessages().contains("some action message 1"));
        assertTrue(action.getActionMessages().contains("some action message 2"));
        assertEquals(((List)action.getFieldErrors().get("field1")).size(), 1);
        assertEquals(((List)action.getFieldErrors().get("field2")).size(), 1);
        assertEquals(((List)action.getFieldErrors().get("field1")).get(0), "some field error 1");
        assertEquals(((List)action.getFieldErrors().get("field2")).get(0), "some field error 2");

        EasyMock.verify(mockActionInvocation);
    }

    public void testRequestOperationMode1() throws Exception {

        Map paramMap = new LinkedHashMap();
        paramMap.put("operationMode", new String[] { MessageStoreInterceptor.RETRIEVE_MODE });

        ActionContext actionContext = new ActionContext(new HashMap());
        actionContext.put(ActionContext.PARAMETERS, paramMap);

        ActionInvocation mockActionInvocation = EasyMock.createControl().createMock(ActionInvocation.class);
        mockActionInvocation.getInvocationContext();
        EasyMock.expectLastCall().andReturn(actionContext);
        EasyMock.expectLastCall().anyTimes();

        EasyMock.replay(mockActionInvocation);

        MessageStoreInterceptor interceptor = new MessageStoreInterceptor();
        String operationMode = interceptor.getRequestOperationMode(mockActionInvocation);

        assertEquals(operationMode, MessageStoreInterceptor.RETRIEVE_MODE);

        EasyMock.verify(mockActionInvocation);
    }

    public void testRequestOperationMode2() throws Exception {

        Map paramMap = new LinkedHashMap();
        paramMap.put("operationMode", new String[] { MessageStoreInterceptor.STORE_MODE });

        ActionContext actionContext = new ActionContext(new HashMap());
        actionContext.put(ActionContext.PARAMETERS, paramMap);

        ActionInvocation mockActionInvocation = EasyMock.createControl().createMock(ActionInvocation.class);
        mockActionInvocation.getInvocationContext();
        EasyMock.expectLastCall().andReturn(actionContext);
        EasyMock.expectLastCall().anyTimes();

        EasyMock.replay(mockActionInvocation);

        MessageStoreInterceptor interceptor = new MessageStoreInterceptor();
        String operationMode = interceptor.getRequestOperationMode(mockActionInvocation);

        assertEquals(operationMode, MessageStoreInterceptor.STORE_MODE);

        EasyMock.verify(mockActionInvocation);
    }

    public void testRequestOperationMode3() throws Exception {

        Map paramMap = new LinkedHashMap();

        ActionContext actionContext = new ActionContext(new HashMap());
        actionContext.put(ActionContext.PARAMETERS, paramMap);

        ActionInvocation mockActionInvocation = EasyMock.createControl().createMock(ActionInvocation.class);
        mockActionInvocation.getInvocationContext();
        EasyMock.expectLastCall().andReturn(actionContext);
        EasyMock.expectLastCall().anyTimes();

        EasyMock.replay(mockActionInvocation);

        MessageStoreInterceptor interceptor = new MessageStoreInterceptor();
        String operationMode = interceptor.getRequestOperationMode(mockActionInvocation);

        assertEquals(operationMode, MessageStoreInterceptor.NONE);

        EasyMock.verify(mockActionInvocation);

    }
}
