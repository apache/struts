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

import org.apache.struts2.conversion.annotations.ConversionType;
import org.apache.struts2.conversion.annotations.TypeConversion;

/**
 * An {@code APPLICATION}-scoped {@link TypeConversion} is stored in the global default converter
 * map, which is keyed by class name. With no explicit {@code key}, a method or field pass would
 * otherwise derive a member name (e.g. {@code applicationScopedMethod}) and register it there,
 * where nothing can ever look it up. Both the method-level and field-level annotation below must
 * be skipped rather than registered under their derived member names.
 */
public class ApplicationScopedWithoutKeyConversionAction {

    @TypeConversion(type = ConversionType.APPLICATION)
    private String applicationScopedField;

    private String applicationScopedMethod;

    public String getApplicationScopedField() {
        return applicationScopedField;
    }

    public void setApplicationScopedField(String applicationScopedField) {
        this.applicationScopedField = applicationScopedField;
    }

    public String getApplicationScopedMethod() {
        return applicationScopedMethod;
    }

    @TypeConversion(type = ConversionType.APPLICATION)
    public void setApplicationScopedMethod(String applicationScopedMethod) {
        this.applicationScopedMethod = applicationScopedMethod;
    }
}
