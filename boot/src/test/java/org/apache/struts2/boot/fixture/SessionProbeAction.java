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
package org.apache.struts2.boot.fixture;

import org.apache.struts2.action.SessionAware;

import java.util.Map;

/**
 * Forces an {@link jakarta.servlet.http.HttpSession} to exist and reports a counter stored in
 * it, so a test can tell whether two requests landed in the *same* session (counter keeps
 * incrementing) or different ones (counter resets to 1).
 */
public class SessionProbeAction implements SessionAware {

    private Map<String, Object> session;

    public String execute() {
        Object current = session.get("probeCounter");
        int next = (current instanceof Integer i ? i : 0) + 1;
        session.put("probeCounter", next);
        return "success";
    }

    public String getCounter() {
        return String.valueOf(session.get("probeCounter"));
    }

    @Override
    public void withSession(Map<String, Object> session) {
        this.session = session;
    }
}
