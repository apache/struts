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
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import org.apache.struts2.interceptor.NoOpInterceptor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.support.StaticApplicationContext;

import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Mike
 * Date: May 6, 2003
 * Time: 3:10:16 PM
 * To change this template use Options | File Templates.
 */
public class XmlConfigurationProviderInterceptorsSpringTest extends ConfigurationTestBase {

    InterceptorConfig noopInterceptor = new InterceptorConfig.Builder("noop", NoOpInterceptor.class.getName()).build();
    ObjectFactory objectFactory;
    StaticApplicationContext sac;


    public void testInterceptorsLoadedFromSpringApplicationContext() throws ConfigurationException {
        sac.registerSingleton("noop-interceptor", NoOpInterceptor.class, new MutablePropertyValues());

        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-interceptors-spring.xml";

        // Expect a ConfigurationException to be thrown if the interceptor reference
        // cannot be resolved
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        // execute the configuration
        provider.init(configuration);
        provider.loadPackages();

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map interceptorConfigs = pkg.getInterceptorConfigs();

        // assertions for size
        assertEquals(1, interceptorConfigs.size());

        // assertions for interceptors
        InterceptorConfig seen = (InterceptorConfig) interceptorConfigs.get("noop");
        assertEquals("noop-interceptor", seen.getClassName());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        sac = new StaticApplicationContext();

        //SpringObjectFactory objFactory = new SpringObjectFactory();
        //objFactory.setApplicationContext(sac);
        //ObjectFactory.setObjectFactory(objFactory);

        objectFactory = container.getInstance(ObjectFactory.class);
    }
}
