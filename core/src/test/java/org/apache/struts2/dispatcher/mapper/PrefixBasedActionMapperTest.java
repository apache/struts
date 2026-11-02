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
package org.apache.struts2.dispatcher.mapper;

import junit.framework.TestCase;
import org.apache.struts2.inject.Container;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PrefixBasedActionMapperTest extends TestCase {

    private Container container;
    private PrefixBasedActionMapper mapper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        container = mock(Container.class);
        mapper = new PrefixBasedActionMapper();
        mapper.setContainer(container);
    }

    public void testSupportsDeclaredExtensionsWhenAllMappersSupportThem() {
        when(container.getInstance(ActionMapper.class, "struts")).thenReturn(new DefaultActionMapper());
        when(container.getInstance(ActionMapper.class, "restful2")).thenReturn(new Restful2ActionMapper());
        mapper.setPrefixBasedActionMappers("/app:struts,/api:restful2");

        assertTrue(mapper.supportsDeclaredExtensions());
    }

    public void testDoesNotSupportDeclaredExtensionsWhenOneMapperDoesNot() {
        when(container.getInstance(ActionMapper.class, "struts")).thenReturn(new DefaultActionMapper());
        when(container.getInstance(ActionMapper.class, "restful")).thenReturn(new RestfulActionMapper());
        mapper.setPrefixBasedActionMappers("/app:struts,/api:restful");

        assertFalse(mapper.supportsDeclaredExtensions());
    }

    public void testDoesNotSupportDeclaredExtensionsWithoutMappers() {
        mapper.setPrefixBasedActionMappers("");

        assertFalse(mapper.supportsDeclaredExtensions());
    }
}
