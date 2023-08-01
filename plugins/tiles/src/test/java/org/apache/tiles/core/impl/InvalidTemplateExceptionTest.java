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

package org.apache.tiles.core.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests {@link InvalidTemplateException}.
 */
public class InvalidTemplateExceptionTest {

    /**
     * Test method for {@link InvalidTemplateException#InvalidTemplateException(String)}.
     */
    @Test
    public void testInvalidTemplateExceptionString() {
        InvalidTemplateException exception = new InvalidTemplateException("my message");
        assertEquals("my message", exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test method for {@link InvalidTemplateException#InvalidTemplateException(Throwable)}.
     */
    @Test
    public void testInvalidTemplateExceptionThrowable() {
        Throwable cause = new Throwable();
        InvalidTemplateException exception = new InvalidTemplateException(cause);
        assertEquals(cause.toString(), exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

}
