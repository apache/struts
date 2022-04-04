/*
 * Copyright 2002-2007,2009 The Apache Software Foundation.
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

import java.util.List;

/**
 * ActionValidatorManager is the main interface for validation managers (regular and annotation based).
 *
 * @author Rainer Hermanns
 */
public interface ActionValidatorManager {

    /**
     * Returns a list of validators for the given class, context, and method. This is the primary
     * lookup method for validators.
     *
     * @param clazz the class to lookup.
     * @param context the context of the action class - can be <tt>null</tt>.
     * @param method the name of the method being invoked on the action - can be <tt>null</tt>.
     * @return a list of all validators for the given class and context.
     */
    List<Validator> getValidators(Class clazz, String context, String method);

    /**
     * Returns a list of validators for the given class and context. This is the primary
     * lookup method for validators.
     *
     * @param clazz the class to lookup.
     * @param context the context of the action class - can be <tt>null</tt>.
     * @return a list of all validators for the given class and context.
     */
    List<Validator> getValidators(Class clazz, String context);

    /**
     * Validates the given object using action and its context.
     *
     * @param object the action to validate.
     * @param context the action's context.
     * @throws ValidationException if an error happens when validating the action.
     */
    void validate(Object object, String context) throws ValidationException;

    /**
     * Validates an action give its context and a validation context.
     *
     * @param object the action to validate.
     * @param context the action's context.
     * @param validatorContext the validation context to use
     * @throws ValidationException if an error happens when validating the action.
     */
    void validate(Object object, String context, ValidatorContext validatorContext) throws ValidationException;

    /**
     * Validates the given object using an action, its context, and the name of the method being invoked on the action.
     *
     * @param object the action to validate.
     * @param context the action's context.
     * @param method the name of the method being invoked on the action - can be <tt>null</tt>.
     * @throws ValidationException if an error happens when validating the action.
     */
    void validate(Object object, String context, String method) throws ValidationException;

    /**
     * Validates an action give its context and a validation context.
     *
     * @param object the action to validate.
     * @param context the action's context.
     * @param validatorContext the validation context to use
     * @param method the name of the method being invoked on the action - can be <tt>null</tt>.
     * @throws ValidationException if an error happens when validating the action.
     */
    void validate(Object object, String context, ValidatorContext validatorContext, String method) throws ValidationException;
}
