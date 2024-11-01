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
package com.opensymphony.xwork2.interceptor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @deprecated since 6.7.0, use {@link org.apache.struts2.interceptor.ValidationAware} instead.
 */
@Deprecated
public interface ValidationAware extends org.apache.struts2.interceptor.ValidationAware {

    static ValidationAware adapt(org.apache.struts2.interceptor.ValidationAware actualValidation) {
        if (actualValidation instanceof ValidationAware) {
            return (ValidationAware) actualValidation;
        }
        return actualValidation != null ? new LegacyAdapter(actualValidation) : null;
    }

    class LegacyAdapter implements ValidationAware {

        private final org.apache.struts2.interceptor.ValidationAware adaptee;

        private LegacyAdapter(org.apache.struts2.interceptor.ValidationAware adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        public void setActionErrors(Collection<String> errorMessages) {
            adaptee.setActionErrors(errorMessages);
        }

        @Override
        public Collection<String> getActionErrors() {
            return adaptee.getActionErrors();
        }

        @Override
        public void setActionMessages(Collection<String> messages) {
            adaptee.setActionMessages(messages);
        }

        @Override
        public Collection<String> getActionMessages() {
            return adaptee.getActionMessages();
        }

        @Override
        public void setFieldErrors(Map<String, List<String>> errorMap) {
            adaptee.setFieldErrors(errorMap);
        }

        @Override
        public Map<String, List<String>> getFieldErrors() {
            return adaptee.getFieldErrors();
        }

        @Override
        public void addActionError(String anErrorMessage) {
            adaptee.addActionError(anErrorMessage);
        }

        @Override
        public void addActionMessage(String aMessage) {
            adaptee.addActionMessage(aMessage);
        }

        @Override
        public void addFieldError(String fieldName, String errorMessage) {
            adaptee.addFieldError(fieldName, errorMessage);
        }

        @Override
        public boolean hasActionErrors() {
            return adaptee.hasActionErrors();
        }

        @Override
        public boolean hasActionMessages() {
            return adaptee.hasActionMessages();
        }

        @Override
        public boolean hasFieldErrors() {
            return adaptee.hasFieldErrors();
        }
    }
}
