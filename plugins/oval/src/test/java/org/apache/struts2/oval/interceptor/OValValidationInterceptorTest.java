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
package org.apache.struts2.oval.interceptor;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import net.sf.oval.configuration.Configurer;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OValValidationInterceptorTest extends XWorkTestCase {
    public void testSimpleFieldsXML() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleFieldsXML", null, null);
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = ((ValidationAware) baseActionProxy.getAction()).getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(2, fieldErrors.size());
        assertValue(fieldErrors, "firstName", Arrays.asList("firstName cannot be null"));
        assertValue(fieldErrors, "lastName", Arrays.asList("lastName cannot be null"));
    }

     public void testSimpleFieldsJPAAnnotations() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleFieldsJPA", null, null);
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = ((ValidationAware) baseActionProxy.getAction()).getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertValue(fieldErrors, "firstName", Arrays.asList("firstName cannot be null"));
    }

    public void testValidationInMethods() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "validationInMethods", null, null);
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = ((ValidationAware) baseActionProxy.getAction()).getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(4, fieldErrors.size());
        assertValue(fieldErrors, "name", Arrays.asList("name cannot be null"));
        assertValue(fieldErrors, "SisyphusHasTheAnswer", Arrays.asList("SisyphusHasTheAnswer() cannot be null"));
        assertValue(fieldErrors, "thereAnyMeaningInLife", Arrays.asList("thereAnyMeaningInLife cannot be null"));
        assertValue(fieldErrors, "theManingOfLife", Arrays.asList("theManingOfLife cannot be null"));
    }

    public void testSimpleFieldsInheritedXML() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleFieldsXMLChild", null, null);
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = ((ValidationAware) baseActionProxy.getAction()).getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(3, fieldErrors.size());
        assertValue(fieldErrors, "firstName", Arrays.asList("firstName cannot be null"));
        assertValue(fieldErrors, "lastName", Arrays.asList("lastName cannot be null"));
        assertValue(fieldErrors, "middleName", Arrays.asList("middleName cannot be null"));
    }

    public void testSlashesInNameWithWildcardsHitsCache() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleFieldsXML/test", null, null);
        baseActionProxy.execute();

        ActionProxy baseActionProxy2 = actionProxyFactory.createActionProxy("oval", "simpleFieldsXML/test2", null, null);
        baseActionProxy2.execute();

        DummyDefaultOValValidationManager manager = (DummyDefaultOValValidationManager) container.getInstance(OValValidationManager.class);
        assertEquals(1, manager.getCache().size());
    }

    public void testXMLLoadCaching() {
        OValValidationManager manager = container.getInstance(OValValidationManager.class);
        List<Configurer> configurers = manager.getConfigurers(SimpleFieldsXML.class, "simpleFieldsXMLCaching", false);
        assertNotNull(configurers);
        assertEquals(2, configurers.size());

        //load again and check it is the same instance
        List<Configurer> configurers2 = manager.getConfigurers(SimpleFieldsXML.class, "simpleFieldsXMLCaching", false);
        assertNotNull(configurers2);
        assertEquals(2, configurers2.size());
        assertSame(configurers.get(0), configurers2.get(0));
    }

    public void testSimpleField() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleField", null, null);
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = ((ValidationAware) baseActionProxy.getAction()).getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertValue(fieldErrors, "name", Arrays.asList("name cannot be null"));
    }

    public void testSimpleFieldNegative() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleField", null, null);
        SimpleField action = (SimpleField) baseActionProxy.getAction();
        action.setName("123");
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = action.getFieldErrors();

        assertNotNull(fieldErrors);
        assertEquals(0, fieldErrors.size());
    }

    public void testSimpleFieldTooLong() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleFieldTooLong", null, null);
        SimpleField action = (SimpleField) baseActionProxy.getAction();
        action.setName("12367");
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = action.getFieldErrors();

        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertValue(fieldErrors, "name", Arrays.asList("name is not between 0 and 3 characters long"));
        assertValue(fieldErrors, "name", Arrays.asList("name is not between 0 and 3 characters long"));
    }

    public void testSimpleFieldMultipleValidators() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleField", null, null);
        SimpleField action = (SimpleField) baseActionProxy.getAction();
        action.setName("12345");
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertNotNull(fieldErrors.get("name"));
    }

    public void testSimpleMethod() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleMethod", null, null);
        SimpleMethod action = (SimpleMethod) baseActionProxy.getAction();
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertNotNull(fieldErrors.get("someName"));
    }

    public void testSimpleFieldOGNLExpression() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleFieldOGNL", null, null);
        SimpleFieldOGNLExpression action = (SimpleFieldOGNLExpression) baseActionProxy.getAction();
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertNotNull(fieldErrors.get("name"));
    }

    public void testFieldsWithMultipleProfiles() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "fieldsWidthProfiles13", null, null);
        FieldsWithProfiles action = (FieldsWithProfiles) baseActionProxy.getAction();
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(2, fieldErrors.size());
        assertNotNull(fieldErrors.get("firstName"));
        assertNotNull(fieldErrors.get("lastName"));
    }

    public void testSimpleFieldOGNLExpressionNegative() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleFieldOGNL", null, null);
        SimpleFieldOGNLExpression action = (SimpleFieldOGNLExpression) baseActionProxy.getAction();
        action.setName("Meursault");
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(0, fieldErrors.size());
    }

    public void testSimpleFieldI18n() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleFieldI18n", null, null);
        SimpleFieldI18n action = (SimpleFieldI18n) baseActionProxy.getAction();
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = action.getFieldErrors();

        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertValue(fieldErrors, "name", Arrays.asList("name cannot be null"));
    }

    public void testSimpleFieldI18n2() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleFieldI18n", null, null);
        SimpleFieldI18n action = (SimpleFieldI18n) baseActionProxy.getAction();
        action.setName("123123");
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = action.getFieldErrors();

        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertValue(fieldErrors, "name", Arrays.asList("name value is too long, allowed length is 3"));
    }

    public void testSimpleFieldI18nDefaultKey() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleFieldI18nDefaultKey", null, null);
        SimpleFieldI18nDefaultKey action = (SimpleFieldI18nDefaultKey) baseActionProxy.getAction();
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = action.getFieldErrors();

        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertValue(fieldErrors, "name", Arrays.asList("notnull.field"));
    }


    public void testProgrammaticValidation() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleField", null, null);
        SimpleField action = (SimpleField) baseActionProxy.getAction();
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = ((ValidationAware) baseActionProxy.getAction()).getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertValue(fieldErrors, "name", Arrays.asList("name cannot be null"));
        assertTrue(action.isValidateCalled());
        assertTrue(action.isValidateExecuteCalled());
    }

    public void testProgrammaticValidationDontInvokeValidate() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleFieldNoValidate", null, null);
        SimpleField action = (SimpleField) baseActionProxy.getAction();
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = ((ValidationAware) baseActionProxy.getAction()).getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertValue(fieldErrors, "name", Arrays.asList("name cannot be null"));
        assertFalse(action.isValidateCalled());
        assertTrue(action.isValidateExecuteCalled());
    }

    public void testProgrammaticValidationDontInvokeProgrammatic() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleFieldNoProgrammatic", null, null);
        SimpleField action = (SimpleField) baseActionProxy.getAction();
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = ((ValidationAware) baseActionProxy.getAction()).getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertValue(fieldErrors, "name", Arrays.asList("name cannot be null"));
        assertFalse(action.isValidateCalled());
        assertFalse(action.isValidateExecuteCalled());
    }


    public void testModelDrivenAction() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "modelDrivenAction", null, null);
        ModelDrivenAction action = (ModelDrivenAction) baseActionProxy.getAction();
        action.getModel().setName(null);
        action.getModel().setEmail(null);
        action.getModel().getAddress().setStreet("short");
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = ((ValidationAware) baseActionProxy.getAction()).getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(5, fieldErrors.size()); // 5: as there will be field errors for 'model' and 'address' themselves
        assertValue(fieldErrors, "name", Arrays.asList("name cannot be null"));
        assertValue(fieldErrors, "email", Arrays.asList("email cannot be null"));
        assertValue(fieldErrors, "address.street", Arrays.asList("street cannot be smaller than 7 characters"));

    }

    public void testMemberObject() throws Exception {
    	ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "memberObject", null, null);
    	MemberObject action = (MemberObject) baseActionProxy.getAction();
    	action.getPerson().setName(null);
    	action.getPerson().setEmail(null);
    	action.getPerson().getAddress().setStreet("short");
    	baseActionProxy.execute();

    	Map<String, List<String>> fieldErrors = ((ValidationAware) baseActionProxy.getAction()).getFieldErrors();
    	assertNotNull(fieldErrors);
    	assertEquals(5, fieldErrors.size()); // 5: as there will be field errors for 'person' and 'person.address' themselves
    	assertValue(fieldErrors, "person.name", Arrays.asList("name cannot be null"));
    	assertValue(fieldErrors, "person.email", Arrays.asList("email cannot be null"));
    	assertValue(fieldErrors, "person.address.street", Arrays.asList("street cannot be smaller than 7 characters"));
    }


    private void assertValue(Map<String, List<String>> map, String key, List<String> expectedValues) {
        assertNotNull(map);
        assertNotNull(key);
        assertNotNull(expectedValues);

        List<String> values = map.get(key);
        assertNotNull(values);
        assertEquals(expectedValues, values);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(new XmlConfigurationProvider("oval-test.xml"));
    }
}
