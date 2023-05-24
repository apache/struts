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
package org.apache.struts2.url;

import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StrutsQueryStringParserTest {

    private QueryStringParser parser;

    @Test
    public void testParseQuery() {
        Map<String, Object> result = parser.parse("aaa=aaaval&bbb=bbbval&ccc=&%3Ca%22%3E=%3Cval%3E", false);

        assertEquals("aaaval", result.get("aaa"));
        assertEquals("bbbval", result.get("bbb"));
        assertEquals("", result.get("ccc"));
        assertEquals("<val>", result.get("<a\">"));
    }

    @Test
    public void testParseQueryIntoArray() {
        Map<String, Object> result = parser.parse("a=1&a=2&a=3", true);

        Object actual = result.get("a");
        assertThat(actual).isInstanceOf(String[].class);
        assertThat(Arrays.asList(actual)).containsOnly("1", "2", "3");
    }

    @Test
    public void testParseEmptyQuery() {
        Map<String, Object> result = parser.parse("", false);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testParseNullQuery() {
        Map<String, Object> result = parser.parse(null, false);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testDecodeSpacesInQueryString() {
        Map<String, Object> queryParameters = parser.parse("name=value+with+space", false);

        assertTrue(queryParameters.containsKey("name"));
        assertEquals("value with space", queryParameters.get("name"));
    }

    @Test
    public void shouldProperlySplitParamsWithDoubleEqualSign() {
        Map<String, Object> queryParameters = parser.parse("id1=n123=&id2=n3456", false);

        assertTrue(queryParameters.containsKey("id1"));
        assertTrue(queryParameters.containsKey("id2"));
        assertEquals("n123=", queryParameters.get("id1"));
        assertEquals("n3456", queryParameters.get("id2"));
    }

    @Test
    public void shouldHandleParamWithNoValue1() {
        Map<String, Object> queryParameters = parser.parse("paramNoValue", false);

        assertTrue(queryParameters.containsKey("paramNoValue"));
        assertEquals("", queryParameters.get("paramNoValue"));
    }

    @Test
    public void shouldHandleParamWithNoValue2() {
        Map<String, Object> queryParameters = parser.parse("paramNoValue&param1=1234", false);

        assertTrue(queryParameters.containsKey("paramNoValue"));
        assertTrue(queryParameters.containsKey("param1"));
        assertEquals("1234", queryParameters.get("param1"));
    }

    @Before
    public void setUp() throws Exception {
        this.parser = new StrutsQueryStringParser(new StrutsUrlDecoder());
    }

}
