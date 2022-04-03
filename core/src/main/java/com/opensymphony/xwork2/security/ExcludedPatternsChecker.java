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
package com.opensymphony.xwork2.security;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Used across different interceptors to check if given string matches one of the excluded patterns.
 */
public interface ExcludedPatternsChecker {

    /**
     * Checks if value matches any of patterns on exclude list
     *
     * @param value to check
     * @return object containing result of matched pattern and pattern itself
     */
    public IsExcluded isExcluded(String value);

    /**
     * Sets excluded patterns during runtime
     *
     * @param commaDelimitedPatterns comma delimited string with patterns
     */
    public void setExcludedPatterns(String commaDelimitedPatterns);

    /**
     * Sets excluded patterns during runtime
     *
     * @param patterns array of additional excluded patterns
     */
    public void setExcludedPatterns(String[] patterns);

    /**
     * Sets excluded patterns during runtime
     *
     * @param patterns set of additional patterns
     */
    public void setExcludedPatterns(Set<String> patterns);

    /**
     * Allow access list of all defined excluded patterns
     *
     * @return set of excluded patterns
     */
    public Set<Pattern> getExcludedPatterns();

    public final static class IsExcluded {

        private final boolean excluded;
        private final String excludedPattern;

        public static IsExcluded yes(Pattern excludedPattern) {
            return new IsExcluded(true, excludedPattern.pattern());
        }

        public static IsExcluded no(Set<Pattern> excludedPatterns) {
            return new IsExcluded(false, excludedPatterns.toString());
        }

        private IsExcluded(boolean excluded, String excludedPattern) {
            this.excluded = excluded;
            this.excludedPattern = excludedPattern;
        }

        public boolean isExcluded() {
            return excluded;
        }

        public String getExcludedPattern() {
            return excludedPattern;
        }

        @Override
        public String toString() {
            return "IsExcluded { " +
                    "excluded=" + excluded +
                    ", excludedPattern=" + excludedPattern +
                    " }";
        }
    }

}
