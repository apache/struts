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
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;
import org.apache.struts2.util.MemberAccessValueStack;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;
import org.apache.struts2.util.reflection.ReflectionContextState;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Binds through a real OGNL value stack, so these tests see the members OGNL actually resolves. No authorizer
 * pre-check runs here unless a test asks for one: what they pin is that the guard alone holds when the prediction in
 * {@link StrutsParameterAuthorizer#isAuthorized} has drifted from OGNL.
 */
public class ParameterMemberGuardTest extends XWorkTestCase {

    private StrutsParameterAuthorizer authorizer;
    private ValueStack stack;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        authorizer = container.inject(StrutsParameterAuthorizer.class);
        authorizer.setRequireAnnotations(Boolean.TRUE.toString());
        stack = container.getInstance(ValueStackFactory.class).createValueStack();
        ReflectionContextState.setCreatingNullObjects(stack.getContext(), true);
        ReflectionContextState.setDenyMethodExecution(stack.getContext(), true);
        ActionContext.getContext().withValueStack(stack);
    }

    private void bind(Object target, Object action, String name) {
        var guarded = (MemberAccessValueStack) stack;
        guarded.useParameterMemberGuard(authorizer.memberGuard(name, target, action, stack.getRoot()));
        try {
            stack.setParameter(name, "value");
        } catch (RuntimeException ignored) {
            // a rejected member surfaces as a failed property lookup
        } finally {
            guarded.useParameterMemberGuard(null);
        }
    }

    private void bindOn(Object action, String name) {
        stack.push(action);
        bind(action, action, name);
    }

    public void testAnnotatedSetterBinds() {
        var action = new GuardedAction();
        bindOn(action, "open");
        assertEquals("value", action.open);
    }

    public void testUnannotatedSetterDoesNotBind() {
        var action = new GuardedAction();
        bindOn(action, "secret");
        assertNull(action.secret);
    }

    public void testUnannotatedFluentSetterDoesNotBind() {
        var action = new GuardedAction();
        bindOn(action, "fluentSecret");
        assertNull(action.fluentSecret);
    }

    public void testAnnotatedFluentSetterBinds() {
        var action = new GuardedAction();
        bindOn(action, "fluentOpen");
        assertEquals("value", action.fluentOpen);
    }

    public void testUnannotatedPublicFieldDoesNotBind() {
        var action = new GuardedAction();
        bindOn(action, "publicSecret");
        assertNull(action.publicSecret);
    }

    public void testAnnotatedPublicFieldBinds() {
        var action = new GuardedAction();
        bindOn(action, "publicOpen");
        assertEquals("value", action.publicOpen);
    }

    public void testAnnotatedPublicFieldAuthorizesItsUnannotatedSetter() {
        var action = new GuardedAction();
        bindOn(action, "fieldBacked");
        assertEquals("value", action.fieldBacked);
    }

    public void testAnnotatedOverloadAuthorizesTheOverloadOgnlPicks() {
        var action = new GuardedAction();
        bindOn(action, "overloaded");
        assertEquals("value", action.overloaded);
    }

    public void testNestedParameterThroughGetterOfEnoughDepthBinds() {
        var action = new GuardedAction();
        action.nested = new Nested();
        bindOn(action, "nested.name");
        assertEquals("value", action.nested.name);
    }

    public void testNestedParameterThroughGetterOfTooLittleDepthDoesNotBind() {
        var action = new GuardedAction();
        action.shallow = new Nested();
        bindOn(action, "shallow.name");
        assertNull(action.shallow.name);
    }

    public void testNestedParameterThroughUnannotatedGetterDoesNotBind() {
        var action = new GuardedAction();
        action.unannotated = new Nested();
        bindOn(action, "unannotated.name");
        assertNull(action.unannotated.name);
    }

    public void testNullHandlerCreatesTheRootThroughItsUnannotatedSetter() {
        var action = new GuardedAction();
        bindOn(action, "nested.name");
        assertNotNull(action.nested);
        assertEquals("value", action.nested.name);
    }

    public void testNullHandlerDoesNotCreateARootWhoseGetterIsUnannotated() {
        var action = new GuardedAction();
        bindOn(action, "unannotated.name");
        assertNull(action.unannotated);
    }

    public void testModelMembersAreExemptButTheActionsAreNot() {
        var action = new GuardedModelAction();
        stack.push(action);
        stack.push(action.getModel());

        bind(action.getModel(), action, "name");
        bind(action.getModel(), action, "secret");

        assertEquals("value", action.getModel().name);
        assertNull(action.secret);
    }

    public void testObjectBelowTheActionIsHeldToTheAnnotation() {
        var previous = new GuardedAction();
        var action = new GuardedModelAction();
        stack.push(previous);
        stack.push(action);
        stack.push(action.getModel());

        bind(action.getModel(), action, "open");
        bind(action.getModel(), action, "fluentSecret");

        assertEquals("value", previous.open);
        assertNull(previous.fluentSecret);
    }

    public void testTransitionModeExemptsDepthZeroOnly() {
        authorizer.setRequireAnnotationsTransitionMode(Boolean.TRUE.toString());
        var action = new GuardedAction();
        action.unannotated = new Nested();

        bindOn(action, "secret");
        bind(action, action, "unannotated.name");

        assertEquals("value", action.secret);
        assertNull(action.unannotated.name);
    }

    public void testGuardPermitsEverythingWithoutRequireAnnotations() {
        authorizer.setRequireAnnotations(Boolean.FALSE.toString());
        var action = new GuardedAction();
        bindOn(action, "secret");
        assertEquals("value", action.secret);
    }

    public void testGuardIsRemovedWhenBindingEnds() {
        var action = new GuardedAction();
        bindOn(action, "secret");
        stack.setValue("secret", "value");
        assertEquals("value", action.secret);
    }

    public void testInterceptorFailsClosedWhenThePredictionHasDrifted() {
        var interceptor = driftedInterceptor();
        interceptor.setRequireAnnotationsOnResolvedMember(Boolean.TRUE.toString());
        var action = new GuardedAction();
        stack.push(action);

        interceptor.applyParametersOnStack(stack, secretParameter(), action);

        assertNull(action.secret);
    }

    public void testInterceptorBindsAsBeforeWhileTheFlagIsOff() {
        var interceptor = driftedInterceptor();
        var action = new GuardedAction();
        stack.push(action);

        interceptor.applyParametersOnStack(stack, secretParameter(), action);

        assertEquals("value", action.secret);
    }

    public void testFlagNeedsRequireAnnotations() {
        var interceptor = driftedInterceptor();
        interceptor.setRequireAnnotations(Boolean.FALSE.toString());
        interceptor.setRequireAnnotationsOnResolvedMember(Boolean.TRUE.toString());
        var action = new GuardedAction();
        stack.push(action);

        interceptor.applyParametersOnStack(stack, secretParameter(), action);

        assertEquals("value", action.secret);
    }

    private ParametersInterceptor driftedInterceptor() {
        var interceptor = new ParametersInterceptor();
        container.inject(interceptor);
        interceptor.setRequireAnnotations(Boolean.TRUE.toString());
        interceptor.setParameterAuthorizer(new DriftedAuthorizer(authorizer));
        return interceptor;
    }

    private static Map<String, Parameter> secretParameter() {
        return new TreeMap<>(HttpParameters.create(Map.of("secret", new String[]{"value"})).build());
    }

    /**
     * Stands for an authorizer whose prediction no longer matches OGNL: it approves the name and leaves the decision
     * to the guard.
     */
    private record DriftedAuthorizer(StrutsParameterAuthorizer delegate) implements ParameterAuthorizer {

        @Override
        public boolean isAuthorized(String parameterName, Object target, Object action) {
            return true;
        }

        @Override
        public ParameterMemberGuard memberGuard(String parameterName, Object target, Object action,
                                                Collection<?> root) {
            return delegate.memberGuard(parameterName, target, action, root);
        }
    }

    public static class Nested {
        private String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class GuardedAction {
        private String open;
        private String secret;
        private String fluentOpen;
        private String fluentSecret;
        private String overloaded;
        private Nested nested;
        private Nested shallow;
        private Nested unannotated;

        @StrutsParameter
        public String publicOpen;

        public String publicSecret;

        @StrutsParameter
        public String fieldBacked;

        @StrutsParameter
        public void setOpen(String open) {
            this.open = open;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        @StrutsParameter
        public GuardedAction setFluentOpen(String fluentOpen) {
            this.fluentOpen = fluentOpen;
            return this;
        }

        public GuardedAction setFluentSecret(String fluentSecret) {
            this.fluentSecret = fluentSecret;
            return this;
        }

        @StrutsParameter
        public void setOverloaded(String[] overloaded) {
            this.overloaded = overloaded[0];
        }

        public void setOverloaded(String overloaded) {
            this.overloaded = overloaded;
        }

        public void setFieldBacked(String fieldBacked) {
            this.fieldBacked = fieldBacked;
        }

        @StrutsParameter(depth = 1)
        public Nested getNested() {
            return nested;
        }

        public void setNested(Nested nested) {
            this.nested = nested;
        }

        @StrutsParameter
        public Nested getShallow() {
            return shallow;
        }

        public Nested getUnannotated() {
            return unannotated;
        }

        public void setUnannotated(Nested unannotated) {
            this.unannotated = unannotated;
        }
    }

    public static class GuardedModel {
        private String name;

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class GuardedModelAction implements ModelDriven<GuardedModel> {
        private final GuardedModel model = new GuardedModel();
        private String secret;

        @Override
        public GuardedModel getModel() {
            return model;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }
}
