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

import com.opensymphony.xwork2.inject.Inject;

import java.util.Set;
import java.util.regex.Pattern;

public class DefaultNotExcludedAcceptedPatternsChecker implements NotExcludedAcceptedPatternsChecker {
    private ExcludedPatternsChecker excludedPatterns;
    private AcceptedPatternsChecker acceptedPatterns;


    @Inject
    public void setExcludedPatterns(ExcludedPatternsChecker excludedPatterns) {
        this.excludedPatterns = excludedPatterns;
    }

    @Inject
    public void setAcceptedPatterns(AcceptedPatternsChecker acceptedPatterns) {
        this.acceptedPatterns = acceptedPatterns;
    }

    @Override
    public IsAllowed isAllowed(String value) {
        IsExcluded isExcluded = isExcluded(value);
        if (isExcluded.isExcluded()) {
            return IsAllowed.no(isExcluded.getExcludedPattern());
        }

        IsAccepted isAccepted = isAccepted(value);
        if (!isAccepted.isAccepted()) {
            return IsAllowed.no(isAccepted.getAcceptedPattern());
        }

        return IsAllowed.yes(isAccepted.getAcceptedPattern());
    }

    @Override
    public IsAccepted isAccepted(String value) {
        return acceptedPatterns.isAccepted(value);
    }

    @Override
    public void setAcceptedPatterns(String commaDelimitedPatterns) {
        acceptedPatterns.setAcceptedPatterns(commaDelimitedPatterns);
    }

    @Override
    public void setAcceptedPatterns(String[] patterns) {
        acceptedPatterns.setAcceptedPatterns(patterns);
    }

    @Override
    public void setAcceptedPatterns(Set<String> patterns) {
        acceptedPatterns.setAcceptedPatterns(patterns);
    }

    @Override
    public Set<Pattern> getAcceptedPatterns() {
        return acceptedPatterns.getAcceptedPatterns();
    }

    @Override
    public IsExcluded isExcluded(String value) {
        return excludedPatterns.isExcluded(value);
    }

    @Override
    public void setExcludedPatterns(String commaDelimitedPatterns) {
        excludedPatterns.setExcludedPatterns(commaDelimitedPatterns);
    }

    @Override
    public void setExcludedPatterns(String[] patterns) {
        excludedPatterns.setExcludedPatterns(patterns);
    }

    @Override
    public void setExcludedPatterns(Set<String> patterns) {
        excludedPatterns.setExcludedPatterns(patterns);
    }

    @Override
    public Set<Pattern> getExcludedPatterns() {
        return excludedPatterns.getExcludedPatterns();
    }
}
