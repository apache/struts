/*
 * $Id$
 *
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

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.StrutsException;

@StrutsTag(name="debug", tldTagClass="org.apache.struts2.views.jsp.ui.DebugTag",
        description="Prints debugging information")
public class Debug extends UIBean {
    public static final String TEMPLATE = "debug";
    
    protected ReflectionProvider reflectionProvider;

    

    public Debug(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider prov) {
        this.reflectionProvider = prov;
    }
    
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);

        ValueStack stack = getStack();
        Iterator iter = stack.getRoot().iterator();
        List stackValues = new ArrayList(stack.getRoot().size());
        while (iter.hasNext()) {
            Object o = iter.next();
            Map values;
            try {
                values = reflectionProvider.getBeanMap(o);
            } catch (Exception e) {
                throw new StrutsException("Caught an exception while getting the property values of " + o, e);
            }
            stackValues.add(new DebugMapEntry(o.getClass().getName(), values));
        }

        addParameter("stackValues", stackValues);

        return result;
    }

    private static class DebugMapEntry implements Map.Entry {
        private Object key;
        private Object value;

        DebugMapEntry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public Object setValue(Object newVal) {
            Object oldVal = value;
            value = newVal;
            return oldVal;
        }
    }

}
