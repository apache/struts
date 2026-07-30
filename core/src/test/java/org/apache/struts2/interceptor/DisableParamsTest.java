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
package org.apache.struts2.interceptor;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;

public class DisableParamsTest extends TestCase {

    public void testDisabledDefaultsToFalse() {
        assertThat(new DisableParams().isDisabled()).isFalse();
    }

    public void testSetDisabledParsesStringValue() {
        DisableParams params = new DisableParams();
        params.setDisabled("true");
        assertThat(params.isDisabled()).isTrue();

        params.setDisabled("false");
        assertThat(params.isDisabled()).isFalse();
    }

    public void testSetDisabledTreatsNonBooleanTextAsFalse() {
        DisableParams params = new DisableParams();
        params.setDisabled("yes");
        assertThat(params.isDisabled()).isFalse();
    }

    public void testCopyConstructorCarriesDisabledFlag() {
        DisableParams original = new DisableParams();
        original.setDisabled("true");

        DisableParams copy = new DisableParams(original);

        assertThat(copy.isDisabled()).isTrue();
    }

    public void testUnresolvedDefaultsToNoOp() {
        DisableParams params = new DisableParams();
        params.unresolved("someParam");   // must not throw
        assertThat(params.isDisabled()).isFalse();
    }
}
