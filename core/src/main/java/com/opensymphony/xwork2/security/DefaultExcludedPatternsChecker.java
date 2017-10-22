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
package com.opensymphony.xwork2.security;

import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class DefaultExcludedPatternsChecker implements ExcludedPatternsChecker {

    private static final Logger LOG = LogManager.getLogger(DefaultExcludedPatternsChecker.class);

    public static final String[] EXCLUDED_PATTERNS = {
        "(^|\\%\\{)((#?)(top(\\.|\\['|\\[\")|\\[\\d\\]\\.)?)(dojo|struts|session|request|response|application|servlet(Request|Response|Context)|parameters|context|_memberAccess)(\\.|\\[).*",
        ".*(^|\\.|\\[|\\'|\"|get)class(\\(\\.|\\[|\\'|\").*"
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

    @Inject(StrutsConstants.STRUTS_ENABLE_DYNAMIC_METHOD_INVOCATION)
    public void setDynamicMethodInvocation(String dmiValue) {
        if (!BooleanUtils.toBoolean(dmiValue)) {
            LOG.debug("DMI is disabled, adding DMI related excluded patterns");
            setAdditionalExcludePatterns("^(action|method):.*");
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
