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
package org.apache.struts2.oval.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.Validateable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.PrefixMethodInvocationUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.opensymphony.xwork2.validator.ValidatorContext;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.Configurer;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.IterableElementContext;
import net.sf.oval.context.MapKeyContext;
import net.sf.oval.context.MapValueContext;
import net.sf.oval.context.MethodReturnValueContext;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.ExpressionEvaluationException;
import net.sf.oval.expression.ExpressionLanguage;
import net.sf.oval.expression.ExpressionLanguageOGNLImpl;
import net.sf.oval.localization.context.OValContextRenderer;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.oval.annotation.Profiles;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/*
 This interceptor provides validation using the OVal validation framework
 */
public class OValValidationInterceptor extends MethodFilterInterceptor implements MethodNameExtractor {

    public static final String STRUTS_OVAL_VALIDATE_JPAANNOTATIONS = "struts.oval.validateJPAAnnotations";

    private static final Logger LOG = LogManager.getLogger(OValValidationInterceptor.class);

    protected final static String VALIDATE_PREFIX = "validate";
    protected final static String ALT_VALIDATE_PREFIX = "validateDo";

    protected boolean alwaysInvokeValidate = true;
    protected boolean programmatic = true;
    protected OValValidationManager validationManager;
    protected boolean validateJPAAnnotations;
    protected TextProviderFactory textProviderFactory;

    private final ExpressionLanguage ognlExpressionLanguage;

    public OValValidationInterceptor() {
        ognlExpressionLanguage = new ExpressionLanguageOGNL();
        Validator.setContextRenderer(new StrutsContextRenderer());
    }

    @Inject
    public void setValidationManager(OValValidationManager validationManager) {
        this.validationManager = validationManager;
    }

    @Inject
    public void setTextProviderFactory(TextProviderFactory textProviderFactory) {
        this.textProviderFactory = textProviderFactory;
    }

    /**
     * Enable OVal support for JPA
     */
    @Inject(value = STRUTS_OVAL_VALIDATE_JPAANNOTATIONS)
    public void setValidateJPAAnnotations(String validateJPAAnnotations) {
        this.validateJPAAnnotations = Boolean.parseBoolean(validateJPAAnnotations);
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
            LOG.debug("Validating [{}/{}] with method [{}]", invocation.getProxy().getNamespace(), invocation.getProxy().getActionName(), methodName);
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
            LOG.debug("Invoking validate() on action [{}]", validateable);

            try {
                PrefixMethodInvocationUtil.invokePrefixMethod(
                    invocation,
                    new String[]{VALIDATE_PREFIX, ALT_VALIDATE_PREFIX});
            } catch (Exception e) {
                // If any exception occurred while doing reflection, we want
                // validate() to be executed
                LOG.warn("An exception occurred while executing the prefix method", e);
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
        Class<?> clazz = action.getClass();
        //read validation from xmls
        List<Configurer> configurers = validationManager.getConfigurers(clazz, context, validateJPAAnnotations);

        Validator validator = configurers.isEmpty() ? new Validator() : new Validator(configurers);
        // Note: For Oval <= 1.70, API requires "validator.addExpressionLanguage("ognl", ognlExpressionLanguage)".
        validator.getExpressionLanguageRegistry().registerExpressionLanguage("ognl", ognlExpressionLanguage);  // Usage for Oval >= 1.80 due to API changes
        //if the method is annotated with a @Profiles annotation, use those profiles
        Method method = clazz.getMethod(methodName);
        Profiles profiles = method.getAnnotation(Profiles.class);
        if (profiles != null) {
            String[] profileNames = profiles.value();
            if (profileNames != null && profileNames.length > 0) {
                validator.disableAllProfiles();
                LOG.debug("Enabling profiles [{}]", StringUtils.join(profileNames, ","));
                for (String profileName : profileNames)
                    validator.enableProfile(profileName);
            }
        }

        //perform validation
        List<ConstraintViolation> violations = validator.validate(action);
        addValidationErrors(violations.toArray(new ConstraintViolation[0]), action, valueStack, null);
    }

    private void addValidationErrors(ConstraintViolation[] violations, Object action, ValueStack valueStack, String parentFieldname) {
        if (violations != null) {
            ValidatorContext validatorContext = new DelegatingValidatorContext(action, textProviderFactory);
            for (ConstraintViolation violation : violations) {
                //translate message
                String key = violation.getMessage();

                String message;
                // push context variable into stack, to allow use ${max}, ${min} etc in error messages
                valueStack.push(violation.getMessageVariables());
                //push the validator into the stack
                valueStack.push(violation.getContext());
                try {
                    message = validatorContext.getText(key);
                } finally {
                    valueStack.pop();
                    valueStack.pop();
                }

                if (isActionError(violation)) {
                    LOG.debug("Adding action error '{}'", message);
                    validatorContext.addActionError(message);
                } else {
                    ValidationError validationError = buildValidationError(violation.getContextPath(), message);

                    // build field name
                    String fieldName = validationError.getFieldName();
                    if (parentFieldname != null) {
                        fieldName = parentFieldname + "." + fieldName;
                    }

                    LOG.debug("Adding field error [{}] with message '{}'", fieldName, validationError.getMessage());
                    validatorContext.addFieldError(fieldName, validationError.getMessage());

                    // add violations of member object fields
                    addValidationErrors(violation.getCauses(), action, valueStack, fieldName);
                }
            }
        }
    }


    /**
     * Get field name and message, used to add the validation error to fieldErrors
     */
    protected ValidationError buildValidationError(List<OValContext> contextPath, String message) {
        StringBuilder fieldName = new StringBuilder();
        String finalMessage = message;
        for (OValContext context : contextPath) {
            if (fieldName.length() > 0) {
                fieldName.append(".");
            }

            if (context instanceof FieldContext) {
                Field field = ((FieldContext) context).getField();
                String className = field.getDeclaringClass().getName();

                //the default OVal message shows the field name as ActionClass.fieldName
                finalMessage = StringUtils.removeStart(finalMessage, className + ".");
                fieldName.append(field.getName());
            } else if (context instanceof MethodReturnValueContext) {
                Method method = ((MethodReturnValueContext) context).getMethod();
                String className = method.getDeclaringClass().getName();
                String methodName = method.getName();

                //the default OVal message shows the field name as ActionClass.fieldName
                finalMessage = StringUtils.removeStart(message, className + ".");
                fieldName.append(extractName(method));

                //the result will have the full method name, like "getName()", replace it by "name" (obnly if it is a field)
                if (fieldName.length() == 0) {
                    finalMessage = finalMessage.replaceAll(methodName + "\\(.*?\\)", fieldName.toString());
                }
            } else {
                fieldName.append(context.toStringUnqualified());
            }
        }

        return new ValidationError(fieldName.toString(), message);
    }

    /**
     * Decide if a violation should be added to the fieldErrors or actionErrors
     */
    protected boolean isActionError(ConstraintViolation violation) {
        return false;
    }

    static class ValidationError {
        private final String fieldName;
        private final String message;

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

class ExpressionLanguageOGNL extends ExpressionLanguageOGNLImpl {

    private static final Logger LOG = LogManager.getLogger(ExpressionLanguageOGNL.class);

    public Object evaluate(final String expression, final Map<String, ?> values) throws ExpressionEvaluationException {
        try {
            LOG.debug("Evaluating OGNL expression: {}", expression);
            return Ognl.getValue(expression, ActionContext.getContext().getContextMap(), values);
        } catch (final OgnlException ex) {
            throw new ExpressionEvaluationException("Evaluating script with OGNL failed.", ex);
        }
    }
}

class StrutsContextRenderer implements OValContextRenderer, MethodNameExtractor {

    @Override
    public String render(final OValContext context) {
        return context.toStringUnqualified();
    }

    @Override
    public String render(List<OValContext> contextPath) {
        final StringBuilder sb = new StringBuilder(3 * contextPath.size());
        boolean isFirst = true;
        for (final OValContext ctx : contextPath) {
            final boolean isContainerElementContext = ctx instanceof IterableElementContext || ctx instanceof MapKeyContext || ctx instanceof MapValueContext;
            if (isFirst) {
                isFirst = false;
            } else if (isContainerElementContext) {
                // do nothing special
            } else {
                sb.append('.');
            }

            if (ctx instanceof MethodReturnValueContext) {
                sb.append(extractName(((MethodReturnValueContext) ctx).getMethod()));
            } else {
                sb.append(render(ctx));
            }
        }
        return sb.toString();
    }

}

interface MethodNameExtractor {

    default String extractName(Method method) {
        String methodName = method.getName();
        if (methodName.startsWith("get")) {
            return StringUtils.uncapitalize(StringUtils.removeStart(methodName, "get"));
        } else if (methodName.startsWith("is")) {
            return StringUtils.uncapitalize(StringUtils.removeStart(methodName, "is"));
        }
        return methodName;
    }

}
