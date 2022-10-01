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
package org.apache.tiles.autotag.core.runtime;

/**
 * Builder interface for creating requests.
 * The implementations are expected to provide a default constructor,
 * and to implement another interface that can be used to provide the
 * parameters needed to build the actual request object.
 */
public interface AutotagRuntime<R> {
    /**
     * Creates a new Request instance.
     *
     * @return The Request.
     */
    R createRequest();

    /**
     * Creates a new ModelBody instance to match the request.
     *
     * @return The ModelBody.
     */
    ModelBody createModelBody();

    /**
     * Extracts a parameter from the tag.
     * @param name The name of the parameter.
     * @param defaultValue The default value if none is specified.
     * @return The value of the parameter.
     */
    <T> T getParameter(String name, Class<T> type, T defaultValue);
}
