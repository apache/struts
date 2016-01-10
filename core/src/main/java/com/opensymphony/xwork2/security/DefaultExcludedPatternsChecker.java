package com.opensymphony.xwork2.security;

import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class DefaultExcludedPatternsChecker implements ExcludedPatternsChecker {

    private static final Logger LOG = LogManager.getLogger(DefaultExcludedPatternsChecker.class);

    public static final String[] EXCLUDED_PATTERNS = {
        "(^|\\%\\{)((#?)(top(\\.|\\['|\\[\")|\\[\\d\\]\\.)?)(dojo|struts|session|request|response|application|servlet(Request|Response|Context)|parameters|context|_memberAccess)(\\.|\\[).*",
        ".*(^|\\.|\\[|\\'|\"|get)class(\\(\\.|\\[|\\'|\").*",
        "^(action|method):.*"
    };

    private Set<Pattern> excludedPatterns;

    public DefaultExcludedPatternsChecker() {
        setExcludedPatterns(EXCLUDED_PATTERNS);
    }

    @Inject(value = XWorkConstants.OVERRIDE_EXCLUDED_PATTERNS, required = false)
    public void setOverrideExcludePatterns(String excludePatterns) {
        LOG.warn("Overriding excluded patterns [{}] with [{}], be aware that this affects all instances and safety of your application!",
                    XWorkConstants.OVERRIDE_EXCLUDED_PATTERNS, excludePatterns);
        excludedPatterns = new HashSet<Pattern>();
        for (String pattern : TextParseUtil.commaDelimitedStringToSet(excludePatterns)) {
            excludedPatterns.add(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
        }
    }

    @Inject(value = XWorkConstants.ADDITIONAL_EXCLUDED_PATTERNS, required = false)
    public void setAdditionalExcludePatterns(String excludePatterns) {
        LOG.debug("Adding additional global patterns [{}] to excluded patterns!", excludePatterns);
        for (String pattern : TextParseUtil.commaDelimitedStringToSet(excludePatterns)) {
            excludedPatterns.add(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
        }
    }

    public void setExcludedPatterns(String commaDelimitedPatterns) {
        setExcludedPatterns(TextParseUtil.commaDelimitedStringToSet(commaDelimitedPatterns));
    }

    public void setExcludedPatterns(String[] patterns) {
        setExcludedPatterns(new HashSet<>(Arrays.asList(patterns)));
    }

    public void setExcludedPatterns(Set<String> patterns) {
        LOG.trace("Sets excluded patterns [{}]", patterns);
        excludedPatterns = new HashSet<>(patterns.size());
        for (String pattern : patterns) {
            excludedPatterns.add(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
        }
    }

    public IsExcluded isExcluded(String value) {
        for (Pattern excludedPattern : excludedPatterns) {
            if (excludedPattern.matcher(value).matches()) {
                LOG.trace("[{}] matches excluded pattern [{}]", value, excludedPattern);
                return IsExcluded.yes(excludedPattern);
            }
        }
        return IsExcluded.no(excludedPatterns);
    }

    public Set<Pattern> getExcludedPatterns() {
        return excludedPatterns;
    }

}
