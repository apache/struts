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
package org.apache.struts2.components;

import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.PrepareOperations;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.ognl.ThreadAllowlist;
import org.apache.struts2.util.CompoundRoot;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.reflection.ReflectionProvider;
import org.apache.struts2.views.annotations.StrutsTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@StrutsTag(name="debug", tldTagClass="org.apache.struts2.views.jsp.ui.DebugTag",
        description="Prints debugging information (Only if 'struts.devMode' is enabled)")
public class Debug extends UIBean {
    public static final String TEMPLATE = "debug";

    protected ReflectionProvider reflectionProvider;

    private ThreadAllowlist threadAllowlist;

    public Debug(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider prov) {
        this.reflectionProvider = prov;
    }

    @Inject
    public void setThreadAllowlist(ThreadAllowlist threadAllowlist) {
        this.threadAllowlist = threadAllowlist;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);

        if (showDebug()) {
            ValueStack stack = getStack();
            allowList(stack.getRoot());

            Iterator<Object> iter = stack.getRoot().iterator();
            List<Object> stackValues = new ArrayList<>(stack.getRoot().size());
            while (iter.hasNext()) {
                Object o = iter.next();
                Map<String, Object> values;
                try {
                    values = reflectionProvider.getBeanMap(o);
                } catch (Exception e) {
                    throw new StrutsException("Caught an exception while getting the property values of " + o, e);
                }
                allowListClass(o);
                stackValues.add(new DebugMapEntry(o.getClass().getName(), values));
            }

            addParameter("stackValues", stackValues);
        }
        return result;
    }

    private void allowList(CompoundRoot root) {
        root.forEach(this::allowListClass);
    }

    private void allowListClass(Object o) {
        threadAllowlist.allowClassHierarchy(o.getClass());
    }

    @Override
    public boolean end(Writer writer, String body) {
        if (showDebug()) {
            return super.end(writer, body);
        } else {
            popComponentStack();
            return false;
        }
    }

    protected boolean showDebug() {
        return (devMode || Boolean.TRUE == PrepareOperations.getDevModeOverride());
    }

    private static class DebugMapEntry implements Map.Entry<String, Object> {
        private final String key;
        private Object value;

        DebugMapEntry(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public Object setValue(Object newVal) {
            Object oldVal = value;
            value = newVal;
            return oldVal;
        }
    }

}
