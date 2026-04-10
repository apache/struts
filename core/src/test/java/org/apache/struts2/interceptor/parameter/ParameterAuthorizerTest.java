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

import org.apache.struts2.ModelDriven;
import org.apache.struts2.ognl.DefaultOgnlBeanInfoCacheFactory;
import org.apache.struts2.ognl.DefaultOgnlExpressionCacheFactory;
import org.apache.struts2.ognl.OgnlUtil;
import org.apache.struts2.ognl.StrutsOgnlGuard;
import org.apache.struts2.ognl.StrutsProxyCacheFactory;
import org.apache.struts2.util.StrutsProxyService;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.struts2.ognl.OgnlCacheFactory.CacheType.LRU;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StrutsParameterAuthorizer} — verifies that the extracted authorization logic works correctly
 * without any OGNL ThreadAllowlist side effects.
 */
public class ParameterAuthorizerTest {

    private StrutsParameterAuthorizer authorizer;

    @Before
    public void setUp() {
        authorizer = new StrutsParameterAuthorizer();
        authorizer.setRequireAnnotations(Boolean.TRUE.toString());

        var ognlUtil = new OgnlUtil(
                new DefaultOgnlExpressionCacheFactory<>(String.valueOf(1000), LRU.toString()),
                new DefaultOgnlBeanInfoCacheFactory<>(String.valueOf(1000), LRU.toString()),
                new StrutsOgnlGuard());
        authorizer.setOgnlUtil(ognlUtil);

        var proxyService = new StrutsProxyService(new StrutsProxyCacheFactory<>("1000", "basic"));
        authorizer.setProxyService(proxyService);
    }

    // --- requireAnnotations=false (backward compat) ---

    @Test
    public void requireAnnotationsDisabled_allAuthorized() {
        authorizer.setRequireAnnotations(Boolean.FALSE.toString());
        assertThat(authorizer.isAuthorized("anything", new SecureAction(), new SecureAction())).isTrue();
        assertThat(authorizer.isAuthorized("unannotatedProp", new SecureAction(), new SecureAction())).isTrue();
    }

    // --- Simple property (depth 0) ---

    @Test
    public void annotatedSetter_authorized() {
        var action = new SecureAction();
        assertThat(authorizer.isAuthorized("name", action, action)).isTrue();
    }

    @Test
    public void unannotatedSetter_rejected() {
        var action = new SecureAction();
        assertThat(authorizer.isAuthorized("role", action, action)).isFalse();
    }

    // --- Nested property (depth >= 1) ---

    @Test
    public void annotatedGetterDepthOne_nestedParam_authorized() {
        var action = new SecureAction();
        assertThat(authorizer.isAuthorized("address.city", action, action)).isTrue();
    }

    @Test
    public void annotatedGetterDepthZero_nestedParam_rejected() {
        var action = new SecureAction();
        assertThat(authorizer.isAuthorized("addressShallow.city", action, action)).isFalse();
    }

    @Test
    public void annotatedGetterDepthOne_doubleNested_rejected() {
        var action = new SecureAction();
        assertThat(authorizer.isAuthorized("address.city.zip", action, action)).isFalse();
    }

    // --- Public field ---

    @Test
    public void annotatedPublicField_authorized() {
        var action = new FieldAction();
        assertThat(authorizer.isAuthorized("publicStr", action, action)).isTrue();
    }

    @Test
    public void unannotatedPublicField_rejected() {
        var action = new FieldAction();
        assertThat(authorizer.isAuthorized("publicStrNotAnnotated", action, action)).isFalse();
    }

    // --- ModelDriven exemption ---

    @Test
    public void modelDriven_targetIsModel_allAuthorized() {
        var action = new ModelAction();
        var model = action.getModel();
        // target != action AND action instanceof ModelDriven → model is exempt
        assertThat(authorizer.isAuthorized("anyProperty", model, action)).isTrue();
        assertThat(authorizer.isAuthorized("nested.deep", model, action)).isTrue();
    }

    @Test
    public void nonModelDrivenAction_differentTarget_notExempt() {
        // Regression test: when target != action but action does NOT implement ModelDriven,
        // the target should NOT be exempt from annotation checks.
        var action = new SecureAction();
        var nonActionTarget = new Pojo(); // different object, but action is not ModelDriven
        // Pojo has no @StrutsParameter annotations, so this should be rejected
        assertThat(authorizer.isAuthorized("name", nonActionTarget, action)).isFalse();
    }

    // --- Transition mode ---

    @Test
    public void transitionMode_depthZeroExempt() {
        authorizer.setRequireAnnotationsTransitionMode(Boolean.TRUE.toString());
        var action = new SecureAction();
        // depth-0 unannotated property should be exempt
        assertThat(authorizer.isAuthorized("role", action, action)).isTrue();
    }

    @Test
    public void transitionMode_depthOneNotExempt() {
        authorizer.setRequireAnnotationsTransitionMode(Boolean.TRUE.toString());
        var action = new SecureAction();
        // depth-1 unannotated property should NOT be exempt
        assertThat(authorizer.isAuthorized("unannotatedNested.prop", action, action)).isFalse();
    }

    // --- No matching member ---

    @Test
    public void nonexistentProperty_rejected() {
        var action = new SecureAction();
        assertThat(authorizer.isAuthorized("doesNotExist", action, action)).isFalse();
    }

    // --- Empty/null parameter name ---

    @Test
    public void nullParameterName_rejected() {
        var action = new SecureAction();
        assertThat(authorizer.isAuthorized(null, action, action)).isFalse();
    }

    @Test
    public void emptyParameterName_rejected() {
        var action = new SecureAction();
        assertThat(authorizer.isAuthorized("", action, action)).isFalse();
    }

    @Test
    public void emptyParameterName_rejectedEvenWhenAnnotationsNotRequired() {
        authorizer.setRequireAnnotations(Boolean.FALSE.toString());
        var action = new SecureAction();
        assertThat(authorizer.isAuthorized("", action, action)).isFalse();
        assertThat(authorizer.isAuthorized(null, action, action)).isFalse();
    }

    // --- Inner test classes ---

    public static class SecureAction {
        private String name;
        private String role;
        private Address address;
        private Address addressShallow;

        @StrutsParameter
        public void setName(String name) { this.name = name; }
        public String getName() { return name; }

        // NO @StrutsParameter — must be rejected
        public void setRole(String role) { this.role = role; }
        public String getRole() { return role; }

        @StrutsParameter(depth = 1)
        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }

        @StrutsParameter
        public Address getAddressShallow() { return addressShallow; }
        public void setAddressShallow(Address address) { this.addressShallow = address; }

        // Unannotated getter for nested param test
        public Object getUnannotatedNested() { return null; }
    }

    public static class Address {
        private String city;
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
    }

    public static class FieldAction {
        @StrutsParameter
        public String publicStr;

        public String publicStrNotAnnotated;
    }

    public static class ModelAction implements ModelDriven<Pojo> {
        @Override
        public Pojo getModel() { return new Pojo(); }
    }

    public static class Pojo {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
