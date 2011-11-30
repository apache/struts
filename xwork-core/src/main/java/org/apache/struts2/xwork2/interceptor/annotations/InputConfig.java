/*
 * Copyright 2002-2009 The Apache Software Foundation.
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

import com.opensymphony.xwork2.Action;

/**
 * <!-- START SNIPPET: description -->
 * Marks a action method that if it's not validated by ValidationInterceptor then execute input method or input result.
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Annotation usage:</u>
 *
 * <!-- START SNIPPET: usage -->
 * The InputConfig annotation can be applied at method level.
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
 * <td class='confluenceTd'>methodName</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'></td>
 * <td class='confluenceTd'>execute this method if specific</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>resultName</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'></td>
 * <td class='confluenceTd'>return this result if methodName not specific</td>
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
 *  public void isValid() throws ValidationException {
 *    // validate model object, throw exception if failed
 *  }
 *
 *  &#64;InputConfig(methodName="input")
 *  public String execute() {
 *     // perform action
 *     return SUCCESS;
 *  }
 *  public String input() {
 *     // perform some data filling
 *     return INPUT;
 *  }
 * }
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author zhouyanming, zhouyanming@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface InputConfig {
    String methodName() default "";
    String resultName() default Action.INPUT;
}
