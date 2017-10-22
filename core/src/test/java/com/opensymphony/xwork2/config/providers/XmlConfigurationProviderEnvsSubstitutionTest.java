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
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import org.apache.struts2.StrutsConstants;

public class XmlConfigurationProviderEnvsSubstitutionTest extends ConfigurationTestBase {

    public void testSubstitution() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-envs-substitution.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        configurationManager.addContainerProvider(provider);
        configurationManager.reload();
        configuration = configurationManager.getConfiguration();
        container = configuration.getContainer();

        String foo = container.getInstance(String.class, "foo");
        assertEquals("bar", foo);

        String user = container.getInstance(String.class, "user");
        assertEquals(System.getenv("USER"), user);

        String home = container.getInstance(String.class, "home");
        assertEquals("Current HOME = " + System.getenv("HOME"), home);

        String os = container.getInstance(String.class, "os");
        assertEquals("Current OS = " + System.getProperty("os.name"), os);

        String unknown = container.getInstance(String.class, "unknown");
        assertEquals("Unknown = default", unknown);

        String devMode = container.getInstance(String.class, StrutsConstants.STRUTS_DEVMODE);
        assertEquals("false", devMode);
    }

}
