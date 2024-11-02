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
package org.apache.struts2.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ActionContext;
import org.apache.struts2.text.CompositeTextProvider;
import org.apache.struts2.locale.LocaleProvider;
import org.apache.struts2.locale.LocaleProviderFactory;
import org.apache.struts2.text.TextProvider;
import org.apache.struts2.text.TextProviderFactory;
import org.apache.struts2.interceptor.ValidationAware;
import org.apache.struts2.util.ValueStack;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

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

    @Override
    public void setActionErrors(Collection<String> errorMessages) {
        validationAware.setActionErrors(errorMessages);
    }

    @Override
    public Collection<String> getActionErrors() {
        return validationAware.getActionErrors();
    }

    @Override
    public void setActionMessages(Collection<String> messages) {
        validationAware.setActionMessages(messages);
    }

    @Override
    public Collection<String> getActionMessages() {
        return validationAware.getActionMessages();
    }

    @Override
    public void setFieldErrors(Map<String, List<String>> errorMap) {
        validationAware.setFieldErrors(errorMap);
    }

    @Override
    public Map<String, List<String>> getFieldErrors() {
        return validationAware.getFieldErrors();
    }

    @Override
    public String getFullFieldName(String fieldName) {
        return fieldName;
    }

    @Override
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

    @Override
    public Locale toLocale(String localeStr) {
        return localeProvider.toLocale(localeStr);
    }

    @Override
    public boolean hasKey(String key) {
    	return textProvider.hasKey(key);
    }

    @Override
    public String getText(String aTextName) {
        return textProvider.getText(aTextName);
    }

    @Override
    public String getText(String aTextName, String defaultValue) {
        return textProvider.getText(aTextName, defaultValue);
    }

    @Override
    public String getText(String aTextName, String defaultValue, String obj) {
        return textProvider.getText(aTextName, defaultValue, obj);
    }

    @Override
    public String getText(String aTextName, List<?> args) {
        return textProvider.getText(aTextName, args);
    }

    @Override
    public String getText(String key, String[] args) {
        return textProvider.getText(key, args);
    }

    @Override
    public String getText(String aTextName, String defaultValue, List<?> args) {
        return textProvider.getText(aTextName, defaultValue, args);
    }

    @Override
    public String getText(String key, String defaultValue, String[] args) {
        return textProvider.getText(key, defaultValue, args);
    }

    @Override
    public ResourceBundle getTexts(String aBundleName) {
        return textProvider.getTexts(aBundleName);
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        return textProvider.getText(key, defaultValue, args, stack);
    }

    @Override
    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        return textProvider.getText(key, defaultValue, args, stack);
    }

    @Override
    public ResourceBundle getTexts() {
        return textProvider.getTexts();
    }

    @Override
    public void addActionError(String anErrorMessage) {
        validationAware.addActionError(anErrorMessage);
    }

    @Override
    public void addActionMessage(String aMessage) {
        validationAware.addActionMessage(aMessage);
    }

    @Override
    public void addFieldError(String fieldName, String errorMessage) {
        validationAware.addFieldError(fieldName, errorMessage);
    }

    @Override
    public boolean hasActionErrors() {
        return validationAware.hasActionErrors();
    }

    @Override
    public boolean hasActionMessages() {
        return validationAware.hasActionMessages();
    }

    @Override
    public boolean hasErrors() {
        return validationAware.hasErrors();
    }

    @Override
    public boolean hasFieldErrors() {
        return validationAware.hasFieldErrors();
    }

    public TextProvider makeTextProvider(Object object, TextProviderFactory textProviderFactory) {
        // the object argument passed through here will most probably be an ActionSupport descendant which does
        // implements TextProvider.
        if (object instanceof DelegatingValidatorContext cast) {
            return cast.getTextProvider();
        }

        if (object instanceof TextProvider cast) {
            if (object instanceof CompositeTextProvider castAgain) {
                return castAgain;
            }
            return new CompositeTextProvider(new TextProvider[]{
                    cast,
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
        if (object instanceof ValidationAware validationAware) {
            return validationAware;
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

        @Override
        public Locale toLocale(String localeStr) {
            return getLocaleProvider().toLocale(localeStr);
        }
    }

    /**
     * An implementation of ValidationAware which logs errors and messages.
     */
    private static class LoggingValidationAware implements ValidationAware {

        private final Logger log;

        public LoggingValidationAware(Class clazz) {
            log = LogManager.getLogger(clazz);
        }

        public LoggingValidationAware(Object obj) {
            log = LogManager.getLogger(obj.getClass());
        }

        @Override
        public void setActionErrors(Collection<String> errorMessages) {
            for (String errorMessage : errorMessages) {
                addActionError(errorMessage);
            }
        }

        @Override
        public Collection<String> getActionErrors() {
            return null;
        }

        @Override
        public void setActionMessages(Collection<String> messages) {
            for (String message : messages) {
                addActionMessage(message);
            }
        }

        @Override
        public Collection<String> getActionMessages() {
            return null;
        }

        @Override
        public void setFieldErrors(Map<String, List<String>> errorMap) {
            for (Map.Entry<String, List<String>> entry : errorMap.entrySet()) {
                addFieldError(entry.getKey(), entry.getValue().toString());
            }
        }

        @Override
        public Map<String, List<String>> getFieldErrors() {
            return null;
        }

        @Override
        public void addActionError(String anErrorMessage) {
            log.error("Validation error: {}", anErrorMessage);
        }

        @Override
        public void addActionMessage(String aMessage) {
            log.info("Validation Message: {}", aMessage);
        }

        @Override
        public void addFieldError(String fieldName, String errorMessage) {
            log.error("Validation error for {}:{}", fieldName, errorMessage);
        }

        @Override
        public boolean hasActionErrors() {
            return false;
        }

        @Override
        public boolean hasActionMessages() {
            return false;
        }

        @Override
        public boolean hasErrors() {
            return false;
        }

        @Override
        public boolean hasFieldErrors() {
            return false;
        }
    }
}
