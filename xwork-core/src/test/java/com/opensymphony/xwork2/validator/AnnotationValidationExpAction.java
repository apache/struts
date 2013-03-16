package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.annotations.ConditionalVisitorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;

/**
 * Sets up all available validation annotations with params as expressions
 */
public class AnnotationValidationExpAction extends ActionSupport {

    @RegexFieldValidator(regexExpression = "${foo}", message = "Foo doesn't match!", key = "regex.key",
            fieldName = "bar", shortCircuit = true, trimExpression = "${trim}", caseSensitiveExpression = "${caseSensitive}",
            messageParams = {"one", "two", "three"})
    @ConditionalVisitorFieldValidator(expression = "foo+bar", context = "some", appendPrefix = false, fieldName = "bar",
            key = "conditional.key", message = "Foo doesn't match!", shortCircuit = true,
            messageParams = {"one", "two", "three"})
    public String execute() {
        return SUCCESS;
    }

    public String getFoo() {
        return "foo";
    }

    public boolean getTrim() {
        return false;
    }

    public boolean getCaseSensitive() {
        return false;
    }

}
