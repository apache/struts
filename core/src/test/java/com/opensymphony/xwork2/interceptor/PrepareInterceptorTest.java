/*
 * $Id$
 * Copyright 2002-2007,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.interceptor;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;
import junit.framework.TestCase;
import org.easymock.MockControl;

/**
 * Unit test for PrepareInterceptor.
 *
 * @author Claus Ibsen
 * @author tm_jee
 */
public class PrepareInterceptorTest extends TestCase {

    private Mock mock;
    private PrepareInterceptor interceptor;

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
    	
    	MockControl controlAction = MockControl.createControl(ActionInterface.class);
    	ActionInterface mockAction = (ActionInterface) controlAction.getMock();
    	mockAction.prepareSubmit();
    	controlAction.setVoidCallable(1);
    	mockAction.prepare();
    	controlAction.setVoidCallable(1);
    	
    	MockControl controlActionProxy = MockControl.createControl(ActionProxy.class);
    	ActionProxy mockActionProxy = (ActionProxy) controlActionProxy.getMock();
    	mockActionProxy.getMethod();
    	controlActionProxy.setDefaultReturnValue("submit");
    	
    	
    	MockControl controlActionInvocation = MockControl.createControl(ActionInvocation.class);
    	ActionInvocation mockActionInvocation = (ActionInvocation) controlActionInvocation.getMock();
    	mockActionInvocation.getAction();
    	controlActionInvocation.setDefaultReturnValue(mockAction);
    	mockActionInvocation.invoke();
    	controlActionInvocation.setDefaultReturnValue("okok");
    	mockActionInvocation.getProxy();
    	controlActionInvocation.setDefaultReturnValue(mockActionProxy);
    	
    	
    	controlAction.replay();
    	controlActionProxy.replay();
    	controlActionInvocation.replay();
    	
    	PrepareInterceptor interceptor = new PrepareInterceptor();
    	String result = interceptor.intercept(mockActionInvocation);
    	
    	assertEquals("okok", result);
    	
    	controlAction.verify();
    	controlActionProxy.verify();
    	controlActionInvocation.verify();
    }
    
    public void testPrefixInvocation2() throws Exception {
    	
    	MockControl controlAction = MockControl.createControl(ActionInterface.class);
    	ActionInterface mockAction = (ActionInterface) controlAction.getMock();
    	mockAction.prepare();
    	controlAction.setVoidCallable(1);
    	
    	MockControl controlActionProxy = MockControl.createControl(ActionProxy.class);
    	ActionProxy mockActionProxy = (ActionProxy) controlActionProxy.getMock();
    	mockActionProxy.getMethod();
    	controlActionProxy.setDefaultReturnValue("save");
    	
    	
    	MockControl controlActionInvocation = MockControl.createControl(ActionInvocation.class);
    	ActionInvocation mockActionInvocation = (ActionInvocation) controlActionInvocation.getMock();
    	mockActionInvocation.getAction();
    	controlActionInvocation.setDefaultReturnValue(mockAction);
    	mockActionInvocation.invoke();
    	controlActionInvocation.setDefaultReturnValue("okok");
    	mockActionInvocation.getProxy();
    	controlActionInvocation.setDefaultReturnValue(mockActionProxy);
    	
    	
    	controlAction.replay();
    	controlActionProxy.replay();
    	controlActionInvocation.replay();
    	
    	PrepareInterceptor interceptor = new PrepareInterceptor();
    	String result = interceptor.intercept(mockActionInvocation);
    	
    	assertEquals("okok", result);
    	
    	controlAction.verify();
    	controlActionProxy.verify();
    	controlActionInvocation.verify();
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
    }

    @Override
    protected void tearDown() throws Exception {
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
