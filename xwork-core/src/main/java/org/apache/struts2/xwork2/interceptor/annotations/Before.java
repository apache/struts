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
package com.opensymphony.xwork2.interceptor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <!-- START SNIPPET: description -->
 * Marks a action method that needs to be executed before the main action method.
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Annotation usage:</u>
 *
 * <!-- START SNIPPET: usage -->
 * The Before annotation can be applied at method level.
 *
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
 * <td class='confluenceTd'>priority</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'>10</td>
 * <td class='confluenceTd'>Priority order of method execution</td>
 * </tr>
 * </table>
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * public class SampleAction extends ActionSupport {
 *
 *  &#64;Before
 *  public void isAuthorized() throws AuthenticationException {
 *    // authorize request, throw exception if failed
 *  }
 *
 *  public String execute() {
 *     // perform secure action
 *     return SUCCESS;
 *  }
 * }
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Zsolt Szasz, zsolt at lorecraft dot com
 * @author Rainer Hermanns
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Before {
    int priority() default 10; 
}
