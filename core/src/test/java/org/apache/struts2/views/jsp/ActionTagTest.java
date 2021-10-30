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
package org.apache.struts2.views.jsp;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.TestAction;
import org.apache.struts2.TestActionTagResult;
import org.apache.struts2.TestConfigurationProvider;
import org.apache.struts2.components.ActionComponent;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for {@link ActionTag}.
 */
public class ActionTagTest extends AbstractTagTest {

    public void testActionTagWithNamespace() {
        request.setupGetServletPath(TestConfigurationProvider.TEST_NAMESPACE + "/" + "foo.action");

        ActionTag tag = new ActionTag();
        tag.setPageContext(pageContext);
        tag.setName(TestConfigurationProvider.TEST_NAMESPACE_ACTION);
        tag.setVar(TestConfigurationProvider.TEST_NAMESPACE_ACTION);

        try {
            tag.doStartTag();
            ActionComponent ac = ((ActionComponent) tag.component);
            tag.doEndTag();
            ActionProxy proxy = ac.getProxy();

            Object o = pageContext.findAttribute(TestConfigurationProvider.TEST_NAMESPACE_ACTION);
            assertTrue(o instanceof TestAction);

            assertEquals(TestConfigurationProvider.TEST_NAMESPACE, proxy.getNamespace());
        } catch (JspException ex) {
            ex.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testActionTagWithNamespace_clearTagStateSet() {
        request.setupGetServletPath(TestConfigurationProvider.TEST_NAMESPACE + "/" + "foo.action");

        ActionTag tag = new ActionTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName(TestConfigurationProvider.TEST_NAMESPACE_ACTION);
        tag.setVar(TestConfigurationProvider.TEST_NAMESPACE_ACTION);

        try {
            tag.doStartTag();
            setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
            ActionComponent ac = ((ActionComponent) tag.component);
            tag.doEndTag();
            ActionProxy proxy = ac.getProxy();

            Object o = pageContext.findAttribute(TestConfigurationProvider.TEST_NAMESPACE_ACTION);
            assertTrue(o instanceof TestAction);

            assertEquals(TestConfigurationProvider.TEST_NAMESPACE, proxy.getNamespace());
        } catch (JspException ex) {
            ex.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testSimple() {
        request.setupGetServletPath("/foo.action");

        ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig("", "testAction");
        container.inject(config.getInterceptors().get(0).getInterceptor());

        ActionTag tag = new ActionTag();
        tag.setPageContext(pageContext);
        tag.setName("testAction");
        tag.setVar("testAction");

        int stackSize = stack.size();

        try {
            tag.doStartTag();
            tag.addParameter("foo", "myFoo");
            tag.doEndTag();

            assertEquals(stack.size(), ActionContext.getContext().getValueStack().size());
            assertEquals("myFoo", stack.findValue("#testAction.foo"));
            assertEquals(stackSize, stack.size());

            Object o = pageContext.findAttribute("testAction");
            assertTrue(o instanceof TestAction);
            assertEquals("myFoo", ((TestAction) o).getFoo());
            assertEquals(Action.SUCCESS, ((TestAction) o).getResult());
        } catch (JspException ex) {
            ex.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));

    }

    public void testSimple_clearTagStateSet() {
        request.setupGetServletPath("/foo.action");

        ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig("", "testAction");
        container.inject(config.getInterceptors().get(0).getInterceptor());

        ActionTag tag = new ActionTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("testAction");
        tag.setVar("testAction");

        int stackSize = stack.size();

        try {
            tag.doStartTag();
            setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
            tag.addParameter("foo", "myFoo");
            tag.doEndTag();

            assertEquals(stack.size(), ActionContext.getContext().getValueStack().size());
            assertEquals("myFoo", stack.findValue("#testAction.foo"));
            assertEquals(stackSize, stack.size());

            Object o = pageContext.findAttribute("testAction");
            assertTrue(o instanceof TestAction);
            assertEquals("myFoo", ((TestAction) o).getFoo());
            assertEquals(Action.SUCCESS, ((TestAction) o).getResult());
        } catch (JspException ex) {
            ex.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));

    }

    public void testSimpleWithoutServletActionContext() {
        ServletActionContext.setRequest(null);
        ServletActionContext.setResponse(null);
        this.testSimple();
    }

    public void testSimpleWithActionMethodInOriginalURI() {
        request.setupGetServletPath("/foo!foo.action");

        ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig("", "testAction");
        container.inject(config.getInterceptors().get(0).getInterceptor());

        ActionTag tag = new ActionTag();
        tag.setPageContext(pageContext);
        tag.setName("testAction");
        tag.setVar("testAction");

        int stackSize = stack.size();

        try {
            tag.doStartTag();
            tag.addParameter("foo", "myFoo");
            tag.doEndTag();

            assertEquals(stack.size(), ActionContext.getContext().getValueStack().size());
            assertEquals("myFoo", stack.findValue("#testAction.foo"));
            assertEquals(stackSize, stack.size());

            Object o = pageContext.findAttribute("testAction");
            assertTrue(o instanceof TestAction);
            assertEquals("myFoo", ((TestAction) o).getFoo());
            assertEquals(Action.SUCCESS, ((TestAction) o).getResult());
        } catch (JspException ex) {
            ex.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));

    }

    public void testSimpleWithctionMethodInOriginalURI_clearTagStateSet() {
        request.setupGetServletPath("/foo!foo.action");

        ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig("", "testAction");
        container.inject(config.getInterceptors().get(0).getInterceptor());

        ActionTag tag = new ActionTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("testAction");
        tag.setVar("testAction");

        int stackSize = stack.size();

        try {
            tag.doStartTag();
            setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
            tag.addParameter("foo", "myFoo");
            tag.doEndTag();

            assertEquals(stack.size(), ActionContext.getContext().getValueStack().size());
            assertEquals("myFoo", stack.findValue("#testAction.foo"));
            assertEquals(stackSize, stack.size());

            Object o = pageContext.findAttribute("testAction");
            assertTrue(o instanceof TestAction);
            assertEquals("myFoo", ((TestAction) o).getFoo());
            assertEquals(Action.SUCCESS, ((TestAction) o).getResult());
        } catch (JspException ex) {
            ex.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));

    }

    public void testActionWithExecuteResult() throws Exception {
        ActionTag tag = new ActionTag();
        tag.setPageContext(pageContext);
        tag.setNamespace("");
        tag.setName("testActionTagAction");
        tag.setExecuteResult(true);

        tag.doStartTag();

        // tag clear components on doEndTag
        ActionComponent component = (ActionComponent) tag.getComponent();

        tag.doEndTag();

        TestActionTagResult result = (TestActionTagResult) component.getProxy().getInvocation().getResult();

        assertTrue(stack.getContext().containsKey(ServletActionContext.PAGE_CONTEXT));
        assertTrue(stack.getContext().get(ServletActionContext.PAGE_CONTEXT) instanceof PageContext);
        assertTrue(result.isExecuted());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));

    }

    public void testActionWithExecuteResult_clearTagStateSet() throws Exception {
        ActionTag tag = new ActionTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setNamespace("");
        tag.setName("testActionTagAction");
        tag.setExecuteResult(true);

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).

        // tag clear components on doEndTag
        ActionComponent component = (ActionComponent) tag.getComponent();

        tag.doEndTag();

        TestActionTagResult result = (TestActionTagResult) component.getProxy().getInvocation().getResult();

        assertTrue(stack.getContext().containsKey(ServletActionContext.PAGE_CONTEXT));
        assertTrue(stack.getContext().get(ServletActionContext.PAGE_CONTEXT)instanceof PageContext);
        assertTrue(result.isExecuted());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));

    }

    public void testActionWithoutExecuteResult() throws Exception {
        ActionTag tag = new ActionTag();
        tag.setPageContext(pageContext);
        tag.setNamespace("");
        tag.setName("testActionTagAction");
        tag.setExecuteResult(false);

        tag.doStartTag();

        // tag clear components on doEndTag, so we need to get it here
        ActionComponent component = (ActionComponent) tag.getComponent();

        tag.doEndTag();

        TestActionTagResult result = (TestActionTagResult) component.getProxy().getInvocation().getResult();

        assertTrue(stack.getContext().containsKey(ServletActionContext.PAGE_CONTEXT));
        assertTrue(stack.getContext().get(ServletActionContext.PAGE_CONTEXT) instanceof PageContext);
        assertNull(result); // result is never executed, hence never set into invocation

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testActionWithoutExecuteResult_clearTagStateSet() throws Exception {
        ActionTag tag = new ActionTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setNamespace("");
        tag.setName("testActionTagAction");
        tag.setExecuteResult(false);

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).

        // tag clear components on doEndTag, so we need to get it here
        ActionComponent component = (ActionComponent) tag.getComponent();

        tag.doEndTag();

        TestActionTagResult result = (TestActionTagResult) component.getProxy().getInvocation().getResult();

        assertTrue(stack.getContext().containsKey(ServletActionContext.PAGE_CONTEXT));
        assertTrue(stack.getContext().get(ServletActionContext.PAGE_CONTEXT)instanceof PageContext);
        assertNull(result); // result is never executed, hence never set into invocation

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testExecuteButResetReturnSameInvocation() throws Exception {
        Mock mockActionInv = new Mock(ActionInvocation.class);
        ActionTag tag = new ActionTag();
        tag.setPageContext(pageContext);
        tag.setNamespace("");
        tag.setName("testActionTagAction");
        tag.setExecuteResult(true);
        ActionContext.getContext().withActionInvocation((ActionInvocation) mockActionInv.proxy());

        ActionInvocation oldInvocation = ActionContext.getContext().getActionInvocation();
        assertNotNull(oldInvocation);

        tag.doStartTag();

        // tag clear components on doEndTag
        ActionComponent component = (ActionComponent) tag.getComponent();

        tag.doEndTag();
        assertSame(oldInvocation, ActionContext.getContext().getActionInvocation());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));

    }

     public void testExecuteButResetReturnSameInvocation_clearTagStateSet() throws Exception {
        Mock mockActionInv = new Mock(ActionInvocation.class);
        ActionTag tag = new ActionTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setNamespace("");
        tag.setName("testActionTagAction");
        tag.setExecuteResult(true);
        ActionContext.getContext().setActionInvocation((ActionInvocation) mockActionInv.proxy());

        ActionInvocation oldInvocation = ActionContext.getContext().getActionInvocation();
        assertNotNull(oldInvocation);

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).

        // tag clear components on doEndTag
        ActionComponent component = (ActionComponent) tag.getComponent();

        tag.doEndTag();
        assertTrue(oldInvocation == ActionContext.getContext().getActionInvocation());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testIngoreContextParamsFalse() throws Exception {
        ActionTag tag = new ActionTag();
        tag.setPageContext(pageContext);
        tag.setNamespace("");
        tag.setName("testActionTagAction");
        tag.setExecuteResult(false);
        tag.setIgnoreContextParams(false);

        Map<String, String[]> params = new HashMap<>();
        params.put("user", new String[]{"Santa Claus"});
        ActionContext.getContext().setParameters(HttpParameters.create(params).build());

        tag.doStartTag();

        // tag clear components on doEndTag, so we need to get it here
        ActionComponent component = (ActionComponent) tag.getComponent();

        tag.doEndTag();

        // check parameters, there should be one
        ActionInvocation ai = component.getProxy().getInvocation();
        ActionContext ac = ai.getInvocationContext();
        assertEquals(1, ac.getParameters().keySet().size());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));

    }

    public void testIngoreContextParamsFalse_clearTagStateSet() throws Exception {
        ActionTag tag = new ActionTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setNamespace("");
        tag.setName("testActionTagAction");
        tag.setExecuteResult(false);
        tag.setIgnoreContextParams(false);

        Map<String, String[]> params = new HashMap<>();
        params.put("user", new String[]{"Santa Claus"});
        ActionContext.getContext().setParameters(HttpParameters.create(params).build());

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).

        // tag clear components on doEndTag, so we need to get it here
        ActionComponent component = (ActionComponent) tag.getComponent();

        tag.doEndTag();

        // check parameters, there should be one
        ActionInvocation ai = component.getProxy().getInvocation();
        ActionContext ac = ai.getInvocationContext();
        assertEquals(1, ac.getParameters().keySet().size());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));

    }

    public void testIngoreContextParamsTrue() throws Exception {
        ActionTag tag = new ActionTag();
        tag.setPageContext(pageContext);
        tag.setNamespace("");
        tag.setName("testActionTagAction");
        tag.setExecuteResult(false);
        tag.setIgnoreContextParams(true);

        Map<String, String[]> params = new HashMap<>();
        params.put("user", new String[]{"Santa Claus"});
        ActionContext.getContext().setParameters(HttpParameters.create(params).build());

        tag.doStartTag();

        // tag clear components on doEndTag, so we need to get it here
        ActionComponent component = (ActionComponent) tag.getComponent();

        tag.doEndTag();

        // check parameters, there should be one
        ActionInvocation ai = component.getProxy().getInvocation();
        ActionContext ac = ai.getInvocationContext();
        assertEquals(0, ac.getParameters().keySet().size());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testIngoreContextParamsTrue_clearTagStateSet() throws Exception {
        ActionTag tag = new ActionTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setNamespace("");
        tag.setName("testActionTagAction");
        tag.setExecuteResult(false);
        tag.setIgnoreContextParams(true);

        Map<String, String[]> params = new HashMap<>();
        params.put("user", new String[] { "Santa Claus" });
        ActionContext.getContext().setParameters(HttpParameters.create(params).build());

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).

        // tag clear components on doEndTag, so we need to get it here
        ActionComponent component = (ActionComponent) tag.getComponent();

        tag.doEndTag();

        // check parameters, there should be one
        ActionInvocation ai = component.getProxy().getInvocation();
        ActionContext ac = ai.getInvocationContext();
        assertEquals(0, ac.getParameters().keySet().size());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testNoNameDefined() throws Exception {
        ActionTag tag = new ActionTag();
        tag.setPageContext(pageContext);
        tag.setNamespace("");
        tag.setName(null);
        tag.setExecuteResult(false);

        try {
            tag.doStartTag();
            tag.doEndTag();
            fail("Should have thrown RuntimeException");
        } catch (StrutsException e) {
            assertEquals("tag 'actioncomponent', field 'name': Action name is required. Example: updatePerson", e.getMessage());
        }

        // The doEndTag() call is expected not to complete.  Cannot perform basic sanity check of clearTagStateForTagPoolingServers() behaviour.
    }

    // FIXME: Logging the error seems to cause the standard Maven build to fail
    public void testUnknownNameDefined() throws Exception {
        ActionTag tag = new ActionTag();
        tag.setPageContext(pageContext);
        tag.setNamespace("");
        tag.setName("UNKNOWN_NAME");
        tag.setExecuteResult(false);

        tag.doStartTag();
        tag.doEndTag();
        // will just log it to ERROR but we run the code to test that it works somehow

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    // FIXME: Logging the error seems to cause the standard Maven build to fail
    public void testUnknownNameDefined_clearTagStateSet() throws Exception {
        ActionTag tag = new ActionTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setNamespace("");
        tag.setName("UNKNOWN_NAME");
        tag.setExecuteResult(false);

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        // will just log it to ERROR but we run the code to test that it works somehow

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testActionMethodWithExecuteResult() throws Exception {
        ActionTag tag = new ActionTag();
        tag.setPageContext(pageContext);
        tag.setNamespace("");
        tag.setName("testActionTagAction!input");
        tag.setExecuteResult(true);
        ((DefaultActionMapper) container.getInstance(ActionMapper.class)).setAllowDynamicMethodCalls("true");

        tag.doStartTag();

        // tag clear components on doEndTag
        ActionComponent component = (ActionComponent) tag.getComponent();

        tag.doEndTag();

        TestActionTagResult result = (TestActionTagResult) component.getProxy().getInvocation().getResult();

        assertTrue(stack.getContext().containsKey(ServletActionContext.PAGE_CONTEXT));
        assertTrue(stack.getContext().get(ServletActionContext.PAGE_CONTEXT) instanceof PageContext);
        assertTrue(result.isExecuted());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testActionMethodWithExecuteResult_clearTagStateSet() throws Exception {
        ActionTag tag = new ActionTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setNamespace("");
        tag.setName("testActionTagAction!input");
        tag.setExecuteResult(true);
        ((DefaultActionMapper)container.getInstance(ActionMapper.class)).setAllowDynamicMethodCalls("true");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).

        // tag clear components on doEndTag
        ActionComponent component = (ActionComponent) tag.getComponent();

        tag.doEndTag();

        TestActionTagResult result = (TestActionTagResult) component.getProxy().getInvocation().getResult();

        assertTrue(stack.getContext().containsKey(ServletActionContext.PAGE_CONTEXT));
        assertTrue(stack.getContext().get(ServletActionContext.PAGE_CONTEXT)instanceof PageContext);
        assertTrue(result.isExecuted());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initDispatcher(new HashMap<String, String>() {{
            put("configProviders", TestConfigurationProvider.class.getName());
        }});
        createMocks();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
