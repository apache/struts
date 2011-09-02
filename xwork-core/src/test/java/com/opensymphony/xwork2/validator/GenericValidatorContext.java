/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.validator;

import java.util.*;


/**
 * Dummy validator context to use to capture error messages.
 *
 * @author Mark Woon
 * @author Matthew Payne
 */
public class GenericValidatorContext extends DelegatingValidatorContext {

    private Collection<String> actionErrors;
    private Collection<String> actionMessages;
    private Map<String, List<String>> fieldErrors;


    public GenericValidatorContext(Object object) {
        super(object);
    }


    @Override
    public synchronized void setActionErrors(Collection<String> errorMessages) {
        this.actionErrors = errorMessages;
    }

    @Override
    public synchronized Collection<String> getActionErrors() {
        return new ArrayList<String>(internalGetActionErrors());
    }

    @Override
    public synchronized void setActionMessages(Collection<String> messages) {
        this.actionMessages = messages;
    }

    @Override
    public synchronized Collection<String> getActionMessages() {
        return new ArrayList<String>(internalGetActionMessages());
    }

    @Override
    public synchronized void setFieldErrors(Map<String, List<String>> errorMap) {
        this.fieldErrors = errorMap;
    }

    /**
     * Get the field specific errors.
     *
     * @return an unmodifiable Map with errors mapped from fieldname (String) to Collection of String error messages
     */
    @Override
    public synchronized Map<String, List<String>> getFieldErrors() {
        return new HashMap<String, List<String>>(internalGetFieldErrors());
    }

    @Override
    public synchronized void addActionError(String anErrorMessage) {
        internalGetActionErrors().add(anErrorMessage);
    }

    /**
     * Add an Action level message to this Action
     */
    @Override
    public void addActionMessage(String aMessage) {
        internalGetActionMessages().add(aMessage);
    }

    @Override
    public synchronized void addFieldError(String fieldName, String errorMessage) {
        final Map<String, List<String>> errors = internalGetFieldErrors();
        List<String> thisFieldErrors = errors.get(fieldName);

        if (thisFieldErrors == null) {
            thisFieldErrors = new ArrayList<String>();
            errors.put(fieldName, thisFieldErrors);
        }

        thisFieldErrors.add(errorMessage);
    }

    @Override
    public synchronized boolean hasActionErrors() {
        return (actionErrors != null) && !actionErrors.isEmpty();
    }

    /**
     * Note that this does not have the same meaning as in WW 1.x
     *
     * @return (hasActionErrors() || hasFieldErrors())
     */
    @Override
    public synchronized boolean hasErrors() {
        return (hasActionErrors() || hasFieldErrors());
    }

    @Override
    public synchronized boolean hasFieldErrors() {
        return (fieldErrors != null) && !fieldErrors.isEmpty();
    }

    private Collection<String> internalGetActionErrors() {
        if (actionErrors == null) {
            actionErrors = new ArrayList<String>();
        }

        return actionErrors;
    }

    private Collection<String> internalGetActionMessages() {
        if (actionMessages == null) {
            actionMessages = new ArrayList<String>();
        }

        return actionMessages;
    }

    private Map<String, List<String>> internalGetFieldErrors() {
        if (fieldErrors == null) {
            fieldErrors = new HashMap<String, List<String>>();
        }

        return fieldErrors;
    }
}
