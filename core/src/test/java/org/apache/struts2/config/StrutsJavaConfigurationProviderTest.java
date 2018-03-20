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
package org.apache.struts2.config;

import java.util.Arrays;
import java.util.List;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.config.entities.BeanConfig;
import org.apache.struts2.config.entities.ConstantConfig;
import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.TestBean;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.impl.MockConfiguration;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.location.LocatableProperties;

public class StrutsJavaConfigurationProviderTest {
    @Test
    public void testRegister() throws Exception {
        final ConstantConfig constantConfig = new ConstantConfig();
        constantConfig.setDevMode(true);

        final String expectedUnknownHandler = "expectedUnknownHandler";

        StrutsJavaConfiguration javaConfig = new StrutsJavaConfiguration() {
            @Override
            public List<String> unknownHandlerStack() {
                return Arrays.asList(expectedUnknownHandler);
            }

            @Override
            public List<ConstantConfig> constants() {
                return Arrays.asList(constantConfig);
            }

            @Override
            public List<BeanConfig> beans() {
                return Arrays.asList(new BeanConfig(TestBean.class),
                        new BeanConfig(TestBean.class, "testBean", TestBean.class, Scope.PROTOTYPE, true, true));
            }
        };
        StrutsJavaConfigurationProvider provider = new StrutsJavaConfigurationProvider(javaConfig);

        Configuration configuration = new MockConfiguration();

        provider.init(configuration);

        ContainerBuilder builder = new ContainerBuilder();
        LocatableProperties props = new LocatableProperties();

        provider.register(builder, props);

        // constant
        Assert.assertEquals(String.valueOf(constantConfig.getDevMode()), props.get(StrutsConstants.STRUTS_DEVMODE));

        // unknown-handler-stack
        Assert.assertNotNull(configuration.getUnknownHandlerStack());
        Assert.assertEquals(1, configuration.getUnknownHandlerStack().size());
        Assert.assertEquals(expectedUnknownHandler, configuration.getUnknownHandlerStack().get(0).getName());

        // bean
        Container container = builder.create(true);
        TestBean testBean = container.getInstance(TestBean.class);
        Assert.assertNotNull(testBean);
    }
}
