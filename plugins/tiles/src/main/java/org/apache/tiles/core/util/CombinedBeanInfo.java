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

import org.apache.tiles.request.reflect.ClassUtil;

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains the bean infos about one or more classes.
 * @since 2.2.0
 */
public class CombinedBeanInfo {
    /**
     * The descriptors of the introspected classes.
     */
    private final List<FeatureDescriptor> descriptors;

    /**
     * Maps analyzed classes to the map of introspected properties.
     */
    private final Map<Class<?>, Map<String, PropertyDescriptor>> class2descriptors;

    /**
     * Constructor.
     * @param clazzes The list of classes to analyze and combine.
     *
     * @since 2.2.0
     */
    public CombinedBeanInfo(Class<?>... clazzes) {
        descriptors = new ArrayList<>();
        class2descriptors = new LinkedHashMap<>();
        for (Class<?> clazz : clazzes) {
            Map<String, PropertyDescriptor> mappedDescriptors = new LinkedHashMap<>();
            ClassUtil.collectBeanInfo(clazz, mappedDescriptors);
            descriptors.addAll(mappedDescriptors.values());
            class2descriptors.put(clazz, mappedDescriptors);
        }
    }

    /**
     * Returns the descriptors of all the introspected classes.
     *
     * @return The feature descriptors.
     * @since 2.2.0
     */
    public List<FeatureDescriptor> getDescriptors() {
        return descriptors;
    }

    /**
     * Returns a map of the introspected properties for the given class.
     *
     * @param clazz The class to get the properties from.
     * @return The map of property descriptors.
     * @since 2.2.0
     */
    public Map<String, PropertyDescriptor> getMappedDescriptors(Class<?> clazz) {
        return class2descriptors.get(clazz);
    }

    /**
     * Returns the set of properties for the given introspected class.
     *
     * @param clazz The class to get the properties from.
     * @return The set of properties.
     * @since 2.2.0
     */
    public Set<String> getProperties(Class<?> clazz) {
        return class2descriptors.get(clazz).keySet();
    }
}
