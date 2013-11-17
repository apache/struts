/*
 * $Id$
 *
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

package org.apache.struts2.dispatcher;

import org.apache.struts2.StrutsInternalTestCase;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.util.ValueStack;


/**
 *
 */
public class VelocityResultTest extends StrutsInternalTestCase {

    ActionInvocation actionInvocation;
    Mock mockActionProxy;
    ValueStack stack;
    String namespace;
    TestVelocityEngine velocity;
    VelocityResult result;


    public void testCanResolveLocationUsingOgnl() throws Exception {
        TestResult result = new TestResult();

        String location = "/myaction.action";
        Bean bean = new Bean();
        bean.setLocation(location);

        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(bean);

        assertEquals(location, stack.findValue("location"));

        result.setLocation("${location}");
        result.execute(actionInvocation);
        assertEquals(location, result.finalLocation);
    }

    public void testCanResolveLocationUsingStaticExpression() throws Exception {
        TestResult result = new TestResult();
        String location = "/any.action";
        result.setLocation("${'" + location + "'}");
        result.execute(actionInvocation);
        assertEquals(location, result.finalLocation);
    }

    public void testResourcesFoundUsingAbsolutePath() throws Exception {
        String location = "/WEB-INF/views/registration.vm";

        Template template = result.getTemplate(stack, velocity, actionInvocation, location, "UTF-8");
        assertNotNull(template);
        assertEquals("expect absolute locations to be handled as is", location, velocity.templateName);
    }

    public void testResourcesFoundUsingNames() throws Exception {
        String location = "Registration.vm";
        String expectedTemplateName = namespace + "/" + location;

        Template template = result.getTemplate(stack, velocity, actionInvocation, location, "UTF-8");
        assertNotNull(template);
        assertEquals("expect the prefix to be appended to the path when the location is not absolute", expectedTemplateName, velocity.templateName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        namespace = "/html";
        result = new VelocityResult();
        stack = ActionContext.getContext().getValueStack();
        ActionContext.getContext().setValueStack(stack);
        velocity = new TestVelocityEngine();
        mockActionProxy = new Mock(ActionProxy.class);
        mockActionProxy.expectAndReturn("getNamespace", "/html");

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionInvocation.expectAndReturn("getProxy", mockActionProxy.proxy());
        mockActionInvocation.expectAndReturn("getStack", stack);
        actionInvocation = (ActionInvocation) mockActionInvocation.proxy();
    }


    class Bean {
        private String location;

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLocation() {
            return location;
        }
    }

    class TestResult extends StrutsResultSupport {

        private static final long serialVersionUID = -1512206785088317315L;

        public String finalLocation;

        protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
            this.finalLocation = finalLocation;
        }
    }

    class TestVelocityEngine extends VelocityEngine {
        public String templateName;

        public Template getTemplate(String templateName) throws ResourceNotFoundException, ParseErrorException, Exception {
            this.templateName = templateName;

            return new Template();
        }

        public Template getTemplate(String templateName, String charSet) throws ResourceNotFoundException, ParseErrorException, Exception {
            this.templateName = templateName;

            return new Template();
        }
    }
}
