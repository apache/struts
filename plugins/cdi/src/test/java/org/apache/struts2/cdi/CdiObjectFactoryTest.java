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

import org.jboss.weld.bootstrap.api.helpers.RegistrySingletonProvider;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import jakarta.enterprise.inject.spi.InjectionTarget;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CdiObjectFactoryTest {

    private static final String SHARED_JNDI = "org.osjava.sj.jndi.shared";
    private static InitialContext context;
    private static WeldContainer container;

    @BeforeClass
    public static void setup() throws Exception {
        container = new Weld().containerId(RegistrySingletonProvider.STATIC_INSTANCE).initialize();

        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.SimpleContextFactory");
        System.setProperty(SHARED_JNDI, "true");
        context = new InitialContext();
        context.bind(CdiObjectFactory.CDI_JNDIKEY_BEANMANAGER_COMP, container.getBeanManager());
    }

    @AfterClass
    public static void tearDown() throws NamingException {
        container.shutdown();

        context.unbind(CdiObjectFactory.CDI_JNDIKEY_BEANMANAGER_COMP);
        context.close();
        System.clearProperty(Context.INITIAL_CONTEXT_FACTORY);
        System.clearProperty(SHARED_JNDI);
    }

    @Test
    public void testFindBeanManager() {
        assertNotNull(new CdiObjectFactory().findBeanManager());
    }

    @Test
    public void testGetBean() throws Exception {
        var cdiObjectFactory = new CdiObjectFactory();
        var fooConsumer = (FooConsumer) cdiObjectFactory.buildBean(FooConsumer.class.getCanonicalName(), null, false);
        assertNotNull(fooConsumer);
        assertNotNull(fooConsumer.fooService);
    }

    @Test
    public void testGetInjectionTarget() {
        var cdiObjectFactory = new CdiObjectFactory();
        InjectionTarget<?> injectionTarget = cdiObjectFactory.getInjectionTarget(FooConsumer.class);
        assertNotNull(injectionTarget);
        assertTrue(cdiObjectFactory.injectionTargetCache.containsKey(FooConsumer.class));
        assertSame(cdiObjectFactory.getInjectionTarget(FooConsumer.class), injectionTarget);
    }
}
