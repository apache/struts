/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.core.factory;

import junit.framework.TestCase;
import org.apache.tiles.api.AttributeContext;
import org.apache.tiles.api.preparer.ViewPreparer;
import org.apache.tiles.core.prepare.factory.BasicPreparerFactory;
import org.apache.tiles.request.Request;

/**
 * Tests the basic preparer factory.
 */
public class BasicPreparerFactoryTest extends TestCase {

    /**
     * The preparer factory.
     */
    private BasicPreparerFactory factory;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp() {
        factory = new BasicPreparerFactory();
    }

    /**
     * Tests getting a preparer.
     */
    public void testGetPreparer() {
        String name = MockViewPreparer.class.getName();
        ViewPreparer p = factory.getPreparer(name, null);
        assertNotNull(p);
        assertTrue(p instanceof MockViewPreparer);

        name = "org.doesnotexist.Class";
        p = factory.getPreparer(name, null);
        assertNull(p);
    }

    /**
     * Mock view preparer.
     *
     * @version $Rev$ $Date$
     */
    public static class MockViewPreparer implements ViewPreparer {

        /**
         * {@inheritDoc}
         */
        public void execute(Request tilesContext, AttributeContext attributeContext) {
        }
    }
}
