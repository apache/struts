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
package org.apache.struts2.conversion.impl;

import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.conversion.ConversionFileProcessor;
import org.apache.struts2.util.ClassLoaderUtil;
import org.apache.struts2.util.PropertiesCollisionBaseAction;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * WW-5685: an already-mapped key must skip that one entry, not abandon the rest of the file.
 */
public class DefaultConversionFileProcessorTest extends XWorkTestCase {

    private static final String FILENAME =
            "org/apache/struts2/util/PropertiesCollisionBaseAction-conversion.properties";

    private static final String SENTINEL = "supplied by a higher precedence source";

    /**
     * {@code Properties} extends {@code Hashtable}, so {@code entrySet()} has no defined iteration
     * order and a fixture cannot pin down which key is seen first. Pre-mapping whichever key the
     * iteration actually yields first makes this test discriminating on any JDK and in any hash
     * order: with the {@code break} this replaced, the loop stopped on that first entry and
     * registered nothing at all.
     */
    public void testEntriesAfterAnAlreadyMappedKeyAreStillRegistered() throws Exception {
        Properties fixture = loadFixture();
        String firstKey = (String) fixture.entrySet().iterator().next().getKey();

        Map<String, Object> mapping = new HashMap<>();
        mapping.put(firstKey, SENTINEL);

        processor().process(mapping, PropertiesCollisionBaseAction.class, FILENAME);

        assertEquals("the already-mapped key must not be overwritten", SENTINEL, mapping.get(firstKey));
        for (Object key : fixture.keySet()) {
            assertTrue("entry [" + key + "] was dropped after the collision on [" + firstKey + "]",
                    mapping.containsKey(key));
        }
    }

    /**
     * The complementary case: with nothing pre-mapped, every entry registers. Guards against a
     * "fix" that skips too much rather than too little.
     */
    public void testAllEntriesRegisterWhenNothingIsAlreadyMapped() throws Exception {
        Properties fixture = loadFixture();

        Map<String, Object> mapping = new HashMap<>();
        processor().process(mapping, PropertiesCollisionBaseAction.class, FILENAME);

        assertEquals("every entry in the file must register", fixture.size(), mapping.size());
    }

    private ConversionFileProcessor processor() {
        return container.getInstance(ConversionFileProcessor.class);
    }

    private Properties loadFixture() throws Exception {
        Properties properties = new Properties();
        try (InputStream is = ClassLoaderUtil.getResourceAsStream(FILENAME, getClass())) {
            properties.load(is);
        }
        assertTrue("the fixture must hold more than one key to be discriminating", properties.size() > 1);
        return properties;
    }
}
