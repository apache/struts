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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.config.entities.BeanConfig;
import org.apache.struts2.config.entities.BeanSelectionConfig;
import org.apache.struts2.config.entities.ConstantConfig;
import org.junit.Assert;
import org.junit.Test;

import org.apache.struts2.TestBean;
import org.apache.struts2.config.Configuration;
import org.apache.struts2.config.impl.MockConfiguration;
import org.apache.struts2.inject.Container;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.inject.Scope;
import org.apache.struts2.util.location.LocatableProperties;

public class StrutsJavaConfigurationProviderTest {
    @Test
    public void testRegister() throws Exception {
        final ConstantConfig constantConfig = new ConstantConfig();
        constantConfig.setDevMode(true);

        final String expectedUnknownHandler = "expectedUnknownHandler";

        StrutsJavaConfiguration javaConfig = new StrutsJavaConfiguration() {
            @Override
            public List<String> unknownHandlerStack() {
                return Collections.singletonList(expectedUnknownHandler);
            }

            @Override
            public List<ConstantConfig> constants() {
                return Collections.singletonList(constantConfig);
            }

            @Override
            public List<BeanConfig> beans() {
                return Arrays.asList(
                    new BeanConfig(TestBean.class, "struts"),
                    new BeanConfig(TestBean.class, "struts.static", TestBean.class, Scope.PROTOTYPE, true, true),
                    new BeanConfig(TestBean.class, "struts.test.bean", TestBean.class)
                );
            }

            @Override
            public Optional<BeanSelectionConfig> beanSelection() {
                return Optional.of(new BeanSelectionConfig(TestBeanSelectionProvider.class, "testBeans"));
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

        testBean = container.getInstance(TestBean.class, "struts");
        Assert.assertNotNull(testBean);

        // bean selection
        Set<String> names = container.getInstanceNames(TestBean.class);
        Assert.assertTrue(names.contains("struts"));
        Assert.assertTrue(names.contains("struts.test.bean"));
    }

    @Test
    /**
     * This test is purely to provide code coverage for {@link AbstractBeanSelectionProvider}.
     * It uses an arbitrary setup to ensure a code path not followed in the registration test
     * is traversed.
     */
    public void testAbstractBeanProviderCoverage() throws Exception {
        final ConstantConfig constantConfig = new ConstantConfig();
        final String expectedUnknownHandler = "expectedUnknownHandler";

        StrutsJavaConfiguration javaConfig = new StrutsJavaConfiguration() {
            @Override
            public List<String> unknownHandlerStack() {
                return Collections.singletonList(expectedUnknownHandler);
            }

            @Override
            public List<ConstantConfig> constants() {
                return Collections.singletonList(constantConfig);
            }

            @Override
            public List<BeanConfig> beans() {
                return Arrays.asList(
                    new BeanConfig(TestBean.class, "struts")
                );
            }

            @Override
            public Optional<BeanSelectionConfig> beanSelection() {
                return Optional.of(new BeanSelectionConfig(TestBeanSelectionProvider.class, "testBeans"));
            }
        };

        StrutsJavaConfigurationProvider provider = new StrutsJavaConfigurationProvider(javaConfig);
        Configuration configuration = new MockConfiguration();
        ContainerBuilder builder = new ContainerBuilder();
        LocatableProperties props = new LocatableProperties();

        provider.init(configuration);
        provider.register(builder, props);

        props.put(CodeCoverageTestClass1.ALIAS_KEY, CodeCoverageTestClass1.ALIAS_VALUE);
        TestBeanSelectionProvider testBeanSelectionProvider = new TestBeanSelectionProvider();
        testBeanSelectionProvider.aliasCallCoverage(CodeCoverageTestClass1.class, builder, props, CodeCoverageTestClass1.ALIAS_KEY, Scope.THREAD);
    }

    final class CodeCoverageTestClass1 extends Object {
        public static final String ALIAS_KEY = "testAliasKey";
        public static final String ALIAS_VALUE  = "testAliasValue";

        public CodeCoverageTestClass1() {
            super();
        }
    }
}
