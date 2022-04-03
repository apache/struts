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

import com.opensymphony.xwork2.interceptor.ValidationAware;

import java.io.Serializable;
import java.util.*;

/**
 * Provides a default implementation of ValidationAware. Returns new collections for
 * errors and messages (defensive copy).
 *
 * @author Jason Carreira
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class ValidationAwareSupport implements ValidationAware, Serializable {

    private Collection<String> actionErrors;
    private Collection<String> actionMessages;
    private Map<String, List<String>> fieldErrors;


    public synchronized void setActionErrors(Collection<String> errorMessages) {
        this.actionErrors = errorMessages;
    }

    public synchronized Collection<String> getActionErrors() {
        return new LinkedList<>(internalGetActionErrors());
    }

    public synchronized void setActionMessages(Collection<String> messages) {
        this.actionMessages = messages;
    }

    public synchronized Collection<String> getActionMessages() {
        return new LinkedList<>(internalGetActionMessages());
    }

    public synchronized void setFieldErrors(Map<String, List<String>> errorMap) {
        this.fieldErrors = errorMap;
    }

    public synchronized Map<String, List<String>> getFieldErrors() {
        return new LinkedHashMap<>(internalGetFieldErrors());
    }

    public synchronized void addActionError(String anErrorMessage) {
        internalGetActionErrors().add(anErrorMessage);
    }

    public synchronized void addActionMessage(String aMessage) {
        internalGetActionMessages().add(aMessage);
    }

    public synchronized void addFieldError(String fieldName, String errorMessage) {
        final Map<String, List<String>> errors = internalGetFieldErrors();
        List<String> thisFieldErrors = errors.get(fieldName);

        if (thisFieldErrors == null) {
            thisFieldErrors = new ArrayList<>();
            errors.put(fieldName, thisFieldErrors);
        }

        thisFieldErrors.add(errorMessage);
    }

    public synchronized boolean hasActionErrors() {
        return (actionErrors != null) && !actionErrors.isEmpty();
    }

    public synchronized boolean hasActionMessages() {
        return (actionMessages != null) && !actionMessages.isEmpty();
    }

    public synchronized boolean hasErrors() {
        return (hasActionErrors() || hasFieldErrors());
    }

    public synchronized boolean hasFieldErrors() {
        return (fieldErrors != null) && !fieldErrors.isEmpty();
    }

    private Collection<String> internalGetActionErrors() {
        if (actionErrors == null) {
            actionErrors = new ArrayList<>();
        }

        return actionErrors;
    }

    private Collection<String> internalGetActionMessages() {
        if (actionMessages == null) {
            actionMessages = new ArrayList<>();
        }

        return actionMessages;
    }

    private Map<String, List<String>> internalGetFieldErrors() {
        if (fieldErrors == null) {
            fieldErrors = new LinkedHashMap<>();
        }

        return fieldErrors;
    }

    /**
     * Clears field errors map.
     *
     * <p>
     * Will clear the map that contains field errors.
     * </p>
     */
    public synchronized void clearFieldErrors() {
        internalGetFieldErrors().clear();
    }

    /**
     * Clears action errors list.
     *
     * <p>
     * Will clear the list that contains action errors.
     * </p>
     */
    public synchronized void clearActionErrors() {
        internalGetActionErrors().clear();
    }

    /**
     * Clears messages list.
     *
     * <p>
     * Will clear the list that contains action messages.
     * </p>
     */
    public synchronized void clearMessages() {
        internalGetActionMessages().clear();
    }

    /**
     * Clears all error list/maps.
     *
     * <p>
     * Will clear the map and list that contain
     * field errors and action errors.
     * </p>
     */
    public synchronized void clearErrors() {
        internalGetFieldErrors().clear();
        internalGetActionErrors().clear();
    }

    /**
     * Clears all error and messages list/maps.
     *
     * <p>
     * Will clear the maps/lists that contain
     * field errors, action errors and action messages.
     * </p>
     */
    public synchronized void clearErrorsAndMessages() {
        internalGetFieldErrors().clear();
        internalGetActionErrors().clear();
        internalGetActionMessages().clear();
    }
}
