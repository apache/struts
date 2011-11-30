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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * Provides a default implementation for the most common actions.
 * See the documentation for all the interfaces this class implements for more detailed information.
 */
public class ActionSupport implements Action, Validateable, ValidationAware, TextProvider, LocaleProvider, Serializable {

    protected static Logger LOG = LoggerFactory.getLogger(ActionSupport.class);

    private final ValidationAwareSupport validationAware = new ValidationAwareSupport();

    private transient TextProvider textProvider;
    private Container container;

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

    /**
     * @deprecated Use {@link #getActionErrors()}.
     */
    @Deprecated
    public Collection<String> getErrorMessages() {
        return getActionErrors();
    }

    /**
     * @deprecated Use {@link #getFieldErrors()}.
     */
    @Deprecated
    public Map<String, List<String>> getErrors() {
        return getFieldErrors();
    }

    public void setFieldErrors(Map<String, List<String>> errorMap) {
        validationAware.setFieldErrors(errorMap);
    }

    public Map<String, List<String>> getFieldErrors() {
        return validationAware.getFieldErrors();
    }

    public Locale getLocale() {
        ActionContext ctx = ActionContext.getContext();
        if (ctx != null) {
            return ctx.getLocale();
        } else {
            if (LOG.isDebugEnabled()) {
        	LOG.debug("Action context not initialized");
            }
            return null;
        }
    }

    public boolean hasKey(String key) {
        return getTextProvider().hasKey(key);
    }

    public String getText(String aTextName) {
        return getTextProvider().getText(aTextName);
    }

    public String getText(String aTextName, String defaultValue) {
        return getTextProvider().getText(aTextName, defaultValue);
    }

    public String getText(String aTextName, String defaultValue, String obj) {
        return getTextProvider().getText(aTextName, defaultValue, obj);
    }

    public String getText(String aTextName, List<?> args) {
        return getTextProvider().getText(aTextName, args);
    }

    public String getText(String key, String[] args) {
        return getTextProvider().getText(key, args);
    }

    public String getText(String aTextName, String defaultValue, List<?> args) {
        return getTextProvider().getText(aTextName, defaultValue, args);
    }

    public String getText(String key, String defaultValue, String[] args) {
        return getTextProvider().getText(key, defaultValue, args);
    }

    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        return getTextProvider().getText(key, defaultValue, args, stack);
    }

    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        return getTextProvider().getText(key, defaultValue, args, stack);
    }

    public ResourceBundle getTexts() {
        return getTextProvider().getTexts();
    }

    public ResourceBundle getTexts(String aBundleName) {
        return getTextProvider().getTexts(aBundleName);
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

    public String input() throws Exception {
        return INPUT;
    }

    public String doDefault() throws Exception {
        return SUCCESS;
    }

    /**
     * A default implementation that does nothing an returns "success".
     * <p/>
     * Subclasses should override this method to provide their business logic.
     * <p/>
     * See also {@link com.opensymphony.xwork2.Action#execute()}.
     *
     * @return returns {@link #SUCCESS}
     * @throws Exception can be thrown by subclasses.
     */
    public String execute() throws Exception {
        return SUCCESS;
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
    public void validate() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * <!-- START SNIPPET: pause-method -->
     * Stops the action invocation immediately (by throwing a PauseException) and causes the action invocation to return
     * the specified result, such as {@link #SUCCESS}, {@link #INPUT}, etc.
     * <p/>
     * <p/>
     * The next time this action is invoked (and using the same continuation ID), the method will resume immediately
     * after where this method was called, with the entire call stack in the execute method restored.
     * <p/>
     * <p/>
     * Note: this method can <b>only</b> be called within the {@link #execute()} method.
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
    private TextProvider getTextProvider() {
        if (textProvider == null) {
            TextProviderFactory tpf = new TextProviderFactory();
            if (container != null) {
                container.inject(tpf);
            }
            textProvider = tpf.createInstance(getClass(), this);
        }
        return textProvider;
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

}
