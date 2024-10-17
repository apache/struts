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
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.util.ValueStack;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @deprecated since 6.7.0, use {@link org.apache.struts2.validator.ValidatorContext} instead.
 */
@Deprecated
public interface ValidatorContext extends org.apache.struts2.validator.ValidatorContext, ValidationAware {

    static ValidatorContext adapt(org.apache.struts2.validator.ValidatorContext actualContext) {
        if (actualContext instanceof ActionContext) {
            return (ValidatorContext) actualContext;
        }
        return actualContext != null ? new LegacyAdapter(actualContext) : null;
    }

    class LegacyAdapter implements ValidatorContext {

        private final org.apache.struts2.validator.ValidatorContext adaptee;

        public LegacyAdapter(org.apache.struts2.validator.ValidatorContext adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        public String getFullFieldName(String fieldName) {
            return adaptee.getFullFieldName(fieldName);
        }

        @Override
        public Locale getLocale() {
            return adaptee.getLocale();
        }

        @Override
        public boolean isValidLocaleString(String localeStr) {
            return adaptee.isValidLocaleString(localeStr);
        }

        @Override
        public boolean isValidLocale(Locale locale) {
            return adaptee.isValidLocale(locale);
        }

        @Override
        public boolean hasKey(String key) {
            return adaptee.hasKey(key);
        }

        @Override
        public String getText(String key) {
            return adaptee.getText(key);
        }

        @Override
        public String getText(String key, String defaultValue) {
            return adaptee.getText(key, defaultValue);
        }

        @Override
        public String getText(String key, String defaultValue, String obj) {
            return adaptee.getText(key, defaultValue, obj);
        }

        @Override
        public String getText(String key, List<?> args) {
            return adaptee.getText(key, args);
        }

        @Override
        public String getText(String key, String[] args) {
            return adaptee.getText(key, args);
        }

        @Override
        public String getText(String key, String defaultValue, List<?> args) {
            return adaptee.getText(key, defaultValue, args);
        }

        @Override
        public String getText(String key, String defaultValue, String[] args) {
            return adaptee.getText(key, defaultValue, args);
        }

        @Override
        public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
            return adaptee.getText(key, defaultValue, args, stack);
        }

        @Override
        public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
            return adaptee.getText(key, defaultValue, args, stack);
        }

        @Override
        public ResourceBundle getTexts(String bundleName) {
            return adaptee.getTexts(bundleName);
        }

        @Override
        public ResourceBundle getTexts() {
            return adaptee.getTexts();
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
