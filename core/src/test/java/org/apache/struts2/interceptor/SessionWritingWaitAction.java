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

import org.apache.struts2.ActionContext;
import org.apache.struts2.action.Action;
import org.apache.struts2.action.SessionAware;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.parameter.StrutsParameter;

import java.util.Map;

/**
 * Used by ExecuteAndWaitInterceptorTest to write to the session from the background thread.
 */
public class SessionWritingWaitAction implements Action, SessionAware {

    private Map<String, Object> session;
    private String failWith;
    private boolean invalidateFirst;

    @Override
    @SuppressWarnings("java:S2925") // simulates the long-running work execAndWait exists for
    public String execute() throws Exception {
        Thread.sleep(200);
        if (invalidateFirst) {
            ((SessionMap) session).invalidate();
        }
        ActionContext.getContext().getSession().put("result", "done");
        session.put("aware", "yes");
        if (failWith != null) {
            throw new IllegalStateException(failWith);
        }
        return SUCCESS;
    }

    @Override
    public void withSession(Map<String, Object> session) {
        this.session = session;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    @StrutsParameter
    public void setFailWith(String failWith) {
        this.failWith = failWith;
    }

    @StrutsParameter
    public void setInvalidateFirst(boolean invalidateFirst) {
        this.invalidateFirst = invalidateFirst;
    }
}
