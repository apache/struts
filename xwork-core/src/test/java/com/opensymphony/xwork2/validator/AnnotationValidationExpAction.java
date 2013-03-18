package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.annotations.ConditionalVisitorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.DateRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.DoubleRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.EmailValidator;
import com.opensymphony.xwork2.validator.annotations.ExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    @ConversionErrorFieldValidator(fieldName = "bar", key = "conversion.key", message = "Foo conversion error!",
            shortCircuit = true, repopulateField = true, messageParams = {"one", "three"})
    @CustomValidator(type = "myValidator", fieldName = "foo", key = "foo.invalid", message = "Foo is invalid!",
            shortCircuit = true, messageParams = {"one", "two", "three"},
            parameters = {
                    @ValidationParameter(name = "value", value = "1")
            }
    )
    @DateRangeFieldValidator(fieldName = "foo", key = "date.foo", maxExpression = "${dateMax}", minExpression = "${dateMin}", dateFormat = "yyyy",
            message = "Foo isn't in range!", shortCircuit = true, messageParams = {"one", "two", "three"})
    @DoubleRangeFieldValidator(minExclusiveExpression = "${doubleMinExclusiveExpression}", maxExclusiveExpression = "${doubleMaxExclusiveExpression}",
            minInclusiveExpression = "${doubleMinInclusiveExpression}", maxInclusiveExpression = "${doubleMaxInclusiveExpression}",
            fieldName = "foo", key = "double.key", message = "Foo is out of range!", shortCircuit = true,
            messageParams = {"one", "two", "three"})
    @EmailValidator(message = "Foo isn't a valid e-mail!", fieldName = "foo", key = "email.key",
            messageParams = {"one", "two", "three"}, shortCircuit = true)
    @ExpressionValidator(expression = "true", message = "Is not true!", key = "expression.key",
            messageParams = {"one", "two", "three"}, shortCircuit = true)
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

    public Date getDateMin() throws ParseException {
        return new SimpleDateFormat("yyyy").parse("2011");
    }

    public Date getDateMax() throws ParseException {
        return new SimpleDateFormat("yyyy").parse("2012");
    }

    public Double getDoubleMinExclusiveExpression() {
        return 1.2;
    }

    public Double getDoubleMaxExclusiveExpression() {
        return 1.4;
    }

    public Double getDoubleMinInclusiveExpression() {
        return 0.0;
    }

    public Double getDoubleMaxInclusiveExpression() {
        return 0.1;
    }

}
