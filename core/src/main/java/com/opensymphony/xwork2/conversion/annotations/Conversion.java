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
package com.opensymphony.xwork2.conversion.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <!-- START SNIPPET: description -->
 * A marker annotation for type conversions at Type level.
 * <!-- END SNIPPET: description -->
 *
 * <p>
 *     <u>Annotation usage:</u>
 * </p>
 *
 * <!-- START SNIPPET: usage -->
 * The Conversion annotation must be applied at Type level.
 * <!-- END SNIPPET: usage -->
 *
 * <p> <u>Annotation parameters:</u> </p>
 *
 * <!-- START SNIPPET: parameters -->
 * <table summary="">
 * <thead>
 * <tr>
 * <th>Parameter</th>
 * <th>Required</th>
 * <th>Default</th>
 * <th>Description</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>conversion</td>
 * <td>no</td>
 * <td>&nbsp;</td>
 * <td>used for Type Conversions applied at Type level.</td>
 * </tr>
 * </tbody>
 * </table>
 * <!-- END SNIPPET: parameters -->
 *
 * <p> <u>Example code:</u> </p>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &#64;Conversion(
 *     conversions = {
 *          // key must be the name of a property for which converter should be used
 *          &#64;TypeConversion(key = "date", converter = "org.demo.converter.DateConverter")
 *     }
 * )
 * public class ConversionAction implements Action {
 *
 *     private Date date;
 *
 *     public setDate(Date date) {
 *         this.date = date;
 *     }
 *
 *     public Date getDate() {
 *         return date;
 *     }
 *
 * }
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Conversion {

    /**
     * Allow Type Conversions being applied at Type level.
     *
     * @return type conversion
     */
    TypeConversion[] conversions() default {};
}
