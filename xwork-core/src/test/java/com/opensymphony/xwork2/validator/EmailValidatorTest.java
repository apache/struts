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
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.validator.validators.EmailValidator;

/**
 * Test case for Email Validator
 * 
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class EmailValidatorTest extends XWorkTestCase {
	
	public void testEmailValidity() throws Exception {
		assertTrue(verifyEmailValidity("tmjee@yahoo.com"));
		assertTrue(verifyEmailValidity("tm_jee@yahoo.co"));
		assertTrue(verifyEmailValidity("tm.jee@yahoo.co.uk"));
		assertTrue(verifyEmailValidity("tm.jee@yahoo.co.biz"));
		assertTrue(verifyEmailValidity("tm_jee@yahoo.com"));
		assertTrue(verifyEmailValidity("tm_jee@yahoo.net"));
		assertTrue(verifyEmailValidity(" user@subname1.subname2.subname3.domainname.co.uk "));
        assertTrue(verifyEmailValidity("tm.j'ee@yahoo.co.uk"));
        assertTrue(verifyEmailValidity("tm.j'e.e'@yahoo.co.uk"));
        assertTrue(verifyEmailValidity("tmj'ee@yahoo.com"));
		
		assertFalse(verifyEmailValidity("tm_jee#marry@yahoo.co.uk"));
		assertFalse(verifyEmailValidity("tm_jee@ yahoo.co.uk"));
		assertFalse(verifyEmailValidity("tm_jee  @yahoo.co.uk"));
		assertFalse(verifyEmailValidity("tm_j ee  @yah oo.co.uk"));
		assertFalse(verifyEmailValidity("tm_jee  @yah oo.co.uk"));
		assertFalse(verifyEmailValidity("tm_jee @ yahoo.com"));
		assertFalse(verifyEmailValidity(" user@subname1.subname2.subname3.domainn#ame.co.uk "));
	}
	
	protected boolean verifyEmailValidity(final String email) throws Exception {
		ActionSupport action = new ActionSupport() {
			public String getMyEmail() {
				return email;
			}
		};
		
		EmailValidator validator = new EmailValidator();
		validator.setValidatorContext(new DelegatingValidatorContext(action));
		validator.setFieldName("myEmail");
		validator.setDefaultMessage("invalid email");
        validator.setValueStack(ActionContext.getContext().getValueStack());
        validator.validate(action);
		
		return (action.getFieldErrors().size() == 0);
	}
}
