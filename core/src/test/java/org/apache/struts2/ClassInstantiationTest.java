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
package org.apache.struts2;

import org.apache.struts2.dispatcher.mapper.ActionMapper;

import java.util.HashMap;

/**
 * Test class instantiation with Container
 */
public class ClassInstantiationTest extends StrutsInternalTestCase {

    public void testCompositeActionMapperInstantiationWithList() throws Exception {
        // given
        initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
            put(StrutsConstants.STRUTS_MAPPER_COMPOSITE, "struts,restful");
        }});

        // when
        ActionMapper instance = container.getInstance(ActionMapper.class, "composite");

        // then
        assertNotNull(instance);
    }

    public void testCompositeActionMapperInstantiationWithoutList() throws Exception {
        // given
        initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
        }});

        // when
        try {
            container.getInstance(ActionMapper.class, "composite");
            fail();
        }catch (Exception e) {
            // then
            // You cannot use CompositeActionMapper without defined list of "struts.mapper.composite"
            assertTrue(e.getMessage().contains("No mapping found for dependency [type=java.lang.String, name='struts.mapper.composite']"));
        }
    }

}
