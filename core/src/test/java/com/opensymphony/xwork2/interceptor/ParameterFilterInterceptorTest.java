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

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.dispatcher.HttpParameters;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for {@link ParameterFilterInterceptor}.
 *
 * @author Gabe
 */
public class ParameterFilterInterceptorTest extends XWorkTestCase {

    private ActionInvocation invocation;
    private ParameterFilterInterceptor interceptor;
    private Mock mockInvocation;
    private ValueStack stack;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        stack = ActionContext.getContext().getValueStack();
        mockInvocation = new Mock(ActionInvocation.class);
        mockInvocation.expectAndReturn("getInvocationContext", ActionContext.getContext());
        mockInvocation.expectAndReturn("getStack", stack);
        mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
        mockInvocation.expectAndReturn("getInvocationContext", ActionContext.getContext());
        mockInvocation.matchAndReturn("getAction", new SimpleAction());
        invocation = (ActionInvocation) mockInvocation.proxy();
        interceptor = new ParameterFilterInterceptor();
        interceptor.init();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        interceptor.destroy();
    }

    public void testBasicBlockAll() throws Exception {
        runFilterTest(null,null,true,new String[] {"blah", "bladeblah", "bladebladeblah"});
        assertEquals(0, getParameterNames().size());
    }
    
    public void testBasicAllowed() throws Exception {
        runFilterTest("blah",null,true,new String[] {"blah"});
        assertEquals(1, getParameterNames().size()); 
    }
    
    public void testBasicBlocked() throws Exception {
        runFilterTest(null,"blah",false,new String[] {"blah"});
        assertEquals(0, getParameterNames().size()); 
    }      
    public void testAllSubpropertiesBlocked() throws Exception {
        runFilterTest(null,"blah",false,new String[] {"blah.deblah", "blah.somethingelse", "blah(22)"});
        assertEquals(0, getParameterNames().size()); 
    }

    public void testAllSubpropertiesAllowed() throws Exception {
        runFilterTest("blah",null,true,
                new String[] {"blah.deblah", "blah.somethingelse", "blah(22)"});
        assertEquals(3, getParameterNames().size()); 
    }
    
    public void testTreeBlocking() throws Exception {
        runFilterTest("blah.deblah","blah,blah.deblah.deblah",false,
                new String[] {"blah", "blah.deblah", "blah.deblah.deblah"});
        assertEquals(1, getParameterNames().size());
        assertEquals(getParameterNames().iterator().next(),"blah.deblah");
    }
    
    public void testEnsureOnlyPropsBlocked() throws Exception {
        runFilterTest(null,"blah",false,new String[] {"blahdeblah"});
        assertEquals(1, getParameterNames().size()); 
    }
  
    
    private void runFilterTest(String allowed, String blocked, boolean defaultBlocked, String[] paramNames) throws Exception {
        interceptor.setAllowed(allowed);
        interceptor.setBlocked(blocked);
        interceptor.setDefaultBlock(defaultBlocked);
        setUpParameters(paramNames);
        runAction();
        
    }
    
    private void setUpParameters(String [] paramNames) {
        Map<String, String> params = new HashMap<>();
        for (String paramName : paramNames) {
            params.put(paramName, "irrelevant what this is");

        }
        ActionContext.getContext().setParameters(HttpParameters.create(params).build());
    }
    
    private Collection<String> getParameterNames() {
        return ActionContext.getContext().getParameters().keySet();
    }
    
    public void runAction() throws Exception  {
        interceptor.intercept(invocation);
    }
    
}
