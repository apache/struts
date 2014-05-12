package com.opensymphony.xwork2;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Used across different interceptors to check if given string matches one of the excluded patterns.
 * User has two options to change its behaviour:
 * - define new set of patterns with <constant name="struts.override.excludedPatterns" value=".."/>
 * - override this class and use then extension point <constant name="struts.excludedPatterns.checker" value="myChecker"/>
 *   to inject it in appropriated places
 */
public class ExcludedPatternsChecker {

    private static final Logger LOG = LoggerFactory.getLogger(ExcludedPatternsChecker.class);

    public static final String[] EXCLUDED_PATTERNS = {
            "(.*\\.|^|.*|\\[('|\"))class(\\.|('|\")]|\\[).*",
            "^dojo\\..*",
            "^struts\\..*",
            "^session\\..*",
            "^request\\..*",
            "^application\\..*",
            "^servlet(Request|Response)\\..*",
            "^parameters\\..*"
    };

    private Set<Pattern> excludedPatterns;

    public ExcludedPatternsChecker() {
        excludedPatterns = new HashSet<Pattern>();
        for (String pattern : EXCLUDED_PATTERNS) {
            excludedPatterns.add(Pattern.compile(pattern));
        }
    }

    @Inject(value = XWorkConstants.OVERRIDE_EXCLUDED_PATTERNS, required = false)
    public void setOverrideExcludePatterns(String excludePatterns) {
        if (LOG.isWarnEnabled()) {
            LOG.warn("Overriding [#0] with [#1], be aware that this can affect safety of your application!",
                    XWorkConstants.OVERRIDE_EXCLUDED_PATTERNS, excludePatterns);
        }
        excludedPatterns = new HashSet<Pattern>();
        for (String pattern : TextParseUtil.commaDelimitedStringToSet(excludePatterns)) {
            excludedPatterns.add(Pattern.compile(pattern));
        }
    }

    /**
     * Allows add additional excluded patterns during runtime
     *
     * @param commaDelimitedPatterns comma delimited string with patterns
     */
    public void addExcludedPatterns(String commaDelimitedPatterns) {
        addExcludedPatterns(TextParseUtil.commaDelimitedStringToSet(commaDelimitedPatterns));
    }

    /**
     * Allows add additional excluded patterns during runtime
     *
     * @param additionalPatterns array of additional excluded patterns
     */
    public void addExcludedPatterns(String[] additionalPatterns) {
        addExcludedPatterns(new HashSet<String>(Arrays.asList(additionalPatterns)));
    }

    /**
     * Allows add additional excluded patterns during runtime
     *
     * @param additionalPatterns set of additional patterns
     */
    public void addExcludedPatterns(Set<String> additionalPatterns) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Adding additional excluded patterns [#0]", additionalPatterns);
        }
        for (String pattern : additionalPatterns) {
            excludedPatterns.add(Pattern.compile(pattern));
        }
    }

    public IsExcluded isExcluded(String value) {
        for (Pattern excludedPattern : excludedPatterns) {
            if (excludedPattern.matcher(value).matches()) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("[#0] matches excluded pattern [#1]", value, excludedPattern);
                }
                return IsExcluded.yes(excludedPattern);
            }
        }
        return IsExcluded.no();
    }

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
