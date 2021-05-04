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
package com.opensymphony.xwork2.interceptor;

import com.mockobjects.dynamic.ConstraintMatcher;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.util.ValueStack;

import java.util.Date;


/**
 * @author $Author$
 * @version $Revision$
 */
public class ModelDrivenInterceptorTest extends XWorkTestCase {

    Action action;
    Mock mockActionInvocation;
    ModelDrivenInterceptor modelDrivenInterceptor;
    Object model;
    PreResultListener preResultListener;
    ValueStack stack;


    public void testModelDrivenGetsPushedOntoStack() throws Exception {
        action = new ModelDrivenAction();
        mockActionInvocation.expectAndReturn("getAction", action);
        mockActionInvocation.expectAndReturn("getStack", stack);
        mockActionInvocation.expectAndReturn("invoke", "foo");

        modelDrivenInterceptor.intercept((ActionInvocation) mockActionInvocation.proxy());

        Object topOfStack = stack.pop();
        assertEquals("our model should be on the top of the stack", model, topOfStack);
    }

    private void setupRefreshModelBeforeResult() {
        action = new ModelDrivenAction();
        mockActionInvocation.expectAndReturn("getAction", action);
        mockActionInvocation.matchAndReturn("getStack", stack);
        mockActionInvocation.expectAndReturn("invoke", "foo");
        mockActionInvocation.expect("addPreResultListener", new ConstraintMatcher() {

            public boolean matches(Object[] objects) {
                preResultListener = (PreResultListener) objects[0];
                return true;
            }

            public Object[] getConstraints() {
                return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        modelDrivenInterceptor.setRefreshModelBeforeResult(true);
    }

    public void testModelDrivenUpdatedAndGetsPushedOntoStack() throws Exception {
        setupRefreshModelBeforeResult();

        modelDrivenInterceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
        assertNotNull(preResultListener);
        model = "this is my new model";
        preResultListener.beforeResult((ActionInvocation) mockActionInvocation.proxy(), "success");

        Object topOfStack = stack.pop();
        assertSame("our new model should be on the top of the stack", model, topOfStack);
        assertEquals(1, stack.getRoot().size());
    }

    public void testWW5126() throws Exception {
        model = new Object() {
            @Override
            public boolean equals(Object obj) {
                return true;
            }
        };
        setupRefreshModelBeforeResult();

        modelDrivenInterceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
        assertNotNull(preResultListener);
        model = "this is my new model";
        preResultListener.beforeResult((ActionInvocation) mockActionInvocation.proxy(), "success");

        Object topOfStack = stack.pop();
        assertSame("our new model should be on the top of the stack regardless of Object.equals", model, topOfStack);
        assertEquals(1, stack.getRoot().size());
    }

    public void testPrimitiveModelDrivenUpdatedAndGetsPushedOntoStack() throws Exception {
        model = 123;
        setupRefreshModelBeforeResult();

        modelDrivenInterceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
        assertNotNull(preResultListener);
        model = new Integer("123");
        preResultListener.beforeResult((ActionInvocation) mockActionInvocation.proxy(), "success");

        Object topOfStack = stack.pop();
        assertSame("our new primitive model should be on the top of the stack", model, topOfStack);
        assertEquals(1, stack.getRoot().size());
    }

    public void testNotNeedsRefresh() throws Exception {
        model = new Object() {
            @Override
            public boolean equals(Object obj) {
                return false;
            }
        };
        setupRefreshModelBeforeResult();

        modelDrivenInterceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
        assertNotNull(preResultListener);
        preResultListener.beforeResult((ActionInvocation) mockActionInvocation.proxy(), "success");

        Object topOfStack = stack.pop();
        assertSame("our original model should be on the top of the stack", model, topOfStack);
        assertEquals(1, stack.getRoot().size());
    }

    public void testNullNewModel() throws Exception {
        setupRefreshModelBeforeResult();

        modelDrivenInterceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
        assertNotNull(preResultListener);
        model = null;
        preResultListener.beforeResult((ActionInvocation) mockActionInvocation.proxy(), "success");

        Object topOfStack = stack.pop();
        assertNotSame("our model should be removed from the stack", model, topOfStack);
        assertEquals(0, stack.getRoot().size());
    }

    public void testStackNotModifiedForNormalAction() throws Exception {
        action = new ActionSupport();
        mockActionInvocation.expectAndReturn("getAction", action);
        mockActionInvocation.expectAndReturn("invoke", "foo");

        // nothing should happen
        modelDrivenInterceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mockActionInvocation = new Mock(ActionInvocation.class);
        modelDrivenInterceptor = new ModelDrivenInterceptor();
        stack = ActionContext.getContext().getValueStack();
        model = new Date(); // any object will do
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mockActionInvocation.verify();
    }


    public class ModelDrivenAction extends ActionSupport implements ModelDriven {

        public Object getModel() {
            return model;
        }

    }
}
