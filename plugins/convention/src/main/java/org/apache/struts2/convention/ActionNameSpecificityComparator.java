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
package org.apache.struts2.convention;

import java.util.Comparator;

/**
 * Orders wildcard action-name patterns most-specific-first so that, under the framework's
 * first-match-wins matching, a specific pattern (e.g. {@code some/usefull/*}) is evaluated
 * before a general one (e.g. {@code some/*}).
 *
 * <p>Ordering keys, applied in order:</p>
 * <ol>
 *   <li>fewer wildcard tokens first (a {@code *}/{@code **} run, or a <code>{var}</code> group);</li>
 *   <li>more literal characters first;</li>
 *   <li>fewer path-spanning {@code **} tokens first;</li>
 *   <li>natural (alphabetical) order of the pattern, for deterministic tie-breaking.</li>
 * </ol>
 *
 * <p>Matcher-agnostic: it recognises both {@code *}/{@code **} (WildcardHelper) and
 * <code>{var}</code> (NamedVariablePatternMatcher) wildcards.</p>
 *
 * @since 7.3.0 (WW-3784)
 */
public class ActionNameSpecificityComparator implements Comparator<String> {

    @Override
    public int compare(String a, String b) {
        Counts ca = count(a);
        Counts cb = count(b);

        int byWildcards = Integer.compare(ca.wildcards, cb.wildcards);
        if (byWildcards != 0) {
            return byWildcards;
        }
        int byLiterals = Integer.compare(cb.literals, ca.literals); // more literals first
        if (byLiterals != 0) {
            return byLiterals;
        }
        int byPathWildcards = Integer.compare(ca.pathWildcards, cb.pathWildcards);
        if (byPathWildcards != 0) {
            return byPathWildcards;
        }
        return a.compareTo(b);
    }

    private Counts count(String pattern) {
        int wildcards = 0;
        int pathWildcards = 0;
        int literals = 0;
        int i = 0;
        int len = pattern.length();
        while (i < len) {
            char c = pattern.charAt(i);
            if (c == '*') {
                int start = i;
                while (i < len && pattern.charAt(i) == '*') {
                    i++;
                }
                wildcards++;
                if (i - start >= 2) {
                    pathWildcards++;
                }
            } else if (c == '{') {
                int close = pattern.indexOf('}', i);
                if (close < 0) {
                    literals += len - i; // malformed: treat the remainder as literal
                    break;
                }
                wildcards++;
                i = close + 1;
            } else {
                literals++;
                i++;
            }
        }
        return new Counts(wildcards, pathWildcards, literals);
    }

    private record Counts(int wildcards, int pathWildcards, int literals) {
    }
}
