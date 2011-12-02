/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package org.apache.struts2.xwork2.validator.validators;

import org.apache.struts2.xwork2.ActionContext;
import org.apache.struts2.xwork2.XWorkTestCase;
import org.apache.struts2.xwork2.ognl.OgnlValueStack;
import org.apache.struts2.xwork2.util.ValueStack;
import org.apache.struts2.xwork2.util.ValueStackFactory;
import org.apache.struts2.xwork2.validator.ValidationException;

/**
 * @author tmjee
 * @version $Date: 2011-12-02 12:24:48 +0100 (Fri, 02 Dec 2011) $ $Id: ValidatorSupportTest.java 1209415 2011-12-02 11:24:48Z lukaszlenart $
 */
public class ValidatorSupportTest extends XWorkTestCase {

	public void testConditionalParseExpression()  throws Exception {
		ValueStack oldStack = ActionContext.getContext().getValueStack();
		try {
			OgnlValueStack stack = (OgnlValueStack) container.getInstance(ValueStackFactory.class).createValueStack();
			stack.getContext().put(ActionContext.CONTAINER, container);
			stack.getContext().put("something", "somevalue");
			ActionContext.getContext().setValueStack(stack);
			ValidatorSupport validator = new ValidatorSupport() {
				public void validate(Object object) throws ValidationException {
				}
			};
            validator.setValueStack(ActionContext.getContext().getValueStack());

            validator.setParse(true);
			String result1 = validator.conditionalParse("${#something}").toString();

			validator.setParse(false);
			String result2 = validator.conditionalParse("${#something}").toString();

			assertEquals(result1, "somevalue");
			assertEquals(result2, "${#something}");
		}
		finally {
			ActionContext.getContext().setValueStack(oldStack);
		}
	}

}
