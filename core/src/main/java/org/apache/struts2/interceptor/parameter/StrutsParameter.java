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
package org.apache.struts2.interceptor.parameter;

import org.apache.struts2.action.Action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate public getter/setter methods or fields on {@link Action} classes that are
 * intended for parameter injection by the {@link ParametersInterceptor}.
 *
 * @since 6.4.0
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrutsParameter {

    /**
     * The depth to which parameter injection is permitted, where a depth of 0 only allows setters/fields directly on
     * the action class. Setting within a POJO on an action will require a depth of 1 or more depending on the level of
     * nesting within the POJO.
     * <p>
     * In a practical sense, the depth dictates the number of periods or brackets that can appear in the parameter name.
     */
    int depth() default 0;
}
