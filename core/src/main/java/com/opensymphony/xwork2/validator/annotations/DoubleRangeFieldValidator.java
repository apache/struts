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
 * This validator checks that a double field has a value within a specified range.
 * If neither min nor max is set, nothing will be done.
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Annotation usage:</u>
 *
 * <!-- START SNIPPET: usage -->
 * <p/>The annotation must be applied at method level.
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
 * <td class='confluenceTd'> minInclusive </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'> Double property.  The inclusive minimum the number must be. </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>minInclusiveExpression</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>OGNL expression used to obtain the inclusive minimum the number must be. </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> maxInclusive </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'> Double property.  The inclusive maximum number can be. </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> maxInclusiveExpression </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>OGNL expression used to obtain the inclusive maximum number can be. </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> minExclusive </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'> Double property.  The exclusive minimum the number must be. </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> minExclusiveExpression </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>OGNL expression used to obtain the exclusive minimum the number must be. </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> maxExclusive </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'> Double property.  The exclusive maximum number can be. </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> maxExclusiveExpression </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>OGNL expression used to obtain the exclusive maximum number can be. </td>
 * </tr>
 * </table>
 *
 * <p>If neither <em>min</em> nor <em>max</em> is set, nothing will be done.</p>
 *
 * <p>The values for min and max must be inserted as String values so that "0" can be handled as a possible value.</p>
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &#64;DoubleRangeFieldValidator(message = "Default message", key = "i18n.key", shortCircuit = true, minInclusive = "0.123", maxInclusive = "99.987")
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DoubleRangeFieldValidator {

    /**
     *  Double property. The inclusive minimum the number must be.
     */
    String minInclusive() default "";

    /**
     * The inclusive minimum the number must be defined as an expression
     */
    String minInclusiveExpression() default "";

    /**
     *  Double property. The inclusive minimum the number must be.
     */
    String maxInclusive() default "";

    /**
     *  The inclusive minimum the number must be defined as an expression
     */
    String maxInclusiveExpression() default "";

    /**
     *  Double property. The exclusive maximum number can be.
     */
    String minExclusive() default "";

    /**
     *  The exclusive maximum number can be defined as an expression
     */
    String minExclusiveExpression() default "";

    /**
     *  Double property. The exclusive maximum number can be.
     */
    String maxExclusive() default "";

    /**
     * The exclusive maximum number can be defined as an expression
     */
    String maxExclusiveExpression() default "";

    /**
     * The default error message for this validator.
     * NOTE: It is required to set a message, if you are not using the message key for 18n lookup!
     */
    String message() default "";

    /**
     * The message key to lookup for i18n.
     */
    String key() default "";

    /**
     * Additional params to be used to customize message - will be evaluated against the Value Stack
     */
    String[] messageParams() default {};

    /**
     * The optional fieldName for SIMPLE validator types.
     */
    String fieldName() default "";

    /**
     * If this is activated, the validator will be used as short-circuit.
     *
     * Adds the short-circuit="true" attribute value if <tt>true</tt>.
     *
     */
    boolean shortCircuit() default false;

    /**
     * The validation type for this field/method.
     */
    ValidatorType type() default ValidatorType.FIELD;
}
