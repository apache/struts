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

package com.opensymphony.xwork2.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <!-- START SNIPPET: description -->
 * <p/>Sets the CreateIfNull for type conversion.
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Annotation usage:</u>
 *
 * <!-- START SNIPPET: usage -->
 * <p/>The CreateIfNull annotation must be applied at field or method level.
 * <!-- END SNIPPET: usage -->
 * <p/> <u>Annotation parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 * <table>
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
 * <td>value</td>
 * <td>no</td>
 * <td>false</td>
 * <td>The CreateIfNull property value.</td>
 * </tr>
 * </tbody>
 * </table>
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Example code:</u>
 * <pre>
 * <!-- START SNIPPET: example -->
 * &#64;CreateIfNull( value = true )
 * private List<User> users;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface CreateIfNull {

    /**
     * The CreateIfNull value.
     * Defaults to <tt>true</tt>.
     */
    boolean value() default true;
}
