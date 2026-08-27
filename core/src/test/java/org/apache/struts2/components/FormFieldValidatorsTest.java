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
package org.apache.struts2.components;

import org.apache.struts2.TestConfigurationProvider;
import org.apache.struts2.mock.MockActionProxy;
import org.apache.struts2.validator.ActionValidatorManager;
import org.apache.struts2.validator.Validator;
import org.apache.struts2.views.jsp.AbstractUITagTest;
import org.apache.struts2.views.jsp.ui.FormTag;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class FormFieldValidatorsTest extends AbstractUITagTest {

    public void testFindsTheFieldsValidators() throws Exception {
        Form form = formForDoubleValidationAction();

        List<Validator> validators = form.getFieldValidators("myUpDownSelectTag");

        assertEquals(1, validators.size());
        assertEquals("double", validators.get(0).getValidatorType());
    }

    public void testReturnsEmptyForAnUnvalidatedField() throws Exception {
        Form form = formForDoubleValidationAction();

        assertTrue(form.getFieldValidators("noSuchField").isEmpty());
    }

    public void testResolvesTheActionsValidatorsOnlyOnceAcrossFields() throws Exception {
        Form form = formForDoubleValidationAction();

        ActionValidatorManager manager = mock(ActionValidatorManager.class);
        when(manager.getValidators(any(Class.class), anyString(), nullable(String.class)))
            .thenReturn(Collections.emptyList());
        form.setActionValidatorManager(manager);

        form.getFieldValidators("myUpDownSelectTag");
        form.getFieldValidators("someOtherField");

        // fully qualified: AbstractUITagTest inherits verify(URL), which would shadow a static import
        org.mockito.Mockito.verify(manager, times(1))
            .getValidators(any(Class.class), anyString(), nullable(String.class));
    }

    private Form formForDoubleValidationAction() throws Exception {
        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setAction("doubleValidationAction");
        tag.setNamespace("");
        tag.doStartTag();
        return (Form) tag.getComponent();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initDispatcher(new HashMap<String, String>() {{
            put("configProviders", TestConfigurationProvider.class.getName());
        }});
        createMocks();
        // AnnotationActionValidatorManager.buildValidatorKey() dereferences the current ActionInvocation's
        // proxy config; at real runtime the Dispatcher always attaches one, but the mock proxy from
        // createMocks() does not, so it has to be wired up explicitly here.
        ((MockActionProxy) actionProxy).setConfig(
            configuration.getRuntimeConfiguration().getActionConfig("", "doubleValidationAction"));
    }
}
