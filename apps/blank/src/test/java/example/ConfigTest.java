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

package example;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import org.apache.struts2.StrutsTestCase;

import java.util.List;
import java.util.Map;

public class ConfigTest extends StrutsTestCase {

    protected void assertSuccess(String result) throws Exception {
        assertTrue("Expected a success result!",
                ActionSupport.SUCCESS.equals(result));
    }

    protected void assertInput(String result) throws Exception {
        assertTrue("Expected an input result!",
                ActionSupport.INPUT.equals(result));
    }

    protected Map<String, List<String>> assertFieldErrors(ActionSupport action) throws Exception {
        assertTrue(action.hasFieldErrors());
        return action.getFieldErrors();
    }

    protected void assertFieldError(Map field_errors, String field_name, String error_message) {

        List errors = (List) field_errors.get(field_name);
        assertNotNull("Expected errors for " + field_name, errors);
        assertTrue("Expected errors for " + field_name, errors.size()>0);
        // TODO: Should be a loop
        assertEquals(error_message,errors.get(0));

    }

    protected void setUp() throws Exception {
        super.setUp();
        XmlConfigurationProvider c = new XmlConfigurationProvider("struts.xml");
        configurationManager.addContainerProvider(c);
        configurationManager.reload();
    }

    protected ActionConfig assertClass(String namespace, String action_name, String class_name) {
        RuntimeConfiguration configuration = configurationManager.getConfiguration().getRuntimeConfiguration();
        ActionConfig config = configuration.getActionConfig(namespace, action_name);
        assertNotNull("Mssing action", config);
        assertTrue("Wrong class name: [" + config.getClassName() + "]",
                class_name.equals(config.getClassName()));
        return config;
    }

    protected ActionConfig assertClass(String action_name, String class_name) {
        return assertClass("", action_name, class_name);
    }

    protected void assertResult(ActionConfig config, String result_name, String result_value) {
        Map results = config.getResults();
        ResultConfig result = (ResultConfig) results.get(result_name);
        Map params = result.getParams();
        String value = (String) params.get("actionName");
        if (value == null)
            value = (String) params.get("location");
        assertTrue("Wrong result value: [" + value + "]",
                result_value.equals(value));
    }

    public void testConfig() throws Exception {
        assertNotNull(configurationManager);
    }

}
