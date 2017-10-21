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
package org.apache.struts2.cdi;

import org.jboss.weld.environment.se.StartMain;
import static org.junit.Assert.*;

import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import javax.enterprise.inject.spi.InjectionTarget;

/**
 * CdiObjectFactoryTest.
 */
public class CdiObjectFactoryTest {

    @Before
    public void setUp() throws Exception {
        SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
        builder.activate();

        StartMain sm = new StartMain(new String[0]);
        WeldContainer weldContainer = sm.go();
        builder.bind(CdiObjectFactory.CDI_JNDIKEY_BEANMANAGER_COMP, weldContainer.getBeanManager());
    }

    @Test
    public void testFindBeanManager() throws Exception {
        assertNotNull(new CdiObjectFactory().findBeanManager());
    }

    @Test
    public void testGetBean() throws Exception {
        final CdiObjectFactory cdiObjectFactory = new CdiObjectFactory();
        FooConsumer fooConsumer = (FooConsumer) cdiObjectFactory.buildBean(FooConsumer.class.getCanonicalName(), null, false);
        assertNotNull(fooConsumer);
        assertNotNull(fooConsumer.fooService);
    }

    @Test public void testGetInjectionTarget() throws Exception {
        final CdiObjectFactory cdiObjectFactory = new CdiObjectFactory();
        final InjectionTarget<?> injectionTarget = cdiObjectFactory.getInjectionTarget(FooConsumer.class);
        assertNotNull(injectionTarget);
        assertTrue(cdiObjectFactory.injectionTargetCache.containsKey(FooConsumer.class));
        assertSame(cdiObjectFactory.getInjectionTarget(FooConsumer.class), injectionTarget);
    }
}
