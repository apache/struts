/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.tiles;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.DispatcherListener;
import org.apache.tiles.core.startup.TilesInitializer;
import org.apache.tiles.request.servlet.ServletApplicationContext;
import org.apache.tiles.web.startup.AbstractTilesListener;

/**
 * Listener used to automatically tie Tiles support into Struts
 *
 * @since Struts 2.0.2
 */
public class StrutsTilesListener extends AbstractTilesListener implements DispatcherListener {

    private static final Logger LOG = LogManager.getLogger(StrutsTilesListener.class);

    private ServletContext servletContext;
    private boolean legacyOgnlEnabled;
    private boolean listeningToDispatcher;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        servletContext = event.getServletContext();
        Dispatcher dispatcher = Dispatcher.getInstance(servletContext);
        if (dispatcher == null) {
            Dispatcher.addDispatcherListener(this);
            listeningToDispatcher = true;
        } else {
            initializeTiles(dispatcher);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (listeningToDispatcher) {
            Dispatcher.removeDispatcherListener(this);
            listeningToDispatcher = false;
        }
        destroyTiles();
        servletContext = null;
    }

    @Override
    public void dispatcherInitialized(Dispatcher dispatcher) {
        initializeTiles(dispatcher);
    }

    @Override
    public void dispatcherDestroyed(Dispatcher dispatcher) {
        destroyTiles();
    }

    @Override
    protected TilesInitializer createTilesInitializer() {
        LOG.info("Starting Struts Tiles 3 integration ...");
        return new StrutsTilesInitializer(legacyOgnlEnabled);
    }

    boolean isLegacyOgnlEnabled() {
        return legacyOgnlEnabled;
    }

    @SuppressWarnings("removal")
    private synchronized void initializeTiles(Dispatcher dispatcher) {
        if (initializer != null) {
            return;
        }
        String configuredValue = dispatcher.getContainer().getInstance(
            String.class, TilesConstants.STRUTS_TILES_OGNL_LEGACY_ENABLED);
        legacyOgnlEnabled = BooleanUtils.toBoolean(configuredValue);
        initializer = createTilesInitializer();
        initializer.initialize(new ServletApplicationContext(servletContext));
    }

    private synchronized void destroyTiles() {
        if (initializer != null) {
            initializer.destroy();
            initializer = null;
        }
    }
}
