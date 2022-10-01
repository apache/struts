/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.jsp.extractor;

import org.apache.tiles.request.attribute.AttributeExtractor;

import javax.servlet.jsp.PageContext;
import java.util.Enumeration;

/**
 * Extracts attributes from session scope from {@link PageContext}.
 */
public class SessionScopeExtractor implements AttributeExtractor {

    /**
     * The page context.
     */
    private final PageContext context;

    /**
     * Constructor.
     *
     * @param context The page context.
     */
    public SessionScopeExtractor(PageContext context) {
        this.context = context;
    }

    @Override
    public void removeValue(String name) {
        if (context.getSession() == null) {
            return;
        }
        context.removeAttribute(name, PageContext.SESSION_SCOPE);
    }

    @Override
    public Enumeration<String> getKeys() {
        if (context.getSession() == null) {
            return null;
        }
        return context.getAttributeNamesInScope(PageContext.SESSION_SCOPE);
    }

    @Override
    public Object getValue(String key) {
        if (context.getSession() == null) {
            return null;
        }
        return context.getAttribute(key, PageContext.SESSION_SCOPE);
    }

    @Override
    public void setValue(String key, Object value) {
        if (context.getSession() == null) {
            return;
        }
        context.setAttribute(key, value, PageContext.SESSION_SCOPE);
    }
}
