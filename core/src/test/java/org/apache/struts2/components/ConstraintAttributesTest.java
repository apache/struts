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

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.TestConfigurationProvider;
import org.apache.struts2.mock.MockActionProxy;
import org.apache.struts2.views.jsp.AbstractUITagTest;
import org.apache.struts2.views.jsp.ui.FormTag;
import org.apache.struts2.views.jsp.ui.TextFieldTag;

import java.util.HashMap;
import java.util.Map;

public class ConstraintAttributesTest extends AbstractUITagTest {

    public void testNoConstraintsWhenTheConstantIsOff() throws Exception {
        initDispatcherWith("false");

        assertNull(renderFieldAndReturnConstraints());
    }

    public void testConstraintsWhenTheConstantIsOn() throws Exception {
        initDispatcherWith("true");

        Map<String, String> constraints = renderFieldAndReturnConstraints();
        assertNotNull("expected constraints to be populated", constraints);
        assertEquals("3", constraints.get("minlength"));
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> renderFieldAndReturnConstraints() throws Exception {
        FormTag form = new FormTag();
        form.setPageContext(pageContext);
        form.setAction("constraintAction");
        form.setNamespace("");
        form.doStartTag();

        TextFieldTag field = new TextFieldTag();
        field.setPageContext(pageContext);
        field.setName("username");
        field.doStartTag();

        Map<String, Object> attributes =
            ((UIBean) field.getComponent()).getAttributes();

        field.doEndTag();
        form.doEndTag();

        return (Map<String, String>) attributes.get("constraints");
    }

    private void initDispatcherWith(String constraintsEnabled) throws Exception {
        initDispatcher(new HashMap<String, String>() {{
            put("configProviders", TestConfigurationProvider.class.getName());
            put(StrutsConstants.STRUTS_UI_HTML5_CONSTRAINTS, constraintsEnabled);
        }});
        createMocks();
        // createMocks() never sets a config on the MockActionProxy it builds; without one,
        // AnnotationActionValidatorManager.buildValidatorKey NPEs dereferencing proxy.getConfig().
        ((MockActionProxy) actionProxy).setConfig(
            configuration.getRuntimeConfiguration().getActionConfig("", "constraintAction"));
    }
}
