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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.SessionMap;

import java.io.Serial;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The session view an execAndWait background process works on: a shallow snapshot of the session taken at
 * construction plus an ordered log of the process's own writes. It never touches the live session; the writes are
 * applied to it later by {@link SessionSandboxReconciler}.
 */
public class SandboxedSessionMap extends SessionMap {

    @Serial
    private static final long serialVersionUID = 2907451127739228064L;

    private static final Logger LOG = LogManager.getLogger(SandboxedSessionMap.class);

    private final transient Map<String, Object> snapshot = new LinkedHashMap<>();
    private final transient Map<String, Object> view = new LinkedHashMap<>();
    private final transient List<SessionOperation> operations = new ArrayList<>();
    private final transient String startSessionId;
    private final transient String processName;
    private final transient AtomicBoolean escapeWarned = new AtomicBoolean();

    /**
     * Snapshots the attributes of the given session. Must be called on the request thread that owns the session.
     *
     * @param session     the live session to snapshot
     * @param processName the name of the background process, used in log messages
     */
    public SandboxedSessionMap(HttpSession session, String processName) {
        super();
        this.startSessionId = session.getId();
        this.processName = processName;
        Enumeration<String> names = session.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            snapshot.put(name, session.getAttribute(name));
        }
        view.putAll(snapshot);
    }

    <T> T runInSandbox(Callable<T> work) throws Exception {
        return callWithLiveWriteListener(this::warnEscapeOnce, work);
    }

    private void warnEscapeOnce() {
        if (escapeWarned.compareAndSet(false, true)) {
            LOG.warn("Background process [{}] wrote to the request session, bypassing its session sandbox; "
                + "use ActionContext.getContext().getSession() or SessionAware", processName);
        }
    }

    String getStartSessionId() {
        return startSessionId;
    }

    String getProcessName() {
        return processName;
    }

    synchronized List<SessionOperation> getOperations() {
        return Collections.unmodifiableList(new ArrayList<>(operations));
    }

    synchronized Object getSnapshotValue(String key) {
        return snapshot.get(key);
    }

    @Override
    public synchronized Object get(Object key) {
        return view.get(asKey(key));
    }

    /**
     * Records the write in the sandbox; a {@code null} value is recorded as a removal.
     *
     * @param key   the name of the session attribute
     * @param value the value to set
     * @return the previous value in the sandbox
     */
    @Override
    public synchronized Object put(String key, Object value) {
        if (value == null) {
            return remove(key);
        }
        operations.add(new SessionOperation.Put(key, value));
        return view.put(key, value);
    }

    @Override
    public synchronized Object remove(Object key) {
        String name = asKey(key);
        operations.add(new SessionOperation.Remove(name));
        return view.remove(name);
    }

    @Override
    public synchronized boolean containsKey(Object key) {
        return view.get(asKey(key)) != null;
    }

    @Override
    public synchronized Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> result = new HashSet<>();
        for (Map.Entry<String, Object> current : view.entrySet()) {
            String key = current.getKey();
            result.add(new SimpleEntry<>(key, current.getValue()) {
                @Override
                public Object setValue(Object value) {
                    return put(key, value);
                }
            });
        }
        return result;
    }

    @Override
    public synchronized void clear() {
        for (String key : new ArrayList<>(view.keySet())) {
            remove(key);
        }
    }

    /**
     * Empties the sandbox and records the invalidation; the live session is invalidated only when the writes are
     * applied.
     */
    @Override
    public synchronized void invalidate() {
        view.clear();
        operations.add(new SessionOperation.Invalidate());
    }

    private static String asKey(Object key) {
        return key != null ? key.toString() : null;
    }
}
