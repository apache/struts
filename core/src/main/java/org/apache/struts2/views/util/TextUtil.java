/*
 * $Id$
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

package org.apache.struts2.views.util;


/**
 * This class handles HTML escaping of text.
 * It was written and optimized to be as fast as possible.
 *
 */
public class TextUtil {

    protected static final int MAX_LENGTH = 300;

    /**
     * We use arrays of char in the lookup table because it is faster
     * appending this to a StringBuilder than appending a String
     */
    protected static final char[][] _stringChars = new char[MAX_LENGTH][];

    static {
        // Initialize the mapping table
        initMapping();
    }


    /**
     * Call escapeHTML(s, false)
     */
    public static final String escapeHTML(String s) {
        return escapeHTML(s, false);
    }

    /**
     * Escape HTML.
     *
     * @param s           string to be escaped
     * @param escapeEmpty if true, then empty string will be escaped.
     */
    public static final String escapeHTML(String s, boolean escapeEmpty) {
        int len = s.length();

        if (len == 0) {
            return s;
        }

        if (!escapeEmpty) {
            String trimmed = s.trim();

            if ((trimmed.length() == 0) || ("\"\"").equals(trimmed)) {
                return s;
            }
        }

        int i = 0;

        // First loop through String and check if escaping is needed at all
        // No buffers are copied at this time
        do {
            int index = s.charAt(i);

            if (index >= MAX_LENGTH) {
                if (index != 0x20AC) { // If not euro symbol

                    continue;
                }

                break;
            } else if (_stringChars[index] != null) {
                break;
            }
        } while (++i < len);

        // If the check went to the end with no escaping then i should be == len now
        // otherwise we must continue escaping for real
        if (i == len) {
            return s;
        }

        // We found a character to escape and broke out at position i
        // Now copy all characters before that to StringBuilder sb
        // Since a char[] will be used for copying we might as well get
        // a complete copy of it so that we can use array indexing instead of charAt
        StringBuilder sb = new StringBuilder(len + 40);
        char[] chars = new char[len];

        // Copy all chars from the String s to the chars buffer
        s.getChars(0, len, chars, 0);

        // Append the first i characters that we have checked to the resulting StringBuilder
        sb.append(chars, 0, i);

        int last = i;
        char[] subst;

        for (; i < len; i++) {
            char c = chars[i];
            int index = c;

            if (index < MAX_LENGTH) {
                subst = _stringChars[index];

                // It is faster to append a char[] than a String which is why we use this
                if (subst != null) {
                    if (i > last) {
                        sb.append(chars, last, i - last);
                    }

                    sb.append(subst);
                    last = i + 1;
                }
            }
            // Check if it is the euro symbol. This could be changed to check in a second lookup
            // table in case one wants to convert more characters in that area
            else if (index == 0x20AC) {
                if (i > last) {
                    sb.append(chars, last, i - last);
                }

                sb.append("&euro;");
                last = i + 1;
            }
        }

        if (i > last) {
            sb.append(chars, last, i - last);
        }

        return sb.toString();
    }

    protected static void addMapping(int c, String txt, String[] strings) {
        strings[c] = txt;
    }

    protected static void initMapping() {
        String[] strings = new String[MAX_LENGTH];

        addMapping(0x22, "&quot;", strings); // "
        addMapping(0x26, "&amp;", strings); // &
        addMapping(0x3c, "&lt;", strings); // <
        addMapping(0x3e, "&gt;", strings); // >

        addMapping(0xa1, "&iexcl;", strings); //
        addMapping(0xa2, "&cent;", strings); //
        addMapping(0xa3, "&pound;", strings); //
        addMapping(0xa9, "&copy;", strings); //
        addMapping(0xae, "&reg;", strings); //
        addMapping(0xbf, "&iquest;", strings); //

        addMapping(0xc0, "&Agrave;", strings); //
        addMapping(0xc1, "&Aacute;", strings); //
        addMapping(0xc2, "&Acirc;", strings); //
        addMapping(0xc3, "&Atilde;", strings); //
        addMapping(0xc4, "&Auml;", strings); //
        addMapping(0xc5, "&Aring;", strings); //
        addMapping(0xc6, "&AElig;", strings); //
        addMapping(0xc7, "&Ccedil;", strings); //
        addMapping(0xc8, "&Egrave;", strings); //
        addMapping(0xc9, "&Eacute;", strings); //
        addMapping(0xca, "&Ecirc;", strings); //
        addMapping(0xcb, "&Euml;", strings); //
        addMapping(0xcc, "&Igrave;", strings); //
        addMapping(0xcd, "&Iacute;", strings); //
        addMapping(0xce, "&Icirc;", strings); //
        addMapping(0xcf, "&Iuml;", strings); //

        addMapping(0xd0, "&ETH;", strings); //
        addMapping(0xd1, "&Ntilde;", strings); //
        addMapping(0xd2, "&Ograve;", strings); //
        addMapping(0xd3, "&Oacute;", strings); //
        addMapping(0xd4, "&Ocirc;", strings); //
        addMapping(0xd5, "&Otilde;", strings); //
        addMapping(0xd6, "&Ouml;", strings); //
        addMapping(0xd7, "&times;", strings); //
        addMapping(0xd8, "&Oslash;", strings); //
        addMapping(0xd9, "&Ugrave;", strings); //
        addMapping(0xda, "&Uacute;", strings); //
        addMapping(0xdb, "&Ucirc;", strings); //
        addMapping(0xdc, "&Uuml;", strings); //
        addMapping(0xdd, "&Yacute;", strings); //
        addMapping(0xde, "&THORN;", strings); //
        addMapping(0xdf, "&szlig;", strings); //

        addMapping(0xe0, "&agrave;", strings); //
        addMapping(0xe1, "&aacute;", strings); //
        addMapping(0xe2, "&acirc;", strings); //
        addMapping(0xe3, "&atilde;", strings); //
        addMapping(0xe4, "&auml;", strings); //
        addMapping(0xe5, "&aring;", strings); //
        addMapping(0xe6, "&aelig;", strings); //
        addMapping(0xe7, "&ccedil;", strings); //
        addMapping(0xe8, "&egrave;", strings); //
        addMapping(0xe9, "&eacute;", strings); //
        addMapping(0xea, "&ecirc;", strings); //
        addMapping(0xeb, "&euml;", strings); //
        addMapping(0xec, "&igrave;", strings); //
        addMapping(0xed, "&iacute;", strings); //
        addMapping(0xee, "&icirc;", strings); //
        addMapping(0xef, "&iuml;", strings); //

        addMapping(0xf0, "&eth;", strings); //
        addMapping(0xf1, "&ntilde;", strings); //
        addMapping(0xf2, "&ograve;", strings); //
        addMapping(0xf3, "&oacute;", strings); //
        addMapping(0xf4, "&ocirc;", strings); //
        addMapping(0xf5, "&otilde;", strings); //
        addMapping(0xf6, "&ouml;", strings); //
        addMapping(0xf7, "&divide;", strings); //
        addMapping(0xf8, "&oslash;", strings); //
        addMapping(0xf9, "&ugrave;", strings); //
        addMapping(0xfa, "&uacute;", strings); //
        addMapping(0xfb, "&ucirc;", strings); //
        addMapping(0xfc, "&uuml;", strings); //
        addMapping(0xfd, "&yacute;", strings); //
        addMapping(0xfe, "&thorn;", strings); //
        addMapping(0xff, "&yuml;", strings); //

        for (int i = 0; i < strings.length; i++) {
            String str = strings[i];

            if (str != null) {
                _stringChars[i] = str.toCharArray();
            }
        }
    }
}
