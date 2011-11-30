/*
 * Copyright 2010 Yahoo, Inc.  All rights reserved.
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

import java.util.regex.Pattern;

/**
 * Helper class to convert wildcard expression to regular expression
 */
public class WildcardUtil {

    /**
     * Convert wildcard pattern to Pattern
     * @param pattern String containing wildcard pattern
     * @return compiled regular expression as a Pattern
     */
    public static Pattern compileWildcardPattern(String pattern) {
        StringBuilder buf = new StringBuilder(pattern);

        for (int i=buf.length()-1; i>=0; i--)
        {
            char c = buf.charAt(i);
            if (c == '*' && (i == 0 || buf.charAt(i-1) != '\\'))
            {
                buf.insert(i+1, '?');
                buf.insert(i, '.');
            }
            else if (c == '*')
            {
                i--;	// skip backslash, too
            }
            else if (needsBackslashToBeLiteralInRegex(c))
            {
                buf.insert(i, '\\');
            }
        }

        return Pattern.compile(buf.toString());
    }

    /**
     * @param c character to test
     * @return true if the given character must be escaped to be a literal
     * inside a regular expression.
     */

    private static final String theSpecialRegexCharList = ".[]\\?*+{}|()^$";

    public static boolean needsBackslashToBeLiteralInRegex(
        char c)
    {
        return (theSpecialRegexCharList.indexOf(c) >= 0);
    }

}
