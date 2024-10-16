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
package org.apache.struts2.views.velocity.result;

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.junit.XWorkJUnit4TestCase;
import org.apache.struts2.result.StrutsResultSupport;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class VelocityResultTest extends XWorkJUnit4TestCase {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    ActionInvocation actionInvocation;
    @Mock
    ActionProxy actionProxy;
    ValueStack stack;
    String namespace;
    TestVelocityEngine velocity;
    VelocityResult velocityResult;

    @Before
    public void init() throws Exception {
        namespace = "/html";
        velocityResult = new VelocityResult();
        stack = ActionContext.getContext().getValueStack();
        velocity = new TestVelocityEngine();
        when(actionProxy.getNamespace()).thenReturn(namespace);
        when(actionInvocation.getProxy()).thenReturn(actionProxy);
        when(actionInvocation.getStack()).thenReturn(stack);
    }

    @Test
    public void testCanResolveLocationUsingOgnl() throws Exception {
        TestResult testResult = new TestResult();

        String location = "/myaction.action";
        Bean bean = new Bean();
        bean.setLocation(location);

        stack.push(bean);

        assertEquals(location, stack.findValue("location"));

        testResult.setLocation("${location}");
        testResult.execute(actionInvocation);
        assertEquals(location, testResult.finalLocation);
    }

    @Test
    public void testCanResolveLocationUsingStaticExpression() throws Exception {
        TestResult result = new TestResult();
        String location = "/any.action";
        result.setLocation("${'" + location + "'}");
        result.execute(actionInvocation);
        assertEquals(location, result.finalLocation);
    }

    @Test
    public void testResourcesFoundUsingAbsolutePath() throws Exception {
        String location = "/WEB-INF/views/registration.vm";

        Template template = velocityResult.getTemplate(stack, velocity, actionInvocation, location, "UTF-8");
        assertNotNull(template);
        assertEquals("expect absolute locations to be handled as is", location, velocity.templateName);
    }

    @Test
    public void testResourcesFoundUsingNames() throws Exception {
        String location = "Registration.vm";
        String expectedTemplateName = namespace + "/" + location;

        Template template = velocityResult.getTemplate(stack, velocity, actionInvocation, location, "UTF-8");
        assertNotNull(template);
        assertEquals("expect the prefix to be appended to the path when the location is not absolute",
                expectedTemplateName,
                velocity.templateName);
    }

    static class Bean {
        private String location;

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLocation() {
            return location;
        }
    }

    static class TestResult extends StrutsResultSupport {

        private static final long serialVersionUID = -1512206785088317315L;

        public String finalLocation;

        protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
            this.finalLocation = finalLocation;
        }
    }

    static class TestVelocityEngine extends VelocityEngine {
        public String templateName;

        public Template getTemplate(String templateName) throws ResourceNotFoundException, ParseErrorException {
            this.templateName = templateName;
            return new Template();
        }

        public Template getTemplate(String templateName,
                                    String charSet) throws ResourceNotFoundException, ParseErrorException {
            this.templateName = templateName;
            return new Template();
        }
    }
}
