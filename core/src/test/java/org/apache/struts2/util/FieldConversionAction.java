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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exercises field level {@link TypeConversion}: {@code fieldOnlyList} is annotated on the field
 * alone, while {@code contestedMap} is annotated on both the field and its setter so the
 * class &gt; method &gt; field precedence can be asserted.
 */
public class FieldConversionAction {

    @TypeConversion(rule = ConversionRule.CREATE_IF_NULL, value = "true")
    private List fieldOnlyList = new ArrayList();

    @TypeConversion(rule = ConversionRule.KEY, converterClass = String.class)
    private Map contestedMap = new HashMap();

    public List getFieldOnlyList() {
        return fieldOnlyList;
    }

    public void setFieldOnlyList(List fieldOnlyList) {
        this.fieldOnlyList = fieldOnlyList;
    }

    public Map getContestedMap() {
        return contestedMap;
    }

    @TypeConversion(rule = ConversionRule.KEY, converterClass = Long.class)
    public void setContestedMap(Map contestedMap) {
        this.contestedMap = contestedMap;
    }
}
