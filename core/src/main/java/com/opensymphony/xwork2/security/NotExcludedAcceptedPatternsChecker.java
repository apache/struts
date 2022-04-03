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

/**
 * Used across different places to check if given string is not excluded and is accepted
 * @see <a href="https://securitylab.github.com/research/apache-struts-double-evaluation/">here</a>
 * @since 2.6
 */
public interface NotExcludedAcceptedPatternsChecker extends ExcludedPatternsChecker, AcceptedPatternsChecker {

    /**
     * Checks if value doesn't match excluded pattern and matches accepted pattern
     *
     * @param value to check
     * @return object containing result of matched pattern and pattern itself
     */
    IsAllowed isAllowed(String value);

    final class IsAllowed {

        private final boolean allowed;
        private final String allowedPattern;

        public static IsAllowed yes(String allowedPattern) {
            return new IsAllowed(true, allowedPattern);
        }

        public static IsAllowed no(String allowedPattern) {
            return new IsAllowed(false, allowedPattern);
        }

        private IsAllowed(boolean allowed, String allowedPattern) {
            this.allowed = allowed;
            this.allowedPattern = allowedPattern;
        }

        public boolean isAllowed() {
            return allowed;
        }

        public String getAllowedPattern() {
            return allowedPattern;
        }

        @Override
        public String toString() {
            return "IsAllowed { " +
                    "allowed=" + allowed +
                    ", allowedPattern=" + allowedPattern +
                    " }";
        }
    }
}
