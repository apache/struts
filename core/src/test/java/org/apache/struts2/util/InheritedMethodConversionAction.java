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

import java.util.ArrayList;
import java.util.List;

/**
 * Declares a bare-key, method level {@link TypeConversion} on a setter that {@link
 * InheritedMethodConversionSubAction} inherits without overriding. {@code Class#getMethods()} on the
 * subclass still returns this method, so
 * {@link org.apache.struts2.conversion.impl.XWorkConverter#buildConverterMapping} visits it once per
 * class in the hierarchy, registering it at the subclass level before that subclass's own field pass
 * runs - used together with {@link InheritedMethodConversionSubAction}'s contesting field annotation
 * to assert that precedence, independent of how many times the (gated) WARN logging fires.
 */
public class InheritedMethodConversionAction {

    private List inheritedList = new ArrayList();

    public List getInheritedList() {
        return inheritedList;
    }

    @TypeConversion(rule = ConversionRule.CREATE_IF_NULL, value = "true")
    public void setInheritedList(List inheritedList) {
        this.inheritedList = inheritedList;
    }
}
