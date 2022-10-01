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
package org.apache.tiles.core.util;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.reflect.ClassUtil;
import org.junit.Before;
import org.junit.Test;

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link CombinedBeanInfo}.
 */
public class CombinedBeanInfoTest {

    /**
     * The bean info to test.
     */
    private CombinedBeanInfo beanInfo;

    /**
     * The property descriptors.
     */
    private List<FeatureDescriptor> descriptors;

    /**
     * The map of property descriptors for request.
     */
    private Map<String, PropertyDescriptor> requestMap;

    /**
     * The map of property descriptors for application.
     */
    private Map<String, PropertyDescriptor> applicationMap;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        beanInfo = new CombinedBeanInfo(Request.class, ApplicationContext.class);
        requestMap = new LinkedHashMap<>();
        ClassUtil.collectBeanInfo(Request.class, requestMap);
        applicationMap = new LinkedHashMap<>();
        ClassUtil.collectBeanInfo(ApplicationContext.class, applicationMap);
        descriptors = new ArrayList<>();
        descriptors.addAll(requestMap.values());
        descriptors.addAll(applicationMap.values());
    }

    @Test
    public void testGetDescriptors() {
        assertEquals(descriptors, beanInfo.getDescriptors());
    }

    @Test
    public void testGetMappedDescriptors() {
        assertEquals(requestMap, beanInfo.getMappedDescriptors(Request.class));
        assertEquals(applicationMap, beanInfo.getMappedDescriptors(ApplicationContext.class));
    }

    @Test
    public void testGetProperties() {
        assertEquals(requestMap.keySet(), beanInfo.getProperties(Request.class));
        assertEquals(applicationMap.keySet(), beanInfo.getProperties(ApplicationContext.class));
    }

}
