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
package org.apache.struts2.interceptor.exec;

import jakarta.servlet.http.HttpSession;
import junit.framework.TestCase;
import org.apache.struts2.action.BackgroundSessionConflictAware;
import org.apache.struts2.action.SessionConflict;
import org.apache.struts2.action.SessionConflictResolution;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class SessionSandboxReconcilerTest extends TestCase {

    private MockHttpSession live;
    private MockHttpServletRequest request;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        live = new MockHttpSession();
        request = new MockHttpServletRequest();
        request.setSession(live);
    }

    public void testOnlyLoggedKeysAreApplied() {
        live.setAttribute("other", "1");
        SandboxedSessionMap sandbox = new SandboxedSessionMap(live, "p");
        live.setAttribute("other", "changed");
        sandbox.put("b", "2");

        SessionSandboxReconciler.reconcile(sandbox, request, new Object());

        assertEquals("changed", live.getAttribute("other"));
        assertEquals("2", live.getAttribute("b"));
    }

    public void testSessionIdChangeDiscardsEverything() {
        SandboxedSessionMap sandbox = new SandboxedSessionMap(live, "p");
        sandbox.put("b", "2");
        live.invalidate();
        HttpSession fresh = request.getSession(true);

        SessionSandboxReconciler.reconcile(sandbox, request, new Object());

        assertNull(fresh.getAttribute("b"));
    }

    public void testNoSessionDiscardsEverything() {
        SandboxedSessionMap sandbox = new SandboxedSessionMap(live, "p");
        sandbox.put("b", "2");
        live.invalidate();

        SessionSandboxReconciler.reconcile(sandbox, request, new Object());

        assertNull(request.getSession(false));
    }

    public void testConflictKeepsCurrentByDefault() {
        SandboxedSessionMap sandbox = conflictOnA();

        SessionSandboxReconciler.reconcile(sandbox, request, new Object());

        assertEquals("live", live.getAttribute("a"));
    }

    public void testHookUseBackground() {
        SandboxedSessionMap sandbox = conflictOnA();

        SessionSandboxReconciler.reconcile(sandbox, request, resolving(conflict -> SessionConflictResolution.useBackground()));

        assertEquals("bg", live.getAttribute("a"));
    }

    public void testHookReplaceWith() {
        SandboxedSessionMap sandbox = conflictOnA();

        SessionSandboxReconciler.reconcile(sandbox, request, resolving(conflict -> SessionConflictResolution.replaceWith("merged")));

        assertEquals("merged", live.getAttribute("a"));
    }

    public void testHookRemove() {
        SandboxedSessionMap sandbox = conflictOnA();

        SessionSandboxReconciler.reconcile(sandbox, request, resolving(conflict -> SessionConflictResolution.remove()));

        assertNull(live.getAttribute("a"));
    }

    public void testHookSeesAllFourValues() {
        SandboxedSessionMap sandbox = conflictOnA();
        List<SessionConflict> seen = new ArrayList<>();

        SessionSandboxReconciler.reconcile(sandbox, request, resolving(conflict -> {
            seen.add(conflict);
            return SessionConflictResolution.keepCurrent();
        }));

        assertEquals(List.of(new SessionConflict("a", "1", "live", "bg")), seen);
    }

    public void testRemovalOfAKeyChangedElsewhereIsAConflict() {
        live.setAttribute("a", "1");
        SandboxedSessionMap sandbox = new SandboxedSessionMap(live, "p");
        live.setAttribute("a", "live");
        sandbox.remove("a");

        SessionSandboxReconciler.reconcile(sandbox, request, new Object());

        assertEquals("live", live.getAttribute("a"));
    }

    public void testBackgroundInvalidateStartsAFreshSession() {
        live.setAttribute("a", "1");
        SandboxedSessionMap sandbox = new SandboxedSessionMap(live, "p");
        live.setAttribute("a", "live");
        sandbox.invalidate();
        sandbox.put("user", "u");
        List<SessionConflict> seen = new ArrayList<>();

        SessionSandboxReconciler.reconcile(sandbox, request, resolving(conflict -> {
            seen.add(conflict);
            return SessionConflictResolution.keepCurrent();
        }));

        HttpSession fresh = request.getSession(false);
        assertTrue(live.isInvalid());
        assertNotSame(live, fresh);
        assertEquals(List.of("user"), Collections.list(fresh.getAttributeNames()));
        assertEquals("u", fresh.getAttribute("user"));
        assertEquals(List.of(), seen);
    }

    public void testHookExceptionPropagates() {
        SandboxedSessionMap sandbox = conflictOnA();
        IllegalArgumentException thrown = new IllegalArgumentException("boom");

        try {
            SessionSandboxReconciler.reconcile(sandbox, request, resolving(conflict -> {
                throw thrown;
            }));
            fail("expected the hook's exception");
        } catch (IllegalArgumentException e) {
            assertSame(thrown, e);
        }
    }

    public void testSessionInvalidatedAfterTheIdCheckDiscardsTheWrites() {
        InvalidatedOnIdCheck session = new InvalidatedOnIdCheck();
        request.setSession(session);
        SandboxedSessionMap sandbox = new SandboxedSessionMap(session, "p");
        sandbox.put("a", "bg");
        session.armed = true;

        SessionSandboxReconciler.reconcile(sandbox, request, new Object());

        assertTrue(session.isInvalid());
        assertNull(request.getSession(false));
    }

    public void testBackgroundInvalidateOfASessionInvalidatedAfterTheIdCheckDiscardsTheWrites() {
        InvalidatedOnIdCheck session = new InvalidatedOnIdCheck();
        request.setSession(session);
        SandboxedSessionMap sandbox = new SandboxedSessionMap(session, "p");
        sandbox.invalidate();
        sandbox.put("user", "u");
        session.armed = true;

        SessionSandboxReconciler.reconcile(sandbox, request, new Object());

        assertNull(request.getSession(false));
    }

    public void testSessionInvalidatedByTheHookDiscardsTheRemainingWrites() {
        SandboxedSessionMap sandbox = conflictOnA();
        sandbox.put("b", "2");

        SessionSandboxReconciler.reconcile(sandbox, request, resolving(conflict -> {
            live.invalidate();
            return SessionConflictResolution.useBackground();
        }));

        assertNull(request.getSession(false));
    }

    public void testHookIllegalStateExceptionPropagates() {
        SandboxedSessionMap sandbox = conflictOnA();
        IllegalStateException thrown = new IllegalStateException("hook");

        try {
            SessionSandboxReconciler.reconcile(sandbox, request, resolving(conflict -> {
                throw thrown;
            }));
            fail("expected the hook's exception");
        } catch (IllegalStateException e) {
            assertSame(thrown, e);
        }
    }

    private SandboxedSessionMap conflictOnA() {
        live.setAttribute("a", "1");
        SandboxedSessionMap sandbox = new SandboxedSessionMap(live, "p");
        live.setAttribute("a", "live");
        sandbox.put("a", "bg");
        return sandbox;
    }

    private static BackgroundSessionConflictAware resolving(Function<SessionConflict, SessionConflictResolution> resolver) {
        return resolver::apply;
    }

    private static class InvalidatedOnIdCheck extends MockHttpSession {
        private boolean armed;

        @Override
        public String getId() {
            String id = super.getId();
            if (armed) {
                armed = false;
                invalidate();
            }
            return id;
        }
    }
}
