/*
 * $Id$
 *
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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.AnnotationUtils;
import com.opensymphony.xwork2.validator.ValidationInterceptor;

import org.apache.struts2.StrutsConstants;

/**
 * Extends the xwork validation interceptor to also check for a @SkipValidation
 * annotation, and if found, don't validate this action method
 */
public class AnnotationValidationInterceptor extends ValidationInterceptor {

    /** Auto-generated serialization id */
    private static final long serialVersionUID = 1813272797367431184L;

    private boolean devMode;

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String devMode) {
        this.devMode = "true".equalsIgnoreCase(devMode);
    }

    protected String doIntercept(ActionInvocation invocation) throws Exception {

        Object action = invocation.getAction();
        if (action != null) {
            Method method = getActionMethod(action.getClass(), invocation.getProxy().getMethod());
            Collection<Method> annotatedMethods = AnnotationUtils.getAnnotatedMethods(action.getClass(), SkipValidation.class);
            if (annotatedMethods.contains(method))
                return invocation.invoke();

            //check if method overwites an annotated method
            Class clazz = action.getClass().getSuperclass();
            while (clazz != null) {
                annotatedMethods = AnnotationUtils.getAnnotatedMethods(clazz, SkipValidation.class);
                if (annotatedMethods != null) {
                    for (Method annotatedMethod : annotatedMethods) {
                        if (annotatedMethod.getName().equals(method.getName())
                                && Arrays.equals(annotatedMethod.getParameterTypes(), method.getParameterTypes())
                                && Arrays.equals(annotatedMethod.getExceptionTypes(), method.getExceptionTypes()))
                            return invocation.invoke();
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }

        return super.doIntercept(invocation);
    }

    // FIXME: This is copied from DefaultActionInvocation but should be exposed through the interface
    protected Method getActionMethod(Class actionClass, String methodName) throws NoSuchMethodException {
        Method method = null;
        try {
            method = actionClass.getMethod(methodName, new Class[0]);
        } catch (NoSuchMethodException e) {
            // hmm -- OK, try doXxx instead
            try {
                String altMethodName = "do" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
                method = actionClass.getMethod(altMethodName, new Class[0]);
            } catch (NoSuchMethodException e1) {
                // throw the original one
                if (devMode) {
                    throw e;
                }
            }
        }
        return method;
    }

}
