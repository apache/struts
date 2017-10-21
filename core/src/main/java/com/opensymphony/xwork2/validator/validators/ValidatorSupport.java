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
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of the Validator interface suitable for subclassing.
 *
 * @author Jason Carreira
 * @author tm_jee
 * @author Martin Gilday
 */
public abstract class ValidatorSupport implements Validator, ShortCircuitableValidator {

    private static final Logger LOG = LogManager.getLogger(ValidatorSupport.class);

    public static final String EMPTY_STRING = "";

    private ValidatorContext validatorContext;
    private boolean shortCircuit;
    private String type;
    private String[] messageParameters;

    protected String defaultMessage = "";
    protected String messageKey;
    protected ValueStack stack;
    protected TextProviderFactory textProviderFactory;

    @Inject
    public void setTextProviderFactory(TextProviderFactory textProviderFactory) {
        this.textProviderFactory = textProviderFactory;
    }

    public void setValueStack(ValueStack stack) {
        this.stack = stack;
    }

    public void setDefaultMessage(String message) {
        if (StringUtils.isNotEmpty(message)) {
            this.defaultMessage = message;
        }
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String getMessage(Object object) {
        String message;
        boolean pop = false;

        if (!stack.getRoot().contains(object)) {
            stack.push(object);
            pop = true;
        }

        stack.push(this);

        if (messageKey != null) {
            if ((defaultMessage == null) || ("".equals(defaultMessage.trim()))) {
                defaultMessage = messageKey;
            }
            if (validatorContext == null) {
                validatorContext = new DelegatingValidatorContext(object, textProviderFactory);
            }
            List<Object> parsedMessageParameters = null;
            if (messageParameters != null) {
                parsedMessageParameters = new ArrayList<>();
                for (String messageParameter : messageParameters) {
                    if (messageParameter != null) {
                        try {
                            Object val = stack.findValue(messageParameter);
                            parsedMessageParameters.add(val);
                        } catch (Exception e) {
                            // if there's an exception in parsing, we'll just treat the expression itself as the
                            // parameter
                            LOG.warn("exception while parsing message parameter [{}]", messageParameter, e);
                            parsedMessageParameters.add(messageParameter);
                        }
                    }
                }
            }

            message = validatorContext.getText(messageKey, defaultMessage, parsedMessageParameters);
        } else {
            message = defaultMessage;
        }

        if (StringUtils.isNotBlank(message))
            message = TextParseUtil.translateVariables(message, stack);

        stack.pop();

        if (pop) {
            stack.pop();
        }

        return message;
    }

    public void setMessageKey(String key) {
        messageKey = key;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String[] getMessageParameters() {
        return this.messageParameters;
    }

    public void setMessageParameters(String[] messageParameters) {
        this.messageParameters = messageParameters;
    }

    public void setShortCircuit(boolean shortcircuit) {
        shortCircuit = shortcircuit;
    }

    public boolean isShortCircuit() {
        return shortCircuit;
    }

    public void setValidatorContext(ValidatorContext validatorContext) {
        this.validatorContext = validatorContext;
    }

    public ValidatorContext getValidatorContext() {
        return validatorContext;
    }

    public void setValidatorType(String type) {
        this.type = type;
    }

    public String getValidatorType() {
        return type;
    }

    /**
     * Parse <code>expression</code> passed in against value stack.
     *
     * @param expression an OGNL expression
     * @param type type to return
     * @return Object
     */
    protected Object parse(String expression, Class type) {
        if (expression == null) {
            return null;
        }
        return TextParseUtil.translateVariables('$', expression, stack, type);
    }

    /**
     * Return the field value named <code>name</code> from <code>object</code>,
     * <code>object</code> should have the appropriate getter/setter.
     *
     * @param name name of the field
     * @param object to search field name on
     * @return Object as field value
     * @throws ValidationException in case of validation problems
     */
    protected Object getFieldValue(String name, Object object) throws ValidationException {

        boolean pop = false;

        if (!stack.getRoot().contains(object)) {
            stack.push(object);
            pop = true;
        }

        Object retVal = stack.findValue(name);

        if (pop) {
            stack.pop();
        }

        return retVal;
    }

    protected void addActionError(Object object) {
        validatorContext.addActionError(getMessage(object));
    }

    protected void addFieldError(String propertyName, Object object) {
        validatorContext.addFieldError(propertyName, getMessage(object));
    }

}
