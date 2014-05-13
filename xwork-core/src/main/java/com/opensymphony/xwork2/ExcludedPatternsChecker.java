package com.opensymphony.xwork2;

import java.util.regex.Pattern;

/**
 * Used across different interceptors to check if given string matches one of the excluded patterns.
 */
public interface ExcludedPatternsChecker {

    public IsExcluded isExcluded(String value);

    public final static class IsExcluded {

        private final boolean excluded;
        private final Pattern excludedPattern;

        public static IsExcluded yes(Pattern excludedPattern) {
            return new IsExcluded(true, excludedPattern);
        }

        public static IsExcluded no() {
            return new IsExcluded(false, null);
        }

        private IsExcluded(boolean excluded, Pattern excludedPattern) {
            this.excluded = excluded;
            this.excludedPattern = excludedPattern;
        }

        public boolean isExcluded() {
            return excluded;
        }

        public Pattern getExcludedPattern() {
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
