package com.opensymphony.xwork2.config.entities;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class AllowedMethods {

    private Set<AllowedMethod> allowedMethods;

    public static AllowedMethods build(Set<String> methods) {

        Set<AllowedMethod> allowedMethods = new HashSet<>();
        for (String method : methods) {
            boolean isPattern = false;
            int len = method.length();
            StringBuilder ret = new StringBuilder();
            char c;
            for (int x = 0; x < len; x++) {
                c = method.charAt(x);
                if (x < len - 2 && c == '{' && '}' == method.charAt(x + 2)) {
                    ret.append("(.*)");
                    isPattern = true;
                    x += 2;
                } else {
                    ret.append(c);
                }
            }
            if (isPattern && !method.startsWith("regex:")) {
                allowedMethods.add(new PatternAllowedMethod(ret.toString(), method));
            } else if (method.startsWith("regex:")) {
                String pattern = method.substring(method.indexOf(":") + 1);
                allowedMethods.add(new PatternAllowedMethod(pattern, method));
            } else if (method.contains("*") && !method.startsWith("regex:")) {
                String pattern = method.replaceAll("\\*", "(.*)");
                allowedMethods.add(new PatternAllowedMethod(pattern, method));
            } else {
                allowedMethods.add(new LiteralAllowedMethod(ret.toString()));
            }
        }

        return new AllowedMethods(allowedMethods);
    }

    private AllowedMethods(Set<AllowedMethod> methods) {
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

            return original.equals(that.original);

        }

        @Override
        public int hashCode() {
            return original.hashCode();
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
