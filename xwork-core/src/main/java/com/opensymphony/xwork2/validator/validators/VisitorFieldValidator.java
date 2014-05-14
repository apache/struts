/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ActionValidatorManager;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.ValidatorContext;

import java.util.Collection;


/**
 * <!-- START SNIPPET: javadoc -->
 * The VisitorFieldValidator allows you to forward validation to object
 * properties of your action using the object's own validation files.  This
 * allows you to use the ModelDriven development pattern and manage your
 * validations for your models in one place, where they belong, next to your
 * model classes.  The VisitorFieldValidator can handle either simple Object
 * properties, Collections of Objects, or Arrays.
 * <!-- END SNIPPET: javadoc -->
 * <p/>
 *
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * <li>fieldName - field name if plain-validator syntax is used, not needed if field-validator syntax is used</li>
 * <li>context - the context of which validation should take place. Optional</li>
 * <li>appendPrefix - the prefix to be added to field. Optional </li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *    &lt;validators&gt;
 *        &lt;!-- Plain Validator Syntax --&gt;
 *        &lt;validator type="visitor"&gt;
 *            &lt;param name="fieldName"&gt;user&lt;/param&gt;
 *            &lt;param name="context"&gt;myContext&lt;/param&gt;
 *            &lt;param name="appendPrefix"&gt;true&lt;/param&gt;
 *        &lt;/validator&gt;
 *
 *        &lt;!-- Field Validator Syntax --&gt;
 *        &lt;field name="user"&gt;
 *           &lt;field-validator type="visitor"&gt;
 *              &lt;param name="context"&gt;myContext&lt;/param&gt;
 *              &lt;param name="appendPrefix"&gt;true&lt;/param&gt;
 *           &lt;/field-validator&gt;
 *        &lt;/field&gt;
 *    &lt;/validators&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * <!-- START SNIPPET: explanation -->
 * <p>In the example above, if the acion's getUser() method return User object, XWork
 * will look for User-myContext-validation.xml for the validators. Since appednPrefix is true,
 * every field name will be prefixed with 'user' such that if the actual field name for 'name' is
 * 'user.name' </p>
 * <!-- END SNIPPET: explanation -->
 *
 * @author Jason Carreira
 * @author Rainer Hermanns
 * @version $Date$ $Id$
 */
public class VisitorFieldValidator extends FieldValidatorSupport {

    private String context;
    private boolean appendPrefix = true;
    private ActionValidatorManager actionValidatorManager;


    @Inject
    public void setActionValidatorManager(ActionValidatorManager mgr) {
        this.actionValidatorManager = mgr;
    }

    /**
     * Sets whether the field name of this field validator should be prepended to the field name of
     * the visited field to determine the full field name when an error occurs.  The default is
     * true.
     */
    public void setAppendPrefix(boolean appendPrefix) {
        this.appendPrefix = appendPrefix;
    }

    /**
     * Flags whether the field name of this field validator should be prepended to the field name of
     * the visited field to determine the full field name when an error occurs.  The default is
     * true.
     */
    public boolean isAppendPrefix() {
        return appendPrefix;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);
        if (value == null) {
            log.warn("The visited object is null, VisitorValidator will not be able to handle validation properly. Please make sure the visited object is not null for VisitorValidator to function properly");
            return;
        }
        ValueStack stack = ActionContext.getContext().getValueStack();

        stack.push(object);

        String visitorContext = (context == null) ? ActionContext.getContext().getName() : context;

        if (value instanceof Collection) {
            Collection coll = (Collection) value;
            Object[] array = coll.toArray();

            validateArrayElements(array, fieldName, visitorContext);
        } else if (value instanceof Object[]) {
            Object[] array = (Object[]) value;

            validateArrayElements(array, fieldName, visitorContext);
        } else {
            validateObject(fieldName, value, visitorContext);
        }

        stack.pop();
    }

    private void validateArrayElements(Object[] array, String fieldName, String visitorContext) throws ValidationException {
        if (array == null) {
            return;
        }

        for (int i = 0; i < array.length; i++) {
            Object o = array[i];
            if (o != null) {
                validateObject(fieldName + "[" + i + "]", o, visitorContext);
            }
        }
    }

    private void validateObject(String fieldName, Object o, String visitorContext) throws ValidationException {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(o);

        ValidatorContext validatorContext;

        if (appendPrefix) {
            validatorContext = new AppendingValidatorContext(getValidatorContext(), o, fieldName, getMessage(o));
        } else {
            ValidatorContext parent = getValidatorContext();
            validatorContext = new DelegatingValidatorContext(parent, DelegatingValidatorContext.makeTextProvider(o, parent), parent);
        }

        actionValidatorManager.validate(o, visitorContext, validatorContext);
        stack.pop();
    }


    public static class AppendingValidatorContext extends DelegatingValidatorContext {
        private String field;
        private String message;
        private ValidatorContext parent;

        public AppendingValidatorContext(ValidatorContext parent, Object object, String field, String message) {
            super(parent, makeTextProvider(object, parent), parent);

            this.field = field;
            this.message = message;
            this.parent = parent;
        }

        /**
         * Translates a simple field name into a full field name in Ognl syntax
         *
         * @param fieldName field name in OGNL syntax
         * @return full field name in OGNL syntax
         */
        @Override
        public String getFullFieldName(String fieldName) {
            if (parent instanceof VisitorFieldValidator.AppendingValidatorContext) {
                return parent.getFullFieldName(field + "." + fieldName);
            }
            return field + "." + fieldName;
        }

        public String getFieldNameWithField(String fieldName) {
            return field + "." + fieldName;
        }

        @Override
        public void addActionError(String anErrorMessage) {
            super.addFieldError(getFieldNameWithField(field), message + anErrorMessage);
        }

        @Override
        public void addFieldError(String fieldName, String errorMessage) {
            super.addFieldError(getFieldNameWithField(fieldName), message + errorMessage);
        }
    }
}
