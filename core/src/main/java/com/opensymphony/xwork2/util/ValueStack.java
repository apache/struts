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
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;

import java.util.Map;

/**
 * @deprecated since 6.7.0, use {@link org.apache.struts2.util.ValueStack} instead.
 */
@Deprecated
public interface ValueStack extends org.apache.struts2.util.ValueStack {

    @Override
    ActionContext getActionContext();

    static ValueStack adapt(org.apache.struts2.util.ValueStack actualStack) {
        return actualStack != null ? new LegacyAdapter(actualStack) : null;
    }

    class LegacyAdapter implements ValueStack {

        private final org.apache.struts2.util.ValueStack adaptee;

        private LegacyAdapter(org.apache.struts2.util.ValueStack adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        public Map<String, Object> getContext() {
            return adaptee.getContext();
        }

        @Override
        public ActionContext getActionContext() {
            return ActionContext.adapt(adaptee.getActionContext());
        }

        @Override
        public void setDefaultType(Class defaultType) {
            adaptee.setDefaultType(defaultType);
        }

        @Override
        public void setExprOverrides(Map<Object, Object> overrides) {
            adaptee.setExprOverrides(overrides);
        }

        @Override
        public Map<Object, Object> getExprOverrides() {
            return adaptee.getExprOverrides();
        }

        @Override
        public CompoundRoot getRoot() {
            return adaptee.getRoot();
        }

        @Override
        public void setValue(String expr, Object value) {
            adaptee.setValue(expr, value);
        }

        @Override
        public void setParameter(String expr, Object value) {
            adaptee.setParameter(expr, value);
        }

        @Override
        public void setValue(String expr, Object value, boolean throwExceptionOnFailure) {
            adaptee.setValue(expr, value, throwExceptionOnFailure);
        }

        @Override
        public String findString(String expr) {
            return adaptee.findString(expr);
        }

        @Override
        public String findString(String expr, boolean throwExceptionOnFailure) {
            return adaptee.findString(expr, throwExceptionOnFailure);
        }

        @Override
        public Object findValue(String expr) {
            return adaptee.findValue(expr);
        }

        @Override
        public Object findValue(String expr, boolean throwExceptionOnFailure) {
            return adaptee.findValue(expr, throwExceptionOnFailure);
        }

        @Override
        public Object findValue(String expr, Class asType) {
            return adaptee.findValue(expr, asType);
        }

        @Override
        public Object findValue(String expr, Class asType, boolean throwExceptionOnFailure) {
            return adaptee.findValue(expr, asType, throwExceptionOnFailure);
        }

        @Override
        public Object peek() {
            return adaptee.peek();
        }

        @Override
        public Object pop() {
            return adaptee.pop();
        }

        @Override
        public void push(Object o) {
            adaptee.push(o);
        }

        @Override
        public void set(String key, Object o) {
            adaptee.set(key, o);
        }

        @Override
        public int size() {
            return adaptee.size();
        }
    }
}
