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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.action.BackgroundSessionConflictAware;
import org.apache.struts2.action.SessionConflict;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Applies the writes an execAndWait background process logged in its {@link SandboxedSessionMap} to the
 * session of the polling request. Writes are discarded when that session is not the one the process started
 * with. A key changed elsewhere since the snapshot keeps its current value unless the action implements
 * {@link BackgroundSessionConflictAware}.
 */
public final class SessionSandboxReconciler {

    private static final Logger LOG = LogManager.getLogger(SessionSandboxReconciler.class);

    private SessionSandboxReconciler() {
    }

    public static void reconcile(SandboxedSessionMap sandbox, HttpServletRequest request, Object action) {
        HttpSession real = request.getSession(false);
        if (real == null || !real.getId().equals(sandbox.getStartSessionId())) {
            LOG.debug("Discarding the session writes of background process [{}]: its session is gone", sandbox.getProcessName());
            return;
        }
        List<SessionOperation> operations = sandbox.getOperations();
        int lastInvalidate = lastInvalidateIndex(operations);
        if (lastInvalidate >= 0) {
            real.invalidate();
            HttpSession fresh = request.getSession(true);
            for (String key : touchedKeys(operations.subList(lastInvalidate + 1, operations.size()))) {
                write(fresh, key, sandbox.get(key));
            }
            return;
        }
        for (String key : touchedKeys(operations)) {
            reconcileKey(sandbox, real, key, action);
        }
    }

    private static void reconcileKey(SandboxedSessionMap sandbox, HttpSession real, String key, Object action) {
        Object current = real.getAttribute(key);
        Object snapshot = sandbox.getSnapshotValue(key);
        Object background = sandbox.get(key);
        if (Objects.equals(current, snapshot)) {
            write(real, key, background);
        } else if (action instanceof BackgroundSessionConflictAware aware) {
            SessionConflict conflict = new SessionConflict(key, snapshot, current, background);
            Object resolved = aware.resolveSessionConflict(conflict).resolve(conflict);
            if (resolved != current) {
                write(real, key, resolved);
            }
        } else {
            LOG.debug("Background process [{}] keeps the current value of session key [{}] changed by another request",
                    sandbox.getProcessName(), key);
        }
    }

    private static int lastInvalidateIndex(List<SessionOperation> operations) {
        for (int i = operations.size() - 1; i >= 0; i--) {
            if (operations.get(i) instanceof SessionOperation.Invalidate) {
                return i;
            }
        }
        return -1;
    }

    private static Set<String> touchedKeys(List<SessionOperation> operations) {
        Set<String> keys = new LinkedHashSet<>();
        for (SessionOperation operation : operations) {
            if (operation instanceof SessionOperation.Put put) {
                keys.add(put.key());
            } else if (operation instanceof SessionOperation.Remove removal) {
                keys.add(removal.key());
            }
        }
        return keys;
    }

    private static void write(HttpSession session, String key, Object value) {
        if (value == null) {
            session.removeAttribute(key);
        } else {
            session.setAttribute(key, value);
        }
    }
}
