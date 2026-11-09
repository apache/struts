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
package org.apache.struts2.tiles;

import org.apache.struts2.ActionContext;
import org.apache.struts2.ObjectFactory;
import org.apache.struts2.inject.Container;
import org.apache.tiles.api.AttributeContext;
import org.apache.tiles.api.preparer.ViewPreparer;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.reflect.CannotInstantiateObjectException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The factory applies the ViewPreparer type before the bean is built, so a configured name that does
 * not denote a preparer is rejected without anything being constructed, and a name that cannot be
 * built is not constructed a second time through the superclass.
 */
public class StrutsPreparerFactoryTypeCheckTest {

    /**
     * The evidence lives in its own class so that reading it does not initialise {@link NotAPreparer}.
     */
    public static final class Evidence {
        public static volatile boolean constructed;
        public static volatile int preparerConstructions;
    }

    public static class NotAPreparer {
        public NotAPreparer() {
            Evidence.constructed = true;
        }
    }

    public static class LegitimatePreparer implements ViewPreparer {
        public LegitimatePreparer() {
            Evidence.preparerConstructions++;
        }

        @Override
        public void execute(Request tilesContext, AttributeContext attributeContext) {
        }
    }

    private ObjectFactory objectFactory;
    private StrutsPreparerFactory factory;

    @Before
    public void setUp() {
        Evidence.constructed = false;
        Evidence.preparerConstructions = 0;

        objectFactory = mock(ObjectFactory.class);
        Container container = mock(Container.class);
        when(container.getInstance(ObjectFactory.class)).thenReturn(objectFactory);

        ActionContext.of().withContainer(container).bind();

        factory = new StrutsPreparerFactory();
    }

    @After
    public void tearDown() {
        ActionContext.clear();
        Evidence.constructed = false;
    }

    @Test
    public void nonPreparerIsRejectedBeforeTheBeanIsBuilt() throws Exception {
        // A class literal does not initialise the class it denotes.
        String name = NotAPreparer.class.getName();

        CannotInstantiateObjectException e = assertThrows(CannotInstantiateObjectException.class,
            () -> factory.getPreparer(name, null));
        assertNotNull(e.getMessage());

        verify(objectFactory, never()).buildBean(anyString(), anyMap());
        assertFalse("a class that is not a ViewPreparer must not be constructed", Evidence.constructed);
    }

    @Test
    public void beanThatIsNotAPreparerIsReportedAndNotBuiltTwice() throws Exception {
        String name = "someContainerManagedName";
        when(objectFactory.buildBean(anyString(), anyMap())).thenReturn("not a preparer");

        assertThrows(CannotInstantiateObjectException.class, () -> factory.getPreparer(name, null));

        verify(objectFactory).buildBean(anyString(), anyMap());
        assertFalse(Evidence.constructed);
    }

    @Test
    public void buildFailureStillFallsBackToTheSuperclassAndConstructsOnce() throws Exception {
        String name = LegitimatePreparer.class.getName();
        when(objectFactory.buildBean(anyString(), anyMap())).thenThrow(new IllegalStateException("boom"));

        assertNotNull("a build failure must still fall back to the superclass", factory.getPreparer(name, null));
        assertEquals("the preparer must be constructed exactly once", 1, Evidence.preparerConstructions);
    }

    @Test
    public void genuinePreparerIsBuiltThroughTheObjectFactory() throws Exception {
        String name = LegitimatePreparer.class.getName();
        LegitimatePreparer expected = new LegitimatePreparer();
        when(objectFactory.buildBean(anyString(), anyMap())).thenReturn(expected);

        ViewPreparer preparer = factory.getPreparer(name, null);

        assertSame(expected, preparer);
        assertSame("a resolved preparer must still be cached and reused", preparer, factory.getPreparer(name, null));
    }
}
