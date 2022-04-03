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
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;
import junit.framework.TestCase;
import static org.easymock.EasyMock.*;


/**
 * Unit test for PrepareInterceptor.
 *
 * @author Claus Ibsen
 * @author tm_jee
 */
public class PrepareInterceptorTest extends TestCase {

    private Mock mock;
    private PrepareInterceptor interceptor;
    
    ActionInterface mockAction;

    public void testPrepareCalledDefault() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy mockActionProxy = new MockActionProxy();
        mockActionProxy.setMethod("execute");
        mai.setProxy(mockActionProxy);
        mai.setAction(mock.proxy());
        mock.expect("prepare");

        interceptor.intercept(mai);
    }

    public void testPrepareCalledFalse() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy mockActionProxy = new MockActionProxy();
        mockActionProxy.setMethod("execute");
        mai.setProxy(mockActionProxy);
        mai.setAction(mock.proxy());

        interceptor.setAlwaysInvokePrepare("false");
        interceptor.intercept(mai);
    }

    public void testPrepareCalledTrue() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy mockActionProxy = new MockActionProxy();
        mockActionProxy.setMethod("execute");
        mai.setProxy(mockActionProxy);
        mai.setAction(mock.proxy());
        mock.expect("prepare");

        interceptor.setAlwaysInvokePrepare("true");
        interceptor.intercept(mai);
    }

    public void testFirstCallPrepareDoIsTrue() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy mockActionProxy = new MockActionProxy();
        mockActionProxy.setMethod("submit");
        mai.setProxy(mockActionProxy);
        mai.setAction(mock.proxy());
        mock.expect("prepareSubmit");
        mock.expect("prepare");

        interceptor.setFirstCallPrepareDo("true");
        interceptor.intercept(mai);
    }

    public void testFirstCallPrepareDoIsFalse() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy mockActionProxy = new MockActionProxy();
        mockActionProxy.setMethod("submit");
        mai.setProxy(mockActionProxy);
        mai.setAction(mock.proxy());
        mock.expect("prepare");
        mock.expect("prepareSubmit");

        interceptor.setFirstCallPrepareDo("false");
        interceptor.intercept(mai);
    }

    public void testNoPrepareCalled() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(new SimpleFooAction());

        interceptor.doIntercept(mai);
    }
    
    public void testPrefixInvocation1() throws Exception {
 	
   
    	
    	ActionProxy mockActionProxy = (ActionProxy) createMock(ActionProxy.class);
    	
    	expect(mockActionProxy.getMethod()).andStubReturn("submit");
    	
    	
    	ActionInvocation mockActionInvocation = (ActionInvocation) createMock(ActionInvocation.class);
    	
    	expect(mockActionInvocation.getAction()).andStubReturn(mockAction);
    	expect(mockActionInvocation.invoke()).andStubReturn("okok");
    	expect(mockActionInvocation.getProxy()).andStubReturn(mockActionProxy);
    	
        mockAction.prepareSubmit();
        expectLastCall().times(1);
        mockAction.prepare();        
        expectLastCall().times(1);
        
    	replay(mockAction, mockActionProxy, mockActionInvocation);
    	
    	PrepareInterceptor interceptor = new PrepareInterceptor();
    	String result = interceptor.intercept(mockActionInvocation);
    	
    	assertEquals("okok", result);
    	
    	verify(mockAction, mockActionProxy, mockActionInvocation);
    }
    
    public void testPrefixInvocation2() throws Exception {

    	
    	ActionProxy mockActionProxy = (ActionProxy) createMock(ActionProxy.class);   	
    	
    	expect(mockActionProxy.getMethod()).andStubReturn("save");
    	
    	
    	ActionInvocation mockActionInvocation = (ActionInvocation) createMock(ActionInvocation.class);
    	
    	expect(mockActionInvocation.getAction()).andStubReturn(mockAction);
    	expect(mockActionInvocation.invoke()).andStubReturn("okok");
    	expect(mockActionInvocation.getProxy()).andStubReturn(mockActionProxy);       
        
    	mockAction.prepare();
        expectLastCall().times(1);  	
    	
        replay(mockAction, mockActionProxy, mockActionInvocation);
    	
    	PrepareInterceptor interceptor = new PrepareInterceptor();
    	String result = interceptor.intercept(mockActionInvocation);
    	
    	assertEquals("okok", result);
    	
    	verify(mockAction, mockActionProxy, mockActionInvocation);
    }
    

    public void testPrepareThrowException() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy mockActionProxy = new MockActionProxy();
        mockActionProxy.setMethod("submit");
        mai.setProxy(mockActionProxy);
        mai.setAction(mock.proxy());

        IllegalAccessException illegalAccessException = new IllegalAccessException();
        mock.expectAndThrow("prepareSubmit", illegalAccessException);
        mock.matchAndThrow("prepare", new RuntimeException());

        try {
            interceptor.intercept(mai);
            fail("Should not have reached this point.");
        } catch (Throwable t) {
            assertSame(illegalAccessException, t);
        }
    }

    @Override
    protected void setUp() throws Exception {
        mock = new Mock(ActionInterface.class);
        interceptor = new PrepareInterceptor();
        mockAction = (ActionInterface) createMock(ActionInterface.class);        
    }
    

    @Override
    protected void tearDown() throws Exception {
        mockAction = null;
        mock.verify();
    }

    
    
    /**
     * Simple interface to test prefix action invocation 
     * eg. prepareSubmit(), prepareSave() etc.
     *
     * @author tm_jee
     */
    public interface ActionInterface extends Action, Preparable {
    	void prepareSubmit() throws Exception;
    }

}
