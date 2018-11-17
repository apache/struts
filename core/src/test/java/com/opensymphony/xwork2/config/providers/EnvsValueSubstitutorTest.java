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
package com.opensymphony.xwork2.config.providers;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang3.SystemUtils;
import org.apache.struts2.StrutsInternalTestCase;

public class EnvsValueSubstitutorTest extends StrutsInternalTestCase {

    private ValueSubstitutor substitutor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        substitutor = new EnvsValueSubstitutor();
    }

    public void testEnvSimpleValue() {
        if (SystemUtils.IS_OS_WINDOWS) {
            assertThat(substitutor.substitute("${env.USERNAME}"), is(System.getenv("USERNAME")));
        } else {
            assertThat(substitutor.substitute("${env.USER}"), is(System.getenv("USER")));
        }
    }

    public void testEnvSimpleDefaultValue() {
        final String defaultValue = "defaultValue";
        assertThat(substitutor.substitute("${env.UNKNOWN:" + defaultValue + "}"), is(defaultValue));
    }

    public void testSystemSimpleValue() {
        final String key = "sysPropKey";
        final String value = "sysPropValue";
        System.setProperty(key, value);

        assertThat(substitutor.substitute("${" + key + "}"), is(value));
    }

    public void testSystemSimpleDefaultValue() {
        final String defaultValue = "defaultValue";
        assertThat(substitutor.substitute("${UNKNOWN:" + defaultValue + "}"), is(defaultValue));
    }

    public void testNoSubstitution() {
        final String value = "val1";
        assertThat(substitutor.substitute(value), is(value));
    }
}
