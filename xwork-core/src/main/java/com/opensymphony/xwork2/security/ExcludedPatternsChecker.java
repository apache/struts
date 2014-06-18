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
     * Allows add additional excluded patterns during runtime
     *
     * @param commaDelimitedPatterns comma delimited string with patterns
     */
    public void addExcludedPatterns(String commaDelimitedPatterns);

    /**
     * Allows add additional excluded patterns during runtime
     *
     * @param additionalPatterns array of additional excluded patterns
     */
    public void addExcludedPatterns(String[] additionalPatterns);

    /**
     * Allows add additional excluded patterns during runtime
     *
     * @param additionalPatterns set of additional patterns
     */
    public void addExcludedPatterns(Set<String> additionalPatterns);

    /**
     * Allow access list of all defined excluded patterns
     *
     * @return set of excluded patterns
     */
    public Set<Pattern> getExcludedPatterns();

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
