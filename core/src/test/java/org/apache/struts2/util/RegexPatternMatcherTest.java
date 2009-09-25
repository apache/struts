/*
 * $Id: ServletContextAware.java 651946 2008-04-27 13:41:38Z apetrelli $
 *
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import junit.framework.TestCase;


public class RegexPatternMatcherTest extends TestCase {
    private RegexPatternMatcher matcher = new RegexPatternMatcher();

    public void testIsLiteral() {
        assertTrue(matcher.isLiteral(null));
        assertTrue(matcher.isLiteral(""));
        assertTrue(matcher.isLiteral("    \t"));
        assertTrue(matcher.isLiteral("something"));

        assertFalse(matcher.isLiteral("{"));
    }

    public void testCompile0() {
        RegexPatternMatcherExpression expr = matcher.compilePattern("/some/{test}");
        assertNotNull(expr);

        //params
        Map<Integer, String> params = expr.getParams();
        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals("test", params.get(1));

        //pattern
        Pattern pattern = expr.getPattern();
        assertNotNull(pattern);
        assertEquals("/some/(.*?)", pattern.pattern());
    }

    public void testCompile1() {
        RegexPatternMatcherExpression expr = matcher.compilePattern("/{test}/some/{test1}/");
        assertNotNull(expr);

        //params
        Map<Integer, String> params = expr.getParams();
        assertNotNull(params);
        assertEquals(2, params.size());
        assertEquals("test", params.get(1));
        assertEquals("test1", params.get(2));

        //pattern
        Pattern pattern = expr.getPattern();
        assertNotNull(pattern);
        assertEquals("/(.*?)/some/(.*?)/", pattern.pattern());
    }
    
    public void testCompileNamedParams0() {
        RegexPatternMatcherExpression expr = matcher.compilePattern("/some/{test:.+}");
        assertNotNull(expr);

        //params
        Map<Integer, String> params = expr.getParams();
        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals("test", params.get(1));

        //pattern
        Pattern pattern = expr.getPattern();
        assertNotNull(pattern);
        assertEquals("/some/(.+)", pattern.pattern());
    }
    
    public void testCompileNamedParams1() {
        RegexPatternMatcherExpression expr = matcher.compilePattern("/some/{test1:.+}/{test2:.*}");
        assertNotNull(expr);

        //params
        Map<Integer, String> params = expr.getParams();
        assertNotNull(params);
        assertEquals(2, params.size());
        assertEquals("test1", params.get(1));
        assertEquals("test2", params.get(2));

        //pattern
        Pattern pattern = expr.getPattern();
        assertNotNull(pattern);
        assertEquals("/some/(.+)/(.*)", pattern.pattern());
    }
    
    public void testMatch0() {
        RegexPatternMatcherExpression expr = matcher.compilePattern("/some/{test}");
        
        Map<String, String> values = new HashMap<String, String>();
        
        assertTrue(matcher.match(values, "/some/val", expr));
        assertEquals(3, values.size());
        assertEquals("val", values.get("test"));
        assertEquals("val", values.get("1"));
        
        assertEquals("/some/val", values.get("0"));
    }
    
    public void testMatch1() {
        RegexPatternMatcherExpression expr = matcher.compilePattern("/some/{test0}/some/{test1}");
        
        Map<String, String> values = new HashMap<String, String>();
        
        assertTrue(matcher.match(values, "/some/val0/some/val1", expr));
        assertEquals(5, values.size());
        assertEquals("val0", values.get("test0"));
        assertEquals("val1", values.get("test1"));
        assertEquals("val0", values.get("1"));
        assertEquals("val1", values.get("2"));
        
        assertEquals("/some/val0/some/val1", values.get("0"));
    }
    
    public void testMatch2() {
        RegexPatternMatcherExpression expr = matcher.compilePattern("/some/{test0}/some/{test1}/.*");
        
        Map<String, String> values = new HashMap<String, String>();
        
        assertTrue(matcher.match(values, "/some/val0/some/val1/buaaa", expr));
        assertEquals(5, values.size());
        assertEquals("val0", values.get("test0"));
        assertEquals("val1", values.get("test1"));
        assertEquals("val0", values.get("1"));
        assertEquals("val1", values.get("2"));
        
        assertEquals("/some/val0/some/val1/buaaa", values.get("0"));
    }

    public void testCompileBad0() {
        try {
            matcher.compilePattern("/{test/some");
            fail("Should have failed");
        } catch (Exception e) {
            //ok
        }
    }
    
    public void testCompileBad1() {
        try {
            matcher.compilePattern("/test/{p:}");
            fail("Should have failed");
        } catch (Exception e) {
            //ok
        }
    }

}
