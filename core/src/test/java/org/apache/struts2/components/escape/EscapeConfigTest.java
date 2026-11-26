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
package org.apache.struts2.components.escape;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EscapeConfigTest {

    @Test
    public void builderDefaultsAllFlagsToFalse() {
        EscapeConfig config = EscapeConfig.builder(TagOrigin.PROPERTY).build();

        assertThat(config.origin()).isEqualTo(TagOrigin.PROPERTY);
        assertThat(config.html()).isFalse();
        assertThat(config.javaScript()).isFalse();
        assertThat(config.xml()).isFalse();
        assertThat(config.csv()).isFalse();
    }

    @Test
    public void builderSetsEachFlag() {
        assertThat(EscapeConfig.builder(TagOrigin.TEXT).html(true).build().html()).isTrue();
        assertThat(EscapeConfig.builder(TagOrigin.TEXT).javaScript(true).build().javaScript()).isTrue();
        assertThat(EscapeConfig.builder(TagOrigin.TEXT).xml(true).build().xml()).isTrue();
        assertThat(EscapeConfig.builder(TagOrigin.TEXT).csv(true).build().csv()).isTrue();
    }

    @Test
    public void equalConfigsAreEqual() {
        EscapeConfig first = EscapeConfig.builder(TagOrigin.TEXT).html(true).xml(true).build();
        EscapeConfig second = EscapeConfig.builder(TagOrigin.TEXT).html(true).xml(true).build();

        assertThat(first).isEqualTo(second).hasSameHashCodeAs(second);
        assertThat(first).isNotEqualTo(EscapeConfig.builder(TagOrigin.PROPERTY).html(true).xml(true).build());
    }
}
