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
package com.opensymphony.xwork2.config.entities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class AllowedMethods {

    private static final Logger LOG = LogManager.getLogger(AllowedMethods.class);

    private Set<AllowedMethod> allowedMethods;
    private final boolean strictMethodInvocation;
    private String defaultRegex;

    public static AllowedMethods build(boolean strictMethodInvocation, Set<String> methods, String defaultRegex) {

        Set<AllowedMethod> allowedMethods = new HashSet<>();
        for (String method : methods) {
            boolean isPattern = false;
            StringBuilder methodPattern = new StringBuilder();
            int len = method.length();
            char c;
            for (int x = 0; x < len; x++) {
                c = method.charAt(x);
                if (x < len - 2 && c == '{' && '}' == method.charAt(x + 2)) {
                    methodPattern.append(defaultRegex);
                    isPattern = true;
                    x += 2;
                } else {
                    methodPattern.append(c);
                }
            }

            if (isPattern && !method.startsWith("regex:") && !strictMethodInvocation) {
                allowedMethods.add(new PatternAllowedMethod(methodPattern.toString(), method));
            } else if (method.startsWith("regex:")) {
                String pattern = method.substring(method.indexOf(':') + 1);
                allowedMethods.add(new PatternAllowedMethod(pattern, method));
            } else if (method.contains("*") && !method.startsWith("regex:") && !strictMethodInvocation) {
                String pattern = method.replace("*", defaultRegex);
                allowedMethods.add(new PatternAllowedMethod(pattern, method));
            } else if (!isPattern) {
                allowedMethods.add(new LiteralAllowedMethod(method));
            } else {
                LOG.trace("Ignoring method name: [{}] when SMI is set to [{}]", method, strictMethodInvocation);
            }
        }

        LOG.debug("Defined allowed methods: {}", allowedMethods);

        return new AllowedMethods(strictMethodInvocation, allowedMethods, defaultRegex);
    }

    private AllowedMethods(boolean strictMethodInvocation, Set<AllowedMethod> methods, String defaultRegex) {
        this.strictMethodInvocation = strictMethodInvocation;
        this.defaultRegex = defaultRegex;
        this.allowedMethods = Collections.unmodifiableSet(methods);
    }

    public boolean isAllowed(String method) {
        for (AllowedMethod allowedMethod : allowedMethods) {
            if (allowedMethod.isAllowed(method)) {
                return true;
            }
        }
        return false;
    }

    public Set<String> list() {
        Set<String> result = new HashSet<>();
        for (AllowedMethod allowedMethod : allowedMethods) {
            result.add(allowedMethod.original());
        }
        return result;
    }

    public String getDefaultRegex() {
        return defaultRegex;
    }

    public boolean isStrictMethodInvocation() {
        return strictMethodInvocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AllowedMethods that = (AllowedMethods) o;

        return allowedMethods.equals(that.allowedMethods);
    }

    @Override
    public int hashCode() {
        return allowedMethods.hashCode();
    }

    private interface AllowedMethod {
        boolean isAllowed(String methodName);

        String original();
    }

    @Override
    public String toString() {
        return "allowedMethods=" + allowedMethods;
    }

    private static class PatternAllowedMethod implements AllowedMethod {

        private final Pattern allowedMethodPattern;
        private String original;

        public PatternAllowedMethod(String pattern, String original) {
            this.original = original;
            allowedMethodPattern = Pattern.compile(pattern);
        }

        @Override
        public boolean isAllowed(String methodName) {
            return allowedMethodPattern.matcher(methodName).matches();
        }

        @Override
        public String original() {
            return original;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PatternAllowedMethod that = (PatternAllowedMethod) o;

            return allowedMethodPattern.pattern().equals(that.allowedMethodPattern.pattern());

        }

        @Override
        public int hashCode() {
            return allowedMethodPattern.pattern().hashCode();
        }

        @Override
        public String toString() {
            return "PatternAllowedMethod{" +
                    "allowedMethodPattern=" + allowedMethodPattern +
                    ", original='" + original + '\'' +
                    '}';
        }
    }

    private static class LiteralAllowedMethod implements AllowedMethod {

        private String allowedMethod;

        public LiteralAllowedMethod(String allowedMethod) {
            this.allowedMethod = allowedMethod;
        }

        @Override
        public boolean isAllowed(String methodName) {
            return methodName.equals(allowedMethod);
        }

        @Override
        public String original() {
            return allowedMethod;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LiteralAllowedMethod that = (LiteralAllowedMethod) o;

            return allowedMethod.equals(that.allowedMethod);

        }

        @Override
        public int hashCode() {
            return allowedMethod.hashCode();
        }

        @Override
        public String toString() {
            return "LiteralAllowedMethod{" +
                    "allowedMethod='" + allowedMethod + '\'' +
                    '}';
        }
    }

}
