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
package org.apache.tiles.template;

import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.AttributeContext;
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.request.Request;

/**
 * The default implementation of AttributeResolver.
 *
 * @since 2.2.0
 */
public class DefaultAttributeResolver implements AttributeResolver {

    /**
     * {@inheritDoc}
     */
    public Attribute computeAttribute(
        TilesContainer container,
        Attribute attribute,
        String name,
        String role,
        boolean ignore,
        Object defaultValue,
        String defaultValueRole,
        String defaultValueType,
        Request request
    ) {
        if (attribute == null) {
            AttributeContext evaluatingContext = container
                .getAttributeContext(request);
            attribute = evaluatingContext.getAttribute(name);
            if (attribute == null) {
                attribute = computeDefaultAttribute(defaultValue,
                    defaultValueRole, defaultValueType);
                if (attribute == null && !ignore) {
                    throw new NoSuchAttributeException("Attribute '" + name + "' not found.");
                }
            }
        }
        if (attribute != null && role != null && !"".equals(role.trim())) {
            attribute = new Attribute(attribute);
            attribute.setRole(role);
        }
        return attribute;
    }

    /**
     * Computes the default attribute.
     *
     * @param defaultValue     The default value of the attribute.
     * @param defaultValueRole The default role of tha attribute.
     * @param defaultValueType The default type of the attribute.
     * @return The default attribute.
     */
    private Attribute computeDefaultAttribute(Object defaultValue, String defaultValueRole, String defaultValueType) {
        Attribute attribute = null;
        if (defaultValue != null) {
            if (defaultValue instanceof Attribute) {
                attribute = (Attribute) defaultValue;
            } else if (defaultValue instanceof String) {
                attribute = new Attribute(defaultValue, null, defaultValueRole, defaultValueType);
            }
        }
        return attribute;
    }
}
