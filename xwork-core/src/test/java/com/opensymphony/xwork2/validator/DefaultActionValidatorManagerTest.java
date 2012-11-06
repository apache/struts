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

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.StubValueStack;
import com.opensymphony.xwork2.TestBean;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.test.DataAware2;
import com.opensymphony.xwork2.test.SimpleAction2;
import com.opensymphony.xwork2.test.SimpleAction3;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.fs.DefaultFileManager;
import com.opensymphony.xwork2.util.fs.DefaultFileManagerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * DefaultActionValidatorManagerTest
 *
 * @author Jason Carreira
 * @author tm_jee 
 * @version $Date$ $Id$
 */
public class DefaultActionValidatorManagerTest extends XWorkTestCase {

    protected final String alias = "validationAlias";

    DefaultActionValidatorManager actionValidatorManager;
    Mock mockValidatorFileParser;
    Mock mockValidatorFactory;
    ValueStack stubValueStack;

    @Override
    protected void setUp() throws Exception {
        actionValidatorManager = new DefaultActionValidatorManager();
        super.setUp();
        mockValidatorFileParser = new Mock(ValidatorFileParser.class);
        actionValidatorManager.setValidatorFileParser((ValidatorFileParser)mockValidatorFileParser.proxy());

        mockValidatorFactory = new Mock(ValidatorFactory.class);
        actionValidatorManager.setValidatorFactory((ValidatorFactory)mockValidatorFactory.proxy());

        stubValueStack = new StubValueStack();
        ActionContext.setContext(new ActionContext(new HashMap<String, Object>()));
        ActionContext.getContext().setValueStack(stubValueStack);

        DefaultFileManagerFactory factory = new DefaultFileManagerFactory();
        factory.setContainer(container);
        factory.setFileManager(new DefaultFileManager());
        actionValidatorManager.setFileManagerFactory(factory);
    }

    @Override
    protected void tearDown() throws Exception {
        actionValidatorManager = null;
        super.tearDown();
        mockValidatorFactory = null;
        mockValidatorFileParser = null;
    }


    public void testBuildValidatorKey() {
        String validatorKey = DefaultActionValidatorManager.buildValidatorKey(SimpleAction.class, alias);
        assertEquals(SimpleAction.class.getName() + "/" + alias, validatorKey);
    }

    public void testBuildsValidatorsForAlias() {
        mockValidatorFileParser.expectAndReturn("parseActionValidatorConfigs",
                C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("com/opensymphony/xwork2/SimpleAction-validation.xml")),
                new ArrayList());
        mockValidatorFileParser.expectAndReturn("parseActionValidatorConfigs",
                C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("com/opensymphony/xwork2/SimpleAction-validationAlias-validation.xml")),
                new ArrayList());
         actionValidatorManager.getValidators(SimpleAction.class, alias);
        mockValidatorFileParser.verify();
    }

    public void testBuildsValidatorsForAliasError() {
        boolean pass = false;
        try {
            mockValidatorFileParser.expectAndReturn("parseActionValidatorConfigs",
                C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("com/opensymphony/xwork2/TestBean-validation.xml")),
                new ArrayList());
            mockValidatorFileParser.expectAndThrow("parseActionValidatorConfigs",
                C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("com/opensymphony/xwork2/TestBean-badtest-validation.xml")),
                new ConfigurationException());
            List validatorList = actionValidatorManager.getValidators(TestBean.class, "badtest");
        } catch (XWorkException ex) {
            pass = true;
        }
        mockValidatorFileParser.verify();
        assertTrue("Didn't throw exception on load failure", pass);
    }


    public void testGetValidatorsForInterface() {
        mockValidatorFileParser.expectAndReturn("parseActionValidatorConfigs",
                C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("com/opensymphony/xwork2/test/DataAware-validation.xml")),
                new ArrayList());
        mockValidatorFileParser.expectAndReturn("parseActionValidatorConfigs",
                C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("com/opensymphony/xwork2/test/DataAware-validationAlias-validation.xml")),
                new ArrayList());
        mockValidatorFileParser.expectAndReturn("parseActionValidatorConfigs",
                C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("com/opensymphony/xwork2/test/DataAware2-validation.xml")),
                new ArrayList());
        actionValidatorManager.getValidators(DataAware2.class, alias);
        mockValidatorFileParser.verify();
    }

    public void testGetValidatorsFromInterface() {
        mockValidatorFileParser.expectAndReturn("parseActionValidatorConfigs",
                C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("com/opensymphony/xwork2/SimpleAction-validation.xml")),
                new ArrayList());
        mockValidatorFileParser.expectAndReturn("parseActionValidatorConfigs",
                C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("com/opensymphony/xwork2/SimpleAction-validationAlias-validation.xml")),
                new ArrayList());
        mockValidatorFileParser.expectAndReturn("parseActionValidatorConfigs",
                C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("com/opensymphony/xwork2/test/DataAware-validation.xml")),
                new ArrayList());
        mockValidatorFileParser.expectAndReturn("parseActionValidatorConfigs",
                C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("com/opensymphony/xwork2/test/DataAware-validationAlias-validation.xml")),
                new ArrayList());
        actionValidatorManager.getValidators(SimpleAction3.class, alias);
        mockValidatorFileParser.verify();
    }

    public void testSameAliasWithDifferentClass() {
        mockValidatorFileParser.expectAndReturn("parseActionValidatorConfigs",
                C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("com/opensymphony/xwork2/SimpleAction-validation.xml")),
                new ArrayList());
        mockValidatorFileParser.expectAndReturn("parseActionValidatorConfigs",
                C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("com/opensymphony/xwork2/SimpleAction-validationAlias-validation.xml")),
                new ArrayList());
        mockValidatorFileParser.expectAndReturn("parseActionValidatorConfigs",
                C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("com/opensymphony/xwork2/test/SimpleAction2-validation.xml")),
                new ArrayList());
        mockValidatorFileParser.expectAndReturn("parseActionValidatorConfigs",
                C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("com/opensymphony/xwork2/test/SimpleAction2-validationAlias-validation.xml")),
                new ArrayList());
        actionValidatorManager.getValidators(SimpleAction.class, alias);
        actionValidatorManager.getValidators(SimpleAction2.class, alias);
        mockValidatorFileParser.verify();
    }

    /**
     * Test to verify WW-3850.
     *
     * @since 2.3.5
     */
    public void testBuildsValidatorsForClassError() {
        // for this test we need to have a file manager with reloadingConfigs to true
        container.getInstance(FileManagerFactory.class).getFileManager().setReloadingConfigs(true);
        // no validator found, but no check on file since it is not in cache
        actionValidatorManager.getValidators(List.class, null);
        // this second call will try reload a not existing file
        // and causes a NPE (see WW-3850)
        try {
            actionValidatorManager.getValidators(List.class, null);
        } catch (Exception e) {
            fail("Exception occurred " + e);
        }
    }

    /*
    // TODO: this all need to be converted to real unit tests

    public void testSkipUserMarkerActionLevelShortCircuit() {
        // get validators
        List validatorList = actionValidatorManager.getValidators(User.class, null);
        assertEquals(10, validatorList.size());

        try {
            User user = new User();
            user.setName("Mark");
            user.setEmail("bad_email");
            user.setEmail2("bad_email");

            ValidatorContext context = new GenericValidatorContext(user);
            actionValidatorManager.validate(user, null, context);
            assertTrue(context.hasFieldErrors());

            // check field errors
            List l = (List) context.getFieldErrors().get("email");
            assertNotNull(l);
            assertEquals(1, l.size());
            assertEquals("Not a valid e-mail.", l.get(0));
            l = (List) context.getFieldErrors().get("email2");
            assertNotNull(l);
            assertEquals(2, l.size());
            assertEquals("Not a valid e-mail2.", l.get(0));
            assertEquals("Email2 not from the right company.", l.get(1));

            // check action errors
            assertTrue(context.hasActionErrors());
            l = (List) context.getActionErrors();
            assertNotNull(l);
            assertEquals(2, l.size()); // both expression test failed see User-validation.xml
            assertEquals("Email does not start with mark", l.get(0));
        } catch (ValidationException ex) {
            ex.printStackTrace();
            fail("Validation error: " + ex.getMessage());
        }
    }

    public void testSkipAllActionLevelShortCircuit2() {
        // get validators
        List validatorList = actionValidatorManager.getValidators(User.class, null);
        assertEquals(10, validatorList.size());

        try {
            User user = new User();
            user.setName("Mark");
            // * mark both email to starts with mark to get pass the action-level validator,
            // so we could concentrate on testing the field-level validators (User-validation.xml)
            // * make both email the same to pass the action-level validator at 
            // UserMarker-validation.xml
            user.setEmail("mark_bad_email_for_field_val@foo.com");
            user.setEmail2("mark_bad_email_for_field_val@foo.com");

            ValidatorContext context = new GenericValidatorContext(user);
            actionValidatorManager.validate(user, null, context);
            assertTrue(context.hasFieldErrors());

            // check field errors
            // we have an error in this field level, email does not ends with mycompany.com
            List l = (List) context.getFieldErrors().get("email");
            assertNotNull(l);
            assertEquals(1, l.size()); // because email-field-val is short-circuit
            assertEquals("Email not from the right company.", l.get(0));

            
            // check action errors
            l = (List) context.getActionErrors();
            assertFalse(context.hasActionErrors());
            assertEquals(0, l.size());
            
            
        } catch (ValidationException ex) {
            ex.printStackTrace();
            fail("Validation error: " + ex.getMessage());
        }
    }

    
    public void testActionLevelShortCircuit() throws Exception {
    	
    	List validatorList = actionValidatorManager.getValidators(User.class, null);
        assertEquals(10, validatorList.size());
        
        User user = new User();
        // all fields will trigger error, but sc of action-level, cause it to not appear
        user.setName(null);		
        user.setEmail("tmjee(at)yahoo.co.uk");
        user.setEmail("tm_jee(at)yahoo.co.uk");
        
        ValidatorContext context = new GenericValidatorContext(user);
        actionValidatorManager.validate(user, null, context);
    	
    	// check field level errors
        // shouldn't have any because action error prevents validation of anything else
        List l = (List) context.getFieldErrors().get("email2");
        assertNull(l);
    	
    	
        // check action errors
        assertTrue(context.hasActionErrors());
        l = (List) context.getActionErrors();
        assertNotNull(l);
        // we only get one, because UserMarker-validation.xml action-level validator
        // already sc it   :-)
        assertEquals(1, l.size()); 
        assertEquals("Email not the same as email2", l.get(0));
    }
    
    
    public void testShortCircuitNoErrors() {
        // get validators
        List validatorList = actionValidatorManager.getValidators(User.class, null);
        assertEquals(10, validatorList.size());

        try {
            User user = new User();
            user.setName("Mark");
            user.setEmail("mark@mycompany.com");
            user.setEmail2("mark@mycompany.com");

            ValidatorContext context = new GenericValidatorContext(user);
            actionValidatorManager.validate(user, null, context);
            assertFalse(context.hasErrors());
        } catch (ValidationException ex) {
            ex.printStackTrace();
            fail("Validation error: " + ex.getMessage());
        }
    }
    
    public void testFieldErrorsOrder() throws Exception {
    	ValidationOrderAction action = new ValidationOrderAction();
    	actionValidatorManager.validate(action, "actionContext");
    	Map fieldErrors = action.getFieldErrors();
    	Iterator i = fieldErrors.entrySet().iterator();
    	
    	assertNotNull(fieldErrors);
    	assertEquals(fieldErrors.size(), 12);
    	
    	
    	Map.Entry e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "username");
    	assertEquals(((List)e.getValue()).get(0), "username required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "password");
    	assertEquals(((List)e.getValue()).get(0), "password required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "confirmPassword");
    	assertEquals(((List)e.getValue()).get(0), "confirm password required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "firstName");
    	assertEquals(((List)e.getValue()).get(0), "first name required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "lastName");
    	assertEquals(((List)e.getValue()).get(0), "last name required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "city");
    	assertEquals(((List)e.getValue()).get(0), "city is required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "province");
    	assertEquals(((List)e.getValue()).get(0), "province is required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "country");
    	assertEquals(((List)e.getValue()).get(0), "country is required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "postalCode");
    	assertEquals(((List)e.getValue()).get(0), "postal code is required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "email");
    	assertEquals(((List)e.getValue()).get(0), "email is required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "website");
    	assertEquals(((List)e.getValue()).get(0), "website is required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "passwordHint");
    	assertEquals(((List)e.getValue()).get(0), "password hint is required");
    	
    }
    */
}
