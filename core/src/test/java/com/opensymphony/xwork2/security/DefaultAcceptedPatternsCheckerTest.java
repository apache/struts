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

import com.opensymphony.xwork2.XWorkTestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class DefaultAcceptedPatternsCheckerTest extends XWorkTestCase {

    private final List<String> params = new ArrayList<String>() {
        {
            add("%{#application['test']}");
            add("%{#application.test}");
            add("%{#Application['test']}");
            add("%{#Application.test}");
            add("%{#session['test']}");
            add("%{#session.test}");
            add("%{#Session['test']}");
            add("%{#Session.test}");
            add("%{#struts['test']}");
            add("%{#struts.test}");
            add("%{#Struts['test']}");
            add("%{#Struts.test}");
            add("%{#request['test']}");
            add("%{#request.test}");
            add("%{#Request['test']}");
            add("%{#Request.test}");
            add("%{#servletRequest['test']}");
            add("%{#servletRequest.test}");
            add("%{#ServletRequest['test']}");
            add("%{#ServletRequest.test}");
            add("%{#servletResponse['test']}");
            add("%{#servletResponse.test}");
            add("%{#ServletResponse['test']}");
            add("%{#ServletResponse.test}");
            add("%{#parameters['test']}");
            add("%{#parameters.test}");
            add("%{#Parameters['test']}");
            add("%{#Parameters.test}");
            add("%{#Parameters['test-1']}");
        }
    };

    public void testHardcodedAcceptedPatterns() {
        // given
        AcceptedPatternsChecker checker = new DefaultAcceptedPatternsChecker();

        for (String param : params) {
            // when
            AcceptedPatternsChecker.IsAccepted actual = checker.isAccepted(param);

            // then
            assertFalse("Access to " + param + " is possible!", actual.isAccepted());
        }
    }

    public void testHardcodedAcceptedPatternsWithDmiEnabled() {
        // given
        AcceptedPatternsChecker checker = new DefaultAcceptedPatternsChecker(Boolean.TRUE.toString());

        for (String param : params) {
            // when
            AcceptedPatternsChecker.IsAccepted actual = checker.isAccepted(param);

            // then
            assertFalse("Access to " + param + " is possible!", actual.isAccepted());
        }
    }

    public void testUnderscoreInParamName() {
        // given
        AcceptedPatternsChecker checker = new DefaultAcceptedPatternsChecker();

        // when
        AcceptedPatternsChecker.IsAccepted actual = checker.isAccepted("mapParam['param_1']");

        // then
        assertTrue("Param with underscore wasn't accepted!", actual.isAccepted());
    }

    public void testDashInParamName() {
        // given
        AcceptedPatternsChecker checker = new DefaultAcceptedPatternsChecker();

        // when
        AcceptedPatternsChecker.IsAccepted actual = checker.isAccepted("mapParam['param-1']");

        // then
        assertTrue("Param with dash wasn't accepted!", actual.isAccepted());

        // when
        actual = checker.isAccepted("mapParam['-param-1']");

        // then
        assertFalse("Param with dash was accepted!", actual.isAccepted());

        // when
        actual = checker.isAccepted("-param");

        // then
        assertFalse("Param with dash was accepted!", actual.isAccepted());

        // when
        actual = checker.isAccepted("param1-param2");

        // then
        assertFalse("Param with dash was accepted!", actual.isAccepted());
    }

    public void testUnderscoreInParamNameWithDmiEnabled() {
        // given
        AcceptedPatternsChecker checker = new DefaultAcceptedPatternsChecker(Boolean.TRUE.toString());

        // when
        AcceptedPatternsChecker.IsAccepted actual = checker.isAccepted("mapParam['param_1']");

        // then
        assertTrue("Param with underscore wasn't accepted!", actual.isAccepted());
    }

    public void testAcceptedPatternsImmutable() {
        AcceptedPatternsChecker checker = new DefaultAcceptedPatternsChecker();

        Set<Pattern> acceptedPatternSet = checker.getAcceptedPatterns();
        assertNotNull("default accepted patterns null?", acceptedPatternSet);
        assertFalse("default accepted patterns empty?", acceptedPatternSet.isEmpty());
        try {
            acceptedPatternSet.add(Pattern.compile("SomeRegexPattern"));
            fail("accepted patterns modifiable?");
        } catch (UnsupportedOperationException uoe) {
            // Expected result
        }
        try {
            acceptedPatternSet.clear();
            fail("accepted patterns modifiable?");
        } catch (UnsupportedOperationException uoe) {
            // Expected result
        }

        checker.setAcceptedPatterns(DefaultAcceptedPatternsChecker.ACCEPTED_PATTERNS);
        acceptedPatternSet = checker.getAcceptedPatterns();
        assertNotNull("replaced default accepted patterns null?", acceptedPatternSet);
        assertFalse("replaced default accepted patterns empty?", acceptedPatternSet.isEmpty());
        try {
            acceptedPatternSet.add(Pattern.compile("SomeRegexPattern"));
            fail("replaced accepted patterns modifiable?");
        } catch (UnsupportedOperationException uoe) {
            // Expected result
        }
        try {
            acceptedPatternSet.clear();
            fail("accepted patterns modifiable?");
        } catch (UnsupportedOperationException uoe) {
            // Expected result
        }

        String[] testPatternArray = {"exactmatch1", "exactmatch2", "exactmatch3", "exactmatch4"};
        checker.setAcceptedPatterns(testPatternArray);
        acceptedPatternSet = checker.getAcceptedPatterns();
        assertNotNull("replaced default accepted patterns null?", acceptedPatternSet);
        assertFalse("replaced default accepted patterns empty?", acceptedPatternSet.isEmpty());
        assertEquals("replaced default accepted patterns not size " + testPatternArray.length + "?", acceptedPatternSet.size(), testPatternArray.length);
        for (String testPatternArray1 : testPatternArray) {
            assertTrue(testPatternArray1 + " not accepted?", checker.isAccepted(testPatternArray1).isAccepted());
        }
        try {
            acceptedPatternSet.add(Pattern.compile("SomeRegexPattern"));
            fail("replaced accepted patterns modifiable?");
        } catch (UnsupportedOperationException uoe) {
            // Expected result
        }
        try {
            acceptedPatternSet.clear();
            fail("accepted patterns modifiable?");
        } catch (UnsupportedOperationException uoe) {
            // Expected result
        }
    }

    public void testDmiIsEnabled() {
        DefaultAcceptedPatternsChecker checker = new DefaultAcceptedPatternsChecker(Boolean.TRUE.toString());

        AcceptedPatternsChecker.IsAccepted accepted = checker.isAccepted("action:myAction!save");

        assertTrue("dmi isn't accepted", accepted.isAccepted());
    }

    public void testDmiIsEnabledAndDash() {
        // given
        DefaultAcceptedPatternsChecker checker = new DefaultAcceptedPatternsChecker(Boolean.TRUE.toString());

        // when
        AcceptedPatternsChecker.IsAccepted accepted = checker.isAccepted("map['param-1']");

        // then
        assertTrue("Dash isn't accepted", accepted.isAccepted());

        // when
        accepted = checker.isAccepted("map['-param-1']");

        // then
        assertFalse("Dash was accepted", accepted.isAccepted());

        // when
        accepted = checker.isAccepted("-param");

        // then
        assertFalse("Dash was accepted", accepted.isAccepted());

        // when
        accepted = checker.isAccepted("param1-param2");

        // then
        assertFalse("Dash was accepted", accepted.isAccepted());
    }


    public static final AcceptedPatternsChecker ACCEPT_ALL_PATTERNS_CHECKER = new AcceptedPatternsChecker() {
        @Override
        public IsAccepted isAccepted(String value) {
            return IsAccepted.yes(".*");
        }

        @Override
        public void setAcceptedPatterns(String commaDelimitedPatterns) {

        }

        @Override
        public void setAcceptedPatterns(String[] patterns) {

        }

        @Override
        public void setAcceptedPatterns(Set<String> patterns) {

        }

        @Override
        public Set<Pattern> getAcceptedPatterns() {
            return null;
        }
    };
}
