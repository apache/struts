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

package org.apache.tiles.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests {@link TilesException}.
 */
public class TilesExceptionTest {

    @Test
    public void testTilesException() {
        TilesException exception = new TilesException();
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void testTilesExceptionString() {
        TilesException exception = new TilesException("my message");
        assertEquals("my message", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void testTilesExceptionThrowable() {
        Throwable cause = new Throwable();
        TilesException exception = new TilesException(cause);
        assertEquals(cause.toString(), exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testTilesExceptionStringThrowable() {
        Throwable cause = new Throwable();
        TilesException exception = new TilesException("my message", cause);
        assertEquals("my message", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

}
