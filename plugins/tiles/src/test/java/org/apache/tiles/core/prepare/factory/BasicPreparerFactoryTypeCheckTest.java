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
package org.apache.tiles.core.prepare.factory;

import org.apache.tiles.api.AttributeContext;
import org.apache.tiles.api.preparer.ViewPreparer;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.reflect.CannotInstantiateObjectException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * The factory applies the ViewPreparer type before the named class is instantiated, so a class that
 * does not conform is rejected without being initialised or constructed, and a lookup that did not
 * produce a preparer is not retained.
 */
public class BasicPreparerFactoryTypeCheckTest {

    /**
     * The evidence lives in its own class. Reading a static member of {@link NotAPreparer} would
     * itself initialise that class and so invalidate the assertions below.
     */
    public static final class Evidence {
        public static volatile boolean classInitialised;
        public static volatile boolean constructed;
    }

    public static class NotAPreparer {
        static {
            Evidence.classInitialised = true;
        }

        public NotAPreparer() {
            Evidence.constructed = true;
        }
    }

    public static class LegitimatePreparer implements ViewPreparer {
        @Override
        public void execute(Request tilesContext, AttributeContext attributeContext) {
        }
    }

    @Test
    public void nonPreparerIsRejectedBeforeInitialisationAndConstruction() {
        CountingPreparerFactory factory = new CountingPreparerFactory();

        // A class literal does not initialise the class it denotes, so naming the class this way
        // does not disturb the assertions below.
        String name = NotAPreparer.class.getName();
        try {
            factory.getPreparer(name, null);
            fail("a class that is not a ViewPreparer must be reported");
        } catch (CannotInstantiateObjectException expected) {
            assertNotNull(expected.getMessage());
        }

        assertFalse("the static initialiser must not run for a class that is not a ViewPreparer",
            Evidence.classInitialised);
        assertFalse("the constructor must not run for a class that is not a ViewPreparer",
            Evidence.constructed);
        assertEquals("a rejected name must not be retained", 0, factory.size());
    }

    @Test
    public void unresolvableNameIsNotRetained() {
        CountingPreparerFactory factory = new CountingPreparerFactory();

        for (int i = 0; i < 100; i++) {
            assertNull(factory.getPreparer("org.doesnotexist.Preparer" + i, null));
        }

        assertEquals("a name that cannot be resolved must not be retained", 0, factory.size());
    }

    @Test
    public void resolvedPreparerIsStillCachedAndReused() {
        CountingPreparerFactory factory = new CountingPreparerFactory();
        String name = LegitimatePreparer.class.getName();

        ViewPreparer first = factory.getPreparer(name, null);
        assertNotNull("a genuine ViewPreparer must still be created", first);
        assertSame("a resolved preparer must be cached and reused", first, factory.getPreparer(name, null));
        assertEquals(1, factory.size());
    }

    /**
     * Exposes the size of the inherited cache.
     */
    private static final class CountingPreparerFactory extends BasicPreparerFactory {
        int size() {
            return preparers.size();
        }
    }
}
