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
package org.apache.struts2.conversion.annotations;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConversionRuleTest {

    // COLLECTION is deprecated but must keep deriving Collection_ for existing annotations
    @SuppressWarnings("deprecation")
    @Test
    public void prefixIsDefinedForEveryRule() {
        assertEquals("", ConversionRule.PROPERTY.prefix());
        assertEquals("", ConversionRule.MAP.prefix());
        assertEquals("Collection_", ConversionRule.COLLECTION.prefix());
        assertEquals("CreateIfNull_", ConversionRule.CREATE_IF_NULL.prefix());
        assertEquals("Element_", ConversionRule.ELEMENT.prefix());
        assertEquals("Key_", ConversionRule.KEY.prefix());
        assertEquals("KeyProperty_", ConversionRule.KEY_PROPERTY.prefix());
    }
}
