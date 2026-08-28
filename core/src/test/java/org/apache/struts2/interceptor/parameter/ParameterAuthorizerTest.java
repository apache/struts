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

import org.apache.struts2.ActionContext;
import org.apache.struts2.ModelDriven;
import org.apache.struts2.StubValueStack;
import org.apache.struts2.ognl.DefaultOgnlBeanInfoCacheFactory;
import org.apache.struts2.ognl.DefaultOgnlExpressionCacheFactory;
import org.apache.struts2.ognl.OgnlUtil;
import org.apache.struts2.ognl.StrutsOgnlGuard;
import org.apache.struts2.ognl.StrutsProxyCacheFactory;
import org.apache.struts2.util.StrutsProxyService;
import org.junit.After;
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

    @After
    public void tearDown() {
        ActionContext.clear();
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
    public void modelDriven_unannotatedActionMember_rejected() {
        // The exemption covers the model, which is declared request surface by getModel().
        // It must not reach members declared on the action itself.
        var action = new ModelActionWithOwnMembers();
        assertThat(authorizer.isAuthorized("actionSecret", action.getModel(), action)).isFalse();
    }

    @Test
    public void modelDriven_annotatedActionMember_authorized() {
        var action = new ModelActionWithOwnMembers();
        assertThat(authorizer.isAuthorized("actionAllowed", action.getModel(), action)).isTrue();
    }

    @Test
    public void modelDriven_modelProperty_stillAuthorizedWithoutAnnotation() {
        // The whole point of the exemption: model properties need no annotation.
        var action = new ModelActionWithOwnMembers();
        assertThat(authorizer.isAuthorized("name", action.getModel(), action)).isTrue();
    }

    @Test
    public void modelDriven_propertyOnNeitherModelNorAction_authorized() {
        // A model bound through a custom OGNL property accessor (e.g. a Map-backed model) declares no
        // bean property, and such a name cannot be reaching a member of the action either.
        var action = new ModelActionWithOwnMembers();
        assertThat(authorizer.isAuthorized("noSuchPropertyAnywhere", action.getModel(), action)).isTrue();
    }

    @Test
    public void modelDriven_modelPropertyShadowingUnannotatedActionProperty_authorized() {
        // Declared on both. OGNL resolves against the stack top, which is the model, so the model's
        // property wins and needs no annotation even though the action's namesake is unannotated.
        var action = new ModelActionWithOwnMembers();
        assertThat(authorizer.isAuthorized("shared", action.getModel(), action)).isTrue();
    }

    @Test
    public void transitionMode_modelDrivenUnannotatedActionMember_exempt() {
        // Transition mode exists so an application can turn requireAnnotations on while it works
        // through annotating. It must reach ModelDriven actions too, or the actions affected by
        // scoping the exemption have no migration path.
        authorizer.setRequireAnnotationsTransitionMode(Boolean.TRUE.toString());
        var action = new ModelActionWithOwnMembers();
        assertThat(authorizer.isAuthorized("actionSecret", action.getModel(), action)).isTrue();
    }

    @Test
    public void modelDriven_readOnlyModelPropertyShadowingUnannotatedActionSetter_rejected() {
        // Verified against a real value stack: with the model on top and only a getter for "shadow",
        // OGNL cannot assign to the model and moves on to the action, whose unannotated setter takes
        // the value. Exempting on the name alone would therefore expose the action's own member.
        var action = new ModelActionWithReadOnlyModelProperty();
        assertThat(authorizer.isAuthorized("shadow", action.getModel(), action)).isFalse();
    }

    @Test
    public void modelDriven_readOnlyModelProperty_stillAuthorizedForNestedParameter() {
        // A getter is all a nested parameter needs of the root property: OGNL reads "shadow" from the
        // model and assigns further in. The model does absorb this one, so the exemption still applies.
        var action = new ModelActionWithReadOnlyModelProperty();
        assertThat(authorizer.isAuthorized("shadow.anything", action.getModel(), action)).isTrue();
    }

    @Test
    public void modelDriven_inheritedPublicFieldOnAction_rejected() {
        // OGNL sets inherited public fields as readily as declared ones, so a field the action inherits
        // is still the action's own member and still needs the annotation.
        var action = new ModelActionInheritingPublicField();
        assertThat(authorizer.isAuthorized("inheritedSecret", action.getModel(), action)).isFalse();
    }

    @Test
    public void modelDriven_inheritedPublicFieldOnModel_authorized() {
        // The mirror case: a public field the model inherits is model surface like any other.
        var action = new ModelActionWithInheritingModel();
        assertThat(authorizer.isAuthorized("inheritedModelField", action.getModel(), action)).isTrue();
    }

    @Test
    public void modelDriven_staticFieldNamesakeOfUnannotatedActionProperty_rejected() {
        // A constant is not per-instance request surface and cannot absorb the parameter, so it must not
        // stand in for the model the way a real field would.
        var action = new ModelActionWithConstantNamesake();
        assertThat(authorizer.isAuthorized("constant", action.getModel(), action)).isFalse();
    }

    @Test
    public void parameterNameBeginningWithNestingChar_rejected() {
        // Such a name has no root property to authorize. It used to reach charAt(0) on an empty string.
        var action = new ModelActionWithOwnMembers();
        assertThat(authorizer.isAuthorized(".actionSecret", action.getModel(), action)).isFalse();
        assertThat(authorizer.isAuthorized("[0].actionSecret", action.getModel(), action)).isFalse();
        assertThat(authorizer.isAuthorized("(actionSecret)", action.getModel(), action)).isFalse();
    }

    @Test
    public void parameterNameBeginningWithNestingChar_nonModelDriven_rejected() {
        var action = new SecureAction();
        assertThat(authorizer.isAuthorized(".annotatedProp", action, action)).isFalse();
        assertThat(authorizer.isAuthorized("[0].annotatedProp", action, action)).isFalse();
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

    // --- resolveTarget ---

    @Test
    public void resolveTarget_nonModelDriven_returnsAction() {
        var action = new SecureAction();
        assertThat(authorizer.resolveTarget(action)).isSameAs(action);
    }

    @Test
    public void resolveTarget_modelDriven_returnsModelFromValueStack() {
        var action = new ModelAction();
        var model = action.getModel();
        var valueStack = new StubValueStack();
        valueStack.push(model);
        ActionContext.of().withValueStack(valueStack).bind();

        assertThat(authorizer.resolveTarget(action)).isSameAs(model);
    }

    @Test
    public void resolveTarget_modelDriven_stackTopEqualsAction_returnsAction() {
        // Edge case: ModelDriven action where stack top equals the action itself.
        // No exemption applies — target stays as action.
        var action = new ModelAction();
        var valueStack = new StubValueStack();
        valueStack.push(action);
        ActionContext.of().withValueStack(valueStack).bind();

        assertThat(authorizer.resolveTarget(action)).isSameAs(action);
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

    public static class ModelActionWithOwnMembers implements ModelDriven<Pojo> {
        private final Pojo model = new Pojo();
        private String actionSecret;
        private String actionAllowed;

        @Override
        public Pojo getModel() { return model; }

        // NO @StrutsParameter — declared on the action, so the model exemption must not cover it
        public void setActionSecret(String actionSecret) { this.actionSecret = actionSecret; }
        public String getActionSecret() { return actionSecret; }

        @StrutsParameter
        public void setActionAllowed(String actionAllowed) { this.actionAllowed = actionAllowed; }
        public String getActionAllowed() { return actionAllowed; }

        // Namesake of a model property, deliberately unannotated
        private String shared;
        public void setShared(String shared) { this.shared = shared; }
        public String getShared() { return shared; }
    }

    public static class ReadOnlyShadowModel {
        public String getShadow() { return "read-only"; }
    }

    public static class ModelActionWithReadOnlyModelProperty implements ModelDriven<ReadOnlyShadowModel> {
        private final ReadOnlyShadowModel model = new ReadOnlyShadowModel();
        private String shadow;

        @Override
        public ReadOnlyShadowModel getModel() { return model; }

        // NO @StrutsParameter — the model only reads "shadow", so a depth-0 parameter lands here
        public void setShadow(String shadow) { this.shadow = shadow; }
        public String getShadow() { return shadow; }
    }

    public static class BaseWithPublicField {
        public String inheritedSecret;
    }

    public static class ModelActionInheritingPublicField extends BaseWithPublicField implements ModelDriven<Pojo> {
        private final Pojo model = new Pojo();

        @Override
        public Pojo getModel() { return model; }
    }

    public static class ModelInheritingPublicField extends BaseWithPublicModelField {
    }

    public static class BaseWithPublicModelField {
        public String inheritedModelField;
    }

    public static class ModelActionWithInheritingModel implements ModelDriven<ModelInheritingPublicField> {
        private final ModelInheritingPublicField model = new ModelInheritingPublicField();

        @Override
        public ModelInheritingPublicField getModel() { return model; }
    }

    public static class ModelWithConstant {
        public static final String constant = "not request surface";
    }

    public static class ModelActionWithConstantNamesake implements ModelDriven<ModelWithConstant> {
        private final ModelWithConstant model = new ModelWithConstant();
        private String constant;

        @Override
        public ModelWithConstant getModel() { return model; }

        // NO @StrutsParameter
        public void setConstant(String constant) { this.constant = constant; }
        public String getConstant() { return constant; }
    }

    public static class Pojo {
        private String name;
        private String shared;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getShared() { return shared; }
        public void setShared(String shared) { this.shared = shared; }
    }
}
