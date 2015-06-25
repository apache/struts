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
 * This validator checks that a field is a valid URL.
 *
 * <pre>
 * &#64;UrlValidator(message = "Default message", key = "i18n.key", shortCircuit = true)
 * </pre>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UrlValidator {

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
     */
    boolean shortCircuit() default false;

    /**
     * The validation type for this field/method.
     */
    ValidatorType type() default ValidatorType.FIELD;

    /**
     * Defines regex to use to validate url
     */
    String urlRegex() default "";

    /**
     * Defines regex as an expression which will be evaluated to string and used to validate url
     */
    String urlRegexExpression() default "";

}
