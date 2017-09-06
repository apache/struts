package com.opensymphony.xwork2;

import com.opensymphony.xwork2.inject.Container;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Regression-test the ObjectFactory API.
 */
public class ObjectFactoryTest {

    private Container container1;
    private Container container2;

    @BeforeMethod
    public void setUp() {
        container1 = EasyMock.createMock(Container.class);
        container2 = EasyMock.createMock(Container.class);
    }

    @Test
    public void shouldInjectContainerByConstructor() {
        ObjectFactory factory = new StubObjectFactory(container1);

        verifyContainer1Injected(factory);
    }

    @Test
    public void shouldInjectContainerBySetterWhenMissing() {
        ObjectFactory factory = new StubObjectFactory();
        factory.setContainer(container1);

        verifyContainer1Injected(factory);
    }

    @Test
    public void shouldIgnoreDifferentContainerWhenInjectedByConstructor() {
        ObjectFactory factory = new StubObjectFactory(container1);
        factory.setContainer(container2);

        verifyContainer1Injected(factory);
    }

    @Test
    public void shouldIgnoreDifferentContainerWhenInjectedBySetterTwice() {
        ObjectFactory factory = new StubObjectFactory();
        factory.setContainer(container1);
        factory.setContainer(container2);

        verifyContainer1Injected(factory);
    }

    @Test
    public void shouldIgnoreInjectingNullContainerBySetter() {
        ObjectFactory factory = new StubObjectFactory(container1);
        factory.setContainer(null);

        verifyContainer1Injected(factory);
    }

    private void verifyContainer1Injected(ObjectFactory factory) {
        Object obj = new Object();
        container1.inject(obj);
        EasyMock.expectLastCall();
        EasyMock.replay(container1, container2);

        factory.injectInternalBeans(obj);
    }

    static class StubObjectFactory extends ObjectFactory {

        StubObjectFactory() {}

        StubObjectFactory(Container container) {
            super(container);
        }

    }

}
