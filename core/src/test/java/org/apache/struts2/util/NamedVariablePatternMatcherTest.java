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

import org.apache.struts2.util.NamedVariablePatternMatcher.CompiledPattern;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class NamedVariablePatternMatcherTest {

    @Test
    public void testCompile() {
        NamedVariablePatternMatcher matcher = new NamedVariablePatternMatcher();

        assertNull(matcher.compilePattern(null));
        assertNull(matcher.compilePattern(""));

        CompiledPattern pattern = matcher.compilePattern("action.{format}");
        assertEquals("\\Qaction.\\E([^/]+)", pattern.getPattern().pattern());

        pattern = matcher.compilePattern("foo");
        assertEquals("\\Qfoo\\E", pattern.getPattern().pattern());

        pattern = matcher.compilePattern("foo{jim}");
        assertEquals("\\Qfoo\\E([^/]+)", pattern.getPattern().pattern());
        assertEquals("jim", pattern.getVariableNames().get(0));

        pattern = matcher.compilePattern("foo{jim}/{bob}");
        assertEquals("\\Qfoo\\E([^/]+)\\Q/\\E([^/]+)", pattern.getPattern().pattern());
        assertEquals("jim", pattern.getVariableNames().get(0));
        assertEquals("bob", pattern.getVariableNames().get(1));
        assertTrue(pattern.getPattern().matcher("foostar/jie").matches());
        assertFalse(pattern.getPattern().matcher("foo/star/jie").matches());

        pattern = matcher.compilePattern("{urlLocale}/eula_cz");
        assertEquals("([^/]+)\\Q/eula_cz\\E", pattern.getPattern().pattern());
        assertEquals("urlLocale", pattern.getVariableNames().get(0));
        assertTrue(pattern.getPattern().matcher("foostar/eula_cz").matches());
        assertFalse(pattern.getPattern().matcher("foo/star/eula_cz").matches());

        pattern = matcher.compilePattern("{test1}/path/{test2}");
        assertEquals("([^/]+)\\Q/path/\\E([^/]+)", pattern.getPattern().pattern());
        assertEquals("test1", pattern.getVariableNames().get(0));
        assertEquals("test2", pattern.getVariableNames().get(1));
        assertTrue(pattern.getPattern().matcher("test1/path/test2").matches());
        assertFalse(pattern.getPattern().matcher("test/1/path/test2").matches());

        pattern = matcher.compilePattern("path1/{test1}/path2/{test2}");
        assertEquals("\\Qpath1/\\E([^/]+)\\Q/path2/\\E([^/]+)", pattern.getPattern().pattern());
        assertEquals("test1", pattern.getVariableNames().get(0));
        assertEquals("test2", pattern.getVariableNames().get(1));
        assertTrue(pattern.getPattern().matcher("path1/test1/path2/test2").matches());
        assertFalse(pattern.getPattern().matcher("path1/test/1/path2/test2").matches());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompileWithMissingVariableName() {
        NamedVariablePatternMatcher matcher = new NamedVariablePatternMatcher();

        matcher.compilePattern("{}");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompileWithMissingOpeningBracket1() {
        NamedVariablePatternMatcher matcher = new NamedVariablePatternMatcher();

        matcher.compilePattern("}");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompileWithMissingOpeningBracket2() {
        NamedVariablePatternMatcher matcher = new NamedVariablePatternMatcher();

        matcher.compilePattern("test}");
    }

    @Test
    public void testMatch() {
        NamedVariablePatternMatcher matcher = new NamedVariablePatternMatcher();

        Map<String, String> vars = new HashMap<>();
        CompiledPattern pattern = new CompiledPattern(Pattern.compile("foo([^/]+)"), Arrays.asList("bar"));

        assertTrue(matcher.match(vars, "foobaz", pattern));
        assertEquals("baz", vars.get("bar"));
    }

    @Test
    public void testIsLiteral() {
        NamedVariablePatternMatcher matcher = new NamedVariablePatternMatcher();

        assertTrue(matcher.isLiteral("bob"));
        assertFalse(matcher.isLiteral("bob{jim}"));
    }
}
