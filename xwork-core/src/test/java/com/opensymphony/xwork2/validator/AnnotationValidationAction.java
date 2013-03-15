package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * Sets up all available validation annotations
 */
public class AnnotationValidationAction extends ActionSupport {

    @Validations(
            regexFields = {
                    @RegexFieldValidator(regex = "foo", message = "Foo doesn't match!", key = "regex.key",
                            fieldName = "bar", shortCircuit = true, trim = false, caseSensitive = false),
            }
    )
    public String execute() {
        return SUCCESS;
    }

}
