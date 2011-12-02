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
 * This annotation is used to specify non-convention based results for
 * the Struts convention handling. This annotation is added to a class
 * and can be used to specify the result location for a specific result
 * code from an action method. Furthermore, this can also be used to
 * handle results only for specific action methods within an action class
 * (if there are multiple).
 * </p>
 *
 * <p>
 * When this annotation is used on an action class, it generates results
 * that are applicable to all of the actions URLs defined in the class.
 * These are considered global results for that class. Here is an example
 * of a global result:
 * </p>
 *
 * <pre>
 * {@code @Result(name="fail", location="failed.jsp")}
 * public class MyAction {
 * }
 * </pre>
 *
 * <p>
 * This annotation can also be used inside an {@link Action} annotation
 * on specific methods. This usage will define results for that specific
 * action URL. Here is an example of an action URL specific result:
 * </p>
 *
 * <pre>
 * {@code @Action(results={@Result(name="success", location="/", type="redirect")})}
 * public String execute() {
 * }
 * </pre>
 * <!-- END SNIPPET: javadoc -->
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Result {
    /**
     * @return  The name of the result mapping. This is the value that is returned from the action
     *          method and is used to associate a location with a return value.
     */
    String name() default com.opensymphony.xwork2.Action.SUCCESS;

    /**
     * @return  The location of the result within the web application or anywhere on disk. This location
     *          can be relative if the type of this result is one of the pre-defined relative result
     *          types (these default to dispatcher, velocity and freemarker). Or this location can be
     *          absolute relative to the root of the web application or the classpath (since velocity
     *          and freemarker templates can be loaded via the classpath).
     */
    String location() default "";

    /**
     * @return  The type of the result. This is usually setup in the struts.xml or struts-plugin.xml
     *          and is a simple name that is mapped to a result Class.
     */
    String type() default "";

    /**
     * @return  The parameters passed to the result. This is a list of strings that form a name/value
     *          pair chain since creating a Map for annotations is not possible. An example would be:
     *          <code>{"key", "value", "key2", "value2"}</code>.
     */
    String[] params() default {};
}