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
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.mock.MockResult;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Mike
 * Date: May 6, 2003
 * Time: 3:10:16 PM
 * To change this template use Options | File Templates.
 */
public class XmlConfigurationProviderResultsTest extends ConfigurationTestBase {

    public void testActions() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-results.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        HashMap<String, String> parameters = new HashMap<String, String>();
        HashMap<String, ResultConfig> results = new HashMap<String, ResultConfig>();

        results.put("chainDefaultTypedResult", new ResultConfig.Builder("chainDefaultTypedResult", ActionChainResult.class.getName()).build());

        results.put("mockTypedResult", new ResultConfig.Builder("mockTypedResult", MockResult.class.getName()).build());

        Map<String, String> resultParams = new HashMap<String, String>();
        resultParams.put("actionName", "bar.vm");
        results.put("specificLocationResult", new ResultConfig.Builder("specificLocationResult", ActionChainResult.class.getName())
                .addParams(resultParams).build());

        resultParams = new HashMap<String, String>();
        resultParams.put("actionName", "foo.vm");
        results.put("defaultLocationResult", new ResultConfig.Builder("defaultLocationResult", ActionChainResult.class.getName())
                .addParams(resultParams).build());

        resultParams = new HashMap<String, String>();
        resultParams.put("foo", "bar");
        results.put("noDefaultLocationResult", new ResultConfig.Builder("noDefaultLocationResult", ActionChainResult.class.getName())
                .addParams(resultParams).build());

        ActionConfig expectedAction = new ActionConfig.Builder("default", "Bar", SimpleAction.class.getName())
            .addParams(parameters)
            .addResultConfigs(results)
            .build();

        // execute the configuration
        provider.init(configuration);
        provider.loadPackages();

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map<String, ActionConfig> actionConfigs = pkg.getActionConfigs();

        // assertions
        assertEquals(1, actionConfigs.size());

        ActionConfig action = actionConfigs.get("Bar");
        assertEquals(expectedAction, action);
    }

    public void testResultInheritance() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-result-inheritance.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        // expectations
        provider.init(configuration);
        provider.loadPackages();

        // assertions
        PackageConfig subPkg = configuration.getPackageConfig("subPackage");
        assertEquals(1, subPkg.getResultTypeConfigs().size());
        assertEquals(3, subPkg.getAllResultTypeConfigs().size());
    }

    public void testResultTypes() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-results.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        // setup expectations
        ResultTypeConfig chainResult = new ResultTypeConfig.Builder("chain", ActionChainResult.class.getName()).build();
        ResultTypeConfig mockResult = new ResultTypeConfig.Builder("mock", MockResult.class.getName()).build();

        // execute the configuration
        provider.init(configuration);
        provider.loadPackages();

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map resultTypes = pkg.getResultTypeConfigs();

        // assertions
        assertEquals(2, resultTypes.size());
        assertEquals("chain", pkg.getDefaultResultType());
        assertEquals(chainResult, resultTypes.get("chain"));
        assertEquals(mockResult, resultTypes.get("mock"));
    }
}
