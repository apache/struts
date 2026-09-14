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

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.TestConfigurationProvider;
import org.apache.struts2.mock.MockActionProxy;
import org.apache.struts2.views.jsp.AbstractUITagTest;
import org.apache.struts2.views.jsp.ui.FormTag;
import org.apache.struts2.views.jsp.ui.TextFieldTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstraintAttributesTest extends AbstractUITagTest {

    private FormTag form;
    private String theme = "html5";
    private String fieldName = "username";
    private final Map<String, String> dynamicAttributes = new HashMap<>();

    public void testNoConstraintsWhenTheConstantIsOff() throws Exception {
        initDispatcherWith("false");

        assertNull(renderFieldAndReturnConstraints(null));
    }

    public void testConstraintsWhenTheConstantIsOn() throws Exception {
        initDispatcherWith("true");

        Map<String, String> constraints = renderFieldAndReturnConstraints(null);
        assertNotNull("expected constraints to be populated", constraints);
        assertEquals("3", constraints.get("minlength"));
    }

    /**
     * Only the html5 theme renders the derived map, so under any other theme the derivation is
     * wasted work per field — validator resolution plus a {@code getMessage()} call each.
     */
    public void testNoConstraintsUnderAThemeThatDoesNotRenderThem() throws Exception {
        initDispatcherWith("true");
        theme = "xhtml";

        assertNull(renderFieldAndReturnConstraints(null));
    }

    /**
     * A custom theme declares {@code parent = html5} in its theme.properties and inherits the
     * templates that render the map, so the gate has to walk the ancestry, not match the name.
     */
    public void testConstraintsUnderAThemeInheritingFromHtml5() throws Exception {
        initDispatcherWith("true");
        theme = "html5child";

        Map<String, String> constraints = renderFieldAndReturnConstraints(null);
        assertNotNull("expected constraints under a child of html5", constraints);
        assertEquals("3", constraints.get("minlength"));
    }

    public void testVisitorValidatedNestedFieldGetsTheConcreteConstraint() throws Exception {
        initDispatcherWith("true");
        fieldName = "user.name";

        Map<String, String> constraints = renderFieldAndReturnConstraints(null);
        assertNotNull("expected constraints for a visitor-validated field", constraints);
        assertEquals("required", constraints.get("required"));
        assertEquals("name is required", constraints.get("data-msg-requiredstring"));
        assertFalse(constraints.containsKey("data-msg-field-visitor"));
    }

    /**
     * HTML attribute names are ASCII case-insensitive, and the documented rule is that the developer's
     * own value always wins — including one typed as {@code MAXLENGTH}.
     */
    public void testADynamicAttributeSuppressesTheDerivedOneRegardlessOfCase() throws Exception {
        initDispatcherWith("true");
        fieldName = "bio";
        dynamicAttributes.put("MAXLENGTH", "5");

        Map<String, String> constraints = renderFieldAndReturnConstraints(null);

        assertTrue("expected the derived maxlength to yield to the developer's MAXLENGTH",
            constraints == null || !constraints.containsKey("maxlength"));
    }

    /**
     * {@code html5/text.ftl} has already emitted a hardcoded {@code type} by the time the constraint
     * map renders, so a {@code type} from a provider would be a duplicate attribute the browser drops.
     */
    public void testATypeFromTheProviderIsDiscarded() throws Exception {
        initDispatcherWith("true");

        TextFieldTag field = startField(null);
        ((UIBean) field.getComponent()).setHtmlConstraintProvider((validators, control, derivedFrom) ->
            new java.util.LinkedHashMap<>(Map.of("type", "email", "required", "required")));
        Map<String, Object> attributes = ((UIBean) field.getComponent()).getAttributes();

        finishField(field);

        @SuppressWarnings("unchecked")
        Map<String, String> constraints = (Map<String, String>) attributes.get("constraints");
        assertNotNull(constraints);
        assertFalse("type must never reach the template", constraints.containsKey("type"));
        assertEquals("required", constraints.get("required"));
    }

    /**
     * Pins the hook to running after {@code evaluateExtraParams()}. A {@code stringlength} validator on
     * a control the browser treats as numeric must not emit {@code minlength} at all — that attribute
     * is not legal there. This can only resolve correctly if the control type ({@code type="number"},
     * resolved by {@code TextField.evaluateExtraParams()}) is already known when the constraint hook
     * fires. Untyped text fields resolve to {@code TEXT} either way, so
     * {@link #testConstraintsWhenTheConstantIsOn()} alone cannot distinguish a correctly-placed hook
     * from one hoisted up to the {@code tagNames} block.
     */
    public void testConstraintsRespectAnExplicitInputType() throws Exception {
        initDispatcherWith("true");

        Map<String, String> constraints = renderFieldAndReturnConstraints("number");

        assertTrue("expected minlength to be suppressed for a numeric control",
            constraints == null || !constraints.containsKey("minlength"));
    }

    /**
     * The action handed to the provider must be the one server-side validation ran against —
     * {@code ValidationInterceptor} validates {@code invocation.getAction()} — because
     * {@code ValidatorSupport.getMessage} builds its {@code DelegatingValidatorContext} from that
     * object, and the context decides which resource bundle a {@code data-msg-*} key resolves in.
     * The top of the value stack is not that action whenever something has been pushed over it:
     * {@code ModelDrivenInterceptor} pushes the model, an {@code <s:iterator>} around the field
     * pushes the current element. The marker pushed here stands in for both.
     */
    public void testActionComesFromTheInvocationNotTheTopOfTheStack() throws Exception {
        initDispatcherWith("true");

        TextFieldTag field = startField(null);
        List<Object> captured = new ArrayList<>();
        // not named `action`: that would shadow the inherited field this test asserts against
        ((UIBean) field.getComponent()).setHtmlConstraintProvider((validators, control, derivedFrom) -> {
            captured.add(derivedFrom);
            return Collections.emptyMap();
        });
        Object pushedOverTheAction = new Object();
        stack.push(pushedOverTheAction);

        finishField(field);

        assertEquals("expected the provider to be consulted once", 1, captured.size());
        assertNotSame("messages must not resolve against whatever sits on top of the stack",
            pushedOverTheAction, captured.get(0));
        assertSame(action, captured.get(0));
    }

    /**
     * Derivation runs against the request-scoped value stack — {@code
     * DefaultActionValidatorManager.getValidators} hands each validator {@code
     * ActionContext.getValueStack()} — and {@code ValidatorSupport.getMessage} pushes the action and
     * the validator onto it with the matching pops outside any {@code finally}. A message that fails
     * to resolve therefore leaves frames behind, and since the failure is deliberately swallowed,
     * every tag rendered afterwards would silently resolve its OGNL against the wrong root. The
     * provider below stands in for that, failing the same way at the same point.
     */
    public void testTheValueStackIsRestoredWhenDerivationFails() throws Exception {
        initDispatcherWith("true");

        TextFieldTag field = startField(null);
        ((UIBean) field.getComponent()).setHtmlConstraintProvider((validators, control, derivedFrom) -> {
            stack.push(new Object());
            throw new IllegalStateException("message resolution failed midway");
        });
        int depthBeforeRendering = stack.getRoot().size();

        finishField(field);

        assertEquals("a swallowed failure must not leave the stack dirty for later tags",
            depthBeforeRendering, stack.getRoot().size());
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> renderFieldAndReturnConstraints(String type) throws Exception {
        TextFieldTag field = startField(type);

        Map<String, Object> attributes =
            ((UIBean) field.getComponent()).getAttributes();

        finishField(field);

        return (Map<String, String>) attributes.get("constraints");
    }

    private TextFieldTag startField(String type) throws Exception {
        form = new FormTag();
        form.setPageContext(pageContext);
        form.setAction("constraintAction");
        form.setNamespace("");
        form.setTheme(theme);
        form.doStartTag();

        TextFieldTag field = new TextFieldTag();
        field.setPageContext(pageContext);
        field.setName(fieldName);
        if (type != null) {
            field.setType(type);
        }
        for (Map.Entry<String, String> dynamic : dynamicAttributes.entrySet()) {
            field.setDynamicAttribute(null, dynamic.getKey(), dynamic.getValue());
        }
        field.doStartTag();
        return field;
    }

    private void finishField(TextFieldTag field) throws Exception {
        field.doEndTag();
        form.doEndTag();
    }

    private void initDispatcherWith(String constraintsEnabled) {
        initDispatcher(new HashMap<String, String>() {{
            put("configProviders", TestConfigurationProvider.class.getName());
            put(StrutsConstants.STRUTS_UI_HTML5_CONSTRAINTS, constraintsEnabled);
        }});
        createMocks();
        // createMocks() never sets a config on the MockActionProxy it builds; without one,
        // AnnotationActionValidatorManager.buildValidatorKey NPEs dereferencing proxy.getConfig().
        ((MockActionProxy) actionProxy).setConfig(
            configuration.getRuntimeConfiguration().getActionConfig("", "constraintAction"));
    }
}
