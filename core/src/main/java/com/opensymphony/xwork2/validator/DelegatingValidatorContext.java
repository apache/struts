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

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * A default implementation of the {@link ValidatorContext} interface.
 *
 * @author Jason Carreira
 * @author Rainer Hermanns
 */
public class DelegatingValidatorContext implements ValidatorContext {

    private LocaleProvider localeProvider;
    private TextProvider textProvider;
    private ValidationAware validationAware;

    /**
     * Creates a new validation context given a ValidationAware object, and a text and locale provider. These objects
     * are used internally to set errors and get and set error text.
     *
     * @param validationAware  a validation aware object
     * @param textProvider  a text provider
     * @param localeProvider a local provider
     */
    public DelegatingValidatorContext(ValidationAware validationAware, TextProvider textProvider,
                                      LocaleProvider localeProvider) {
        this.textProvider = textProvider;
        this.validationAware = validationAware;
        this.localeProvider = localeProvider;
    }

    /**
     * Creates a new validation context given an object - usually an Action. The internal objects
     * (validation aware instance and a locale and text provider) are created based on the given action.
     *
     * @param object the object to use for validation (usually an Action).
     */
    public DelegatingValidatorContext(Object object, TextProviderFactory textProviderFactory) {
        this.localeProvider = makeLocaleProvider(object);
        this.validationAware = makeValidationAware(object);
        this.textProvider = makeTextProvider(object, textProviderFactory);
    }

    /**
     * Create a new validation context given a Class definition. The locale provider, text provider and
     * the validation context are created based on the class.
     *
     * @param clazz the class to initialize the context with.
     *
     * @deprecated will be removed, do not use!
     */
    @Deprecated
    public DelegatingValidatorContext(Class clazz) {
        localeProvider = new ActionContextLocaleProvider();
        textProvider = new StrutsTextProviderFactory().createInstance(clazz);
        validationAware = new LoggingValidationAware(clazz);
    }

    public void setActionErrors(Collection<String> errorMessages) {
        validationAware.setActionErrors(errorMessages);
    }

    public Collection<String> getActionErrors() {
        return validationAware.getActionErrors();
    }

    public void setActionMessages(Collection<String> messages) {
        validationAware.setActionMessages(messages);
    }

    public Collection<String> getActionMessages() {
        return validationAware.getActionMessages();
    }

    public void setFieldErrors(Map<String, List<String>> errorMap) {
        validationAware.setFieldErrors(errorMap);
    }

    public Map<String, List<String>> getFieldErrors() {
        return validationAware.getFieldErrors();
    }

    public String getFullFieldName(String fieldName) {
        return fieldName;
    }

    public Locale getLocale() {
        return localeProvider.getLocale();
    }

    @Override
    public boolean isValidLocaleString(String localeStr) {
        return localeProvider.isValidLocaleString(localeStr);
    }

    @Override
    public boolean isValidLocale(Locale locale) {
        return localeProvider.isValidLocale(locale);
    }

    public boolean hasKey(String key) {
    	return textProvider.hasKey(key);
    }
    
    public String getText(String aTextName) {
        return textProvider.getText(aTextName);
    }

    public String getText(String aTextName, String defaultValue) {
        return textProvider.getText(aTextName, defaultValue);
    }

    public String getText(String aTextName, String defaultValue, String obj) {
        return textProvider.getText(aTextName, defaultValue, obj);
    }

    public String getText(String aTextName, List<?> args) {
        return textProvider.getText(aTextName, args);
    }

    public String getText(String key, String[] args) {
        return textProvider.getText(key, args);
    }

    public String getText(String aTextName, String defaultValue, List<?> args) {
        return textProvider.getText(aTextName, defaultValue, args);
    }

    public String getText(String key, String defaultValue, String[] args) {
        return textProvider.getText(key, defaultValue, args);
    }

    public ResourceBundle getTexts(String aBundleName) {
        return textProvider.getTexts(aBundleName);
    }

    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        return textProvider.getText(key, defaultValue, args, stack);
    }

    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        return textProvider.getText(key, defaultValue, args, stack);
    }

    public ResourceBundle getTexts() {
        return textProvider.getTexts();
    }

    public void addActionError(String anErrorMessage) {
        validationAware.addActionError(anErrorMessage);
    }

    public void addActionMessage(String aMessage) {
        validationAware.addActionMessage(aMessage);
    }

    public void addFieldError(String fieldName, String errorMessage) {
        validationAware.addFieldError(fieldName, errorMessage);
    }

    public boolean hasActionErrors() {
        return validationAware.hasActionErrors();
    }

    public boolean hasActionMessages() {
        return validationAware.hasActionMessages();
    }

    public boolean hasErrors() {
        return validationAware.hasErrors();
    }

    public boolean hasFieldErrors() {
        return validationAware.hasFieldErrors();
    }

    public TextProvider makeTextProvider(Object object, TextProviderFactory textProviderFactory) {
        // the object argument passed through here will most probably be an ActionSupport descendant which does
        // implements TextProvider.
        if (object != null && object instanceof DelegatingValidatorContext) {
            return ((DelegatingValidatorContext) object).getTextProvider();
        }

        if ((object != null) && (object instanceof TextProvider)) {
            if (object instanceof CompositeTextProvider) {
                return (CompositeTextProvider) object;
            }
            return new CompositeTextProvider(new TextProvider[]{
                    ((TextProvider) object),
                    textProviderFactory.createInstance(object.getClass())
            });
        } else {
            return textProviderFactory.createInstance(
                    object != null ? object.getClass() : DelegatingValidatorContext.class);
        }
    }

    protected static LocaleProvider makeLocaleProvider(Object object) {
        if (object instanceof LocaleProvider) {
            return (LocaleProvider) object;
        } else {
            return new ActionContextLocaleProvider();
        }
    }

    protected static ValidationAware makeValidationAware(Object object) {
        if (object instanceof ValidationAware) {
            return (ValidationAware) object;
        } else {
            return new LoggingValidationAware(object);
        }
    }

    protected void setTextProvider(TextProvider textProvider) {
        this.textProvider = textProvider;
    }

    protected TextProvider getTextProvider() {
        return textProvider;
    }

    protected void setValidationAware(ValidationAware validationAware) {
        this.validationAware = validationAware;
    }

    protected ValidationAware getValidationAware() {
        return validationAware;
    }

    /**
     * An implementation of LocaleProvider which gets the locale from the action context.
     */
    private static class ActionContextLocaleProvider implements LocaleProvider {

        private LocaleProvider localeProvider;

        private LocaleProvider getLocaleProvider() {
            if (localeProvider == null) {
                LocaleProviderFactory localeProviderFactory = ActionContext.getContext().getInstance(LocaleProviderFactory.class);
                localeProvider = localeProviderFactory.createLocaleProvider();
            }
            return localeProvider;
        }

        @Override
        public Locale getLocale() {
            return getLocaleProvider().getLocale();
        }

        @Override
        public boolean isValidLocaleString(String localeStr) {
            return getLocaleProvider().isValidLocaleString(localeStr);
        }

        @Override
        public boolean isValidLocale(Locale locale) {
            return getLocaleProvider().isValidLocale(locale);
        }
    }

    /**
     * An implementation of ValidationAware which logs errors and messages.
     */
    private static class LoggingValidationAware implements ValidationAware {

        private Logger log;

        public LoggingValidationAware(Class clazz) {
            log = LogManager.getLogger(clazz);
        }

        public LoggingValidationAware(Object obj) {
            log = LogManager.getLogger(obj.getClass());
        }

        public void setActionErrors(Collection<String> errorMessages) {
            for (Object errorMessage : errorMessages) {
                String s = (String) errorMessage;
                addActionError(s);
            }
        }

        public Collection<String> getActionErrors() {
            return null;
        }

        public void setActionMessages(Collection<String> messages) {
            for (Object message : messages) {
                String s = (String) message;
                addActionMessage(s);
            }
        }

        public Collection<String> getActionMessages() {
            return null;
        }

        public void setFieldErrors(Map<String, List<String>> errorMap) {
            for (Map.Entry<String, List<String>> entry : errorMap.entrySet()) {
                addFieldError(entry.getKey(), entry.getValue().toString());
            }
        }

        public Map<String, List<String>> getFieldErrors() {
            return null;
        }

        public void addActionError(String anErrorMessage) {
            log.error("Validation error: {}", anErrorMessage);
        }

        public void addActionMessage(String aMessage) {
            log.info("Validation Message: {}", aMessage);
        }

        public void addFieldError(String fieldName, String errorMessage) {
            log.error("Validation error for {}:{}", fieldName, errorMessage);
        }

        public boolean hasActionErrors() {
            return false;
        }

        public boolean hasActionMessages() {
            return false;
        }

        public boolean hasErrors() {
            return false;
        }

        public boolean hasFieldErrors() {
            return false;
        }
    }
}
