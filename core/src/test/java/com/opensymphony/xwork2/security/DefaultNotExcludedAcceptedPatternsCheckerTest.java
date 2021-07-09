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

import com.opensymphony.xwork2.XWorkTestCase;

import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class DefaultNotExcludedAcceptedPatternsCheckerTest extends XWorkTestCase {

    public void testNoExclusionAcceptAllPatternsChecker() {
        assertTrue(NO_EXCLUSION_ACCEPT_ALL_PATTERNS_CHECKER.isAllowed("%{1+1}").isAllowed());
    }

    public static final NotExcludedAcceptedPatternsChecker NO_EXCLUSION_ACCEPT_ALL_PATTERNS_CHECKER
            = new NotExcludedAcceptedPatternsChecker() {
        @Override
        public IsAllowed isAllowed(String value) {
            return IsAllowed.yes("*");
        }

        @Override
        public IsAccepted isAccepted(String value) {
            return null;
        }

        @Override
        public void setAcceptedPatterns(String commaDelimitedPatterns) {

        }

        @Override
        public void setAcceptedPatterns(String[] patterns) {

        }

        @Override
        public void setAcceptedPatterns(Set<String> patterns) {

        }

        @Override
        public Set<Pattern> getAcceptedPatterns() {
            return null;
        }

        @Override
        public IsExcluded isExcluded(String value) {
            return null;
        }

        @Override
        public void setExcludedPatterns(String commaDelimitedPatterns) {

        }

        @Override
        public void setExcludedPatterns(String[] patterns) {

        }

        @Override
        public void setExcludedPatterns(Set<String> patterns) {

        }

        @Override
        public Set<Pattern> getExcludedPatterns() {
            return null;
        }
    };
}