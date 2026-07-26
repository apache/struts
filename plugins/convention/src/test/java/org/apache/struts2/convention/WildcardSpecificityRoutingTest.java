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
package org.apache.struts2.convention;

import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.entities.PackageConfig;
import org.apache.struts2.config.impl.ActionConfigMatcher;
import org.apache.struts2.util.WildcardHelper;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * End-to-end proof that specificity ordering fixes the WW-3784 shadowing bug: it drives the
 * production reorder ({@link PackageConfig.Builder#reorderActionConfigs} with
 * {@link ActionNameSpecificityComparator}) through the real runtime matcher
 * ({@link ActionConfigMatcher} over {@link WildcardHelper}) and asserts that a request which two
 * genuinely-overlapping wildcard patterns can both match is routed to the more specific action.
 *
 * <p>{@code some/**} and {@code some/usefull/*} genuinely overlap for {@code some/usefull/sleeping}
 * because {@code **} ({@code MATCH_PATH}) crosses '/', so first-match-wins order decides the winner.
 * With the general {@code some/**} registered first, it shadows {@code some/usefull/*}; after
 * specificity ordering, the specific pattern wins.</p>
 */
public class WildcardSpecificityRoutingTest {

    private static final String GENERAL_PATTERN = "some/**";
    private static final String SPECIFIC_PATTERN = "some/usefull/*";
    private static final String GENERAL_CLASS = "com.example.GeneralAction";
    private static final String SPECIFIC_CLASS = "com.example.SpecificAction";
    private static final String OVERLAPPING_REQUEST = "some/usefull/sleeping";
    private static final String GENERAL_ONLY_REQUEST = "some/eating";

    @Test
    public void generalPatternShadowsSpecificWhenRegisteredFirst() {
        // Reproduces the bug: general-before-specific insertion order, no reordering applied.
        Map<String, ActionConfig> configs = generalFirstBuilder().build().getActionConfigs();
        ActionConfigMatcher matcher = new ActionConfigMatcher(new WildcardHelper(), configs, false);

        ActionConfig matched = matcher.match(OVERLAPPING_REQUEST);

        assertEquals("general some/** shadows the specific action when registered first",
                GENERAL_CLASS, matched.getClassName());
    }

    @Test
    public void specificPatternWinsAfterSpecificityOrdering() {
        // Same actions, same insertion order, but reordered most-specific-first before matching.
        PackageConfig.Builder builder = generalFirstBuilder();
        builder.reorderActionConfigs(new ActionNameSpecificityComparator());
        Map<String, ActionConfig> configs = builder.build().getActionConfigs();
        ActionConfigMatcher matcher = new ActionConfigMatcher(new WildcardHelper(), configs, false);

        assertEquals("specific some/usefull/* is now reachable for the overlapping request",
                SPECIFIC_CLASS, matcher.match(OVERLAPPING_REQUEST).getClassName());
        assertEquals("general some/** still handles requests only it can match",
                GENERAL_CLASS, matcher.match(GENERAL_ONLY_REQUEST).getClassName());
    }

    private PackageConfig.Builder generalFirstBuilder() {
        PackageConfig.Builder builder = new PackageConfig.Builder("test");
        builder.addActionConfig(GENERAL_PATTERN, action(GENERAL_PATTERN, GENERAL_CLASS));
        builder.addActionConfig(SPECIFIC_PATTERN, action(SPECIFIC_PATTERN, SPECIFIC_CLASS));
        return builder;
    }

    private ActionConfig action(String name, String className) {
        return new ActionConfig.Builder("test", name, className)
                .methodName("execute")
                .setStrictMethodInvocation(false)
                .build();
    }
}
