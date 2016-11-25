/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
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
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.validators.URLValidator;

import java.util.regex.Pattern;

/**
 * Test case for URLValidator
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class URLValidatorTest extends XWorkTestCase {

	
	ValueStack stack;
	ActionContext actionContext;
	
	public void testAcceptNullValueForMutualExclusionOfValidators() throws Exception {
		
		URLValidator validator = new URLValidator();
		validator.setValidatorContext(new GenericValidatorContext(new Object()));
		validator.setFieldName("testingUrl1");
        validator.setValueStack(ActionContext.getContext().getValueStack());
        validator.validate(new MyObject());
		
		assertFalse(validator.getValidatorContext().hasErrors());
		assertFalse(validator.getValidatorContext().hasActionErrors());
		assertFalse(validator.getValidatorContext().hasActionMessages());
		assertFalse(validator.getValidatorContext().hasFieldErrors());
	}
	
	public void testInvalidEmptyValue() throws Exception {
		
		URLValidator validator = new URLValidator();
		validator.setValidatorContext(new GenericValidatorContext(new Object()));
		validator.setFieldName("testingUrl2");
        validator.setValueStack(ActionContext.getContext().getValueStack());
        validator.validate(new MyObject());
		
		assertFalse(validator.getValidatorContext().hasErrors());
		assertFalse(validator.getValidatorContext().hasActionErrors());
		assertFalse(validator.getValidatorContext().hasActionMessages());
		assertFalse(validator.getValidatorContext().hasFieldErrors());
	}
	
	public void testInvalidValue() throws Exception {
		
		URLValidator validator = new URLValidator();
		validator.setValidatorContext(new GenericValidatorContext(new Object()));
		validator.setFieldName("testingUrl3");
        validator.setValueStack(ActionContext.getContext().getValueStack());
        validator.validate(new MyObject());
		
		assertTrue(validator.getValidatorContext().hasErrors());
		assertFalse(validator.getValidatorContext().hasActionErrors());
		assertFalse(validator.getValidatorContext().hasActionMessages());
		assertTrue(validator.getValidatorContext().hasFieldErrors());
	}
	
	
	public void testValidUrl1() throws Exception {
		
		URLValidator validator = new URLValidator();
		validator.setValidatorContext(new GenericValidatorContext(new Object()));
		validator.setFieldName("testingUrl4");
        validator.setValueStack(ActionContext.getContext().getValueStack());
        validator.validate(new MyObject());
		
		assertFalse(validator.getValidatorContext().hasErrors());
		assertFalse(validator.getValidatorContext().hasActionErrors());
		assertFalse(validator.getValidatorContext().hasActionMessages());
		assertFalse(validator.getValidatorContext().hasFieldErrors());
	}
	
	public void testValidUrl2() throws Exception {
		
		URLValidator validator = new URLValidator();
		validator.setValidatorContext(new GenericValidatorContext(new Object()));
		validator.setFieldName("testingUrl5");
        validator.setValueStack(ActionContext.getContext().getValueStack());
        validator.validate(new MyObject());
		
		assertFalse(validator.getValidatorContext().hasErrors());
		assertFalse(validator.getValidatorContext().hasActionErrors());
		assertFalse(validator.getValidatorContext().hasActionMessages());
		assertFalse(validator.getValidatorContext().hasFieldErrors());
	}
	
	public void testValidUrlWithRegex() throws Exception {
		URLValidator validator = new URLValidator();

        validator.setUrlRegex("^myapp:\\/\\/[a-z]*\\.com$");

        Pattern pattern = Pattern.compile(validator.getUrlRegex());

        assertTrue(pattern.matcher("myapp://test.com").matches());
        assertFalse(pattern.matcher("myap://test.com").matches());
	}

	public void testValidUrlWithRegexExpression() throws Exception {
		URLValidator validator = new URLValidator();
        ActionContext.getContext().getValueStack().push(new MyAction());
        validator.setValueStack(ActionContext.getContext().getValueStack());
        validator.setUrlRegexExpression("${urlRegex}");

        Pattern pattern = Pattern.compile(validator.getUrlRegex());

        assertTrue(pattern.matcher("myapp://test.com").matches());
        assertFalse(pattern.matcher("myap://test.com").matches());
	}

	public void testValidUrlWithDefaultRegex() throws Exception {
		URLValidator validator = new URLValidator();

        Pattern pattern = Pattern.compile(validator.getUrlRegex(), Pattern.CASE_INSENSITIVE);

        assertFalse(pattern.matcher("myapp://test.com").matches());
        assertFalse(pattern.matcher("myap://test.com").matches());
        assertFalse(pattern.matcher("").matches());
        assertFalse(pattern.matcher("   ").matches());
        assertFalse(pattern.matcher("no url").matches());
        assertFalse(pattern.matcher("http://example.com////////////////////////////////////////////////////////////////////////////////////??").matches());

        assertTrue(pattern.matcher("http://www.legalspace.com/__media__/js/netsoltrademark.php?d=www.a-vos-travaux.fr%2Facheter-un-aspirateur-sans-sac-pas-cher%2F").matches());
        assertTrue(pattern.matcher("http://www.duadmin.isaev.Infoduadmin.Isaev.info/?a%5B%5D=%3Ca%20href%3Dhttp%3A%2F%2Fwww.aspert.fr%2Fun-seche-cheveux-lisseur-est-il-vraiment-utile%2F%3Eseche%20cheveux%20dyson%20test%3C%2Fa").matches());
        assertTrue(pattern.matcher("http://netsol-underconstruction-page-monitor-1.com/__media__/js/netsoltrademark.php?d=www.le-soutien-scolaire.fr%2Favis-et-test-comparatifs-des-robots-multifonctions%2F").matches());

        assertTrue(pattern.matcher("http://www.opensymphony.com").matches());
        assertTrue(pattern.matcher("https://www.opensymphony.com").matches());
        assertTrue(pattern.matcher("https://www.opensymphony.com:443/login").matches());
        assertTrue(pattern.matcher("http://localhost:8080/myapp").matches());
    }

	@Override
    protected void setUp() throws Exception {
	    super.setUp();
		stack = ActionContext.getContext().getValueStack();
		actionContext = ActionContext.getContext();
	}
	
	@Override
    protected void tearDown() throws Exception {
	    super.tearDown();
		stack = null;
		actionContext = null;
	}
	
	
	class MyObject {
		public String getTestingUrl1() {
			return null;
		}
		
		public String getTestingUrl2() {
			return "";
		}
		
		public String getTestingUrl3() {
			return "sasdasd@asddd";
		}
		
		public String getTestingUrl4() {
			//return "http://yahoo.com/";
			return "http://www.jroller.com1?qwe=qwe";
		}
		
		public String getTestingUrl5() {
			return "http://yahoo.com/articles?id=123\n";
		}
	}

    class MyAction {

        public String getUrlRegex() {
            return "myapp:\\/\\/[a-z]*\\.com";
        }
    }
}
