package com.opensymphony.xwork2.security;

import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class DefaultAcceptedPatternsChecker implements AcceptedPatternsChecker {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAcceptedPatternsChecker.class);

    public static final String[] ACCEPTED_PATTERNS = {
            "\\w+((\\.\\w+)|(\\[\\d+\\])|(\\(\\d+\\))|(\\['(\\w|[\\u4e00-\\u9fa5])+'\\])|(\\('(\\w|[\\u4e00-\\u9fa5])+'\\)))*"
    };

    private Set<Pattern> acceptedPatterns;

    public DefaultAcceptedPatternsChecker() {
        setAcceptedPatterns(ACCEPTED_PATTERNS);
    }

    @Inject(value = XWorkConstants.OVERRIDE_ACCEPTED_PATTERNS, required = false)
    public void setOverrideAcceptedPatterns(String acceptablePatterns) {
        if (LOG.isWarnEnabled()) {
            LOG.warn("Overriding accepted patterns [#0] with [#1], be aware that this affects all instances and safety of your application!",
                    XWorkConstants.OVERRIDE_ACCEPTED_PATTERNS, acceptablePatterns);
        }
        acceptedPatterns = new HashSet<Pattern>();
        for (String pattern : TextParseUtil.commaDelimitedStringToSet(acceptablePatterns)) {
            acceptedPatterns.add(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
        }
    }

    @Inject(value = XWorkConstants.ADDITIONAL_ACCEPTED_PATTERNS, required = false)
    public void setAdditionalAcceptedPatterns(String acceptablePatterns) {
        if (LOG.isDebugEnabled()) {
            LOG.warn("Adding additional global patterns [#0] to accepted patterns!", acceptablePatterns);
        }
        for (String pattern : TextParseUtil.commaDelimitedStringToSet(acceptablePatterns)) {
            acceptedPatterns.add(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
        }
    }

    public void setAcceptedPatterns(String commaDelimitedPatterns) {
        setAcceptedPatterns(TextParseUtil.commaDelimitedStringToSet(commaDelimitedPatterns));
    }

    public void setAcceptedPatterns(String[] additionalPatterns) {
        setAcceptedPatterns(new HashSet<String>(Arrays.asList(additionalPatterns)));
    }

    public void setAcceptedPatterns(Set<String> patterns) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Sets accepted patterns [#0]", patterns);
        }
        acceptedPatterns = new HashSet<Pattern>(patterns.size());
        for (String pattern : patterns) {
            acceptedPatterns.add(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
        }
    }

    public IsAccepted isAccepted(String value) {
        for (Pattern acceptedPattern : acceptedPatterns) {
            if (acceptedPattern.matcher(value).matches()) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("[#0] matches accepted pattern [#1]", value, acceptedPattern);
                }
                return IsAccepted.yes(acceptedPattern.toString());
            }
        }
        return IsAccepted.no(acceptedPatterns.toString());
    }

    public Set<Pattern> getAcceptedPatterns() {
        return acceptedPatterns;
    }

}
