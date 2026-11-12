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
package org.apache.struts2.config.impl;

import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.entities.ExceptionMappingConfig;
import org.apache.struts2.config.entities.ResultConfig;
import org.apache.struts2.util.WildcardHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WildcardClassNameHardeningTest extends XWorkTestCase {

    private ActionConfigMatcher matcherFor(ActionConfig config) {
        Map<String, ActionConfig> map = new HashMap<>();
        map.put(config.getName(), config);
        return new ActionConfigMatcher(new WildcardHelper(), map, false);
    }

    public void testActionClassNameRejectsSegmentWithDot() {
        ActionConfigMatcher matcher = matcherFor(new ActionConfig.Builder("package", "*", "actions.{1}Action")
                .methodName("execute")
                .build());

        assertNull(matcher.match("util.Harmless"));
        assertEquals("actions.UserAction", matcher.match("User").getClassName());
    }

    public void testResultClassNameRejectsSegmentWithDot() {
        ActionConfigMatcher matcher = matcherFor(new ActionConfig.Builder("package", "*", "actions.FixedAction")
                .addResultConfig(new ResultConfig.Builder("success", "results.{1}Result").build())
                .build());

        assertNull(matcher.match("util.Harmless"));
        assertEquals("results.UserResult", matcher.match("User").getResults().get("success").getClassName());
    }

    public void testExceptionMappingClassNameRejectsSegmentWithDot() {
        ActionConfigMatcher matcher = matcherFor(new ActionConfig.Builder("package", "*", "actions.FixedAction")
                .addExceptionMapping(new ExceptionMappingConfig.Builder("ex", "errors.{1}Exception", "error").build())
                .build());

        assertNull(matcher.match("util.Harmless"));
        assertEquals("errors.UserException", matcher.match("User").getExceptionMappings().get(0).getExceptionClassName());
    }

    public void testClassNameAcceptsNonAsciiIdentifierAndDollar() {
        ActionConfigMatcher matcher = matcherFor(new ActionConfig.Builder("package", "*", "actions.{1}Action")
                .build());

        assertEquals("actions.ÜserAction", matcher.match("Üser").getClassName());
        assertEquals("actions.Inner$1Action", matcher.match("Inner$1").getClassName());
    }

    public void testDollarAndBackslashAreSubstitutedLiterally() {
        ActionConfigMatcher matcher = matcherFor(new ActionConfig.Builder("package", "*", "actions.FixedAction")
                .addParam("value", "{1}")
                .build());

        assertEquals("a$1b", matcher.match("a$1b").getParams().get("value"));
        assertEquals("a\\b", matcher.match("a\\b").getParams().get("value"));
    }

    public void testMethodNameWildcardIsGated() {
        ActionConfigMatcher matcher = matcherFor(new ActionConfig.Builder("package", "run/*", "actions.RunAction")
                .methodName("{1}")
                .addAllowedMethod(Set.of("execute"))
                .setStrictMethodInvocation(true)
                .build());

        assertNull(matcher.match("run/deleteAll"));
        assertEquals("execute", matcher.match("run/execute").getMethodName());
    }
}
