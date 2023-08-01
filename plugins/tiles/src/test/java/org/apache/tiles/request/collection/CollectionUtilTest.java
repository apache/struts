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
package org.apache.tiles.request.collection;

import org.junit.Test;

import java.util.Enumeration;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

public class CollectionUtilTest {

    @Test
    public void testKey() {
        assertEquals("1", CollectionUtil.key(1));
        assertEquals("hello", CollectionUtil.key("hello"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testKeyException() {
        CollectionUtil.key(null);
    }

    @Test
    public void testEnumerationSize() {
        Enumeration<Object> enumeration = createMock(Enumeration.class);

        expect(enumeration.hasMoreElements()).andReturn(true);
        expect(enumeration.nextElement()).andReturn(1);
        expect(enumeration.hasMoreElements()).andReturn(true);
        expect(enumeration.nextElement()).andReturn(1);
        expect(enumeration.hasMoreElements()).andReturn(false);

        replay(enumeration);
        assertEquals(2, CollectionUtil.enumerationSize(enumeration));
        verify(enumeration);
    }

}
