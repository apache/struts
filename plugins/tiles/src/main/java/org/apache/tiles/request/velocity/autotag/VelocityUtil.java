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
package org.apache.tiles.request.velocity.autotag;

import java.util.Map;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.node.ASTMap;
import org.apache.velocity.runtime.parser.node.Node;

/**
 * Utilities for Velocity usage in Tiles.
 */
public final class VelocityUtil {

    /**
     * Private constructor to avoid instantiation.
     */
    private VelocityUtil() {
    }

    /**
     * Extracts the parameters from the directives, by getting the child at position
     * 0 supposing it is a map.
     *
     * @param context The Velocity context.
     * @param node    The node to use.
     * @return The extracted parameters.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getParameters(InternalContextAdapter context, Node node) {
        ASTMap astMap = (ASTMap) node.jjtGetChild(0);
        Map<String, Object> params = (Map<String, Object>) astMap.value(context);
        return params;
    }

    /**
     * Returns the "value" parameter if it is not null, otherwise returns
     * "defaultValue".
     *
     * @param value        The value to return, if it is not null.
     * @param defaultValue The value to return, if <code>value</code> is null.
     * @return The value, defaulted if necessary.
     */
    public static Object getObject(Object value, Object defaultValue) {
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }
}
