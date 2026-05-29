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
package org.apache.struts2.interceptor.parameter;

import org.junit.After;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParameterAuthorizationContextTest {

    @After
    public void tearDown() {
        ParameterAuthorizationContext.unbind();
    }

    @Test
    public void notActive_byDefault() {
        assertThat(ParameterAuthorizationContext.isActive()).isFalse();
    }

    @Test
    public void bind_thenActive() {
        ParameterAuthorizer authorizer = (n, t, a) -> true;
        Object action = new Object();
        ParameterAuthorizationContext.bind(authorizer, action, action);
        assertThat(ParameterAuthorizationContext.isActive()).isTrue();
    }

    @Test
    public void unbind_clearsState() {
        ParameterAuthorizer authorizer = (n, t, a) -> true;
        Object action = new Object();
        ParameterAuthorizationContext.bind(authorizer, action, action);
        ParameterAuthorizationContext.unbind();
        assertThat(ParameterAuthorizationContext.isActive()).isFalse();
    }

    @Test
    public void isAuthorized_delegatesToBoundAuthorizer() {
        Object action = new Object();
        ParameterAuthorizationContext.bind((n, t, a) -> "name".equals(n), action, action);
        assertThat(ParameterAuthorizationContext.isAuthorized("name")).isTrue();
        assertThat(ParameterAuthorizationContext.isAuthorized("role")).isFalse();
    }

    @Test
    public void isAuthorized_returnsTrue_whenNotActive() {
        // Defensive default: no context bound = no enforcement
        assertThat(ParameterAuthorizationContext.isAuthorized("anything")).isTrue();
    }

    @Test
    public void pathStack_emptyByDefault() {
        assertThat(ParameterAuthorizationContext.currentPathPrefix()).isEmpty();
    }

    @Test
    public void pushPath_buildsPrefix() {
        ParameterAuthorizationContext.pushPath("address");
        assertThat(ParameterAuthorizationContext.currentPathPrefix()).isEqualTo("address");
        ParameterAuthorizationContext.pushPath("address.city");
        assertThat(ParameterAuthorizationContext.currentPathPrefix()).isEqualTo("address.city");
    }

    @Test
    public void popPath_unwinds() {
        ParameterAuthorizationContext.pushPath("address");
        ParameterAuthorizationContext.pushPath("address.city");
        ParameterAuthorizationContext.popPath();
        assertThat(ParameterAuthorizationContext.currentPathPrefix()).isEqualTo("address");
        ParameterAuthorizationContext.popPath();
        assertThat(ParameterAuthorizationContext.currentPathPrefix()).isEmpty();
    }

    @Test
    public void pathFor_concatenatesPropertyName() {
        assertThat(ParameterAuthorizationContext.pathFor("name")).isEqualTo("name");
        ParameterAuthorizationContext.pushPath("address");
        assertThat(ParameterAuthorizationContext.pathFor("city")).isEqualTo("address.city");
    }

    @Test
    public void unbind_clearsPathStack() {
        ParameterAuthorizationContext.bind((n, t, a) -> true, new Object(), new Object());
        ParameterAuthorizationContext.pushPath("address");
        ParameterAuthorizationContext.unbind();
        assertThat(ParameterAuthorizationContext.currentPathPrefix()).isEmpty();
    }

    @Test
    public void bind_replacesPriorState_doesNotResetPathStack() {
        Object firstAction = new Object();
        Object secondAction = new Object();
        ParameterAuthorizationContext.bind((n, t, a) -> "first".equals(n), firstAction, firstAction);
        ParameterAuthorizationContext.pushPath("address");
        // Rebind with a different authorizer
        ParameterAuthorizationContext.bind((n, t, a) -> "second".equals(n), secondAction, secondAction);
        // New authorizer in effect
        assertThat(ParameterAuthorizationContext.isAuthorized("first")).isFalse();
        assertThat(ParameterAuthorizationContext.isAuthorized("second")).isTrue();
        // Path stack is preserved across rebind (it's a separate ThreadLocal)
        assertThat(ParameterAuthorizationContext.currentPathPrefix()).isEqualTo("address");
    }

    @Test
    public void unbind_whenNeverBound_isSafeNoOp() {
        // Should not throw; isActive should remain false
        ParameterAuthorizationContext.unbind();
        assertThat(ParameterAuthorizationContext.isActive()).isFalse();
        assertThat(ParameterAuthorizationContext.currentPathPrefix()).isEmpty();
    }
}
