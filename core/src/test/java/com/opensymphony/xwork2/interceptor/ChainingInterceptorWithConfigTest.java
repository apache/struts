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
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;

import java.util.*;

import org.apache.struts2.TestResult;


/**
 * Unit test for {@link ChainingInterceptor} with a configuration provider.
 *
 */
public class ChainingInterceptorWithConfigTest extends XWorkTestCase {

    static String CHAINED_ACTION = "chainedAction";
    static String CHAINTO_ACTION = "chaintoAction";
    ObjectFactory objectFactory;

    public void testTwoExcludesPropertiesChained() throws Exception {
        assertNotNull(objectFactory);
        ActionProxy proxy = actionProxyFactory.createActionProxy("", CHAINED_ACTION, null, null);
        SimpleAction chainedAction = (SimpleAction) proxy.getAction();
        chainedAction.setBar(1);
        chainedAction.setFoo(1);
        chainedAction.setBlah("WW-4528");
        proxy.execute();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        XmlConfigurationProvider provider = new XmlConfigurationProvider("xwork-default.xml");
        container.inject(provider);
        this.objectFactory = container.getInstance(ObjectFactory.class);
        loadConfigurationProviders(provider, new MockConfigurationProvider());
    }

    
    private class MockConfigurationProvider implements ConfigurationProvider {
        private Configuration config;

        public void init(Configuration configuration) throws ConfigurationException {
            this.config = configuration;
        }

        public boolean needsReload() {
            return false;
        }

        public void destroy() { }


        public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
            if (!builder.contains(ObjectFactory.class)) {
                builder.factory(ObjectFactory.class);
            }
            if (!builder.contains(ActionProxyFactory.class)) {
                builder.factory(ActionProxyFactory.class, DefaultActionProxyFactory.class);
            }
        }

        public void loadPackages() throws ConfigurationException {
            HashMap<String, String> interceptorParams = new HashMap<>();
            interceptorParams.put("excludes", "blah,bar");

            HashMap successParams1 = new HashMap();
            successParams1.put("propertyName", "baz");
            successParams1.put("expectedValue", 1);

            HashMap successParams2 = new HashMap();
            successParams2.put("propertyName", "blah");
            successParams2.put("expectedValue", null);

            InterceptorConfig chainingInterceptorConfig =  new InterceptorConfig.Builder("chainStack", ChainingInterceptor.class.getName()).build();
            PackageConfig packageConfig = new PackageConfig.Builder("default")
                    .addActionConfig(CHAINED_ACTION, new ActionConfig.Builder("defaultPackage", CHAINED_ACTION, SimpleAction.class.getName())
                            .addResultConfig(new ResultConfig.Builder(Action.ERROR, ActionChainResult.class.getName()).addParam("actionName", CHAINTO_ACTION).build())
                            .build())
                    .addActionConfig(CHAINTO_ACTION, new ActionConfig.Builder("defaultPackage", CHAINTO_ACTION, SimpleAction.class.getName())
                            .addInterceptors(Collections.singletonList(new InterceptorMapping("chainStack", objectFactory.buildInterceptor(chainingInterceptorConfig, interceptorParams))))
                            .addResultConfig(new ResultConfig.Builder(Action.SUCCESS, TestResult.class.getName()).addParams(successParams1).build())
                            .addResultConfig(new ResultConfig.Builder(Action.ERROR, TestResult.class.getName()).addParams(successParams2).build())
                            .build())
                    .build();
            config.addPackageConfig("defaultPackage", packageConfig);
            config.addPackageConfig("default", new PackageConfig.Builder(packageConfig).name("default").build());
        }
    }
}
