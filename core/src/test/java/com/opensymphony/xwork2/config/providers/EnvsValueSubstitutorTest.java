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

import org.apache.struts2.StrutsInternalTestCase;


public class EnvsValueSubstitutorTest extends StrutsInternalTestCase {

    public void testSimpleValue() throws Exception {
        // given
        String expected = System.getenv("USER");
        ValueSubstitutor substitutor = new EnvsValueSubstitutor();

        // when
        String actual = substitutor.substitute("${env.USER}");

        // then
        assertEquals(expected, actual);
    }

    public void testNoSubstitution() throws Exception {
        // given
        ValueSubstitutor substitutor = new EnvsValueSubstitutor();

        // when
        String actual = substitutor.substitute("val1");

        // then
        assertEquals("val1", actual);
    }
}