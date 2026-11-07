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
import org.apache.struts2.util.WildcardHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * The wildcard "{n}" substitution that fills in a
 * {@code class=} (and method/package) template is unconfined -- any matched path
 * segment, including one containing '.' or '/'-decoded dots, lands verbatim in the
 * resulting class name. A sibling slot (the method name) IS gated, by
 * {@link ActionConfig#isAllowedMethod(String)} at ActionConfigMatcher.java:113. There is
 * no equivalent gate for the class name at ActionConfigMatcher.java:117 /
 * AbstractMatcher.convertParam():201-215.
 *
 * Benign probe only: the "attacker" value is a harmless literal package/class suffix
 * ("util.Harmless"), not a real sensitive class, and no class is actually loaded here --
 * this test only proves the ActionConfig the matcher hands back contains the attacker's
 * wildcard segment unconfined in getClassName(), which is what a default ObjectFactory
 * would later pass to Class.forName/buildBean with no further check
 * (DefaultActionFactory.buildAction -> objectFactory.buildBean(config.getClassName(), ...)).
 */
public class WildcardClassNameHardeningTest extends XWorkTestCase {

    public void testClassNameWildcardRejectsNonIdentifierSegment() {
        Map<String, ActionConfig> map = new HashMap<>();
        // A typical CRUD-style wildcard mapping a developer might write:
        // <action name="*" class="actions.{1}Action"> -- intended for simple names
        // like "user" or "order", never audited against the request path.
        map.put("*", new ActionConfig.Builder("package", "*", "actions.{1}Action")
                .methodName("execute")
                .build());

        ActionConfigMatcher matcher = new ActionConfigMatcher(new WildcardHelper(), map, false);

        // Benign "attacker" path segment: contains '.' so it walks the class name into a
        // different package than the template author intended. No real/dangerous class
        // name is used -- this is a structural probe, not an exploit payload.
        ActionConfig matched = matcher.match("util.Harmless");

        assertNotNull("ActionConfig should be matched", matched);
        // GREEN (post-fix): the '.' in the matched segment is not a safe class-name
        // character, so the substitution is dropped instead of walking the class out of
        // the mapped package -- the attacker-controlled segment no longer changes the
        // resolved package.
        assertEquals("Class-name wildcard substitution must reject non-identifier characters",
                "actions.Action", matched.getClassName());

        // Benign identifier-only segment still substitutes normally.
        ActionConfig normal = matcher.match("User");
        assertEquals("actions.UserAction", normal.getClassName());
    }

    public void testMethodNameWildcardIsGated() {
        // Control: the sibling slot (method name) IS gated by isAllowedMethod, so a
        // non-allowed substituted method value causes convert() to return null instead
        // of an ActionConfig that would invoke an arbitrary method.
        Map<String, ActionConfig> map = new HashMap<>();
        map.put("run/*", new ActionConfig.Builder("package", "run/*", "actions.RunAction")
                .methodName("{1}")
                .addAllowedMethod(java.util.Set.of("execute"))
                .setStrictMethodInvocation(true)
                .build());

        ActionConfigMatcher matcher = new ActionConfigMatcher(new WildcardHelper(), map, false);

        ActionConfig notAllowed = matcher.match("run/deleteAll");
        assertNull("GREEN control: method substitution is gated by isAllowedMethod", notAllowed);

        ActionConfig allowed = matcher.match("run/execute");
        assertNotNull(allowed);
        assertEquals("execute", allowed.getMethodName());
    }
}
