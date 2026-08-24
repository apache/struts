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
package org.apache.struts2.components;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EcmaScriptSafeRegexTest {

    @Test
    public void acceptsPortableConstructs() {
        assertThat(EcmaScriptSafeRegex.isSafe("[a-z]+")).isTrue();
        assertThat(EcmaScriptSafeRegex.isSafe("\\d{3}-\\d{4}")).isTrue();
        assertThat(EcmaScriptSafeRegex.isSafe("(foo|bar)?baz")).isTrue();
        assertThat(EcmaScriptSafeRegex.isSafe("^\\w+@\\w+\\.\\w{2,6}$")).isTrue();
        assertThat(EcmaScriptSafeRegex.isSafe("(?:ab)+")).isTrue();
        assertThat(EcmaScriptSafeRegex.isSafe("a(?=b)")).isTrue();
        assertThat(EcmaScriptSafeRegex.isSafe("a(?!b)")).isTrue();
    }

    @Test
    public void rejectsJavaOnlyEscapes() {
        assertThat(EcmaScriptSafeRegex.isSafe("\\p{Alpha}+")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("\\A\\d+\\z")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("\\Qliteral\\E")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("\\h+")).isFalse();
    }

    @Test
    public void rejectsPossessiveQuantifiers() {
        assertThat(EcmaScriptSafeRegex.isSafe("\\d++")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("a*+")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("a?+")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("a{2,3}+")).isFalse();
    }

    @Test
    public void rejectsNonPortableGroups() {
        assertThat(EcmaScriptSafeRegex.isSafe("(?<name>a)")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("(?<=a)b")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("(?>a)")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("(?i)abc")).isFalse();
    }

    @Test
    public void rejectsJavaCharacterClassFeatures() {
        assertThat(EcmaScriptSafeRegex.isSafe("[[:alpha:]]")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("[a-z&&[^aeiou]]")).isFalse();
    }

    @Test
    public void rejectsUnusableInput() {
        assertThat(EcmaScriptSafeRegex.isSafe(null)).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("abc\\")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("[abc")).isFalse();
    }
}
