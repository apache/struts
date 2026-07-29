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
 * Declares a method level {@link TypeConversion} with an explicit, unprefixed key. Before WW-3871
 * this registered a bare {@code bareList} mapping that nothing ever read.
 */
public class ExplicitKeyConversionAction {

    private List bareList = new ArrayList();

    public List getBareList() {
        return bareList;
    }

    @TypeConversion(key = "bareList", rule = ConversionRule.CREATE_IF_NULL, value = "true")
    public void setBareList(List bareList) {
        this.bareList = bareList;
    }
}
