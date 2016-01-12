package org.apache.struts2.showcase.validation;

import java.sql.Date;

import com.opensymphony.xwork2.validator.annotations.DateRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.EmailValidator;
import com.opensymphony.xwork2.validator.annotations.FieldExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.IntRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;
import com.opensymphony.xwork2.validator.annotations.UrlValidator;

/**
 * <!-- START SNIPPET: ajaxFormSubmit -->
 */
/**
 * Example Action that shows how forms can be validated and submitted via AJAX
 * only. Form-submit-and-page-reload functionality of browsers is not used for
 * this action.
 * <p>Some things to note:
 * <ul>
 *   <li>Depends on <code>json-plugin</code>.</li>
 *   <li>Requires <code>jsonValidationInterceptor</code> to be on stack.</li>
 *   <li>Uses result type <code>jsonActionRedirect</code>.</li>
 *   <li>Uses http parameters <code>struts.enableJSONValidation=true</code> and <code>struts.validateOnly=false</code>.</li>
 *   <li>Uses a customized theme to make sure html elements required as error containers are always present and easily selectable in JS.</li>
 *   <li>Uses some custom JS code depending on jQuery to issue AJAX request and to render errors in html.</li>
 *   <li>Shows visual feedback while waiting for AJAX response.</li>
 * </ul>
 * </p>
 *
 */
public class AjaxFormSubmitAction extends AbstractValidationActionSupport {

    private String requiredValidatorField = null;
    private String requiredStringValidatorField = null;
    private Integer integerValidatorField = null;
    private Date dateValidatorField = null;
    private String emailValidatorField = null;
    private String urlValidatorField = null;
    private String stringLengthValidatorField = null;
    private String regexValidatorField = null;
    private String fieldExpressionValidatorField = null;

    @Override
    public void validate() {
        if (hasFieldErrors()) {
            addActionError("Errors present!");
        }
    }

    public Date getDateValidatorField() {
        return dateValidatorField;
    }

    @DateRangeFieldValidator(
        min="01/01/1990", 
        max="01/01/2000", 
        message="must be a min 01-01-1990 max 01-01-2000 if supplied")
    public void setDateValidatorField(Date dateValidatorField) {
        this.dateValidatorField = dateValidatorField;
    }

    public String getEmailValidatorField() {
        return emailValidatorField;
    }

    @EmailValidator(message="must be a valid email if supplied")
    public void setEmailValidatorField(String emailValidatorField) {
        this.emailValidatorField = emailValidatorField;
    }

    public Integer getIntegerValidatorField() {
        return integerValidatorField;
    }

    @IntRangeFieldValidator(min="1", max="10", message="must be integer min 1 max 10 if supplied")
    public void setIntegerValidatorField(Integer integerValidatorField) {
        this.integerValidatorField = integerValidatorField;
    }

    public String getRegexValidatorField() {
        return regexValidatorField;
    }

    @RegexFieldValidator(
        regex="[^<>]+", 
        message="regexValidatorField must match a regexp (.*\\.txt) if specified")
    public void setRegexValidatorField(String regexValidatorField) {
        this.regexValidatorField = regexValidatorField;
    }

    public String getRequiredStringValidatorField() {
        return requiredStringValidatorField;
    }

    @RequiredStringValidator(trim=true, message="required and must be string")
    public void setRequiredStringValidatorField(String requiredStringValidatorField) {
        this.requiredStringValidatorField = requiredStringValidatorField;
    }

    public String getRequiredValidatorField() {
        return requiredValidatorField;
    }

    @RequiredFieldValidator(message="required")
    public void setRequiredValidatorField(String requiredValidatorField) {
        this.requiredValidatorField = requiredValidatorField;
    }

    public String getStringLengthValidatorField() {
        return stringLengthValidatorField;
    }

    @StringLengthFieldValidator(
        minLength="2", 
        maxLength="4", 
        trim=true, 
        message="must be a String of a specific greater than 1 less than 5 if specified")
    public void setStringLengthValidatorField(String stringLengthValidatorField) {
        this.stringLengthValidatorField = stringLengthValidatorField;
    }

    public String getFieldExpressionValidatorField() {
        return fieldExpressionValidatorField;
    }

	@FieldExpressionValidator(
        expression = "(fieldExpressionValidatorField == requiredValidatorField)", 
        message = "must be the same as the Required Validator Field if specified")
    public void setFieldExpressionValidatorField(
            String fieldExpressionValidatorField) {
        this.fieldExpressionValidatorField = fieldExpressionValidatorField;
    }

    public String getUrlValidatorField() {
        return urlValidatorField;
    }

    @UrlValidator(message="must be a valid url if supplied")
    public void setUrlValidatorField(String urlValidatorField) {
        this.urlValidatorField = urlValidatorField;
    }
}

/**
 * <!-- END SNIPPET: ajaxFormSubmit -->
 */


