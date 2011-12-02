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

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.interceptor.DefaultWorkflowInterceptor;

import java.util.*;

import org.easymock.EasyMock;
import org.easymock.IAnswer;


/**
 * VisitorFieldValidatorTest
 *
 * @author Jason Carreira
 *         Created Aug 4, 2003 1:26:01 AM
 */
public class VisitorFieldValidatorTest extends XWorkTestCase {

    protected VisitorValidatorTestAction action;
    private Locale origLocale;


    @Override
    public void setUp() throws Exception {
        super.setUp();
        origLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        action = new VisitorValidatorTestAction();

        TestBean bean = action.getBean();
        Calendar cal = new GregorianCalendar(1900, 01, 01);
        bean.setBirth(cal.getTime());
        bean.setCount(-1);

        ActionConfig config = new ActionConfig.Builder("", "name", "").build();
        ActionInvocation invocation = EasyMock.createNiceMock(ActionInvocation.class);
        ActionProxy proxy = EasyMock.createNiceMock(ActionProxy.class);

        EasyMock.expect(invocation.getProxy()).andReturn(proxy).anyTimes();
        EasyMock.expect(invocation.getAction()).andReturn(action).anyTimes();
        EasyMock.expect(invocation.invoke()).andReturn(Action.SUCCESS).anyTimes();
        EasyMock.expect(proxy.getMethod()).andReturn("execute").anyTimes();
        EasyMock.expect(proxy.getConfig()).andReturn(config).anyTimes();
        

        EasyMock.replay(invocation);
        EasyMock.replay(proxy);

        ActionContext.getContext().setActionInvocation(invocation);
    }

    public void testArrayValidation() throws Exception {
        TestBean[] beanArray = action.getTestBeanArray();
        TestBean testBean = beanArray[0];
        testBean.setName("foo");
        validate("validateArray");

        assertTrue(action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();

        //4 errors for the array, one for context
        assertEquals(5, fieldErrors.size());
        assertTrue(fieldErrors.containsKey("testBeanArray[1].name"));

        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));

        List<String> errors = fieldErrors.get("testBeanArray[1].name");
        assertEquals(1, errors.size());
        errors = fieldErrors.get("testBeanArray[2].name");
        assertEquals(1, errors.size());
        errors = fieldErrors.get("testBeanArray[3].name");
        assertEquals(1, errors.size());
        errors = fieldErrors.get("testBeanArray[4].name");
        assertEquals(1, errors.size());
    }

    public void testBeanMessagesUseBeanResourceBundle() throws Exception {
        validate("beanMessageBundle");
        assertTrue(action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertTrue(fieldErrors.containsKey("bean.count"));

        List<String> beanCountMessages = fieldErrors.get("bean.count");
        assertEquals(1, beanCountMessages.size());

        String beanCountMessage = beanCountMessages.get(0);
        assertEquals("bean: Count must be between 1 and 100, current value is -1.", beanCountMessage);
    }

    public void testCollectionValidation() throws Exception {
        List testBeanList = action.getTestBeanList();
        TestBean testBean = (TestBean) testBeanList.get(0);
        testBean.setName("foo");
        validate("validateList");

        assertTrue(action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();

        //4 for the list, 1 for context
        assertEquals(5, fieldErrors.size());
        assertTrue(fieldErrors.containsKey("testBeanList[1].name"));

        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));

        List<String> errors = fieldErrors.get("testBeanList[1].name");
        assertEquals(1, errors.size());
        errors = fieldErrors.get("testBeanList[2].name");
        assertEquals(1, errors.size());
        errors = fieldErrors.get("testBeanList[3].name");
        assertEquals(1, errors.size());
        errors = fieldErrors.get("testBeanList[4].name");
        assertEquals(1, errors.size());
    }

    public void testContextIsOverriddenByContextParamInValidationXML() throws Exception {
        validate("visitorValidationAlias");
        assertTrue(action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertEquals(3, fieldErrors.size());
        assertTrue(fieldErrors.containsKey("bean.count"));
        assertTrue(fieldErrors.containsKey("bean.name"));
        assertTrue(!fieldErrors.containsKey("bean.birth"));

        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));
    }

    public void testContextIsPropagated() throws Exception {
        validate("visitorValidation");
        assertTrue(action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertEquals(3, fieldErrors.size());
        assertTrue(!fieldErrors.containsKey("bean.count"));
        assertTrue(fieldErrors.containsKey("bean.name"));
        assertTrue(fieldErrors.containsKey("bean.birth"));

        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));
    }

    public void testVisitorChildValidation() throws Exception {
        validate("visitorChildValidation");
        assertTrue(action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertEquals(5, fieldErrors.size());
        assertTrue(!fieldErrors.containsKey("bean.count"));
        assertTrue(fieldErrors.containsKey("bean.name"));
        assertTrue(fieldErrors.containsKey("bean.birth"));

        assertTrue(fieldErrors.containsKey("bean.child.name"));
        assertTrue(fieldErrors.containsKey("bean.child.birth"));
        
        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        ActionContext.setContext(null);
        Locale.setDefault(origLocale);
    }

    private void validate(String context) throws ValidationException {
        ActionContext actionContext = ActionContext.getContext();
        actionContext.setName(context);
        container.getInstance(ActionValidatorManager.class).validate(action, context);
    }
}
