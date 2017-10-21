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
package org.apache.struts2.json;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: mcucchiara
 * Date: 10/11/11
 * Time: 17.26
 */
public class JSONReaderTest {
    private JSONReader reader = new JSONReader();

    @Test
    public void testExponentialNumber() throws Exception {
        Object ret = reader.read("5e-5");
        assertNotNull(ret);
        assertEquals(Double.class, ret.getClass());
        assertEquals(5.0E-5, ret);
    }

    @Test
    public void testExponentialNumber2() throws Exception {
        Object ret = reader.read("123.4e10");
        assertNotNull(ret);
        assertEquals(Double.class, ret.getClass());
        assertEquals(123.4e10, ret);
    }

    @Test
    public void testDecimalNumber() throws Exception {
        Object ret = reader.read("3.2");
        assertNotNull(ret);
        assertEquals(Double.class, ret.getClass());
        assertEquals(3.2, ret);
    }

    @Test
    public void testNaturalNumber() throws Exception {
        Object ret = reader.read("123");
        assertNotNull(ret);
        assertEquals(Long.class, ret.getClass());
        assertEquals(123L, ret);
    }
}
