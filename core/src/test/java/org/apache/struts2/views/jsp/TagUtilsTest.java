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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.struts2.ActionContext;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.TestAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for {@link TagUtils} class.
 * Verifies security warning behavior when JSP tags are accessed directly without action flow.
 */
public class TagUtilsTest extends StrutsInternalTestCase {

    private StrutsMockHttpServletRequest request;
    private StrutsMockPageContext pageContext;
    private StrutsMockHttpServletResponse response;
    private TestAppender testAppender;
    private Logger tagUtilsLogger;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        request = new StrutsMockHttpServletRequest();
        response = new StrutsMockHttpServletResponse();
        request.setSession(new StrutsMockHttpSession());

        pageContext = new StrutsMockPageContext(servletContext, request, response);

        // Setup log appender to capture warnings
        testAppender = new TestAppender();
        tagUtilsLogger = (Logger) LogManager.getLogger(TagUtils.class);
        tagUtilsLogger.addAppender(testAppender);
        testAppender.start();
    }

    @Override
    protected void tearDown() throws Exception {
        testAppender.stop();
        tagUtilsLogger.removeAppender(testAppender);
        ActionContext.clear();
        super.tearDown();
    }

    public void testGetStack_withNullValueStack_throwsConfigurationException() {
        // Setup: no ValueStack in request or ActionContext
        ActionContext.of().bind();

        try {
            TagUtils.getStack(pageContext);
            fail("Expected ConfigurationException to be thrown");
        } catch (ConfigurationException e) {
            assertTrue("Exception message should contain 'Rendering tag out of Action scope'",
                    e.getMessage().contains("Rendering tag out of Action scope"));
            assertTrue("Exception message should contain security warning",
                    e.getMessage().contains("accessing directly JSPs is not recommended"));
        }
    }

    public void testGetStack_withNullActionInvocation_logsWarning() {
        // Setup: ValueStack exists but no ActionInvocation
        ValueStack stack = ActionContext.getContext().getValueStack();
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);

        // Ensure ActionInvocation is null
        ActionContext.of(stack.getContext())
                .withActionInvocation(null)
                .bind();

        // Execute
        ValueStack result = TagUtils.getStack(pageContext);

        // Verify
        assertNotNull("ValueStack should be returned", result);
        assertTrue("Warning about direct JSP access should be logged",
                hasWarningLogMessage("Rendering tag out of Action scope"));
    }

    public void testGetStack_withNullAction_logsWarning() {
        // Setup: ValueStack and ActionInvocation exist but action is null
        ValueStack stack = ActionContext.getContext().getValueStack();
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);

        MockActionInvocation actionInvocation = new MockActionInvocation();
        actionInvocation.setAction(null);

        ActionContext.of(stack.getContext())
                .withActionInvocation(actionInvocation)
                .bind();

        // Execute
        ValueStack result = TagUtils.getStack(pageContext);

        // Verify
        assertNotNull("ValueStack should be returned", result);
        assertTrue("Warning about direct JSP access should be logged",
                hasWarningLogMessage("Rendering tag out of Action scope"));
    }

    public void testGetStack_withValidAction_noWarning() {
        // Setup: normal action flow with valid action
        ValueStack stack = ActionContext.getContext().getValueStack();
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);

        TestAction action = new TestAction();
        MockActionInvocation actionInvocation = new MockActionInvocation();
        actionInvocation.setAction(action);

        ActionContext.of(stack.getContext())
                .withActionInvocation(actionInvocation)
                .bind();

        // Execute
        ValueStack result = TagUtils.getStack(pageContext);

        // Verify
        assertNotNull("ValueStack should be returned", result);
        assertFalse("Warning should NOT be logged when action is present",
                hasWarningLogMessage("Rendering tag out of Action scope"));
    }

    public void testGetStack_warningMessageContainsSecurityUrl() {
        // Setup: ValueStack exists but no ActionInvocation
        ValueStack stack = ActionContext.getContext().getValueStack();
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);

        ActionContext.of(stack.getContext())
                .withActionInvocation(null)
                .bind();

        // Execute
        TagUtils.getStack(pageContext);

        // Verify warning contains security documentation URL
        assertTrue("Warning should contain security documentation URL",
                hasWarningLogMessage("https://struts.apache.org/security/#never-expose-jsp-files-directly"));
    }

    private boolean hasWarningLogMessage(String messageSubstring) {
        return testAppender.logEvents.stream()
                .anyMatch(event -> event.getLevel() == Level.WARN
                        && event.getMessage().getFormattedMessage().contains(messageSubstring));
    }

    /**
     * Test appender to capture log events for verification.
     */
    static class TestAppender extends AbstractAppender {
        List<LogEvent> logEvents = new ArrayList<>();

        TestAppender() {
            super("TestAppender", null, null, false, null);
        }

        @Override
        public void append(LogEvent logEvent) {
            logEvents.add(logEvent.toImmutable());
        }
    }
}
