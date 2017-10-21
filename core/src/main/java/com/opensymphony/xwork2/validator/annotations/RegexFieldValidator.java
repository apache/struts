/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.opensymphony.xwork2.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <!-- START SNIPPET: description -->
 * Validates a string field using a regular expression.
 * <!-- END SNIPPET: description -->
 *
 * <p><u>Annotation usage:</u></p>
 *
 * <!-- START SNIPPET: usage -->
 * <p>The annotation must be applied at method level.</p>
 * <!-- END SNIPPET: usage -->
 *
 * <p><u>Annotation parameters:</u></p>
 *
 * <!-- START SNIPPET: parameters -->
 * <table class='confluenceTable' summary=''>
 * <tr>
 * <th class='confluenceTh'> Parameter </th>
 * <th class='confluenceTh'> Required </th>
 * <th class='confluenceTh'> Default </th>
 * <th class='confluenceTh'> Notes </th>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>message</td>
 * <td class='confluenceTd'>yes</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>field error message</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>key</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>i18n key from language specific properties file.</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>messageParams</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>Additional params to be used to customize message - will be evaluated against the Value Stack</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>fieldName</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>shortCircuit</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'>false</td>
 * <td class='confluenceTd'>If this validator should be used as shortCircuit.</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>type</td>
 * <td class='confluenceTd'>yes</td>
 * <td class='confluenceTd'>ValidatorType.FIELD</td>
 * <td class='confluenceTd'>Enum value from ValidatorType. Either FIELD or SIMPLE can be used here.</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>regex</td>
 * <td class='confluenceTd'>yes</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>The regex to validate the field value against.</td>
 * </tr>
 * </table>
 * <!-- END SNIPPET: parameters -->
 *
 * <p><u>Example code:</u></p>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &#64;RegexFieldValidator( key = "regex.field", regex = "yourregexp")
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RegexFieldValidator {

    /**
     * @return The default error message for this validator.
     * NOTE: It is required to set a message, if you are not using the message key for 18n lookup!
     */
    String message() default "";

    /**
     * @return Additional params to be used to customize message - will be evaluated against the Value Stack
     */
    String[] messageParams() default {};

    /**
     * @return The message key to lookup for i18n.
     */
    String key() default "";

    /**
     * @return The optional fieldName for SIMPLE validator types.
     */
    String fieldName() default "";

    /**
     * Regex used to evaluate field against it
     *
     * @return String regular expression
     */
    String regex() default "";

    /**
     * Defines regex as an expression which first will be evaluated against the Value Stack to get proper regex.
     * Thus allow to dynamically change regex base on user actions.
     *
     * @return String an expression which starts with '$' or '%'
     */
    String regexExpression() default "";

    /**
     * To trim or not the value, default true - trim
     *
     * @return boolean trim or not the value before validation
     */
    boolean trim() default true;

    /**
     * Allows specify trim as an expression which will be evaluated during validation
     *
     * @return String an expression which starts with '$' or '%'
     */
    String trimExpression() default "";

    /**
     * Match the value in case sensitive manner, default true
     *
     * @return boolean use case sensitive match or not
     */
    boolean caseSensitive() default true;

    /**
     * Allows specify caseSensitive as an expression which will be evaluated during validation
     *
     * @return boolean use case sensitive match or not
     */
    String caseSensitiveExpression() default "";

    /**
     * If this is activated, the validator will be used as short-circuit.
     *
     * Adds the short-circuit='true' attribute value if <tt>true</tt>.
     *
     * @return true if validator will be used as short-circuit. Default is false.
     */
    boolean shortCircuit() default false;

    /**
     * @return The validation type for this field/method.
     */
    ValidatorType type() default ValidatorType.FIELD;

}
