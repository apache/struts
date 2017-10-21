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

import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.mock.MockResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Matthew E. Porter (matthew dot porter at metissian dot com)
 * Date: Aug 15, 2005
 * Time: 2:05:36 PM
 */
public class XmlConfigurationProviderExceptionMappingsTest extends ConfigurationTestBase {

    public void testActions() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-exception-mappings.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        List<ExceptionMappingConfig> exceptionMappings = new ArrayList<>();
        HashMap<String, String> parameters = new HashMap<>();
        HashMap<String, ResultConfig> results = new HashMap<>();

        exceptionMappings.add(
                new ExceptionMappingConfig.Builder("spooky-result", "com.opensymphony.xwork2.SpookyException", "spooky-result")
                        .build());
        results.put("spooky-result", new ResultConfig.Builder("spooky-result", MockResult.class.getName()).build());

        Map<String, String> resultParams = new HashMap<>();
        resultParams.put("actionName", "bar.vm");
        results.put("specificLocationResult",
                new ResultConfig.Builder("specificLocationResult", ActionChainResult.class.getName())
                        .addParams(resultParams)
                        .build());

        ActionConfig expectedAction = new ActionConfig.Builder("default", "Bar", SimpleAction.class.getName())
            .addParams(parameters)
            .addResultConfigs(results)
            .addExceptionMappings(exceptionMappings)
            .build();

        // execute the configuration
        provider.init(configuration);
        provider.loadPackages();

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map actionConfigs = pkg.getActionConfigs();

        // assertions
        assertEquals(1, actionConfigs.size());

        ActionConfig action = (ActionConfig) actionConfigs.get("Bar");
        assertEquals(expectedAction, action);
    }

}
