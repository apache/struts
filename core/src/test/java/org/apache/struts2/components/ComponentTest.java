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

import com.opensymphony.xwork2.LocalizedTextProvider;
import org.apache.struts2.StrutsException;
import org.apache.struts2.TestConfigurationProvider;
import org.apache.struts2.views.jsp.AbstractTagTest;
import org.apache.struts2.views.jsp.ActionTag;
import org.apache.struts2.views.jsp.BeanTag;
import org.apache.struts2.views.jsp.ElseIfTag;
import org.apache.struts2.views.jsp.ElseTag;
import org.apache.struts2.views.jsp.I18nTag;
import org.apache.struts2.views.jsp.IfTag;
import org.apache.struts2.views.jsp.IteratorTag;
import org.apache.struts2.views.jsp.PropertyTag;
import org.apache.struts2.views.jsp.PushTag;
import org.apache.struts2.views.jsp.SetTag;
import org.apache.struts2.views.jsp.TextTag;
import org.apache.struts2.views.jsp.URLTag;
import org.apache.struts2.views.jsp.iterator.AppendIteratorTag;
import org.apache.struts2.views.jsp.iterator.MergeIteratorTag;
import org.apache.struts2.views.jsp.ui.TextFieldTag;
import org.apache.struts2.views.jsp.ui.UpDownSelectTag;

import javax.servlet.jsp.tagext.TagSupport;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Stack;

/**
 * Test case for method findAncestor(Class) in Component and some commons
 * test cases for Component in general.
 */
public class ComponentTest extends AbstractTagTest {

    public void testFindAncestorTest() {
        Property property = new Property(stack);
        Form form = new Form(stack, request, response);
        ActionComponent actionComponent = new ActionComponent(stack, request, response);
        Anchor anchor = new Anchor(stack, request, response);
        Form form2 = new Form(stack, request, response);
        TextField textField = new TextField(stack, request, response);


        Stack<Component> stack = property.getComponentStack();
        Iterator<Component> i = stack.iterator();


        try {
            // component stack
            assertEquals(property.getComponentStack().size(), 6);
            assertEquals(i.next(), property);
            assertEquals(i.next(), form);
            assertEquals(i.next(), actionComponent);
            assertEquals(i.next(), anchor);
            assertEquals(i.next(), form2);
            assertEquals(i.next(), textField);


            // property
            assertNull(property.findAncestor(Component.class));

            // form
            assertEquals(form.findAncestor(Component.class), property);
            assertEquals(form.findAncestor(Property.class), property);

            // action
            assertEquals(actionComponent.findAncestor(Component.class), form);
            assertEquals(actionComponent.findAncestor(Property.class), property);
            assertEquals(actionComponent.findAncestor(Form.class), form);

            // anchor
            assertEquals(anchor.findAncestor(Component.class), actionComponent);
            assertEquals(anchor.findAncestor(ActionComponent.class), actionComponent);
            assertEquals(anchor.findAncestor(Form.class), form);
            assertEquals(anchor.findAncestor(Property.class), property);

            // form2
            assertEquals(form2.findAncestor(Component.class), anchor);
            assertEquals(form2.findAncestor(Anchor.class), anchor);
            assertEquals(form2.findAncestor(ActionComponent.class), actionComponent);
            assertEquals(form2.findAncestor(Form.class), form);
            assertEquals(form2.findAncestor(Property.class), property);

            // textField
            assertEquals(textField.findAncestor(Component.class), form2);
            assertEquals(textField.findAncestor(Form.class), form2);
            assertEquals(textField.findAncestor(Anchor.class), anchor);
            assertEquals(textField.findAncestor(ActionComponent.class), actionComponent);
            assertEquals(textField.findAncestor(Property.class), property);
        } finally {
            property.getComponentStack().pop();
            property.getComponentStack().pop();
            property.getComponentStack().pop();
            property.getComponentStack().pop();
            property.getComponentStack().pop();
        }
    }

    // Action Component
    public void testActionComponentDisposeItselfFromComponentStack() {
        request.setupGetServletPath(TestConfigurationProvider.TEST_NAMESPACE + "/" + "foo.action");
        try {
            TextFieldTag t = new TextFieldTag();
            t.setName("textFieldName");
            t.setPageContext(pageContext);
            t.doStartTag();

            ActionTag tag = new ActionTag();
            tag.setPageContext(pageContext);
            tag.setName(TestConfigurationProvider.TEST_NAMESPACE_ACTION);
            tag.setId(TestConfigurationProvider.TEST_NAMESPACE_ACTION);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // AppendInterator
    public void testAppendIteratorDisposeItselfFromComponentStack() {
        TextFieldTag t = new TextFieldTag();
        t.setPageContext(pageContext);
        t.setName("textFieldName");

        AppendIteratorTag tag = new AppendIteratorTag();
        tag.setPageContext(pageContext);

        try {
            t.doStartTag();
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
            t.doEndTag();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }


    // Bean
    public void testBeanComponentDisposeItselfFromComponentStack() {
        TextFieldTag t = new TextFieldTag();
        t.setPageContext(pageContext);
        t.setName("textFieldName");

        BeanTag tag = new BeanTag();
        tag.setName("org.apache.struts2.util.Counter");
        tag.setPageContext(pageContext);

        try {
            t.doStartTag();
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
            t.doEndTag();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


    // ElseIf
    public void testElseIfComponentDisposeItselfFromComponentStack() {
        TextFieldTag t = new TextFieldTag();
        t.setPageContext(pageContext);
        t.setName("textFieldName");

        ElseIfTag tag = new ElseIfTag();
        tag.setPageContext(pageContext);

        try {
            t.doStartTag();
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
            t.doEndTag();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }


    // Else
    public void testElseComponentDisposeItselfFromComponentStack() {
        TextFieldTag t = new TextFieldTag();
        t.setPageContext(pageContext);
        t.setName("textFieldName");

        ElseTag tag = new ElseTag();
        tag.setPageContext(pageContext);

        try {
            t.doStartTag();
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
            t.doEndTag();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }


    // If
    public void testIfComponentDisposeItselfFromComponentStack() {
        TextFieldTag t = new TextFieldTag();
        t.setPageContext(pageContext);
        t.setName("textFieldName");

        IfTag tag = new IfTag();
        tag.setTest("false");
        tag.setPageContext(pageContext);

        try {
            t.doStartTag();
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
            t.doEndTag();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }


    // Iterator
    public void testIteratorComponentDisposeItselfFromComponentStack() {
        TextFieldTag t = new TextFieldTag();
        t.setPageContext(pageContext);
        t.setName("textFieldName");

        IteratorTag tag = new IteratorTag();
        tag.setValue("{1,2}");
        tag.setPageContext(pageContext);

        try {
            t.doStartTag();
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            int endIt = tag.doAfterBody();
            while (TagSupport.EVAL_BODY_AGAIN == endIt) {
                assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
                endIt = tag.doAfterBody();
            }
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
            t.doEndTag();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }


    // MergeIterator
    public void testMergeIteratorComponentDisposeItselfFromComponentStack() {
        TextFieldTag t = new TextFieldTag();
        t.setPageContext(pageContext);
        t.setName("textFieldName");

        MergeIteratorTag tag = new MergeIteratorTag();
        tag.setPageContext(pageContext);

        try {
            t.doStartTag();
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
            t.doEndTag();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }


    // Property
    public void testPropertyComponentDisposeItselfFromComponentStack() {
        TextFieldTag t = new TextFieldTag();
        t.setPageContext(pageContext);
        t.setName("textFieldName");

        PropertyTag tag = new PropertyTag();
        tag.setPageContext(pageContext);

        try {
            t.doStartTag();
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
            t.doEndTag();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }


    // Push
    public void testPushComponentDisposeItselfFromComponentStack() {
        TextFieldTag t = new TextFieldTag();
        t.setPageContext(pageContext);
        t.setName("textFieldName");

        PushTag tag = new PushTag();
        tag.setValue("'aaaa'");
        tag.setPageContext(pageContext);

        try {
            t.doStartTag();
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
            t.doEndTag();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }


    // Set
    public void testSetComponentDisposeItselfFromComponentStack() {
        TextFieldTag t = new TextFieldTag();
        t.setPageContext(pageContext);
        t.setName("textFieldName");

        SetTag tag = new SetTag();
        tag.setName("name");
        tag.setValue("'value'");
        tag.setPageContext(pageContext);

        try {
            t.doStartTag();
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
            t.doEndTag();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }


    // Text
    public void testTextComponentDisposeItselfFromComponentStack() {
        TextFieldTag t = new TextFieldTag();
        t.setPageContext(pageContext);
        t.setName("textFieldName");

        TextTag tag = new TextTag();
        tag.setName("some.i18n.key");
        tag.setPageContext(pageContext);

        try {
            t.doStartTag();
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
            t.doEndTag();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }


    public void testI18nComponentDisposeItselfFromComponentStack() {
        stack.getActionContext().withLocale(Locale.getDefault());

        TextFieldTag t = new TextFieldTag();
        t.setPageContext(pageContext);
        t.setName("textFieldName");

        container.getInstance(LocalizedTextProvider.class).addDefaultResourceBundle("org.apache.struts2.components.temp");

        I18nTag tag = new I18nTag();
        tag.setName("org.apache.struts2.components.tempo");
        tag.setPageContext(pageContext);

        try {
            t.doStartTag();
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
            t.doEndTag();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // URL
    public void testURLComponentDisposeItselfFromComponentStack() {
        TextFieldTag t = new TextFieldTag();
        t.setPageContext(pageContext);
        t.setName("textFieldName");

        URLTag tag = new URLTag();
        tag.setPageContext(pageContext);

        try {
            t.doStartTag();
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
            t.doEndTag();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // updownselect
    public void testUpDownSelectDisposeItselfFromComponentStack() {
        TextFieldTag t = new TextFieldTag();
        t.setPageContext(pageContext);
        t.setName("textFieldName");

        UpDownSelectTag tag = new UpDownSelectTag();
        tag.setId("myId");
        tag.setPageContext(pageContext);
        tag.setName("updownselectName");
        tag.setList("{}");

        try {
            t.doStartTag();
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
            t.doEndTag();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    /**
     * Test the expected default behaviour for component body state methods.
     */
    public void testComponentDefaultBodyStates() {
        Component component = new Component(stack);
        // Test expected default results for: usesBody(), escapeHtmlBody().
        assertFalse("Component default usesBody not false ?", component.usesBody());
        assertFalse("Component default htmlEscapeBody not false ?", component.escapeHtmlBody());
    }

    /**
     * Test the behaviour for Anchor body state methods.
     */
    public void testAnchorBodyStates() {
        Anchor anchor = new Anchor(stack, request, response);
        // Test expected default results for: usesBody(), escapeHtmlBody().
        assertTrue("Anchor default usesBody not true ?", anchor.usesBody());
        assertFalse("Anchor default htmlEscapeBody not false ?", anchor.escapeHtmlBody());
        anchor.setEscapeHtmlBody(false);
        assertFalse("Anchor htmlEscapeBody not false after set false ?", anchor.escapeHtmlBody());
        anchor.setEscapeHtmlBody(true);
        assertTrue("Anchor htmlEscapeBody not true after set true ?", anchor.escapeHtmlBody());
    }

    /**
     * Test the behaviour for Submit body state methods.
     */
    public void testSubmitBodyStates() {
        Submit submit = new Submit(stack, request, response);
        // Test expected default results for: usesBody(), escapeHtmlBody().
        assertTrue("Submit default usesBody not true ?", submit.usesBody());
        assertFalse("Submit default htmlEscapeBody not false ?", submit.escapeHtmlBody());
        submit.setEscapeHtmlBody(false);
        assertFalse("Submit htmlEscapeBody not false after set false ?", submit.escapeHtmlBody());
        submit.setEscapeHtmlBody(true);
        assertTrue("Submit htmlEscapeBody not true after set true ?", submit.escapeHtmlBody());
    }

    /**
     * Attempt some code coverage tests for {@link Component} that can be achieved without
     * too much difficulty.
     */
    public void testComponent_coverageTest() {
        HashMap<String, Object> propertyMap = new HashMap<>();
        Exception exception = new Exception("Generic exception");
        Property property = new Property(stack);
        ActionComponent actionComponent = new ActionComponent(stack, request, response);

        try {
            actionComponent.setName("componentName");
            // Simulate component attribute with a hyphen in its name.
            propertyMap.put("hyphen-keyname-for-coverage", "hyphen-keyname-for-coverage-value");
            propertyMap.put("someKeyName", "someKeyValue");
            actionComponent.copyParams(propertyMap);
            actionComponent.addAllParameters(propertyMap);
            try {
                actionComponent.findString(null, "fieldName", "errorMessage");
                fail("null expr parameter should cause a StrutsException");
            } catch (StrutsException se) {
                // expected
            }
            assertNull("completeExpression of a null expression returned non-null result ?", actionComponent.completeExpression(null));
            try {
                actionComponent.findValue(null, "fieldName", "errorMessage");
                fail("null expr parameter should cause a StrutsException");
            } catch (StrutsException se) {
                // expected
            }
            try {
                actionComponent.findValue("", "fieldName", "errorMessage");
                fail("empty expr parameter should cause a StrutsException due to finding a null value");
            } catch (StrutsException se) {
                // expected
            }
            assertNotNull("the toString() method with Exception parameter returned null result ?", actionComponent.toString(exception));
            assertFalse("Initial performClearTagStateForTagPoolingServers not false ?", actionComponent.getPerformClearTagStateForTagPoolingServers());
            actionComponent.setPerformClearTagStateForTagPoolingServers(true);
            assertTrue("performClearTagStateForTagPoolingServers false after setting to true ?", actionComponent.getPerformClearTagStateForTagPoolingServers());
        } finally {
            property.getComponentStack().pop();
        }
    }
}
