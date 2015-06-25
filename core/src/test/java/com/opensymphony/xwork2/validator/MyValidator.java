package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * TODO lukaszlenart: write a JavaDoc
 */
public class MyValidator implements FieldValidator, ShortCircuitableValidator {

    private String message;
    private String fieldName;
    private String key;
    private String[] messageParameters;
    private ValidatorContext validatorContext;
    private String type;
    private ValueStack stack;
    private boolean shortcircuit;

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setDefaultMessage(String message) {
        this.message = message;
    }

    public String getDefaultMessage() {
        return message;
    }

    public String getMessage(Object object) {
        return "Message";
    }

    public void setMessageKey(String key) {
        this.key = key;
    }

    public String getMessageKey() {
        return key;
    }

    public void setMessageParameters(String[] messageParameters) {
        this.messageParameters = messageParameters;
    }

    public String[] getMessageParameters() {
        return messageParameters;
    }

    public void setValidatorContext(ValidatorContext validatorContext) {
        this.validatorContext = validatorContext;
    }

    public ValidatorContext getValidatorContext() {
        return validatorContext;
    }

    public void validate(Object object) throws ValidationException {
        // pass
    }

    public void setValidatorType(String type) {
        this.type = type;
    }

    public String getValidatorType() {
        return type;
    }

    public void setValueStack(ValueStack stack) {
        this.stack = stack;
    }

    public void setShortCircuit(boolean shortcircuit) {
        this.shortcircuit = shortcircuit;
    }

    public boolean isShortCircuit() {
        return shortcircuit;
    }
}
