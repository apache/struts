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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ParameterTest {

    private static final String PARAM_NAME = "param";

    @DataProvider(name = "paramValues")
    Object[][] paramValues() {
        return new Object[][] {
            {null, new String[0]},
            {"input", new String[] {"input"}},
            {Integer.valueOf(5), new String[] {"5"}},
            {new String[] {"foo"}, new String[] {"foo"}},
            {new Object[] {null}, new String[] {null}},
        };
    }

    @Test(dataProvider = "paramValues")
    public void shouldConvertRequestValuesToStringArrays(Object input, String[] expected) {
        Parameter.Request request = new Parameter.Request(PARAM_NAME, input);

        String[] result = request.getMultipleValues();

        assertEquals(result, expected);
        assertNotSame(result, input);
    }
}
