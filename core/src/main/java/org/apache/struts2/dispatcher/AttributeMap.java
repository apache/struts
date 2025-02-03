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

import org.apache.struts2.StrutsStatics;

import jakarta.servlet.jsp.PageContext;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A Map that holds 4 levels of scope.
 * <p>
 * The scopes are the ones known in the web world:
 * </p>
 *
 * <ul>
 *   <li>Page scope</li>
 *   <li>Request scope</li>
 *   <li>Session scope</li>
 *   <li>Application scope</li>
 * </ul>
 * <p>
 * A object is searched in the order above, starting from page and ending at application scope.
 */
public class AttributeMap extends AbstractMap<String, Object> {

    protected static final String UNSUPPORTED = "method makes no sense for a simplified map";

    private final Map<String, Object> context;

    public AttributeMap(Map<String, Object> context) {
        this.context = context;
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    @Override
    public boolean containsKey(Object key) {
        return (get(key) != null);
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return Collections.unmodifiableSet(this.context.entrySet());
    }

    @Override
    public Object get(Object key) {
        if (key == null) {
            return null;
        }

        PageContext pc = getPageContext();

        if (pc == null || pc.getRequest() == null) {
            RequestMap request = (RequestMap) context.get(DispatcherConstants.REQUEST);
            SessionMap session = (SessionMap) context.get(DispatcherConstants.SESSION);
            ApplicationMap application = (ApplicationMap) context.get(DispatcherConstants.APPLICATION);

            if ((request != null) && (request.get(key) != null)) {
                return request.get(key);
            } else if ((session != null) && (session.get(key) != null)) {
                return session.get(key);
            } else if ((application != null) && (application.get(key) != null)) {
                return application.get(key);
            }
        } else {
            return pc.findAttribute(key.toString());
        }

        return null;
    }

    @Override
    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.context.keySet());
    }

    @Override
    public Object put(String key, Object value) {
        PageContext pc = getPageContext();
        if (pc != null) {
            pc.setAttribute(key, value);
            return value;
        }

        return null;
    }

    @Override
    public void putAll(Map t) {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    @Override
    public Collection<Object> values() {
        return Collections.unmodifiableCollection(this.context.values());
    }

    private PageContext getPageContext() {
        return (PageContext) context.get(StrutsStatics.PAGE_CONTEXT);
    }

    @Override
    public String toString() {
        return "AttributeMap {" +
            "request=" + toStringSafe(context.get(DispatcherConstants.REQUEST)) +
            ", session=" + toStringSafe(context.get(DispatcherConstants.SESSION)) +
            ", application=" + toStringSafe(context.get(DispatcherConstants.APPLICATION)) +
            '}';
    }

    private String toStringSafe(Object obj) {
        try {
            if (obj != null) {
                return String.valueOf(obj);
            }
            return "";
        } catch (Exception e) {
            return "Exception thrown: " + e;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttributeMap that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), context);
    }

}
