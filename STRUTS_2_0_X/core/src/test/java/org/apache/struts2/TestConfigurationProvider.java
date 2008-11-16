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
package org.apache.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.struts2.dispatcher.ServletDispatcherResult;
import org.apache.struts2.interceptor.TokenInterceptor;
import org.apache.struts2.interceptor.TokenSessionStoreInterceptor;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.DefaultActionProxyFactory;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.mock.MockResult;
import com.opensymphony.xwork2.util.location.LocatableProperties;


/**
 * TestConfigurationProvider provides a simple configuration class without the need for xml files, etc. for simple testing.
 *
 */
public class TestConfigurationProvider implements ConfigurationProvider {

    public static final String TEST_ACTION_NAME = "testAction";
    public static final String EXECUTION_COUNT_ACTION_NAME = "executionCountAction";
    public static final String TOKEN_ACTION_NAME = "tokenAction";
    public static final String TOKEN_SESSION_ACTION_NAME = "tokenSessionAction";
    public static final String TEST_NAMESPACE = "/testNamespace";
    public static final String TEST_NAMESPACE_ACTION = "testNamespaceAction";
    private Configuration configuration;


    /**
     * Allows the configuration to clean up any resources used
     */
    public void destroy() {
    }
    
    public void init(Configuration config) {
        this.configuration = config;
    }

    /**
     * Initializes the configuration object.
     */
    public void loadPackages() {
        PackageConfig defaultPackageConfig = new PackageConfig("");

        HashMap results = new HashMap();

        HashMap successParams = new HashMap();
        successParams.put("propertyName", "executionCount");
        successParams.put("expectedValue", "1");

        ResultConfig successConfig = new ResultConfig(Action.SUCCESS, TestResult.class.getName(), successParams);

        results.put(Action.SUCCESS, successConfig);

        List interceptors = new ArrayList();

        ActionConfig executionCountActionConfig = new ActionConfig(null, ExecutionCountTestAction.class, null, results, interceptors);
        defaultPackageConfig.addActionConfig(EXECUTION_COUNT_ACTION_NAME, executionCountActionConfig);

        results = new HashMap();

        successParams = new HashMap();
        successParams.put("location", "success.jsp");

        successConfig = new ResultConfig(Action.SUCCESS, ServletDispatcherResult.class.getName(), successParams);

        results.put(Action.SUCCESS, successConfig);

        interceptors.add(new InterceptorMapping("params", new ParametersInterceptor()));

        ActionConfig testActionConfig = new ActionConfig(null, TestAction.class, null, results, interceptors);
        defaultPackageConfig.addActionConfig(TEST_ACTION_NAME, testActionConfig);

        interceptors = new ArrayList();
        interceptors.add(new InterceptorMapping("token", new TokenInterceptor()));

        results = new HashMap();

        ActionConfig tokenActionConfig = new ActionConfig(null, TestAction.class, null, results, interceptors);
        tokenActionConfig.addResultConfig(new ResultConfig("invalid.token", MockResult.class.getName()));
        tokenActionConfig.addResultConfig(new ResultConfig("success", MockResult.class.getName()));
        defaultPackageConfig.addActionConfig(TOKEN_ACTION_NAME, tokenActionConfig);

        interceptors = new ArrayList();
        interceptors.add(new InterceptorMapping("token-session", new TokenSessionStoreInterceptor()));

        results = new HashMap();

        successParams = new HashMap();
        successParams.put("actionName", EXECUTION_COUNT_ACTION_NAME);

        successConfig = new ResultConfig(Action.SUCCESS, ActionChainResult.class.getName(), successParams);

        results.put(Action.SUCCESS, successConfig);

        // empty results for token session unit test
        results = new HashMap();
        ActionConfig tokenSessionActionConfig = new ActionConfig(null, TestAction.class, null, results, interceptors);
        tokenSessionActionConfig.addResultConfig(new ResultConfig("invalid.token", MockResult.class.getName()));
        tokenSessionActionConfig.addResultConfig(new ResultConfig("success", MockResult.class.getName()));
        defaultPackageConfig.addActionConfig(TOKEN_SESSION_ACTION_NAME, tokenSessionActionConfig);

        configuration.addPackageConfig("", defaultPackageConfig);

        Map testActionTagResults = new HashMap();
        testActionTagResults.put(Action.SUCCESS, new ResultConfig(Action.SUCCESS, TestActionTagResult.class.getName(), new HashMap()));
        testActionTagResults.put(Action.INPUT, new ResultConfig(Action.INPUT, TestActionTagResult.class.getName(), new HashMap()));
        ActionConfig testActionTagActionConfig = new ActionConfig((String) null, TestAction.class, (Map) null, testActionTagResults, new ArrayList());
        defaultPackageConfig.addActionConfig("testActionTagAction", testActionTagActionConfig);

        PackageConfig namespacePackageConfig = new PackageConfig("namespacePackage");
        namespacePackageConfig.setNamespace(TEST_NAMESPACE);
        namespacePackageConfig.addParent(defaultPackageConfig);

        ActionConfig namespaceAction = new ActionConfig(null, TestAction.class, null, null, null);
        namespacePackageConfig.addActionConfig(TEST_NAMESPACE_ACTION, namespaceAction);

        configuration.addPackageConfig("namespacePackage", namespacePackageConfig);
    }

    /**
     * Tells whether the ConfigurationProvider should reload its configuration
     *
     * @return
     */
    public boolean needsReload() {
        return false;
    }

    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
        if (!builder.contains(ObjectFactory.class)) {
            builder.factory(ObjectFactory.class);
        }
        if (!builder.contains(ActionProxyFactory.class)) {
            builder.factory(ActionProxyFactory.class, DefaultActionProxyFactory.class);
        }
    }
}
