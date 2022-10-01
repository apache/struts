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

import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.api.access.TilesAccess;
import org.apache.tiles.core.factory.AbstractTilesContainerFactory;
import org.apache.tiles.request.ApplicationAccess;
import org.apache.tiles.request.ApplicationContext;

/**
 * Default Tiles initialization delegate implementation under a servlet
 * environment. It uses init parameters to create the
 * {@link ApplicationContext} and the {@link TilesContainer}.
 *
 * @since 2.2.0
 */
public abstract class AbstractTilesInitializer implements TilesInitializer {

    /**
     * The initialized application context.
     */
    private ApplicationContext applicationContext;

    /**
     * {@inheritDoc}
     */
    public void initialize(ApplicationContext applicationContext) {
        this.applicationContext = createTilesApplicationContext(applicationContext);
        ApplicationAccess.register(applicationContext);
        String key = getContainerKey(this.applicationContext);
        // The initialized container.
        TilesContainer container = createContainer(this.applicationContext);
        TilesAccess.setContainer(this.applicationContext, container, key);
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() {
        TilesAccess.setContainer(applicationContext, null, getContainerKey(applicationContext));
    }

    /**
     * Creates the Tiles application context, to be used across all the
     * Tiles-based application. If you override this class, please override this
     * method.<br>
     * This implementation returns the preliminary context passed as a parameter
     *
     * @param preliminaryContext The preliminary application context to use.
     * @return The Tiles application context.
     * @since 2.2.0
     */
    protected ApplicationContext createTilesApplicationContext(ApplicationContext preliminaryContext) {
        return preliminaryContext;
    }

    /**
     * Returns the container key under which the container will be stored.
     * This implementation returns <code>null</code> so that the container will
     * be the default one.
     *
     * @param applicationContext The Tiles application context to use.
     * @return The container key.
     * @since 2.2.0
     */
    protected String getContainerKey(ApplicationContext applicationContext) {
        return null;
    }

    /**
     * Creates a Tiles container. If you override this class, please override
     * this method or {@link #createContainerFactory(ApplicationContext)}.
     *
     * @param context The servlet context to use.
     * @return The created container.
     * @since 2.2.0
     */
    protected TilesContainer createContainer(ApplicationContext context) {
        AbstractTilesContainerFactory factory = createContainerFactory(context);
        return factory.createContainer(context);
    }

    /**
     * Creates a Tiles container factory. If you override this class, please
     * override this method or {@link #createContainer(ApplicationContext)}.
     *
     * @param context The servlet context to use.
     * @return The created container factory.
     * @since 2.2.0
     */
    protected abstract AbstractTilesContainerFactory createContainerFactory(
        ApplicationContext context);
}
