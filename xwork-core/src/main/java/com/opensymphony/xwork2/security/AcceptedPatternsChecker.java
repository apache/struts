package com.opensymphony.xwork2.security;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Used across different interceptors to check if given string matches one of the excluded patterns.
 */
public interface AcceptedPatternsChecker {

    /**
     * Checks if value matches any of patterns on exclude list
     *
     * @param value to check
     * @return object containing result of matched pattern and pattern itself
     */
    public IsAccepted isAccepted(String value);

    /**
     * Sets excluded patterns during runtime
     *
     * @param commaDelimitedPatterns comma delimited string with patterns
     */
    public void setAcceptedPatterns(String commaDelimitedPatterns);

    /**
     * Set excluded patterns during runtime
     *
     * @param patterns array of additional excluded patterns
     */
    public void setAcceptedPatterns(String[] patterns);

    /**
     * Sets excluded patterns during runtime
     *
     * @param patterns set of additional patterns
     */
    public void setAcceptedPatterns(Set<String> patterns);

    /**
     * Allow access list of all defined excluded patterns
     *
     * @return set of excluded patterns
     */
    public Set<Pattern> getAcceptedPatterns();

    public final static class IsAccepted {

        private final boolean accepted;
        private final Pattern acceptedPattern;

        public static IsAccepted yes(Pattern acceptedPattern) {
            return new IsAccepted(true, acceptedPattern);
        }

        public static IsAccepted no() {
            return new IsAccepted(false, null);
        }

        private IsAccepted(boolean accepted, Pattern acceptedPattern) {
            this.accepted = accepted;
            this.acceptedPattern = acceptedPattern;
        }

        public boolean isAccepted() {
            return accepted;
        }

        public Pattern getAcceptedPattern() {
            return acceptedPattern;
        }

        @Override
        public String toString() {
            return "IsAccepted {" +
                    "accepted=" + accepted +
                    ", acceptedPattern=" + acceptedPattern +
                    " }";
        }
    }

}
