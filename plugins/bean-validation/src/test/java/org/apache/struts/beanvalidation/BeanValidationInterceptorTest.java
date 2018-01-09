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
package org.apache.struts.beanvalidation;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import org.apache.struts.beanvalidation.actions.FieldAction;
import org.apache.struts.beanvalidation.actions.FieldMatchAction;
import org.apache.struts.beanvalidation.actions.ModelDrivenAction;
import org.apache.struts.beanvalidation.actions.ValidateGroupAction;

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

    public void testModelDrivenActionSkipValidationByInterface() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("bean-validation", "modelDrivenActionSkipValidationByInterface", null, null);
        ModelDrivenAction action = (ModelDrivenAction) baseActionProxy.getAction();
        action.getModel().setName(null);
        action.getModel().setEmail(null);
        action.getModel().getAddress().setStreet(null);
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


    public void testValidationGroupActionStandard() throws Exception {
        ActionProxy baseActionProxy = getValidateGroupAction("actionStandard");
        ValidateGroupAction action = (ValidateGroupAction) baseActionProxy.getAction();
        action.getModel().setName(null);
        action.getModel().setEmail(null);
        action.getModel().getAddress().setStreet(null);
        baseActionProxy.execute();
        assertEquals("every properties not valid", 3, ((ValidationAware) baseActionProxy.getAction()).getFieldErrors().size());
    }


    public void testValidationGroupActionDefault() throws Exception {
        ActionProxy baseActionProxy = getValidateGroupAction("actionDefault");
        ValidateGroupAction action = (ValidateGroupAction) baseActionProxy.getAction();
        action.getModel().setName(null);
        action.getModel().setEmail(null);
        action.getModel().getAddress().setStreet(null);

        baseActionProxy.execute();
        assertEquals("every properties not valid", 3, ((ValidationAware) baseActionProxy.getAction()).getFieldErrors().size());
    }

    public void testValidationGroupActionNameChecks() throws Exception {
        ActionProxy baseActionProxy = getValidateGroupAction("actionNameChecks");
        baseActionProxy.execute();
        Map<String, List<String>> fieldErrors = ((ValidateGroupAction) baseActionProxy.getAction()).getFieldErrors();
        assertEquals(1, fieldErrors.size());
        assertEquals("check name-property @NotNull", "nameNotNull", fieldErrors.get("name").get(0));

        baseActionProxy = getValidateGroupAction("actionNameChecks");
        ((ValidateGroupAction) baseActionProxy.getAction()).getModel().setName("a");
        baseActionProxy.execute();
        fieldErrors = ((ValidateGroupAction) baseActionProxy.getAction()).getFieldErrors();
        assertEquals(1, fieldErrors.size());
        assertEquals("check name-property @Size", "nameSize", fieldErrors.get("name").get(0));

        baseActionProxy = getValidateGroupAction("actionNameChecks");
        ((ValidateGroupAction) baseActionProxy.getAction()).getModel().setName("aName");
        baseActionProxy.execute();
        assertTrue("name-property valid", ((ValidateGroupAction) baseActionProxy.getAction()).getFieldErrors().isEmpty());
    }


    public void testValidationGroupActionStreetChecks() throws Exception {
        ActionProxy baseActionProxy = getValidateGroupAction("actionStreetChecks");
        baseActionProxy.execute();
        Map<String, List<String>> fieldErrors = ((ValidateGroupAction) baseActionProxy.getAction()).getFieldErrors();
        assertEquals(1, fieldErrors.size());
        assertEquals("street-property @NotNull", "streetNotNull", fieldErrors.get("address.street").get(0));

        baseActionProxy = getValidateGroupAction("actionStreetChecks");
        ((ValidateGroupAction) baseActionProxy.getAction()).getModel().getAddress().setStreet("a");
        baseActionProxy.execute();
        fieldErrors = ((ValidateGroupAction) baseActionProxy.getAction()).getFieldErrors();
        assertEquals(1, fieldErrors.size());
        assertEquals("street-property @Size", "streetSize", fieldErrors.get("address.street").get(0));

        baseActionProxy = getValidateGroupAction("actionStreetChecks");
        ((ValidateGroupAction) baseActionProxy.getAction()).getModel().getAddress().setStreet("aStreet");
        baseActionProxy.execute();
        assertTrue("street-property valid", ((ValidateGroupAction) baseActionProxy.getAction()).getFieldErrors().isEmpty());
    }


    public void testValidationGroupActionNameAndStreetChecks() throws Exception {
        ActionProxy baseActionProxy = getValidateGroupAction("actionNameAndStreetChecks");
        baseActionProxy.execute();
        Map<String, List<String>> fieldErrors = ((ValidateGroupAction) baseActionProxy.getAction()).getFieldErrors();
        assertEquals(2, fieldErrors.size());
        assertTrue("name-property @NotNull", fieldErrors.containsKey("name"));
        assertTrue("street-property @NotNull", fieldErrors.containsKey("address.street"));

        baseActionProxy = getValidateGroupAction("actionNameAndStreetChecks");
        ((ValidateGroupAction) baseActionProxy.getAction()).getModel().setName("aName");
        ((ValidateGroupAction) baseActionProxy.getAction()).getModel().getAddress().setStreet("aStreet");
        baseActionProxy.execute();
        assertTrue("name and street-property valid", ((ValidateGroupAction) baseActionProxy.getAction()).getFieldErrors().isEmpty());
    }

    public void testValidationGroupActionMultiGroupsChecks() throws Exception {
        ActionProxy baseActionProxy = getValidateGroupAction("actionMultiGroupsChecks");
        baseActionProxy.execute();
        Map<String, List<String>> fieldErrors = ((ValidateGroupAction) baseActionProxy.getAction()).getFieldErrors();
        assertEquals(2, fieldErrors.size());
        assertTrue("name-property @NotNull", fieldErrors.containsKey("name"));
        assertTrue("firstName-property @NotBlank", fieldErrors.containsKey("firstName"));

        baseActionProxy = getValidateGroupAction("actionMultiGroupsChecks");
        ((ValidateGroupAction) baseActionProxy.getAction()).getModel().setName("aName");
        ((ValidateGroupAction) baseActionProxy.getAction()).getModel().setFirstName("aFirstName");
        baseActionProxy.execute();
        assertTrue("name and firstName-property valid", ((ValidateGroupAction) baseActionProxy.getAction()).getFieldErrors().isEmpty());
    }

    public void testValidationGroupActionLongNameChecks() throws Exception {
        ActionProxy baseActionProxy = getValidateGroupAction("actionLongNameChecks");
        baseActionProxy.execute();
        Map<String, List<String>> fieldErrors = ((ValidateGroupAction) baseActionProxy.getAction()).getFieldErrors();
        assertEquals(3, fieldErrors.size());
        assertTrue("name-property @NotNull", fieldErrors.containsKey("name"));
        assertTrue("email-property @NotNull", fieldErrors.containsKey("email"));
        assertTrue("street-property @NotNull", fieldErrors.containsKey("address.street"));

        baseActionProxy = getValidateGroupAction("actionLongNameChecks");
        ((ValidateGroupAction) baseActionProxy.getAction()).getModel().setName("toShortName");
        ((ValidateGroupAction) baseActionProxy.getAction()).getModel().setEmail("actionLongNameChecks@mail.org");
        ((ValidateGroupAction) baseActionProxy.getAction()).getModel().getAddress().setStreet("aStreet");
        baseActionProxy.execute();
        fieldErrors = ((ValidateGroupAction) baseActionProxy.getAction()).getFieldErrors();
        assertEquals(1, fieldErrors.size());
        assertEquals("name-property @Size", "nameSize20", fieldErrors.get("name").get(0));


        baseActionProxy = getValidateGroupAction("actionLongNameChecks");
        ((ValidateGroupAction) baseActionProxy.getAction()).getModel().setName("this_is_a_really_long_Name");
        ((ValidateGroupAction) baseActionProxy.getAction()).getModel().setEmail("actionLongNameChecks@mail.org");
        ((ValidateGroupAction) baseActionProxy.getAction()).getModel().getAddress().setStreet("aStreet");
        baseActionProxy.execute();
        assertTrue("every properties not valid", ((ValidateGroupAction) baseActionProxy.getAction()).getFieldErrors().isEmpty());
    }

    private ActionProxy getValidateGroupAction(String methodName) {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("bean-validation", "validateGroupActions", methodName, null);
        ValidateGroupAction action = (ValidateGroupAction) baseActionProxy.getAction();
        action.getModel().setName(null);
        action.getModel().setEmail(null);
        action.getModel().getAddress().setStreet(null);
        return baseActionProxy;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(new XmlConfigurationProvider("bean-validation-test.xml"));
    }
}