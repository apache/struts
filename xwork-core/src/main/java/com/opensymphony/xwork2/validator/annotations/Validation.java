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
 * This annotation has been deprecated since 2.1 as its previous purpose, to define classes that support annotation validations,
 * is no longer necessary.
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Annotation usage:</u>
 *
 * <!-- START SNIPPET: usage -->
 *  <p/>The Validation annotation must be applied at Type level.
 * <!-- END SNIPPET: usage -->
 *
 * <p/> <u>Annotation parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 * <table class='confluenceTable'>
 * <tr>
 * <th class='confluenceTh'> Parameter </th>
 * <th class='confluenceTh'> Required </th>
 * <th class='confluenceTh'> Default </th>
 * <th class='confluenceTh'> Notes </th>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>validations</td>
 * <td class='confluenceTd'>yes</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'></td>
 * </tr>
 * </table>
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Example code:</u>
 *
 * <u>An Annotated Interface</u>
 * <pre>
 * <!-- START SNIPPET: example -->
 * &#64;Validation()
 * public interface AnnotationDataAware {
 *
 *     void setBarObj(Bar b);
 *
 *     Bar getBarObj();
 *
 *     &#64;RequiredFieldValidator(message = "You must enter a value for data.")
 *     &#64;RequiredStringValidator(message = "You must enter a value for data.")
 *     void setData(String data);
 *
 *     String getData();
 * }
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * <p/> <u>Example code:</u>
 *
 * <u>An Annotated Class</u>
 * <pre>
 * <!-- START SNIPPET: example2 -->
 * &#64;Validation()
 * public class SimpleAnnotationAction extends ActionSupport {
 *
 *     &#64;RequiredFieldValidator(type = ValidatorType.FIELD, message = "You must enter a value for bar.")
 *     &#64;IntRangeFieldValidator(type = ValidatorType.FIELD, min = "6", max = "10", message = "bar must be between ${min} and ${max}, current value is ${bar}.")
 *     public void setBar(int bar) {
 *         this.bar = bar;
 *     }
 *
 *     public int getBar() {
 *         return bar;
 *     }
 *
 *     &#64;Validations(
 *             requiredFields =
 *                     {&#64;RequiredFieldValidator(type = ValidatorType.SIMPLE, fieldName = "customfield", message = "You must enter a value for field.")},
 *             requiredStrings =
 *                     {&#64;RequiredStringValidator(type = ValidatorType.SIMPLE, fieldName = "stringisrequired", message = "You must enter a value for string.")},
 *             emails =
 *                     { &#64;EmailValidator(type = ValidatorType.SIMPLE, fieldName = "emailaddress", message = "You must enter a value for email.")},
 *             urls =
 *                     { &#64;UrlValidator(type = ValidatorType.SIMPLE, fieldName = "hreflocation", message = "You must enter a value for email.")},
 *             stringLengthFields =
 *                     {&#64;StringLengthFieldValidator(type = ValidatorType.SIMPLE, trim = true, minLength="10" , maxLength = "12", fieldName = "needstringlength", message = "You must enter a stringlength.")},
 *             intRangeFields =
 *                     { @IntRangeFieldValidator(type = ValidatorType.SIMPLE, fieldName = "intfield", min = "6", max = "10", message = "bar must be between ${min} and ${max}, current value is ${bar}.")},
 *             dateRangeFields =
 *                     {&#64;DateRangeFieldValidator(type = ValidatorType.SIMPLE, fieldName = "datefield", min = "-1", max = "99", message = "bar must be between ${min} and ${max}, current value is ${bar}.")},
 *             expressions = {
 *                 &#64;ExpressionValidator(expression = "foo &gt; 1", message = "Foo must be greater than Bar 1. Foo = ${foo}, Bar = ${bar}."),
 *                 &#64;ExpressionValidator(expression = "foo &gt; 2", message = "Foo must be greater than Bar 2. Foo = ${foo}, Bar = ${bar}."),
 *                 &#64;ExpressionValidator(expression = "foo &gt; 3", message = "Foo must be greater than Bar 3. Foo = ${foo}, Bar = ${bar}."),
 *                 &#64;ExpressionValidator(expression = "foo &gt; 4", message = "Foo must be greater than Bar 4. Foo = ${foo}, Bar = ${bar}."),
 *                 &#64;ExpressionValidator(expression = "foo &gt; 5", message = "Foo must be greater than Bar 5. Foo = ${foo}, Bar = ${bar}.")
 *     }
 *     )
 *     public String execute() throws Exception {
 *         return SUCCESS;
 *     }
 * }
 *
 * <!-- END SNIPPET: example2 -->
 * </pre>
 *
 * @author Rainer Hermanns
 * @deprecated Since Struts 2.1 because it isn't necessary anymore
 * @version $Id$
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Validation {

    /**
     * Used for class or interface validation rules.
     */
    Validations[] validations() default {};
}
