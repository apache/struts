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

import org.apache.struts2.ActionInvocation;
import org.apache.struts2.interceptor.ExecuteAndWaitInterceptor;

/**
 * Interface used to create a background process which will be executed by
 * {@link ExecuteAndWaitInterceptor}.
 *
 * <p>A process built on {@link StrutsBackgroundProcess} works on a session sandbox, a snapshot of the session taken
 * when the process starts, and receives it through {@link #withSessionSandbox(SandboxedSessionMap)}. Its writes are
 * applied to the real session by the polling request that sees the process finish, and only if the session id is
 * unchanged; conflicts keep the real session's value unless the action implements
 * {@link org.apache.struts2.action.BackgroundSessionConflictAware}. A custom implementation that does not extend
 * {@link StrutsBackgroundProcess}, or a subclass overriding {@link StrutsBackgroundProcess#prepare()}, gets no
 * sandbox.</p>
 */
public interface BackgroundProcess extends Runnable {

    BackgroundProcess prepare();

    Object getAction();

    ActionInvocation getInvocation();

    String getResult();

    Exception getException();

    boolean isDone();

    default BackgroundProcess withSessionSandbox(SandboxedSessionMap sandbox) {
        return this;
    }

    default SandboxedSessionMap getSessionSandbox() {
        return null;
    }
}
