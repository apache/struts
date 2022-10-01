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
package org.apache.tiles.core.renderer;

import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.render.CannotRenderException;
import org.apache.tiles.request.render.Renderer;

import java.io.IOException;

/**
 * Renders an attribute that contains a reference to a definition.
 *
 * @since 3.0.0
 */
public class DefinitionRenderer implements Renderer {

    /**
     * The Tiles container.
     */
    private final TilesContainer container;

    /**
     * Constructor.
     *
     * @param container The Tiles container.
     */
    public DefinitionRenderer(TilesContainer container) {
        this.container = container;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(String path, Request request) throws IOException {
        if (path == null) {
            throw new CannotRenderException("Cannot dispatch a null path");
        }

        container.render(path, request);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRenderable(String path, Request request) {
        return path != null && container.isValidDefinition(path, request);
    }
}
