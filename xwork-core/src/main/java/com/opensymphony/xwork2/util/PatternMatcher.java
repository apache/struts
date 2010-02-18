/*
 * $Id$
 *
 * Copyright 2003-2004 The Apache Software Foundation.
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
package com.opensymphony.xwork2.util;

import java.util.Map;

/**
 * Compiles and matches a pattern against a value
 * 
 * @since 2.1
 */
public interface PatternMatcher<E extends Object> {

    /**
     * Determines if the pattern is a simple literal string or contains wildcards that will need to be processed
     * @param pattern The string pattern
     * @return True if the pattern doesn't contain processing elements, false otherwise
     */
    boolean isLiteral(String pattern);

    /**
     * <p> Translate the given <code>String</code> into an object
     * representing the pattern matchable by this class. 
     *
     * @param data The string to translate.
     * @return The encoded string 
     * @throws NullPointerException If data is null.
     */
    E compilePattern(String data);

    /**
     * Match a pattern against a string 
     *
     * @param map  The map to store matched values
     * @param data The string to match
     * @param expr The compiled wildcard expression
     * @return True if a match
     * @throws NullPointerException If any parameters are null
     */
    boolean match(Map<String,String> map, String data, E expr);
    
}