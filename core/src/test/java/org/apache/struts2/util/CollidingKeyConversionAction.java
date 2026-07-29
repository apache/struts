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

import org.apache.struts2.conversion.annotations.Conversion;
import org.apache.struts2.conversion.annotations.ConversionRule;
import org.apache.struts2.conversion.annotations.TypeConversion;

import java.util.ArrayList;
import java.util.List;

/**
 * The first conversion entry collides with a key already supplied by
 * {@code CollidingKeyConversionAction-conversion.properties}; the second must still be registered.
 */
@Conversion(
        conversions = {
                @TypeConversion(key = "fromProperties", rule = ConversionRule.CREATE_IF_NULL, value = "false"),
                @TypeConversion(key = "afterTheCollision", rule = ConversionRule.CREATE_IF_NULL, value = "true")
        })
public class CollidingKeyConversionAction {

    private List afterTheCollision = new ArrayList();

    public List getAfterTheCollision() {
        return afterTheCollision;
    }

    public void setAfterTheCollision(List afterTheCollision) {
        this.afterTheCollision = afterTheCollision;
    }
}
