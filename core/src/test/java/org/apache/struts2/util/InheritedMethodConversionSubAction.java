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
package org.apache.struts2.util;

import org.apache.struts2.conversion.annotations.ConversionRule;
import org.apache.struts2.conversion.annotations.TypeConversion;

import java.util.List;

/**
 * Extends {@link InheritedMethodConversionAction} without overriding its annotated setter, so {@code
 * XWorkConverter.buildConverterMapping}'s walk up the hierarchy - this class, then its parent, then
 * stopping at {@code Object} - visits the inherited annotated setter once per class it processes.
 *
 * <p>Declares its own {@code inheritedList} field, annotated with a contesting {@code
 * CREATE_IF_NULL} value, for the same property the inherited setter already claims. This is used to
 * assert that the inherited method annotation - registered at this subclass level, before this
 * class's own field pass ever runs - keeps winning over the field annotation declared here, per the
 * precedence documented on {@code XWorkConverter#processFieldAnnotations}.</p>
 */
public class InheritedMethodConversionSubAction extends InheritedMethodConversionAction {

    @TypeConversion(rule = ConversionRule.CREATE_IF_NULL, value = "false")
    private List inheritedList;
}
