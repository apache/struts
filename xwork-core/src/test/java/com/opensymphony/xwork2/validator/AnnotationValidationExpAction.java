package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * Sets up all available validation annotations with params as expressions
 */
public class AnnotationValidationExpAction extends ActionSupport {

    @Validations(
            regexFields = {
                    @RegexFieldValidator(regexExpression = "${foo}", message = "Foo doesn't match!", key = "regex.key",
                            fieldName = "bar", shortCircuit = true, trimExpression = "${trim}", caseSensitiveExpression = "${caseSensitive}"),
            }
    )
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
