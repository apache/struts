/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.util.WildcardHelper;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

public class NamespaceMatcherTest extends TestCase {

    public void testMatch() {
        Set<String> names = new HashSet<String>();
        names.add("/bar");
        names.add("/foo/*/bar");
        names.add("/foo/*");
        names.add("/foo/*/jim/*");
        NamespaceMatcher matcher = new NamespaceMatcher(new WildcardHelper(), names);
        assertEquals(3, matcher.compiledPatterns.size());

        assertNull(matcher.match("/asd"));
        assertEquals("/foo/*", matcher.match("/foo/23").getPattern());
        assertEquals("/foo/*/bar", matcher.match("/foo/23/bar").getPattern());
        assertEquals("/foo/*/jim/*", matcher.match("/foo/23/jim/42").getPattern());
        assertNull(matcher.match("/foo/23/asd"));
    }
}
