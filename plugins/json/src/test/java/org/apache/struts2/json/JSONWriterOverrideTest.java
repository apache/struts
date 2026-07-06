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
package org.apache.struts2.json;

import org.apache.struts2.junit.StrutsTestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Regression test for WW-5641: {@code struts.json.writer} / {@code struts.json.reader}
 * overrides from an application config were ignored on the 7.2.x line because the JSON
 * plugin's {@code <bean-selection>} ran at plugin-parse time, before the app config.
 * Moving it to {@code struts-deferred.xml} makes it run last, so the override wins.
 *
 * <p>Boots the real Dispatcher chain. {@code struts-deferred.xml} is loaded unconditionally
 * by {@code Dispatcher.init()} after the {@code config} chain, so it is intentionally omitted
 * from the config init param below.</p>
 */
public class JSONWriterOverrideTest extends StrutsTestCase {

    @Override
    protected void setupBeforeInitDispatcher() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("config", "struts-default.xml,struts-plugin.xml,struts-json-override.xml");
        dispatcherInitParams = params;
    }

    /**
     * The default JSONWriter binding must resolve to the app override, not StrutsJSONWriter.
     */
    public void testCustomWriterWinsAsDefaultBinding() {
        JSONWriter writer = container.getInstance(JSONWriter.class);
        assertEquals("struts.json.writer override was ignored; default StrutsJSONWriter was used",
                CustomTestJSONWriter.class, writer.getClass());
    }

    /**
     * The default JSONReader binding must resolve to the app override, not StrutsJSONReader.
     */
    public void testCustomReaderWinsAsDefaultBinding() {
        JSONReader reader = container.getInstance(JSONReader.class);
        assertEquals("struts.json.reader override was ignored; default StrutsJSONReader was used",
                CustomTestJSONReader.class, reader.getClass());
    }

    /**
     * End-to-end: the writer JSONUtil actually serializes with must be the override.
     */
    public void testJSONUtilUsesCustomWriter() throws Exception {
        JSONUtil jsonUtil = container.getInstance(JSONUtil.class);
        assertEquals(CustomTestJSONWriter.SENTINEL, jsonUtil.serialize(new Object(), false));
    }

    /**
     * End-to-end: the reader JSONUtil exposes must be the override.
     */
    public void testJSONUtilUsesCustomReader() {
        JSONUtil jsonUtil = container.getInstance(JSONUtil.class);
        assertEquals(CustomTestJSONReader.class, jsonUtil.getReader().getClass());
    }
}
