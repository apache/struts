package com.opensymphony.xwork2;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class DefaultExcludedPatternsChecker implements ExcludedPatternsChecker {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultExcludedPatternsChecker.class);

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

    public DefaultExcludedPatternsChecker() {
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

}
