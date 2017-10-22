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


import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Container;

/**
 * <code>XmlConfigurationProviderGlobalResultInheritenceTest</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @author tm_jee
 * @version $Id$
 */
public class XmlConfigurationProviderGlobalResultInheritenceTest extends ConfigurationTestBase {

    public void testGlobalResultInheritenceTest() throws Exception {
        ConfigurationProvider provider = buildConfigurationProvider("com/opensymphony/xwork2/config/providers/xwork-test-global-result-inheritence.xml");

        ConfigurationManager configurationManager = new ConfigurationManager(Container.DEFAULT_NAME);
        configurationManager.addContainerProvider(new XWorkConfigurationProvider());
        configurationManager.addContainerProvider(provider);
        Configuration configuration = configurationManager.getConfiguration();

        ActionConfig parentActionConfig = configuration.getRuntimeConfiguration().getActionConfig("/base", "parentAction");
        ActionConfig anotherActionConfig = configuration.getRuntimeConfiguration().getActionConfig("/base", "anotherAction");
        ActionConfig childActionConfig = configuration.getRuntimeConfiguration().getActionConfig("/base", "childAction");

        ResultConfig parentResultConfig1 = parentActionConfig.getResults().get("mockResult1");
        ResultConfig parentResultConfig2 = parentActionConfig.getResults().get("mockResult2");
        ResultConfig anotherResultConfig1 = anotherActionConfig.getResults().get("mockResult1");
        ResultConfig anotherResultConfig2 = anotherActionConfig.getResults().get("mockResult2");
        ResultConfig childResultConfig1 = childActionConfig.getResults().get("mockResult1");
        ResultConfig childResultConfig2 = childActionConfig.getResults().get("mockResult2");

        System.out.println(parentResultConfig1.getParams().get("identity"));
        System.out.println(parentResultConfig2.getParams().get("identity"));
        System.out.println(anotherResultConfig1.getParams().get("identity"));
        System.out.println(anotherResultConfig2.getParams().get("identity"));
        System.out.println(childResultConfig1.getParams().get("identity"));
        System.out.println(childResultConfig2.getParams().get("identity"));

        assertFalse(parentResultConfig1 == anotherResultConfig1);
        assertFalse(parentResultConfig2 == anotherResultConfig2);

        assertFalse(parentResultConfig1 == childResultConfig1);
        assertTrue(parentResultConfig2 == childResultConfig2);
    }
}


