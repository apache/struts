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
package org.apache.struts2.util;

import com.mockobjects.dynamic.Mock;
import com.mockobjects.servlet.MockPageContext;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.StrutsInternalTestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * InvocationSessionStoreTest
 *
 */
public class InvocationSessionStoreTest extends StrutsInternalTestCase {

    private static final String INVOCATION_KEY = "org.apache.struts2.util.InvocationSessionStoreTest.invocation";
    private static final String TOKEN_VALUE = "org.apache.struts2.util.InvocationSessionStoreTest.token";


    private ActionInvocation invocation;
    private Map<String, Object> session;
    private Mock invocationMock;
    private ValueStack stack;


    public void testStore() {
        assertNull(InvocationSessionStore.loadInvocation(INVOCATION_KEY, TOKEN_VALUE));
        InvocationSessionStore.storeInvocation(INVOCATION_KEY, TOKEN_VALUE, invocation);
        assertNotNull(InvocationSessionStore.loadInvocation(INVOCATION_KEY, TOKEN_VALUE));
        assertEquals(invocation, InvocationSessionStore.loadInvocation(INVOCATION_KEY, TOKEN_VALUE));
    }

    public void testValueStackReset() {
        ActionContext actionContext = ActionContext.getContext();
        assertEquals(stack, actionContext.getValueStack());

        InvocationSessionStore.loadInvocation(INVOCATION_KEY, TOKEN_VALUE);
        assertEquals(stack, actionContext.getValueStack());
    }

    public void testActionContextReset() {
        ActionContext actionContext = ActionContext.getContext();
        InvocationSessionStore.storeInvocation(INVOCATION_KEY, TOKEN_VALUE, invocation);

        ActionContext actionContext2 = ActionContext.of(new HashMap<>()).bind();
        actionContext2.setSession(session);

        assertEquals(actionContext2, ActionContext.getContext());

        InvocationSessionStore.loadInvocation(INVOCATION_KEY, TOKEN_VALUE);
        assertEquals(actionContext, ActionContext.getContext());
    }

    public void testStoreAndLoadFromDeserializedSession() throws Exception {
        InvocationSessionStore.storeInvocation(INVOCATION_KEY, TOKEN_VALUE, invocation);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(session);//WW-4873 invocation is not serializable but we should not fail at this line
        oos.close();
        byte[] b = baos.toByteArray();
        baos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(bais);
        session = (Map<String, Object>) ois.readObject();
        ActionContext.getContext().setSession(session);
        ois.close();
        bais.close();

        ActionInvocation savedInvocation = InvocationSessionStore.loadInvocation(INVOCATION_KEY, TOKEN_VALUE);
        assertNull(savedInvocation);//Currently we don't support invocation restore from serialized session
    }

    public void testStoreAndLoadPreservesPageContext() {
        // Create mock PageContext to put with the current context (simulating a PageContext
        // associated with the current (active) process flow).  In real-world processing it
        // will usually be null, but if non-null it should be preserved/restored upon load of the
        // saved context.
        MockPageContext mockPreviousPageContext = new MockPageContext();

        // Create mock PageContext to put with the saved context (simulating a PageContext previously
        // used and closed after generating JSP output).
        MockPageContext mockSavedPageContext = new MockPageContext();
        ActionContext actionContext = ActionContext.getContext()
            .withPageContext(mockSavedPageContext);

        assertEquals(mockSavedPageContext, ActionContext.getContext().getPageContext());

        InvocationSessionStore.storeInvocation(INVOCATION_KEY, TOKEN_VALUE, invocation);

        ActionContext actionContext2 = ActionContext.of(new HashMap<>())
            .withSession(session)
            .withPageContext(mockPreviousPageContext)
            .bind();

        assertEquals(actionContext2, ActionContext.getContext());

        actionContext2.withPageContext(mockPreviousPageContext);
        assertEquals(mockPreviousPageContext, ActionContext.getContext().getPageContext());

        InvocationSessionStore.loadInvocation(INVOCATION_KEY, TOKEN_VALUE);
        assertEquals(actionContext, ActionContext.getContext());
        assertEquals(mockPreviousPageContext, ActionContext.getContext().getPageContext());
    }

    protected void setUp() throws Exception {
        super.setUp();
        stack = ActionContext.getContext().getValueStack();

        session = new HashMap<>();

        ActionContext actionContext = ActionContext.of(stack.getContext())
            .withSession(session)
            .withValueStack(stack)
            .bind();

        invocationMock = new Mock(ActionInvocation.class);
        invocation = (ActionInvocation) invocationMock.proxy();
        invocationMock.matchAndReturn("getInvocationContext", actionContext);
        invocationMock.matchAndReturn("getStack", stack);

        Mock proxyMock = new Mock(ActionProxy.class);
        proxyMock.matchAndReturn("getInvocation", invocation);

        ActionProxy proxy = (ActionProxy) proxyMock.proxy();

        invocationMock.matchAndReturn("getProxy", proxy);
    }
}
