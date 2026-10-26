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
package org.apache.struts2.factory;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ObjectFactory;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.util.reflection.ReflectionException;
import org.apache.struts2.util.reflection.ReflectionProvider;
import org.apache.struts2.validator.Validator;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Map;

/**
 * Default implementation
 */
public class DefaultValidatorFactory implements ValidatorFactory {

    private static final Logger LOG = LogManager.getLogger(DefaultValidatorFactory.class);

    private ObjectFactory objectFactory;
    private ReflectionProvider reflectionProvider;
    private boolean devMode;

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }

    @Inject(value = StrutsConstants.STRUTS_DEVMODE, required = false)
    public void setDevMode(String devMode) {
        this.devMode = BooleanUtils.toBoolean(devMode);
    }

    public Validator buildValidator(String className, Map<String, Object> params, Map<String, Object> extraContext) throws Exception {
        Validator validator = (Validator) objectFactory.buildBean(className, extraContext);
        if (params != null) {
            params.forEach((name, value) -> setParam(className, validator, name, value, extraContext));
        }

        return validator;
    }

    private void setParam(String className, Validator validator, String name, Object value, Map<String, Object> extraContext) {
        try {
            reflectionProvider.setProperty(name, value, validator, extraContext, true);
        } catch (ReflectionException e) {
            if (hasWritableProperty(validator, name)) {
                LOG.log(devMode ? Level.ERROR : Level.WARN, "Validator [{}] ignores param [{}]: value [{}] cannot be converted ({})",
                        className, name, value, ExceptionUtils.getRootCauseMessage(e));
            }
        }
    }

    private boolean hasWritableProperty(Validator validator, String name) {
        try {
            PropertyDescriptor descriptor = reflectionProvider.getPropertyDescriptor(validator.getClass(), name);
            return descriptor != null && descriptor.getWriteMethod() != null;
        } catch (IntrospectionException | ReflectionException e) {
            return false;
        }
    }
}
