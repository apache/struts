package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.annotations.ConditionalVisitorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;

/**
 * Sets up all available validation annotations
 */
public class AnnotationValidationAction extends ActionSupport {

    @RegexFieldValidator(regex = "foo", message = "Foo doesn't match!", key = "regex.key",
            fieldName = "bar", shortCircuit = true, trim = false, caseSensitive = false,
            messageParams = {"one", "two", "three"})
    @ConditionalVisitorFieldValidator(expression = "foo+bar", context = "some", appendPrefix = false, fieldName = "bar",
            key = "conditional.key", message = "Foo doesn't match!", shortCircuit = true,
            messageParams = {"one", "two", "three"})
    @ConversionErrorFieldValidator(fieldName = "bar", key = "conversion.key", message = "Foo conversion error!",
            shortCircuit = true, repopulateField = true, messageParams = {"one", "three"})
    @CustomValidator(type = "myValidator", fieldName = "foo", key = "foo.invalid", message = "Foo is invalid!",
            shortCircuit = true, messageParams = {"one", "two", "three"},
            parameters = {
                    @ValidationParameter(name = "value", value = "1")
            }
    )
    public String execute() {
        return SUCCESS;
    }

}
