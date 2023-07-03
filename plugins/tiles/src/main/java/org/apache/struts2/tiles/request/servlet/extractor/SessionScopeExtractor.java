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
package org.apache.struts2.tiles.request.servlet.extractor;

import java.util.Collections;
import java.util.Enumeration;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.tiles.request.attribute.AttributeExtractor;

public class SessionScopeExtractor implements AttributeExtractor {
    private HttpServletRequest request;

    public SessionScopeExtractor(HttpServletRequest request) {
        this.request = request;
    }

    public void setValue(String name, Object value) {
        this.request.getSession().setAttribute(name, value);
    }

    public void removeValue(String name) {
        HttpSession session = this.request.getSession(false);
        if (session != null) {
            session.removeAttribute(name);
        }

    }

    public Enumeration<String> getKeys() {
        HttpSession session = this.request.getSession(false);
        return session != null ? session.getAttributeNames() : Collections.enumeration(Collections.emptySet());
    }

    public Object getValue(String key) {
        HttpSession session = this.request.getSession(false);
        return session != null ? session.getAttribute(key) : null;
    }
}

