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
package com.opensymphony.xwork2.config.impl;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

public class AbstractMatcherTest extends TestCase {
    @SuppressWarnings({"serial", "rawtypes"})
    private class AbstractMatcherImpl extends AbstractMatcher {
        @SuppressWarnings({"unchecked"})
        public AbstractMatcherImpl() {
            super(null, false);
        }

        @Override
        protected Object convert(String path, Object orig, Map vars) {
            return null;
        }
    }

    public void testConvertParam() {
        AbstractMatcher<?> matcher = new AbstractMatcherImpl();
        Map<String, String> replacements = new HashMap<>();
        replacements.put("x", "something");
        replacements.put("y", "else");

        assertEquals("should return the original input", "blablablabla",
            matcher.convertParam("blablablabla", replacements));
        assertEquals("should replace x", "blasomethingblablabla",
            matcher.convertParam("bla{x}blablabla", replacements));
        assertEquals("should replace unknown values with empty string", "blablablabla",
            matcher.convertParam("bla{z}blablabla", replacements));
        assertEquals("should replace all occurrences, no mapping", "blasomethingblasomethingblabla",
            matcher.convertParam("bla{x}bla{x}blabla", replacements));
        assertEquals("should work for multiple different replacements", "blasomethingblaelseblabla",
            matcher.convertParam("bla{x}bla{y}bla{z}bla", replacements));
    }
}
