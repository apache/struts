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
package org.apache.struts2.interceptor.validation;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.validator.ValidationInterceptor;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

/**
 * Extends the xwork validation interceptor to also check for a @SkipValidation
 * annotation, and if found, don't validate this action method
 */
public class AnnotationValidationInterceptor extends ValidationInterceptor {

    private static final Logger LOG = LogManager.getLogger(AnnotationValidationInterceptor.class);

    protected String doIntercept(ActionInvocation invocation) throws Exception {

        Object action = invocation.getAction();
        if (action != null) {
            Method method = getActionMethod(action.getClass(), invocation.getProxy().getMethod());

            if (null != MethodUtils.getAnnotation(method, SkipValidation.class, true, true)) {
                return invocation.invoke();
            }
        }

        return super.doIntercept(invocation);
    }

    protected Method getActionMethod(Class<?> actionClass, String methodName) {
        try {
            return actionClass.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            throw new ConfigurationException("Wrong method was defined as an action method: " + methodName, e);
        }
    }

}
