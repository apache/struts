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
package org.apache.struts.beanvalidation.constraints;

import javax.validation.groups.Default;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation with Group Constrains on execution method (Action)
 * <p>
 * Example, Validate with on special group
 * </p>
 * <pre>
 *
 *  {@literal @}ValidationGroup(CarChecks.class)
 *  {@literal @}Action...
 * </pre>
 * <p>
 * <p>
 * Example, Validate with severals special group
 * </p>
 * <pre>
 *  {@literal @}ValidationGroup(Default.class, CarChecks.class, DriverChecks.class)
 *  {@literal @}Action...
 * </pre>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidationGroup {

    Class<?>[] value() default {Default.class};
}