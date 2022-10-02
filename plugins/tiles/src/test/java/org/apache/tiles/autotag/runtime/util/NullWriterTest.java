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
package org.apache.tiles.autotag.runtime.util;

import org.apache.tiles.autotag.core.runtime.util.NullWriter;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link NullWriter}.
 */
public class NullWriterTest {

    /**
     * A dummy size.
     */
    private static final int DUMMY_SIZE = 15;
    /**
     * The object to test.
     */
    private NullWriter writer;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        writer = new NullWriter();
    }

    /**
     * Test method for {@link NullWriter#write(char[], int, int)}.
     */
    @Test
    public void testWriteCharArrayIntInt() {
        writer.write("Hello there".toCharArray(), 0, DUMMY_SIZE);
    }

    /**
     * Test method for {@link NullWriter#flush()}.
     */
    @Test
    public void testFlush() {
        writer.flush();
    }

    /**
     * Test method for {@link NullWriter#close()}.
     */
    @Test
    public void testClose() {
        writer.close();
    }

}
