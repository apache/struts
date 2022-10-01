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

package org.apache.tiles.core.definition;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests {@link DefinitionsFactoryException}.
 */
public class DefinitionsFactoryExceptionTest {

    /**
     * Test method for {@link DefinitionsFactoryException#DefinitionsFactoryException()}.
     */
    @Test
    public void testDefinitionsFactoryException() {
        DefinitionsFactoryException exception = new DefinitionsFactoryException();
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test method for {@link DefinitionsFactoryException#DefinitionsFactoryException(String)}.
     */
    @Test
    public void testDefinitionsFactoryExceptionString() {
        DefinitionsFactoryException exception = new DefinitionsFactoryException("my message");
        assertEquals("my message", exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test method for {@link DefinitionsFactoryException#DefinitionsFactoryException(Throwable)}.
     */
    @Test
    public void testDefinitionsFactoryExceptionThrowable() {
        Throwable cause = new Throwable();
        DefinitionsFactoryException exception = new DefinitionsFactoryException(cause);
        assertEquals(cause.toString(), exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    /**
     * Test method for {@link DefinitionsFactoryException#DefinitionsFactoryException(String, Throwable)}.
     */
    @Test
    public void testDefinitionsFactoryExceptionStringThrowable() {
        Throwable cause = new Throwable();
        DefinitionsFactoryException exception = new DefinitionsFactoryException("my message", cause);
        assertEquals("my message", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

}
