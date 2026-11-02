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
package org.apache.struts2.dispatcher.mapper;

import junit.framework.TestCase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ActionExtensionsTest extends TestCase {

    public void testParseKeepsOrder() {
        assertThat(ActionExtensions.parse("html,action")).isEqualTo(List.of("html", "action"));
    }

    public void testParseTrailingCommaAddsEmpty() {
        assertThat(ActionExtensions.parse("action,,")).isEqualTo(List.of("action", ""));
    }

    public void testParseEmptyMeansNoExtensionOnly() {
        assertThat(ActionExtensions.parse("")).isEqualTo(List.of(""));
    }

    public void testParseNullIsEmpty() {
        assertThat(ActionExtensions.parse(null)).isEmpty();
    }

    public void testAllowsNullAsEmpty() {
        assertThat(ActionExtensions.allows(List.of("html", ""), null)).isTrue();
        assertThat(ActionExtensions.allows(List.of("html"), null)).isFalse();
    }

    public void testSelectKeepsAllowedCurrent() {
        assertThat(ActionExtensions.select(List.of("action", "html"), "html")).isEqualTo("html");
    }

    public void testSelectFallsBackToFirst() {
        assertThat(ActionExtensions.select(List.of("action", ""), "html")).isEqualTo("action");
    }
}
