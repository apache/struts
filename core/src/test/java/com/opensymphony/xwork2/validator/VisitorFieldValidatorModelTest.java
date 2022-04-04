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
import com.opensymphony.xwork2.test.TestBean2;

import java.util.*;

import org.easymock.EasyMock;


/**
 * VisitorFieldValidatorModelTest
 *
 * @author Jason Carreira
 *         Date: Mar 18, 2004 2:51:42 PM
 */
public class VisitorFieldValidatorModelTest extends XWorkTestCase {

    protected VisitorValidatorModelAction action;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        action = new VisitorValidatorModelAction();

        TestBean bean = action.getBean();
        Calendar cal = new GregorianCalendar(1900, 01, 01);
        bean.setBirth(cal.getTime());
        bean.setCount(-1);

        ActionConfig config = new ActionConfig.Builder("", "name", "").build();
        ActionInvocation invocation = EasyMock.createNiceMock(ActionInvocation.class);
        ActionProxy proxy = EasyMock.createNiceMock(ActionProxy.class);

        EasyMock.expect(invocation.getProxy()).andReturn(proxy).anyTimes();
        EasyMock.expect(invocation.getAction()).andReturn(null).anyTimes();
        EasyMock.expect(invocation.invoke()).andReturn(Action.SUCCESS).anyTimes();
        EasyMock.expect(proxy.getMethod()).andReturn("execute").anyTimes();
        EasyMock.expect(proxy.getConfig()).andReturn(config).anyTimes();


        EasyMock.replay(invocation);
        EasyMock.replay(proxy);

        ActionContext.getContext().setActionInvocation(invocation);

    }

    public void testModelFieldErrorsAddedWithoutFieldPrefix() throws Exception {
        container.getInstance(ActionValidatorManager.class).validate(action, null);
        assertTrue(action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();

        // the required string validation inherited from the VisitorValidatorTestAction
        assertTrue(fieldErrors.containsKey("context"));

        // the bean validation which is now at the top level because we set the appendPrefix to false
        assertTrue(fieldErrors.containsKey("name"));

        List<String> nameMessages = fieldErrors.get("name");
        assertEquals(1, nameMessages.size());

        String nameMessage = (String) nameMessages.get(0);
        assertEquals("You must enter a name.", nameMessage);
    }

    public void testModelFieldErrorsAddedWithoutFieldPrefixForInterface() throws Exception {
        TestBean origBean = action.getBean();
        TestBean2 bean = new TestBean2();
        bean.setBirth(origBean.getBirth());
        bean.setCount(origBean.getCount());
        action.setBean(bean);
        assertTrue(action.getBean() instanceof TestBean2);

        container.getInstance(ActionValidatorManager.class).validate(action, null);
        assertTrue(action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();

        // the required string validation inherited from the VisitorValidatorTestAction
        assertTrue(fieldErrors.containsKey("context"));

        // the bean validation which is now at the top level because we set the appendPrefix to false
        assertTrue(fieldErrors.containsKey("name"));

        List<String> nameMessages = fieldErrors.get("name");
        assertEquals(1, nameMessages.size());

        String nameMessage = nameMessages.get(0);
        assertEquals("You must enter a name.", nameMessage);

        // should also have picked up validation check for DataAware interface
        assertTrue(fieldErrors.containsKey("data"));

        List<String> dataMessages = fieldErrors.get("data");
        assertEquals(1, dataMessages.size());

        String dataMessage = dataMessages.get(0);
        assertEquals("You must enter a value for data.", dataMessage);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        ActionContext.setContext(null);
    }
}
