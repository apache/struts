/*
 * $Id$
 *
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
package org.apache.struts2.convention.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>
 * This annotation can be used to control the URL that maps to
 * a specific method in an Action class. By default, the method
 * that is invoked is the execute method of the action and the
 * URL is based on the package and class names. This annotation
 * allows developers to change the URL or invoke a different
 * method. This also allows developers to specify multiple URLs
 * that will be handled by a single class or a single method.
 * </p>
 * <p>
 * This can also be used via the {@link Actions} annotation
 * to associate multiple URLs with a single method.
 * </p>
 * <p>
 * Here's an example:
 * </p>
 *
 * <pre>
 * public class MyAction implements Action {
 *   {@code @Action("/foo/bar")}
 *   public String execute() {}
 * }
 * </pre>
 * <!-- END SNIPPET: javadoc -->
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {
    String DEFAULT_VALUE = "DEFAULT_VALUE";

    /**
     * Allows actions to specify different URLs rather than the default that is based on the package
     * and action name. This also allows methods other than execute() to be invoked or multiple URLs
     * to map to a single class or a single method to handle multiple URLs.
     *
     * @return  The action URL.
     */
    String value() default DEFAULT_VALUE;

    /**
     * Allows action methods to specifically control the results for specific return values. These
     * results are not used for other method/URL invocations on the action. These are only used for
     * the URL that this action is associated with.
     *
     * @return  The results for the action.
     */
    Result[] results() default {};

    /**
     * Allows action methods to specify what interceptors must be applied to it.
     * @return Interceptors to be applied to the action
     */
    InterceptorRef[] interceptorRefs() default {};

    /**
     * @return  The parameters passed to the action. This is a list of strings that form a name/value
     *          pair chain since creating a Map for annotations is not possible. An example would be:
     *          <code>{"key", "value", "key2", "value2"}</code>.
     */
    String[] params() default {};

    /**
     * @return Maps return codes to exceptions. The "exceptions" interceptor must be applied to the action.
     */
    ExceptionMapping[] exceptionMappings() default {};

    /**
     * Allows actions to specify different class name.
     * 
     * @return The class name for the action.
     */
    String className() default DEFAULT_VALUE;
}