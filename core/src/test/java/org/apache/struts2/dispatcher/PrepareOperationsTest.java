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
package org.apache.struts2.dispatcher;

import org.apache.struts2.ActionContext;
import org.apache.struts2.StubValueStack;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.IntStream;

import static org.junit.Assert.assertThrows;

public class PrepareOperationsTest extends StrutsInternalTestCase {
    public void testCreateActionContextWhenRequestHasOne() {
        HttpServletRequest req = new MockHttpServletRequest();
        StubValueStack stack = new StubValueStack();
        req.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);

        PrepareOperations prepare = new PrepareOperations(null);

        ActionContext.clear();
        ActionContext actionContext = prepare.createActionContext(req, null);

        assertEquals(stack.getContext(), actionContext.getContextMap());
    }

    public void testRequestCleanup() {
        HttpServletRequest req = new MockHttpServletRequest();
        PrepareOperations prepare = new PrepareOperations(dispatcher);
        int mockedRecursions = 5;
        IntStream.range(0, mockedRecursions).forEach(i -> prepare.trackRecursion(req));
        IntStream.range(0, mockedRecursions - 1).forEach(i -> prepare.cleanupRequest(req));

        assertNotNull(ActionContext.getContext());
        assertNotNull(Dispatcher.getInstance());
        assertNotNull(ContainerHolder.get());

        prepare.cleanupRequest(req);

        assertNull(ActionContext.getContext());
        assertNull(Dispatcher.getInstance());
        assertNull(ContainerHolder.get());
    }

    public void testRequestCleanupDetachesTheContextSessionMap() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        SessionMap sessionMap = new SessionMap(req);
        ActionContext.getContext().withSession(sessionMap);
        PrepareOperations prepare = new PrepareOperations(dispatcher);
        prepare.trackRecursion(req);

        prepare.cleanupRequest(req);

        assertThrows(IllegalStateException.class, () -> sessionMap.put("key", "value"));
        assertNull(req.getSession(false));
    }

    public void testRequestCleanupDetachesTheHelperSessionMapAfterCreateSession() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        SessionMap original = new SessionMap(req);
        ActionContext context = ActionContext.getContext()
            .withSession(original)
            .with(DispatcherConstants.SESSION, original)
            .withSession(new SessionMap(req));
        PrepareOperations prepare = new PrepareOperations(dispatcher);
        prepare.trackRecursion(req);

        prepare.cleanupRequest(req);

        SessionMap helper = (SessionMap) context.get(DispatcherConstants.SESSION);
        assertThrows(IllegalStateException.class, () -> helper.put("key", "value"));
        assertNull(req.getSession(false));
    }

    public void testRequestCleanupDetachesEveryRequestSessionMap() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        SessionMap first = new SessionMap(req);
        SessionMap second = new SessionMap(req);
        PrepareOperations prepare = new PrepareOperations(dispatcher);
        prepare.trackRecursion(req);

        prepare.cleanupRequest(req);

        assertThrows(IllegalStateException.class, () -> first.put("key", "value"));
        assertThrows(IllegalStateException.class, () -> second.put("key", "value"));
        assertNull(req.getSession(false));
    }

    public void testRequestCleanupWithoutActionContext() {
        HttpServletRequest req = new MockHttpServletRequest();
        PrepareOperations prepare = new PrepareOperations(dispatcher);
        prepare.trackRecursion(req);
        ActionContext.clear();

        prepare.cleanupRequest(req);

        assertNull(Dispatcher.getInstance());
        assertNull(ContainerHolder.get());
    }

    public void testWrappedRequestCleanup() {
        HttpServletRequest req = new MockHttpServletRequest();
        PrepareOperations prepare = new PrepareOperations(dispatcher);
        int mockedRecursions = 5;
        IntStream.range(0, mockedRecursions).forEach(i -> {
            try {
                prepare.wrapRequest(req);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        });
        IntStream.range(0, mockedRecursions - 1).forEach(i -> prepare.cleanupWrappedRequest(req));

        // Assert org.apache.struts2.dispatcher.Dispatcher#cleanUpRequest has not yet run
        assertNotNull(ContainerHolder.get());

        prepare.cleanupWrappedRequest(req);

        // Assert org.apache.struts2.dispatcher.Dispatcher#cleanUpRequest has run after final #cleanupWrappedRequest
        assertNull(ContainerHolder.get());
    }
}
