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
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.request.Request;

/**
 * Resolves an attribute, depending on the given parameters.
 *
 * @since 2.2.0
 */
public interface AttributeResolver {

    /**
     * Computes the attribute.
     *
     * @param container        The Tiles container to use.
     * @param attribute        The attribute to return immediately, if not null.
     * @param name             The name of the attribute.
     * @param role             A comma-separated list of roles. If present, the attribute
     *                         will be rendered only if the current user belongs to one of the roles.
     * @param ignore           If <code>true</code> if the computed attribute is null, this problem will be ignored.
     * @param defaultValue     The default value of the attribute. To use only if the attribute was not computed.
     * @param defaultValueRole The default comma-separated list of roles. To use only if the attribute was not computed.
     * @param defaultValueType The default type of the attribute. To use only if the attribute was not computed.
     * @param request          TODO
     * @return The computed attribute.
     * @since 2.2.0
     */
    Attribute computeAttribute(
        TilesContainer container,
        Attribute attribute,
        String name,
        String role,
        boolean ignore,
        Object defaultValue,
        String defaultValueRole,
        String defaultValueType,
        Request request);
}
