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

package com.opensymphony.xwork2.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <!-- START SNIPPET: description -->
 * <p>If you want to use several annotations of the same type, these annotations must be nested within the @Validations() annotation.</p>
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Annotation usage:</u>
 *
 * <!-- START SNIPPET: usage -->
 * <p/>Used at METHOD level.
 * <!-- END SNIPPET: usage -->
 *
 * <p/> <u>Annotation parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 * <table class='confluenceTable'>
 *
 * <tr>
 * <th class='confluenceTh'> Parameter </th>
 * <th class='confluenceTh'> Required </th>
 * <th class='confluenceTh'> Notes </th>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> requiredFields </td>
 * <td class='confluenceTd'> no </td>
 *
 * <td class='confluenceTd'> Add list of RequiredFieldValidators  </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> customValidators </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'> Add list of CustomValidators </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> conversionErrorFields </td>
 *
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'> Add list of ConversionErrorFieldValidators </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> dateRangeFields </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'> Add list of DateRangeFieldValidators </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> emails </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'> Add list of EmailValidators </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> fieldExpressions </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'> Add list of FieldExpressionValidators </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> intRangeFields </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'> Add list of IntRangeFieldValidators </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> requiredStrings </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'> Add list of RequiredStringValidators </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> stringLengthFields </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'> Add list of StringLengthFieldValidators </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> urls </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'> Add list of UrlValidators </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> visitorFields </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'> Add list of VisitorFieldValidators </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> regexFields </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'> Add list of RegexFieldValidator </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> expressions </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'> Add list of ExpressionValidator </td>
 * </tr>
 * </table>
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &#64;Validations(
 *           requiredFields =
 *                   {&#64;RequiredFieldValidator(type = ValidatorType.SIMPLE, fieldName = "customfield", message = "You must enter a value for field.")},
 *           requiredStrings =
 *                   {&#64;RequiredStringValidator(type = ValidatorType.SIMPLE, fieldName = "stringisrequired", message = "You must enter a value for string.")},
 *           emails =
 *                   { &#64;EmailValidator(type = ValidatorType.SIMPLE, fieldName = "emailaddress", message = "You must enter a value for email.")},
 *           urls =
 *                   { &#64;UrlValidator(type = ValidatorType.SIMPLE, fieldName = "hreflocation", message = "You must enter a value for email.")},
 *           stringLengthFields =
 *                   {&#64;StringLengthFieldValidator(type = ValidatorType.SIMPLE, trim = true, minLength="10" , maxLength = "12", fieldName = "needstringlength", message = "You must enter a stringlength.")},
 *           intRangeFields =
 *                   { &#64;IntRangeFieldValidator(type = ValidatorType.SIMPLE, fieldName = "intfield", min = "6", max = "10", message = "bar must be between ${min} and ${max}, current value is ${bar}.")},
 *           shortRangeFields =
 *                   { &#64;ShortRangeFieldValidator(type = ValidatorType.SIMPLE, fieldName = "shortfield", min = "1", max = "128", message = "bar must be between ${min} and ${max}, current value is ${bar}.")},
 *           dateRangeFields =
 *                   {&#64;DateRangeFieldValidator(type = ValidatorType.SIMPLE, fieldName = "datefield", min = "-1", max = "99", message = "bar must be between ${min} and ${max}, current value is ${bar}.")},
 *           expressions = {
 *               &#64;ExpressionValidator(expression = "foo &gt; 1", message = "Foo must be greater than Bar 1. Foo = ${foo}, Bar = ${bar}."),
 *               &#64;ExpressionValidator(expression = "foo &gt; 2", message = "Foo must be greater than Bar 2. Foo = ${foo}, Bar = ${bar}."),
 *               &#64;ExpressionValidator(expression = "foo &gt; 3", message = "Foo must be greater than Bar 3. Foo = ${foo}, Bar = ${bar}."),
 *               &#64;ExpressionValidator(expression = "foo &gt; 4", message = "Foo must be greater than Bar 4. Foo = ${foo}, Bar = ${bar}."),
 *               &#64;ExpressionValidator(expression = "foo &gt; 5", message = "Foo must be greater than Bar 5. Foo = ${foo}, Bar = ${bar}.")
 *   }
 *   )
 *   public String execute() throws Exception {
 *       return SUCCESS;
 *   }
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author jepjep
 * @author Rainer Hermanns
 * @version $Id$
 */
@Target( { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Validations {

    /**
     * Custom Validation rules.
     */
    public CustomValidator[] customValidators() default {};

    public ConversionErrorFieldValidator[] conversionErrorFields() default {};

    public DateRangeFieldValidator[] dateRangeFields() default {};

    public EmailValidator[] emails() default {};

    public FieldExpressionValidator[] fieldExpressions() default {};

    public IntRangeFieldValidator[] intRangeFields() default {};

    public RequiredFieldValidator[] requiredFields() default {};

    public RequiredStringValidator[] requiredStrings() default {};

    public StringLengthFieldValidator[] stringLengthFields() default {};

    public UrlValidator[] urls() default {};
    
    public ConditionalVisitorFieldValidator[] conditionalVisitorFields() default {};

    public VisitorFieldValidator[] visitorFields() default {};

    public RegexFieldValidator[] regexFields() default {};

    public ExpressionValidator[] expressions() default {};
}
