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

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.DefaultActionProxyFactory;
import com.opensymphony.xwork2.security.DefaultExcludedPatternsChecker;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
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
import com.opensymphony.xwork2.validator.ValidationInterceptor;
import org.apache.struts2.dispatcher.ServletDispatcherResult;
import org.apache.struts2.interceptor.TokenInterceptor;
import org.apache.struts2.interceptor.TokenSessionStoreInterceptor;
import org.apache.struts2.views.jsp.ui.DoubleValidationAction;

import java.util.HashMap;


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

        HashMap successParams = new HashMap();
        successParams.put("propertyName", "executionCount");
        successParams.put("expectedValue", "1");

        ActionConfig executionCountActionConfig = new ActionConfig.Builder("", "", ExecutionCountTestAction.class.getName())
            .addResultConfig(new ResultConfig.Builder(Action.SUCCESS, TestResult.class.getName())
                .addParams(successParams)
                .build())
            .build();

        ValidationInterceptor validationInterceptor = new ValidationInterceptor();
        validationInterceptor.setIncludeMethods("*");

        ActionConfig doubleValidationActionConfig = new ActionConfig.Builder("", "doubleValidationAction", DoubleValidationAction.class.getName())
            .addResultConfig(new ResultConfig.Builder(Action.SUCCESS, ServletDispatcherResult.class.getName())
                    .addParam("location", "success.jsp")
                    .build())
            .addInterceptor(new InterceptorMapping("validation", validationInterceptor))
            .build();

        ActionConfig testActionConfig = new ActionConfig.Builder("", "", TestAction.class.getName())
            .addResultConfig(new ResultConfig.Builder(Action.SUCCESS, ServletDispatcherResult.class.getName())
                    .addParam("location", "success.jsp")
                    .build())
            .addInterceptor(new InterceptorMapping("params", new ParametersInterceptor()))
            .build();

        ActionConfig tokenActionConfig = new ActionConfig.Builder("", "", TestAction.class.getName())
            .addInterceptor(new InterceptorMapping("token", new TokenInterceptor()))
            .addResultConfig(new ResultConfig.Builder("invalid.token", MockResult.class.getName()).build())
            .addResultConfig(new ResultConfig.Builder("success", MockResult.class.getName()).build())
            .build();


        // empty results for token session unit test
        ActionConfig tokenSessionActionConfig = new ActionConfig.Builder("", "", TestAction.class.getName())
            .addResultConfig(new ResultConfig.Builder("invalid.token", MockResult.class.getName()).build())
            .addResultConfig(new ResultConfig.Builder("success", MockResult.class.getName()).build())
            .addInterceptor(new InterceptorMapping("tokenSession", new TokenSessionStoreInterceptor()))
            .build();

        PackageConfig defaultPackageConfig = new PackageConfig.Builder("")
            .addActionConfig(EXECUTION_COUNT_ACTION_NAME, executionCountActionConfig)
            .addActionConfig(TEST_ACTION_NAME, testActionConfig)
            .addActionConfig("doubleValidationAction", doubleValidationActionConfig)
            .addActionConfig(TOKEN_ACTION_NAME, tokenActionConfig)
            .addActionConfig(TOKEN_SESSION_ACTION_NAME, tokenSessionActionConfig)
            .addActionConfig("testActionTagAction", new ActionConfig.Builder("", "", TestAction.class.getName())
                .addResultConfig(new ResultConfig.Builder(Action.SUCCESS, TestActionTagResult.class.getName()).build())
                .addResultConfig(new ResultConfig.Builder(Action.INPUT, TestActionTagResult.class.getName()).build())
                .build())
            .build();

        configuration.addPackageConfig("", defaultPackageConfig);

        PackageConfig namespacePackageConfig = new PackageConfig.Builder("namespacePackage")
            .namespace(TEST_NAMESPACE)
            .addParent(defaultPackageConfig)
            .addActionConfig(TEST_NAMESPACE_ACTION, new ActionConfig.Builder("", "", TestAction.class.getName()).build())
            .build();

        configuration.addPackageConfig("namespacePackage", namespacePackageConfig);

        PackageConfig testActionWithNamespacePackageConfig = new PackageConfig.Builder("testActionNamespacePackages")
            .namespace(TEST_NAMESPACE)
            .addParent(defaultPackageConfig)
            .addActionConfig(TEST_ACTION_NAME, new ActionConfig.Builder("", "", TestAction.class.getName()).build())
            .build();

        configuration.addPackageConfig("testActionNamespacePackages", testActionWithNamespacePackageConfig);

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
        if (!builder.contains(ExcludedPatternsChecker.class)) {
            builder.factory(ExcludedPatternsChecker.class, DefaultExcludedPatternsChecker.class);
        }
    }
}
