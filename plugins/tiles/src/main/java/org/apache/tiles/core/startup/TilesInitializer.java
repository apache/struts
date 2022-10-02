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
package org.apache.tiles.core.startup;

import org.apache.tiles.request.ApplicationContext;

/**
 * Tiles initialization delegate. It initializes Tiles container(s) and the
 * application context to use across all the application.
 */
public interface TilesInitializer {

    /**
     * Initializes Tiles.
     *
     * @param preliminaryContext The preliminary application context to use. It
     * will be overwritten with the real instance later. Use a context usable
     * under your current environment, like ServletTilesApplicationContext or
     * PortletTilesApplicationContext.
     */
    void initialize(ApplicationContext preliminaryContext);

    /**
     * Destroys the Tiles container.
     *
     * @since 2.2.0
     */
    void destroy();
}
