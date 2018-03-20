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
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.conversion.impl.ConversionData;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.validators.RepopulateConversionErrorFieldValidatorSupport;

import java.util.Map;

/**
 * Test RepopulateConversionErrorFieldValidatorSupport.
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class RepopulateConversionErrorFieldValidatorSupportTest extends XWorkTestCase {

	
	InternalRepopulateConversionErrorFieldValidatorSupport validator1;
	InternalRepopulateConversionErrorFieldValidatorSupport validator2;
	ActionSupport action;
	
	public void testUseFullFieldName() throws Exception {
		validator2.setRepopulateField(true);
		validator2.validate(action);
		
		ActionContext.getContext().getActionInvocation().invoke();
		Object valueFromStack1 = ActionContext.getContext().getValueStack().findValue("someFieldName", String.class);
		Object valueFromStack2 = ActionContext.getContext().getValueStack().findValue("xxxsomeFieldName", String.class);
		
		assertNull(valueFromStack1);
		assertEquals(valueFromStack2, "some value");
	}
	
	public void testGetterSetterGetsCalledApropriately1() throws Exception {
		
		validator1.setRepopulateField(true);
		validator1.validate(action);

		
		ActionContext.getContext().getActionInvocation().invoke();
		
		Object valueFromStack = ActionContext.getContext().getValueStack().findValue("someFieldName", String.class);
		
		assertEquals(valueFromStack, "some value");
	}
	
	
	public void testGetterSetterGetsCalledApropriately2() throws Exception {
		
		validator1.setRepopulateField(false);
		validator1.validate(action);

		
		ActionContext.getContext().getActionInvocation().invoke();
		
		Object valueFromStack = ActionContext.getContext().getValueStack().findValue("someFieldName", String.class);
		
		assertEquals(valueFromStack, null);
	}
	
	
	@Override
    protected void setUp() throws Exception {
	    super.setUp();
		ValueStack stack = ActionContext.getContext().getValueStack();
		MockActionInvocation invocation = new MockActionInvocation();
		invocation.setStack(stack);
		ActionContext.getContext().setValueStack(stack);
		ActionContext.getContext().setActionInvocation(invocation);
		
		String[] conversionErrorValue = new String[] { "some value" };
		Map<String, ConversionData> conversionErrors = ActionContext.getContext().getConversionErrors();
		conversionErrors.put("someFieldName", new ConversionData(conversionErrorValue, Integer.class));
		conversionErrors.put("xxxsomeFieldName", new ConversionData(conversionErrorValue, Integer.class));

		TextProviderFactory tpf = container.getInstance(TextProviderFactory.class);

		action = container.inject(ActionSupport.class);
		validator1 = 
			new InternalRepopulateConversionErrorFieldValidatorSupport();
		validator1.setFieldName("someFieldName");
		validator1.setValidatorContext(new DelegatingValidatorContext(action, tpf));
		
		validator2 = 
			new InternalRepopulateConversionErrorFieldValidatorSupport();
		validator2.setFieldName("someFieldName");
		validator2.setValidatorContext(new DelegatingValidatorContext(action, tpf) {
			@Override
            public String getFullFieldName(String fieldName) {
				return "xxx"+fieldName;
			}
		});
	}
	
	@Override
    protected void tearDown() throws Exception {
	    super.tearDown();
		validator1 = null;
		action = null;
	}
	
	
	// === inner class ============
	
	class InternalRepopulateConversionErrorFieldValidatorSupport extends RepopulateConversionErrorFieldValidatorSupport {
		public boolean doValidateGetsCalled = false;
		
		@Override
        protected void doValidate(Object object) throws ValidationException {
			doValidateGetsCalled = true;
		}
	}
}
