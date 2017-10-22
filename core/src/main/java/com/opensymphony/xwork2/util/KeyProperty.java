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
package com.opensymphony.xwork2.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <!-- START SNIPPET: description -->
 * <p>Sets the KeyProperty for type conversion.</p>
 * <!-- END SNIPPET: description -->
 *
 * <p><u>Annotation usage:</u></p>
 *
 * <!-- START SNIPPET: usage -->
 * <p>The KeyProperty annotation must be applied at field or method level.</p>
 * <p>This annotation should be used with Generic types, if the key property of the key element needs to be specified.</p>
 * <!-- END SNIPPET: usage -->
 * <p><u>Annotation parameters:</u></p>
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
 * <td>value</td>
 * <td>no</td>
 * <td>id</td>
 * <td>The key property value.</td>
 * </tr>
 * </tbody>
 * </table>
 * <!-- END SNIPPET: parameters -->
 *
 * <p><u>Example code:</u></p>
 * <pre>
 * <!-- START SNIPPET: example -->
 * // The key property for User objects within the users collection is the <code>userName</code> attribute.
 * &#64;KeyProperty( value = "userName" )
 * protected List&lt;User&gt; users = null;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Patrick Lightbody
 * @author Rainer Hermanns
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface KeyProperty {

    /**
     * @return The KeyProperty value. Defaults to the <tt>id</tt> attribute.
     */
    String value() default "id";
}
