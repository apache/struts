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
