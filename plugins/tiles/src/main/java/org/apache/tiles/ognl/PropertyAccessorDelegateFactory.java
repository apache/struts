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

import ognl.PropertyAccessor;

/**
 * Decides a {@link PropertyAccessor} depending on the property name and the
 * object to evaluate.
 *
 * @param <T> The type of the root object to evaluate.
 * @since 2.2.0
 */
public interface PropertyAccessorDelegateFactory<T> {

    /**
     * Returns a prooerty accessor appropriate for the property name and the
     * object passed.
     *
     * @param propertyName The name of the property.
     * @param obj The root object to evaluate.
     * @return The appropriate property accessor.
     * @since 2.2.0
     */
    PropertyAccessor getPropertyAccessor(String propertyName, T obj);
}
