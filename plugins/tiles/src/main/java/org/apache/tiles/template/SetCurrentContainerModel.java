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

import org.apache.tiles.api.access.TilesAccess;
import org.apache.tiles.request.Request;

/**
 * Selects a container to be used as the "current" container.
 *
 *  @since 3.0.0
 */
public class SetCurrentContainerModel {

    /**
     * Executes the model.
     *
     * @param containerKey The key of the container to be used as "current". If
     *                     <code>null</code>, the default one will be used.
     * @param request      The request.
     */
    public void execute(String containerKey, Request request) {
        TilesAccess.setCurrentContainer(request, containerKey);
    }
}
