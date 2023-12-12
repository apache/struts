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
package org.apache.struts2.dispatcher;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HttpParametersTest {

    @Test
    public void shouldGetBeCaseInsensitive() {
        // given
        HttpParameters params = HttpParameters.create(new HashMap<String, Object>() {{
            put("param1", "value1");
        }}).build();

        // then
        assertEquals("value1", params.get("Param1").getValue());
        assertEquals("value1", params.get("paraM1").getValue());
        assertEquals("value1", params.get("pAraM1").getValue());
    }

    @Test
    public void shouldRemoveBeCaseInsensitive() {
        // given
        HttpParameters params = HttpParameters.create(new HashMap<String, Object>() {{
            put("param1", "value1");
        }}).build();

        // then
        assertFalse(params.remove("Param1").contains("param1"));
        assertNull(params.get("param1"));
    }

    @Test
    public void shouldAppendSameParamsIgnoringCase() {
        // given
        HttpParameters params = HttpParameters.create(new HashMap<String, Object>() {{
            put("param1", "value1");
        }}).build();

        // when
        assertEquals("value1", params.get("param1").getValue());

        params = params.appendAll(HttpParameters.create(new HashMap<String, String>() {{
            put("Param1", "Value1");
        }}).build());

        // then
        assertTrue(params.contains("param1"));
        assertTrue(params.contains("Param1"));

        assertEquals("Value1", params.get("param1").getValue());
        assertEquals("Value1", params.get("Param1").getValue());
    }

}