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

import junit.framework.TestCase;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.test.LogCapture;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SandboxedSessionMapTest extends TestCase {

    public void testReadsSeeSnapshotPlusOwnWritesAndNeverTheLiveSession() {
        MockHttpSession live = new MockHttpSession();
        live.setAttribute("a", "1");
        SandboxedSessionMap sandbox = new SandboxedSessionMap(live, "p");

        live.setAttribute("a", "changed");
        sandbox.put("b", "2");

        assertEquals("1", sandbox.get("a"));
        assertEquals("2", sandbox.get("b"));
        assertNull(live.getAttribute("b"));
        assertEquals(live.getId(), sandbox.getStartSessionId());
        assertEquals("p", sandbox.getProcessName());
        assertEquals(List.of(new SessionOperation.Put("b", "2")), sandbox.getOperations());
    }

    public void testRemoveClearAndInvalidateAreLoggedInOrder() {
        MockHttpSession live = new MockHttpSession();
        live.setAttribute("a", "1");
        SandboxedSessionMap sandbox = new SandboxedSessionMap(live, "p");

        sandbox.remove("a");
        sandbox.invalidate();
        sandbox.put("user", "u");

        assertEquals(List.of(new SessionOperation.Remove("a"), new SessionOperation.Invalidate(),
            new SessionOperation.Put("user", "u")), sandbox.getOperations());
        assertEquals(Map.of("user", "u"), new HashMap<>(sandbox));
        assertEquals("1", sandbox.getSnapshotValue("a"));
    }

    public void testPutNullIsLoggedAsRemove() {
        SandboxedSessionMap sandbox = new SandboxedSessionMap(new MockHttpSession(), "p");
        sandbox.put("a", null);
        assertEquals(List.of(new SessionOperation.Remove("a")), sandbox.getOperations());
    }

    public void testEntrySetValueIsLogged() {
        MockHttpSession live = new MockHttpSession();
        live.setAttribute("a", "1");
        SandboxedSessionMap sandbox = new SandboxedSessionMap(live, "p");

        sandbox.entrySet().iterator().next().setValue("2");

        assertEquals("2", sandbox.get("a"));
        assertEquals(List.of(new SessionOperation.Put("a", "2")), sandbox.getOperations());
    }

    public void testClearLogsOneRemovePerKeyPresent() {
        MockHttpSession live = new MockHttpSession();
        live.setAttribute("a", "1");
        live.setAttribute("b", "2");
        SandboxedSessionMap sandbox = new SandboxedSessionMap(live, "p");

        sandbox.clear();

        assertTrue(sandbox.isEmpty());
        assertEquals(2, sandbox.getOperations().size());
        assertTrue(sandbox.getOperations().contains(new SessionOperation.Remove("a")));
        assertTrue(sandbox.getOperations().contains(new SessionOperation.Remove("b")));
        assertEquals("1", live.getAttribute("a"));
    }

    public void testWriteThroughRequestMapFromBackgroundThreadWarnsOnce() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession live = new MockHttpSession();
        request.setSession(live);
        SessionMap requestMap = new SessionMap(request);
        SandboxedSessionMap sandbox = new SandboxedSessionMap(live, "longJob");

        try (LogCapture logs = new LogCapture(SandboxedSessionMap.class)) {
            Thread background = new Thread(() -> SandboxedSessionMap.runInSandbox(sandbox, () -> {
                requestMap.put("a", "1");
                requestMap.put("b", "2");
                sandbox.put("c", "3");
            }));
            background.start();
            background.join(5000);

            assertEquals("1", live.getAttribute("a"));
            assertEquals(1, logs.messages().size());
            assertTrue(logs.messages().get(0).contains("longJob"));
            assertFalse(logs.messages().get(0).contains("1"));
        }
        assertNull(SandboxedSessionMap.current());
    }
}
