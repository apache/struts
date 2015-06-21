/*
 * $Id$
 *
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
package org.apache.struts.beanvalidation;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

import org.apache.struts.beanvalidation.actions.FieldAction;
import org.apache.struts.beanvalidation.actions.FieldActionDoExecute;
import org.apache.struts.beanvalidation.actions.FieldMatchAction;
import org.apache.struts.beanvalidation.actions.ModelDrivenAction;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BeanValidationInterceptorTest extends XWorkTestCase {


    public void testModelDrivenAction() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("bean-validation", "modelDrivenAction", null, null);
        ModelDrivenAction action = (ModelDrivenAction) baseActionProxy.getAction();
        action.getModel().setName(null);
        action.getModel().setEmail(null);
        action.getModel().getAddress().setStreet(null);
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = ((ValidationAware) baseActionProxy.getAction()).getFieldErrors();

        assertNotNull(fieldErrors);
        assertEquals(3, fieldErrors.size());
        assertTrue(fieldErrors.get("name").size() > 0);
        assertEquals(fieldErrors.get("name").get(0), "nameNotNull");
        assertTrue(fieldErrors.get("email").size() > 0);
        assertEquals(fieldErrors.get("email").get(0), "emailNotNull");
        assertTrue(fieldErrors.get("address.street").size() > 0);
        assertEquals(fieldErrors.get("address.street").get(0), "streetNotNull");
    }


    public void testModelDrivenActionEmailField() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("bean-validation", "modelDrivenAction", null, null);
        ModelDrivenAction action = (ModelDrivenAction) baseActionProxy.getAction();
        action.getModel().setName("name");
        action.getModel().setEmail("notamail");
        action.getModel().getAddress().setStreet("street");
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = ((ValidationAware) baseActionProxy.getAction()).getFieldErrors();

        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertTrue(fieldErrors.get("email").size() > 0);
        assertEquals(fieldErrors.get("email").get(0), "emailNotValid");
    }

    public void testModelDrivenActionSize() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("bean-validation", "modelDrivenAction", null, null);
        ModelDrivenAction action = (ModelDrivenAction) baseActionProxy.getAction();
        action.getModel().setName("j");
        action.getModel().setEmail("jogep@apache.org");
        action.getModel().getAddress().setStreet("st");
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = ((ValidationAware) baseActionProxy.getAction()).getFieldErrors();
        System.out.println(fieldErrors);
        assertNotNull(fieldErrors);
        assertEquals(2, fieldErrors.size());
        assertTrue(fieldErrors.get("name").size() > 0);
        assertEquals(fieldErrors.get("name").get(0), "nameSize");
        assertTrue(fieldErrors.get("address.street").size() > 0);
        assertEquals(fieldErrors.get("address.street").get(0), "streetSize");
    }

    public void testModelDrivenActionSuccess() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("bean-validation", "modelDrivenAction", null, null);
        ModelDrivenAction action = (ModelDrivenAction) baseActionProxy.getAction();
        action.getModel().setName("name");
        action.getModel().setEmail("jogep@apache.org");
        action.getModel().getAddress().setStreet("street");
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = ((ValidationAware) baseActionProxy.getAction()).getFieldErrors();

        assertNotNull(fieldErrors);
        assertEquals(0, fieldErrors.size());
    }

    public void testFieldAction() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("bean-validation", "fieldAction", null, null);
        FieldAction action = (FieldAction) baseActionProxy.getAction();
        action.setTest(" ");
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = ((ValidationAware) baseActionProxy.getAction()).getFieldErrors();

        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertTrue(fieldErrors.get("test").size() > 0);
    }

    public void testFieldMatchAction() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("bean-validation", "fieldMatchAction", null, null);
        FieldMatchAction action = (FieldMatchAction) baseActionProxy.getAction();
        action.setPassword("pass1");
        action.setConfirmPassword("pass2");
        action.setEmail("test1@mail.org");
        action.setConfirmEmail("test2@mail.org");
        baseActionProxy.execute();

        Collection<String> actionErrors = ((ValidationAware) baseActionProxy.getAction()).getActionErrors();
        System.out.println(actionErrors);

        assertNotNull(actionErrors);
        assertEquals(2, actionErrors.size());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(new XmlConfigurationProvider("bean-validation-test.xml"));
    }
}
