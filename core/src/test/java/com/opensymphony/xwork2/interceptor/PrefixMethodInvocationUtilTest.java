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

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import junit.framework.TestCase;
import static org.easymock.EasyMock.*;

import java.lang.reflect.Method;

/**
 * Test case for PrefixMethodInovcationUtil.
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class PrefixMethodInvocationUtilTest extends TestCase {

	// === capitalizeMethodName ===
	public void testCapitalizeMethodName() throws Exception {
		assertEquals("SomeMethod", 
				PrefixMethodInvocationUtil.capitalizeMethodName("someMethod"));
		assertEquals("AnotherMethod", 
				PrefixMethodInvocationUtil.capitalizeMethodName("anotherMethod"));
	}
	
	// === getPrefixMethod ===
	public void testGetPrefixMethod1() throws Exception {
		Object action = new PrefixMethodInvocationUtilTest.Action1();
		Method m = PrefixMethodInvocationUtil.getPrefixedMethod(
				new String[] { "prepare", "prepareDo" }, "save", action);
		assertNotNull(m);
		assertEquals(m.getName(), "prepareSave");
	}
	
	public void testGetPrefixMethod2() throws Exception {
		Object action = new PrefixMethodInvocationUtilTest.Action1();
		Method m = PrefixMethodInvocationUtil.getPrefixedMethod(
				new String[] { "prepare", "prepareDo" }, "submit", action);
		assertNotNull(m);
		assertEquals(m.getName(), "prepareSubmit");
	}
	
	public void testGetPrefixMethod3() throws Exception {
		Object action = new PrefixMethodInvocationUtilTest.Action1();
		Method m = PrefixMethodInvocationUtil.getPrefixedMethod(
				new String[] { "prepare", "prepareDo" }, "cancel", action);
		assertNotNull(m);
		assertEquals(m.getName(), "prepareDoCancel");
	}
	
	public void testGetPrefixMethod4() throws Exception {
		Object action = new PrefixMethodInvocationUtilTest.Action1();
		Method m = PrefixMethodInvocationUtil.getPrefixedMethod(
				new String[] { "prepare", "prepareDo" }, "noSuchMethod", action);
		assertNull(m);
	}
	
	public void testGetPrefixMethod5() throws Exception {
		Object action = new PrefixMethodInvocationUtilTest.Action1();
		Method m = PrefixMethodInvocationUtil.getPrefixedMethod(
				new String[] { "noSuchPrefix", "noSuchPrefixDo" }, "save", action);
		assertNull(m);
	}
	
	
	// === invokePrefixMethod === 
	public void testInvokePrefixMethod1() throws Exception {
		PrefixMethodInvocationUtilTest.Action1 action = new PrefixMethodInvocationUtilTest.Action1();
		
		// ActionProxy
		ActionProxy mockActionProxy = (ActionProxy) createMock(ActionProxy.class);		
		
		expect(mockActionProxy.getMethod()).andStubReturn("save");
		
		
		// ActionInvocation
		ActionInvocation mockActionInvocation = (ActionInvocation) createMock(ActionInvocation.class);
		
		expect(mockActionInvocation.getAction()).andStubReturn(action);
		expect(mockActionInvocation.getProxy()).andStubReturn(mockActionProxy);
		
		replay(mockActionProxy, mockActionInvocation);
		
		
		PrefixMethodInvocationUtil.invokePrefixMethod(
				mockActionInvocation, 
				new String[] { "prepare", "prepareDo" });
			
		assertTrue(action.prepareSaveInvoked);
		assertFalse(action.prepareDoSaveInvoked);
		assertFalse(action.prepareSubmitInvoked);
		assertFalse(action.prepareDoCancelInvoked);
		
		verify(mockActionProxy, mockActionInvocation);
	}
	
	public void testInvokePrefixMethod2() throws Exception {
		PrefixMethodInvocationUtilTest.Action1 action = new PrefixMethodInvocationUtilTest.Action1();
		
		// ActionProxy
		ActionProxy mockActionProxy = (ActionProxy) createMock(ActionProxy.class);		
		expect(mockActionProxy.getMethod()).andStubReturn("submit");
		
		
		// ActionInvocation
		ActionInvocation mockActionInvocation = (ActionInvocation) createMock(ActionInvocation.class);
		
		expect(mockActionInvocation.getAction()).andStubReturn(action);
		expect(mockActionInvocation.getProxy()).andStubReturn(mockActionProxy);
		
		replay(mockActionProxy, mockActionInvocation);
		
		PrefixMethodInvocationUtil.invokePrefixMethod(
				mockActionInvocation, 
				new String[] { "prepare", "prepareDo" });
		
	
		assertFalse(action.prepareSaveInvoked);
		assertFalse(action.prepareDoSaveInvoked);
		assertTrue(action.prepareSubmitInvoked);
		assertFalse(action.prepareDoCancelInvoked);
		
		verify(mockActionProxy, mockActionInvocation);
	}
	
	public void testInvokePrefixMethod3() throws Exception {
		PrefixMethodInvocationUtilTest.Action1 action = new PrefixMethodInvocationUtilTest.Action1();
		
		// ActionProxy
		ActionProxy mockActionProxy = (ActionProxy) createMock(ActionProxy.class);		
		
		expect(mockActionProxy.getMethod()).andStubReturn("cancel");
				
		// ActionInvocation
		ActionInvocation mockActionInvocation = (ActionInvocation) createMock(ActionInvocation.class);
		
        expect(mockActionInvocation.getAction()).andStubReturn(action);
        expect(mockActionInvocation.getProxy()).andStubReturn(mockActionProxy);
		
		replay(mockActionProxy, mockActionInvocation);
		
		PrefixMethodInvocationUtil.invokePrefixMethod(
				mockActionInvocation, 
				new String[] { "prepare", "prepareDo" });
		

		assertFalse(action.prepareSaveInvoked);
		assertFalse(action.prepareDoSaveInvoked);
		assertFalse(action.prepareSubmitInvoked);
		assertTrue(action.prepareDoCancelInvoked);

		verify(mockActionProxy, mockActionInvocation);
	}
		
	public void testInvokePrefixMethod4() throws Exception {
		PrefixMethodInvocationUtilTest.Action1 action = new PrefixMethodInvocationUtilTest.Action1();
		
		// ActionProxy
        ActionProxy mockActionProxy = (ActionProxy) createMock(ActionProxy.class);   
        
        expect(mockActionProxy.getMethod()).andStubReturn("noSuchMethod");
		
		
		// ActionInvocation
        ActionInvocation mockActionInvocation = (ActionInvocation) createMock(ActionInvocation.class);
        
        expect(mockActionInvocation.getAction()).andStubReturn(action);
        expect(mockActionInvocation.getProxy()).andStubReturn(mockActionProxy);
		
        replay(mockActionProxy, mockActionInvocation);
		
		
		PrefixMethodInvocationUtil.invokePrefixMethod(
				mockActionInvocation, 
				new String[] { "prepare", "prepareDo" });
				
		assertFalse(action.prepareSaveInvoked);
		assertFalse(action.prepareDoSaveInvoked);
		assertFalse(action.prepareSubmitInvoked);
		assertFalse(action.prepareDoCancelInvoked);
		
		verify(mockActionProxy, mockActionInvocation);
	}
		
	public void testInvokePrefixMethod5() throws Exception {
		PrefixMethodInvocationUtilTest.Action1 action = new PrefixMethodInvocationUtilTest.Action1();
		
		// ActionProxy
        ActionProxy mockActionProxy = (ActionProxy) createMock(ActionProxy.class);  
        
		expect(mockActionProxy.getMethod()).andStubReturn("save");
		
		
		// ActionInvocation
        ActionInvocation mockActionInvocation = (ActionInvocation) createMock(ActionInvocation.class);
        
        expect(mockActionInvocation.getAction()).andStubReturn(action);
        expect(mockActionInvocation.getProxy()).andStubReturn(mockActionProxy);

        replay(mockActionProxy, mockActionInvocation);
		
		
		PrefixMethodInvocationUtil.invokePrefixMethod(
				mockActionInvocation, 
				new String[] { "noSuchPrefix", "noSuchPrefixDo" });
			
		assertFalse(action.prepareSaveInvoked);
		assertFalse(action.prepareDoSaveInvoked);
		assertFalse(action.prepareSubmitInvoked);
		assertFalse(action.prepareDoCancelInvoked);
		
		verify(mockActionProxy, mockActionInvocation);
	}
	
	
	
	
	/**
	 * Just a simple object for testing method invocation on its methods.
	 * 
	 * @author tm_jee
	 * @version $Date$ $Id$
	 */
	public static class Action1 {
		
		boolean prepareSaveInvoked = false;
		boolean prepareDoSaveInvoked = false;
		boolean prepareSubmitInvoked = false;
		boolean prepareDoCancelInvoked = false;
		
		
		// save
		public void prepareSave() {
			prepareSaveInvoked = true;
		}
		public void prepareDoSave() {
			prepareDoSaveInvoked = true;
		}
		
		// submit
		public void prepareSubmit() {
			prepareSubmitInvoked = true;
		}
		
		// cancel
		public void prepareDoCancel() {
			prepareDoCancelInvoked = true;
		}
	}
}
