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

import com.opensymphony.xwork2.util.ValueStack;

/**
 * {@inheritDoc}
 *
 * @deprecated since 6.7.0, use {@link org.apache.struts2.validator.Validator} instead.
 */
@Deprecated
public interface Validator<T> extends org.apache.struts2.validator.Validator<T> {

    @Override
    default void setValidatorContext(org.apache.struts2.validator.ValidatorContext validatorContext) {
        setValidatorContext(ValidatorContext.adapt(validatorContext));
    }

    void setValidatorContext(ValidatorContext validatorContext);

    @Override
    ValidatorContext getValidatorContext();

    @Override
    void validate(Object object) throws ValidationException;

    @Override
    default void setValueStack(org.apache.struts2.util.ValueStack stack) {
        setValueStack(ValueStack.adapt(stack));
    }

    void setValueStack(ValueStack stack);
}
