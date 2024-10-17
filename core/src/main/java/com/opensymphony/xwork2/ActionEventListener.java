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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * {@inheritDoc}
 *
 * @deprecated since 6.7.0, use {@link org.apache.struts2.ActionEventListener} instead.
 */
@Deprecated
public interface ActionEventListener extends org.apache.struts2.ActionEventListener {

    @Override
    default Object prepare(Object action, org.apache.struts2.util.ValueStack stack) {
        return prepare(action, ValueStack.adapt(stack));
    }

    @Override
    default String handleException(Throwable t, org.apache.struts2.util.ValueStack stack) {
        return handleException(t, ValueStack.adapt(stack));
    }

    Object prepare(Object action, ValueStack stack);

    String handleException(Throwable t, ValueStack stack);

    static ActionEventListener adapt(org.apache.struts2.ActionEventListener actualListener) {
        return actualListener != null ? new LegacyAdapter(actualListener) : null;
    }

    class LegacyAdapter implements ActionEventListener {

        private final org.apache.struts2.ActionEventListener adaptee;

        private LegacyAdapter(org.apache.struts2.ActionEventListener adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        public Object prepare(Object action, ValueStack stack) {
            return adaptee.prepare(action, stack);
        }

        @Override
        public String handleException(Throwable t, ValueStack stack) {
            return adaptee.handleException(t, stack);
        }
    }
}
