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
package org.apache.tiles.api.mgmt;

import org.apache.tiles.api.Definition;
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.request.Request;

/**
 * Defines a mutable version of the TilesContainer.
 *
 * @since Tiles 2.0
 */
public interface MutableTilesContainer extends TilesContainer {

    /**
     * Register a new definition with the container.
     *
     * @param definition The definition to register.
     * @param request TODO
     */
    void register(Definition definition, Request request);
}
