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
package org.apache.struts2.components;

/**
 * Decides whether a Java regular expression can be handed to a browser as an HTML5 {@code pattern}
 * attribute without changing meaning.
 * <p>
 * This is an allowlist by design. A denylist of Java-only constructs would violate the
 * never-false-reject rule the first time it missed one, because a missed construct becomes a pattern
 * the browser interprets differently and the user cannot get past. Anything not provably common to
 * both engines is rejected, and the field simply gets no client-side check.
 *
 * @since 7.4.0
 */
public final class EcmaScriptSafeRegex {

    /**
     * Escapes with identical meaning in both engines.
     * <p>
     * {@code \s} and {@code \S} are deliberately absent. Java's {@code \s} is ASCII-only by default
     * while ECMAScript's is the wider Unicode set, so {@code ^\S+$} accepts a value containing NBSP
     * on the server and rejects it in the browser. {@code \d} and {@code \w} are safe — both engines
     * are ASCII-only for those, and JavaScript never widens them.
     * <p>
     * {@code \b} and {@code \B} are absent for a sharper reason: their meaning is not even stable
     * across the JDKs Struts supports. Up to Java 18 the boundary was decided by
     * {@code Character.isLetterOrDigit}, making it Unicode-aware while {@code \w} stayed ASCII;
     * JDK 19 resolved that inconsistency. So {@code ^\bäiti\b$} matches {@code äiti} on Java 17 and
     * not on Java 21, while ECMAScript — whose boundary is always ASCII-word based — rejects it in
     * every browser. On the Java 17 baseline that is a false reject, and no version check could fix
     * it: one {@code validation.xml} would have to mean two different things depending on the JVM.
     */
    private static final String ALLOWED_ESCAPES = "dDwWnrtf\\.*+?()[]{}|^$/-";

    private EcmaScriptSafeRegex() {
    }

    public static boolean isSafe(String regex) {
        if (regex == null || regex.isEmpty()) {
            return false;
        }
        boolean inCharClass = false;
        int i = 0;
        while (i < regex.length()) {
            char current = regex.charAt(i);
            if (!isPortable(regex, i, current, inCharClass)) {
                return false;
            }
            if (current == '[') {
                inCharClass = true;
            } else if (current == ']') {
                inCharClass = false;
            }
            // an escape consumes the character it escapes, which must not be scanned again
            i += (current == '\\') ? 2 : 1;
        }
        return !inCharClass;
    }

    /**
     * Whether the construct starting at {@code index} means the same thing to both engines. This is
     * the whole allowlist: anything that reaches {@code default} is a character with no special
     * meaning in either engine, or one whose meaning is shared.
     */
    private static boolean isPortable(String regex, int index, char current, boolean inCharClass) {
        switch (current) {
            case '\\':
                return isAllowedEscape(regex, index);
            case '[':
                // Java allows nested classes and POSIX names; ECMAScript allows neither
                return !inCharClass && !regex.startsWith("[:", index);
            case '&':
                // Java character-class intersection
                return !inCharClass || !isFollowedBy(regex, index, '&');
            case '(':
                return isPortableGroup(regex, index);
            case '*', '+', '?', '}':
                // possessive quantifier
                return !isFollowedBy(regex, index, '+');
            default:
                return true;
        }
    }

    private static boolean isAllowedEscape(String regex, int index) {
        return index + 1 < regex.length() && ALLOWED_ESCAPES.indexOf(regex.charAt(index + 1)) >= 0;
    }

    /**
     * Only non-capturing groups and lookahead are portable; named groups, lookbehind, atomic groups
     * and inline flags are not. A plain capturing group is always fine.
     */
    private static boolean isPortableGroup(String regex, int index) {
        if (!isFollowedBy(regex, index, '?')) {
            return true;
        }
        if (index + 2 >= regex.length()) {
            return false;
        }
        char kind = regex.charAt(index + 2);
        return kind == ':' || kind == '=' || kind == '!';
    }

    private static boolean isFollowedBy(String regex, int index, char expected) {
        return index + 1 < regex.length() && regex.charAt(index + 1) == expected;
    }
}
