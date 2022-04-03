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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.conversion.impl.ConversionData;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;

import java.io.Serializable;
import java.util.*;

/**
 * Provides a default implementation for the most common actions.
 * See the documentation for all the interfaces this class implements for more detailed information.
 */
public class ActionSupport implements Action, Validateable, ValidationAware, TextProvider, LocaleProvider, Serializable {

    private static final Logger LOG = LogManager.getLogger(ActionSupport.class);

    private final ValidationAwareSupport validationAware = new ValidationAwareSupport();

    private transient TextProvider textProvider;
    private transient LocaleProvider localeProvider;

    protected Container container;

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
    public boolean hasKey(String key) {
        return getTextProvider().hasKey(key);
    }

    @Override
    public String getText(String aTextName) {
        return getTextProvider().getText(aTextName);
    }

    @Override
    public String getText(String aTextName, String defaultValue) {
        return getTextProvider().getText(aTextName, defaultValue);
    }

    @Override
    public String getText(String aTextName, String defaultValue, String obj) {
        return getTextProvider().getText(aTextName, defaultValue, obj);
    }

    @Override
    public String getText(String aTextName, List<?> args) {
        return getTextProvider().getText(aTextName, args);
    }

    @Override
    public String getText(String key, String[] args) {
        return getTextProvider().getText(key, args);
    }

    @Override
    public String getText(String aTextName, String defaultValue, List<?> args) {
        return getTextProvider().getText(aTextName, defaultValue, args);
    }

    @Override
    public String getText(String key, String defaultValue, String[] args) {
        return getTextProvider().getText(key, defaultValue, args);
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        return getTextProvider().getText(key, defaultValue, args, stack);
    }

    @Override
    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        return getTextProvider().getText(key, defaultValue, args, stack);
    }

    /**
     * Dedicated method to support I10N and conversion errors
     *
     * @param key message which contains formatting string
     * @param expr that should be formatted
     * @return formatted expr with format specified by key
     */
    public String getFormatted(String key, String expr) {
        Map<String, ConversionData> conversionErrors = ActionContext.getContext().getConversionErrors();
        if (conversionErrors.containsKey(expr)) {
            String[] vals = (String[]) conversionErrors.get(expr).getValue();
            return vals[0];
        } else {
            final ValueStack valueStack = ActionContext.getContext().getValueStack();
            final Object val = valueStack.findValue(expr);
            return getText(key, Arrays.asList(val));
        }
    }

    @Override
    public ResourceBundle getTexts() {
        return getTextProvider().getTexts();
    }

    @Override
    public ResourceBundle getTexts(String aBundleName) {
        return getTextProvider().getTexts(aBundleName);
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

    public String input() throws Exception {
        return INPUT;
    }

    /**
     * A default implementation that does nothing an returns "success".
     *
     * <p>
     * Subclasses should override this method to provide their business logic.
     * </p>
     *
     * <p>
     * See also {@link com.opensymphony.xwork2.Action#execute()}.
     * </p>
     *
     * @return returns {@link #SUCCESS}
     * @throws Exception can be thrown by subclasses.
     */
    @Override
    public String execute() throws Exception {
        return SUCCESS;
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

    /**
     * Clears field errors. Useful for Continuations and other situations
     * where you might want to clear parts of the state on the same action.
     */
    public void clearFieldErrors() {
        validationAware.clearFieldErrors();
    }

    /**
     * Clears action errors. Useful for Continuations and other situations
     * where you might want to clear parts of the state on the same action.
     */
    public void clearActionErrors() {
        validationAware.clearActionErrors();
    }

    /**
     * Clears messages. Useful for Continuations and other situations
     * where you might want to clear parts of the state on the same action.
     */
    public void clearMessages() {
        validationAware.clearMessages();
    }

    /**
     * Clears all errors. Useful for Continuations and other situations
     * where you might want to clear parts of the state on the same action.
     */
    public void clearErrors() {
        validationAware.clearErrors();
    }

    /**
     * Clears all errors and messages. Useful for Continuations and other situations
     * where you might want to clear parts of the state on the same action.
     */
    public void clearErrorsAndMessages() {
        validationAware.clearErrorsAndMessages();
    }

    /**
     * A default implementation that validates nothing.
     * Subclasses should override this method to provide validations.
     */
    @Override
    public void validate() {
        // A default implementation that validates nothing
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * <!-- START SNIPPET: pause-method -->
     * Stops the action invocation immediately (by throwing a PauseException) and causes the action invocation to return
     * the specified result, such as {@link #SUCCESS}, {@link #INPUT}, etc.
     *
     * <p>
     * The next time this action is invoked (and using the same continuation ID), the method will resume immediately
     * after where this method was called, with the entire call stack in the execute method restored.
     * </p>
     *
     * <p>
     * Note: this method can <b>only</b> be called within the {@link #execute()} method.
     * </p>
     *
     * <!-- END SNIPPET: pause-method -->
     *
     * @param result the result to return - the same type of return value in the {@link #execute()} method.
     */
    public void pause(String result) {
    }

    /**
     * If called first time it will create {@link com.opensymphony.xwork2.TextProviderFactory},
     * inject dependency (if {@link com.opensymphony.xwork2.inject.Container} is accesible) into in,
     * then will create new {@link com.opensymphony.xwork2.TextProvider} and store it in a field
     * for future references and at the returns reference to that field
     *
     * @return reference to field with TextProvider
     */
    protected TextProvider getTextProvider() {
        if (textProvider == null) {
            final TextProviderFactory tpf = getContainer().getInstance(TextProviderFactory.class);
            textProvider = tpf.createInstance(getClass());
        }
        return textProvider;
    }

    protected LocaleProvider getLocaleProvider() {
        if (localeProvider == null) {
            final LocaleProviderFactory localeProviderFactory = getContainer().getInstance(LocaleProviderFactory.class);
            localeProvider = localeProviderFactory.createLocaleProvider();
        }
        return localeProvider;
    }

    /**
     * TODO: This a temporary solution, maybe we should consider stop injecting container into beans
     */
    protected Container getContainer() {
        if (container == null) {
            container = ActionContext.getContext().getContainer();
            if (container != null) {
                boolean devMode = Boolean.parseBoolean(container.getInstance(String.class, StrutsConstants.STRUTS_DEVMODE));
                if (devMode) {
                    LOG.warn("Container is null, action was created manually? Fallback to ActionContext");
                } else {
                    LOG.debug("Container is null, action was created manually? Fallback to ActionContext");
                }
            } else {
                LOG.warn("Container is null, action was created out of ActionContext scope?!?");
            }
        }
        return container;
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

}
