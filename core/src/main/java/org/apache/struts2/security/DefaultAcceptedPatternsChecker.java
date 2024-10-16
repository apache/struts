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
package org.apache.struts2.security;

import org.apache.struts2.inject.Inject;
import org.apache.struts2.util.TextParseUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.joining;

public class DefaultAcceptedPatternsChecker implements AcceptedPatternsChecker {

    private static final Logger LOG = LogManager.getLogger(DefaultAcceptedPatternsChecker.class);

    public static final String[] ACCEPTED_PATTERNS = {
            "\\w+((\\.\\w+)|(\\[\\d+])|(\\(\\d+\\))|(\\['(\\w-?|[\\u4e00-\\u9fa5]-?)+'])|(\\('(\\w-?|[\\u4e00-\\u9fa5]-?)+'\\)))*"
    };

    /**
     * Must match {@link #ACCEPTED_PATTERNS} RegEx. Signifies characters which result in a nested lookup via OGNL.
     */
    public static final Set<Character> NESTING_CHARS = Set.of('.', '[', '(');
    public static final String NESTING_CHARS_STR = NESTING_CHARS.stream().map(String::valueOf).collect(joining());

    public static final String[] DMI_AWARE_ACCEPTED_PATTERNS = {
            "\\w+([:]?\\w+)?((\\.\\w+)|(\\[\\d+])|(\\(\\d+\\))|(\\['(\\w-?|[\\u4e00-\\u9fa5]-?)+'])|(\\('(\\w-?|[\\u4e00-\\u9fa5]-?)+'\\)))*([!]?\\w+)?"
    };

    protected Set<Pattern> acceptedPatterns;

    public DefaultAcceptedPatternsChecker() {
        setAcceptedPatterns(ACCEPTED_PATTERNS);
    }

    public DefaultAcceptedPatternsChecker(
            @Inject(value = StrutsConstants.STRUTS_ENABLE_DYNAMIC_METHOD_INVOCATION, required = false) String dmiValue
    ) {
        if (BooleanUtils.toBoolean(dmiValue)) {
            LOG.debug("DMI is enabled, adding DMI related accepted patterns");
            setAcceptedPatterns(DMI_AWARE_ACCEPTED_PATTERNS);
        } else {
            setAcceptedPatterns(ACCEPTED_PATTERNS);
        }
    }

    @Inject(value = StrutsConstants.STRUTS_OVERRIDE_ACCEPTED_PATTERNS, required = false)
    protected void setOverrideAcceptedPatterns(String acceptablePatterns) {
        setAcceptedPatterns(acceptablePatterns);
    }

    @Inject(value = StrutsConstants.STRUTS_ADDITIONAL_ACCEPTED_PATTERNS, required = false)
    protected void setAdditionalAcceptedPatterns(String acceptablePatterns) {
        LOG.warn("Adding additional global patterns [{}] to accepted patterns!", acceptablePatterns);
        Set<Pattern> newAcceptedPatterns = new HashSet<>(acceptedPatterns);
        try {
            for (String pattern : TextParseUtil.commaDelimitedStringToSet(acceptablePatterns)) {
                newAcceptedPatterns.add(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
            }
        } finally {
            acceptedPatterns = unmodifiableSet(newAcceptedPatterns);
        }
    }

    @Override
    public void setAcceptedPatterns(String commaDelimitedPatterns) {
        setAcceptedPatterns(TextParseUtil.commaDelimitedStringToSet(commaDelimitedPatterns));
    }

    @Override
    public void setAcceptedPatterns(String[] additionalPatterns) {
        setAcceptedPatterns(new HashSet<>(asList(additionalPatterns)));
    }

    @Override
    public void setAcceptedPatterns(Set<String> patterns) {
        logPatternChange(patterns);
        Set<Pattern> newAcceptedPatterns = new HashSet<>(patterns.size());
        try {
            for (String pattern : patterns) {
                newAcceptedPatterns.add(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
            }
        } finally {
            acceptedPatterns = unmodifiableSet(newAcceptedPatterns);
        }
    }

    protected void logPatternChange(Set<String> newPatterns) {
        if (acceptedPatterns == null) {
            // No need to warn on class initialisation
            LOG.debug("Sets accepted patterns to [{}], note this impacts the safety of your application!", newPatterns);
        } else {
            LOG.warn("Replacing accepted patterns [{}] with [{}], be aware that this affects all instances and safety of your application!",
                    acceptedPatterns, newPatterns);
        }
    }

    @Override
    public IsAccepted isAccepted(String value) {
        for (Pattern acceptedPattern : acceptedPatterns) {
            if (acceptedPattern.matcher(value).matches()) {
                LOG.trace("[{}] matches accepted pattern [{}]", value, acceptedPattern);
                return IsAccepted.yes(acceptedPattern.toString());
            }
        }
        return IsAccepted.no(acceptedPatterns.toString());
    }

    @Override
    public Set<Pattern> getAcceptedPatterns() {
        return acceptedPatterns;
    }

}
