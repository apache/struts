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
    private static final String ALLOWED_ESCAPES = "dDwWnrtf\\.*+?()[]{}|^$/";

    /**
     * Browsers compile {@code pattern} with the {@code v} (unicode sets) flag. Inside a character
     * class that mode reserves these unescaped, and any doubled {@link #CLASS_PUNCTUATORS}, as syntax;
     * Java reads them as literals.
     */
    private static final String CLASS_SYNTAX = "(){}/|";
    private static final String CLASS_PUNCTUATORS = "&!#$%*+,.:;<=>?@^`~";

    private EcmaScriptSafeRegex() {
    }

    public static boolean isSafe(String regex) {
        if (regex == null || regex.isEmpty()) {
            return false;
        }
        Scanner scanner = new Scanner(regex);
        while (scanner.hasMore()) {
            if (!scanner.scanNext()) {
                return false;
            }
        }
        return !scanner.inCharClass;
    }

    /**
     * Walks the regex one unit at a time — a character, an escape pair, or a whole {@code {n,m}}
     * quantifier — and refuses the first one the two engines disagree on. The allowlist lives in
     * {@link #isPortable} and {@link #isPortableInClass}: anything that reaches their default branch is
     * a character with no special meaning in either engine, or one whose meaning is shared.
     */
    private static final class Scanner {
        private final String regex;
        private int index;
        private boolean inCharClass;
        // true while the previous unit is a plain literal a range can start from
        private boolean rangeStartAvailable;
        // true right after a quantifier: Java stacks them (a{2}{3}), the browser has nothing to repeat
        private boolean afterQuantifier;

        Scanner(String regex) {
            this.regex = regex;
        }

        boolean hasMore() {
            return index < regex.length();
        }

        boolean scanNext() {
            char current = regex.charAt(index);
            if (current == '\\') {
                return scanEscape();
            }
            if (inCharClass) {
                return scanInClass(current);
            }
            if (current == '{') {
                return scanQuantifier();
            }
            return scanOutsideClass(current);
        }

        private boolean scanEscape() {
            if (!isAllowedEscape()) {
                return false;
            }
            // an escape consumes the character it escapes, which must not be scanned again;
            // in unicode-sets mode a class escape cannot bound a range either
            rangeStartAvailable = false;
            afterQuantifier = false;
            index += 2;
            return true;
        }

        private boolean scanInClass(char current) {
            if (current == ']') {
                inCharClass = false;
                index++;
                return true;
            }
            if (current == '-') {
                if (!isRangeOperator()) {
                    return false;
                }
                rangeStartAvailable = false;
                index += 2;
                return true;
            }
            if (!isPortableInClass(current)) {
                return false;
            }
            rangeStartAvailable = true;
            index++;
            return true;
        }

        private boolean scanQuantifier() {
            int close = endOfQuantifier();
            if (afterQuantifier || close < 0 || isFollowedBy(close, '+')) {
                return false;
            }
            afterQuantifier = true;
            index = close + 1;
            return true;
        }

        private boolean scanOutsideClass(char current) {
            boolean stacked = afterQuantifier && (current == '*' || current == '+');
            if (stacked || !isPortable(current)) {
                return false;
            }
            if (current == '[') {
                inCharClass = true;
                rangeStartAvailable = false;
                // the negation marker is part of the class opening, not a literal
                index += isFollowedBy(index, '^') ? 2 : 1;
            } else {
                index++;
            }
            afterQuantifier = current == '*' || current == '+' || current == '?';
            return true;
        }

        /**
         * Whether the construct at the current position, outside a character class, means the same
         * thing to both engines.
         */
        private boolean isPortable(char current) {
            switch (current) {
                case '[':
                    // Java allows POSIX names and a leading literal ]; ECMAScript allows neither
                    return !regex.startsWith("[:", index) && !opensWithLiteralBracket();
                case ']', '}':
                    // a literal in Java, "lone quantifier brackets" in the browser
                    return false;
                case '(':
                    return isPortableGroup();
                case '*', '+', '?':
                    // possessive quantifier
                    return !isFollowedBy(index, '+');
                default:
                    return true;
            }
        }

        private boolean isPortableInClass(char current) {
            if (current == '[' || CLASS_SYNTAX.indexOf(current) >= 0) {
                // nested classes are Java-only; the rest are unicode-sets syntax characters
                return false;
            }
            return CLASS_PUNCTUATORS.indexOf(current) < 0 || !isFollowedBy(index, current);
        }

        /**
         * Inside a class an unescaped hyphen is portable only as a range operator between two plain
         * literals: {@code [a-z]}. Anywhere else Java reads it as a literal and the browser throws.
         */
        private boolean isRangeOperator() {
            if (!rangeStartAvailable || index + 1 >= regex.length()) {
                return false;
            }
            char end = regex.charAt(index + 1);
            return end != ']' && end != '\\' && end != '-' && end != '[' && CLASS_SYNTAX.indexOf(end) < 0;
        }

        /**
         * Index of the {@code }} closing the {@code {n}}, {@code {n,}} or {@code {n,m}} quantifier that
         * opens at the current position, or -1 when the braces do not form one — which Java rejects as well.
         */
        private int endOfQuantifier() {
            int close = regex.indexOf('}', index);
            if (close < 0 || !regex.substring(index + 1, close).matches("\\d+(,\\d*)?")) {
                return -1;
            }
            return close;
        }

        private boolean isAllowedEscape() {
            if (index + 1 >= regex.length()) {
                return false;
            }
            char escaped = regex.charAt(index + 1);
            // in unicode mode \- is only legal inside a class
            if (escaped == '-') {
                return inCharClass;
            }
            return ALLOWED_ESCAPES.indexOf(escaped) >= 0;
        }

        /**
         * Java reads a {@code ]} directly after {@code [} or {@code [^} as a literal member of the
         * class; the browser's unicode-mode compiler rejects it.
         */
        private boolean opensWithLiteralBracket() {
            int first = isFollowedBy(index, '^') ? index + 2 : index + 1;
            return first < regex.length() && regex.charAt(first) == ']';
        }

        /**
         * Only non-capturing groups and lookahead are portable; named groups, lookbehind, atomic
         * groups and inline flags are not. A plain capturing group is always fine.
         */
        private boolean isPortableGroup() {
            if (!isFollowedBy(index, '?')) {
                return true;
            }
            if (index + 2 >= regex.length()) {
                return false;
            }
            char kind = regex.charAt(index + 2);
            return kind == ':' || kind == '=' || kind == '!';
        }

        private boolean isFollowedBy(int at, char expected) {
            return at + 1 < regex.length() && regex.charAt(at + 1) == expected;
        }
    }
}
