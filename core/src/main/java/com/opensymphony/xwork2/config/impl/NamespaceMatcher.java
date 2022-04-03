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

import com.opensymphony.xwork2.util.PatternMatcher;

import java.util.Map;
import java.util.Set;

/**
 * Matches namespace strings against a wildcard pattern matcher
 *
 * @since 2.1
 */
public class NamespaceMatcher extends AbstractMatcher<NamespaceMatch> {

    public NamespaceMatcher(PatternMatcher<?> patternMatcher, Set<String> namespaces) {
        this(patternMatcher, namespaces, true);
    }

    /**
     * Matches namespace strings against a wildcard pattern matcher
     *
     * @param patternMatcher pattern matcher
     * @param namespaces A set of namespaces to process
     * @param appendNamedParameters To append named parameters or not
     *
     * @since 2.5.23
     * See WW-5065
     */
    public NamespaceMatcher(PatternMatcher<?> patternMatcher, Set<String> namespaces, boolean appendNamedParameters) {
        super(patternMatcher, appendNamedParameters);
        for (String name : namespaces) {
            if (!patternMatcher.isLiteral(name)) {
                addPattern(name, new NamespaceMatch(name, null), false);
            }
        }
    }

    @Override
    protected NamespaceMatch convert(String path, NamespaceMatch orig, Map<String, String> vars) {
        /*Map<String,String> origVars = (Map<String,String>)vars;
        Map<String,String> map = new HashMap<String,String>();
        for (Map.Entry<String,String> entry : origVars.entrySet()) {
            if (entry.getKey().length() == 1) {
                map.put("ns"+entry.getKey(), entry.getValue());
            }
        }
        */
        return new NamespaceMatch(orig.getPattern(), vars);
    }
}
