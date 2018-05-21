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

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.*;
import com.opensymphony.xwork2.mock.MockInterceptor;
import com.opensymphony.xwork2.mock.MockResult;
import org.apache.struts2.interceptor.NoOpInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Mike
 * @author Rainer Hermanns
 */
public class XmlConfigurationProviderActionsTest extends ConfigurationTestBase {

    private List<InterceptorMapping> interceptors;
    private List<ExceptionMappingConfig> exceptionMappings;
    private Map<String, String> params;
    private Map<String, ResultConfig> results;
    private ObjectFactory objectFactory;


    public void testActions() throws Exception {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-actions.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        // setup expectations
        // bar action is very simple, just two params
        params.put("foo", "17");
        params.put("bar", "23");
        params.put("testXW412", "foo.jspa?fooID=${fooID}&something=bar");
        params.put("testXW412Again", "something");


        ActionConfig barAction = new ActionConfig.Builder("", "Bar", SimpleAction.class.getName())
                .addParams(params).build();

        // foo action is a little more complex, two params, a result and an interceptor stack
        results = new HashMap<>();
        params = new HashMap<>();
        params.put("foo", "18");
        params.put("bar", "24");
        results.put("success", new ResultConfig.Builder("success", MockResult.class.getName()).build());

        InterceptorConfig noopInterceptorConfig = new InterceptorConfig.Builder("noop", NoOpInterceptor.class.getName()).build();
        interceptors.add(new InterceptorMapping("noop", objectFactory.buildInterceptor(noopInterceptorConfig, new HashMap<String, String>())));

        ActionConfig fooAction = new ActionConfig.Builder("", "Foo", SimpleAction.class.getName())
                .addParams(params)
                .addResultConfigs(results)
                .addInterceptors(interceptors)
                .build();

        // wildcard action is simple wildcard example
        results = new HashMap<>();
        results.put("*", new ResultConfig.Builder("*", MockResult.class.getName()).build());

        ActionConfig wildcardAction = new ActionConfig.Builder("", "WildCard", SimpleAction.class.getName())
                .addResultConfigs(results)
                .addInterceptors(interceptors)
                .build();

        // fooBar action is a little more complex, two params, a result and an interceptor stack
        params = new HashMap<String, String>();
        params.put("foo", "18");
        params.put("bar", "24");
        results = new HashMap<>();
        results.put("success", new ResultConfig.Builder("success", MockResult.class.getName()).build());

        ExceptionMappingConfig exceptionConfig = new ExceptionMappingConfig.Builder("runtime", "java.lang.RuntimeException", "exception")
                .build();
        exceptionMappings.add(exceptionConfig);

        ActionConfig fooBarAction = new ActionConfig.Builder("", "FooBar", SimpleAction.class.getName())
                .addParams(params)
                .addResultConfigs(results)
                .addInterceptors(interceptors)
                .addExceptionMappings(exceptionMappings)
                .build();

        // TestInterceptorParam action tests that an interceptor worked
        HashMap<String, String> interceptorParams = new HashMap<>();
        interceptorParams.put("expectedFoo", "expectedFooValue");
        interceptorParams.put("foo", MockInterceptor.DEFAULT_FOO_VALUE);

        InterceptorConfig mockInterceptorConfig = new InterceptorConfig.Builder("test", MockInterceptor.class.getName()).build();
        interceptors = new ArrayList<>();
        interceptors.add(new InterceptorMapping("test", objectFactory.buildInterceptor(mockInterceptorConfig, interceptorParams)));

        ActionConfig intAction = new ActionConfig.Builder("", "TestInterceptorParam", SimpleAction.class.getName())
                .addInterceptors(interceptors)
                .build();

        // TestInterceptorParamOverride action tests that an interceptor with a param override worked
        interceptorParams = new HashMap<>();
        interceptorParams.put("expectedFoo", "expectedFooValue");
        interceptorParams.put("foo", "foo123");
        interceptors = new ArrayList<>();
        interceptors.add(new InterceptorMapping("test", objectFactory.buildInterceptor(mockInterceptorConfig, interceptorParams)));

        ActionConfig intOverAction = new ActionConfig.Builder("", "TestInterceptorParamOverride", SimpleAction.class.getName())
                .addInterceptors(interceptors)
                .build();

        // execute the configuration
        provider.init(configuration);
        provider.loadPackages();

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map actionConfigs = pkg.getActionConfigs();

        // assertions
        assertEquals(7, actionConfigs.size());
        assertEquals(barAction, actionConfigs.get("Bar"));
        assertEquals(fooAction, actionConfigs.get("Foo"));
        assertEquals(wildcardAction, actionConfigs.get("WildCard"));
        assertEquals(fooBarAction, actionConfigs.get("FooBar"));
        assertEquals(intAction, actionConfigs.get("TestInterceptorParam"));
        assertEquals(intOverAction, actionConfigs.get("TestInterceptorParamOverride"));
    }

    public void testInvalidActions() throws Exception {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-action-invalid.xml";

        try {
            ConfigurationProvider provider = buildConfigurationProvider(filename);
            fail("Should have thrown an exception");
        } catch (ConfigurationException ex) {
            // it worked correctly
        }
    }

    public void testPackageDefaultClassRef() throws Exception {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-actions-packagedefaultclassref.xml";
        final String testDefaultClassName = "com.opensymphony.xwork2.UserSpecifiedDefaultAction";

        ConfigurationProvider provider = buildConfigurationProvider(filename);

        // setup expectations
        params.put("foo", "17");
        params.put("bar", "23");

        ActionConfig barWithPackageDefaultClassRefConfig =
                new ActionConfig.Builder("", "Bar", "")
                        .addParams(params)
                        .build();

        // execute the configuration
        provider.init(configuration);

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map actionConfigs = pkg.getActionConfigs();

        // assertions
        assertEquals(1, actionConfigs.size());
        assertEquals(barWithPackageDefaultClassRefConfig, actionConfigs.get("Bar"));
    }

    public void testDefaultActionClass() throws Exception {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-actions.xml";
        final String testDefaultClassName = "com.opensymphony.xwork2.ActionSupport";

        ConfigurationProvider provider = buildConfigurationProvider(filename);

        // setup expectations
        params.put("foo", "17");
        params.put("bar", "23");

        ActionConfig barWithoutClassNameConfig =
                new ActionConfig.Builder("", "BarWithoutClassName", "")
                        .addParams(params)
                        .build();

        // execute the configuration
        provider.init(configuration);

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map actionConfigs = pkg.getActionConfigs();

        // assertions
        assertEquals(7, actionConfigs.size());
        assertEquals(barWithoutClassNameConfig, actionConfigs.get("BarWithoutClassName"));
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        params = new HashMap<>();
        results = new HashMap<>();
        interceptors = new ArrayList<>();
        exceptionMappings = new ArrayList<>();
        this.objectFactory = container.getInstance(ObjectFactory.class);
    }
}
