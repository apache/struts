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

package org.apache.tiles.ognl;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.junit.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link TilesApplicationContextNestedObjectExtractor}.
 */
public class TilesApplicationContextNestedObjectExtractorTest {

    /**
     * Tests {@link TilesApplicationContextNestedObjectExtractor#getNestedObject(Request)}.
     */
    @Test
    public void testGetNestedObject() {
        Request request = createMock(Request.class);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        expect(request.getApplicationContext()).andReturn(applicationContext);

        replay(request, applicationContext);
        NestedObjectExtractor<Request> extractor = new TilesApplicationContextNestedObjectExtractor();
        assertEquals(applicationContext, extractor.getNestedObject(request));
        verify(request, applicationContext);
    }
}
