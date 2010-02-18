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

import java.util.Map;

/**
 * Represents a match from a namespace pattern matching.
 *
 * @Since 2.1
 */
public class NamespaceMatch {
    private String pattern;
    private Map<String,String> variables;

    public NamespaceMatch(String pattern, Map<String, String> variables) {
        this.pattern = pattern;
        this.variables = variables;
    }

    /**
     * @return The pattern that was matched
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * @return The variables containing the matched values
     */
    public Map<String, String> getVariables() {
        return variables;
    }
}
