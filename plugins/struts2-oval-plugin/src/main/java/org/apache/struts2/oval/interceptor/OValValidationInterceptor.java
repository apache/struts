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
package org.apache.struts2.oval.interceptor;

import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.PrefixMethodInvocationUtil;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.Validateable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.ValueStack;
import net.sf.oval.Validator;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.configuration.Configurer;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.OValContext;
import net.sf.oval.context.MethodReturnValueContext;

import java.util.List;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

import org.apache.struts2.oval.annotation.Profiles;
import org.apache.commons.lang.xwork.StringUtils;

/*
 This interceptor provides validation using the OVal validation framework
 */
public class OValValidationInterceptor extends MethodFilterInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(OValValidationInterceptor.class);

    protected final static String VALIDATE_PREFIX = "validate";
    protected final static String ALT_VALIDATE_PREFIX = "validateDo";

    protected boolean alwaysInvokeValidate = true;
    protected boolean programmatic = true;
    protected OValValidationManager validationManager;
    private boolean validateJPAAnnotations;

    @Inject
    public void setValidationManager(OValValidationManager validationManager) {
        this.validationManager = validationManager;
    }

    /**
     * Enable OVal support fopr JPA
     */
    public void setValidateJPAAnnotations(boolean validateJPAAnnotations) {
        this.validateJPAAnnotations = validateJPAAnnotations;
    }

    /**
     * Determines if {@link com.opensymphony.xwork2.Validateable}'s <code>validate()</code> should be called,
     * as well as methods whose name that start with "validate". Defaults to "true".
     *
     * @param programmatic <tt>true</tt> then <code>validate()</code> is invoked.
     */
    public void setProgrammatic(boolean programmatic) {
        this.programmatic = programmatic;
    }

    /**
     * Determines if {@link com.opensymphony.xwork2.Validateable}'s <code>validate()</code> should always
     * be invoked. Default to "true".
     *
     * @param alwaysInvokeValidate <tt>true</tt> then <code>validate()</code> is always invoked.
     */
    public void setAlwaysInvokeValidate(String alwaysInvokeValidate) {
        this.alwaysInvokeValidate = Boolean.parseBoolean(alwaysInvokeValidate);
    }

    protected String doIntercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        ActionProxy proxy = invocation.getProxy();
        ValueStack valueStack = invocation.getStack();
        String methodName = proxy.getMethod();
        String context = proxy.getConfig().getName();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Validating [#0/#1] with method [#2]", invocation.getProxy().getNamespace(), invocation.getProxy().getActionName(), methodName);
        }

        //OVal vallidatio (no XML yet)
        performOValValidation(action, valueStack, methodName, context);

        //Validatable.valiedate() and validateX()
        performProgrammaticValidation(invocation, action);

        return invocation.invoke();
    }

    private void performProgrammaticValidation(ActionInvocation invocation, Object action) throws Exception {
        if (action instanceof Validateable && programmatic) {
            // keep exception that might occured in validateXXX or validateDoXXX
            Exception exception = null;

            Validateable validateable = (Validateable) action;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Invoking validate() on action [#0]", validateable.toString());
            }

            try {
                PrefixMethodInvocationUtil.invokePrefixMethod(
                        invocation,
                        new String[]{VALIDATE_PREFIX, ALT_VALIDATE_PREFIX});
            } catch (Exception e) {
                // If any exception occurred while doing reflection, we want
                // validate() to be executed
                LOG.warn("An exception occured while executing the prefix method", e);
                exception = e;
            }

            if (alwaysInvokeValidate) {
                validateable.validate();
            }

            if (exception != null) {
                // rethrow if something is wrong while doing validateXXX / validateDoXXX
                throw exception;
            }
        }
    }

    protected void performOValValidation(Object action, ValueStack valueStack, String methodName, String context) throws NoSuchMethodException {
        Class clazz = action.getClass();
        //read validation from xmls
        List<Configurer> configurers = validationManager.getConfigurers(clazz, context, validateJPAAnnotations);

        Validator validator = configurers.isEmpty() ? new Validator() : new Validator(configurers);
        //if the method is annotated with a @Profiles annotation, use those profiles
        Method method = clazz.getMethod(methodName, new Class[0]);
        if (method != null) {
            Profiles profiles = method.getAnnotation(Profiles.class);
            if (profiles != null) {
                String[] profileNames = profiles.value();
                if (profileNames != null && profileNames.length > 0) {
                    validator.disableAllProfiles();
                    if (LOG.isDebugEnabled())
                        LOG.debug("Enabling profiles [#0]", StringUtils.join(profileNames, ","));
                    for (String profileName : profileNames)
                        validator.enableProfile(profileName);
                }
            }
        }

        //perform validation
        List<ConstraintViolation> violations = validator.validate(action);
        if (violations != null) {
            ValidatorContext validatorContext = new DelegatingValidatorContext(action);
            for (ConstraintViolation violation : violations) {
                //translate message
                String key = violation.getMessage();

                //push the validator into the stack
                valueStack.push(violation.getContext());
                String message = key;
                try {
                    message = validatorContext.getText(key);
                } finally {
                    valueStack.pop();
                }

                if (isActionError(violation))
                    validatorContext.addActionError(message);
                else {
                    ValidationError validationError = buildValidationError(violation, message);
                    validatorContext.addFieldError(validationError.getFieldName(), validationError.getMessage());
                }
            }
        }
    }


    /**
     * Get field name and message, used to add the validation error to fieldErrors
     */
    protected ValidationError buildValidationError(ConstraintViolation violation, String message) {
        OValContext context = violation.getContext();
        if (context instanceof FieldContext) {
            Field field = ((FieldContext) context).getField();
            String className = field.getDeclaringClass().getName();

            //the default OVal message shows the field name as ActionClass.fieldName
            String finalMessage = StringUtils.removeStart(message, className + ".");

            return new ValidationError(field.getName(), finalMessage);
        } else if (context instanceof MethodReturnValueContext) {
            Method method = ((MethodReturnValueContext) context).getMethod();
            String className = method.getDeclaringClass().getName();
            String methodName = method.getName();

            //the default OVal message shows the field name as ActionClass.fieldName
            String finalMessage = StringUtils.removeStart(message, className + ".");

            String fieldName = null;
            if (methodName.startsWith("get")) {
                fieldName = StringUtils.uncapitalize(StringUtils.removeStart(methodName, "get"));
            } else if (methodName.startsWith("is")) {
                fieldName = StringUtils.uncapitalize(StringUtils.removeStart(methodName, "is"));
            }

            //the result will have the full method name, like "getName()", replace it by "name" (obnly if it is a field)
            if (fieldName != null)
                finalMessage = finalMessage.replaceAll(methodName + "\\(.*?\\)", fieldName);

            return new ValidationError(StringUtils.defaultString(fieldName, methodName), finalMessage);
        }

        return new ValidationError(violation.getCheckName(), message);
    }

    /**
     * Decide if a violation should be added to the fieldErrors or actionErrors
     */
    protected boolean isActionError(ConstraintViolation violation) {
        return false;
    }

    class ValidationError {
        private String fieldName;
        private String message;

        ValidationError(String fieldName, String message) {
            this.fieldName = fieldName;
            this.message = message;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getMessage() {
            return message;
        }
    }
}
