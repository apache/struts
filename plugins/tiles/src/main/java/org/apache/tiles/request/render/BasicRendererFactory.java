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
package org.apache.tiles.request.render;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic renderer factory implementation.
 */
public class BasicRendererFactory implements RendererFactory {

    /**
     * The renderer name/renderer map.
     */
    protected Map<String, Renderer> renderers;

    /**
     * The default renderer.
     */
    protected Renderer defaultRenderer;

    /**
     * Constructor.
     */
    public BasicRendererFactory() {
        renderers = new HashMap<>();
    }

    /** {@inheritDoc} */
    public Renderer getRenderer(String name) {
        Renderer retValue;
        if (name != null) {
            retValue = renderers.get(name);
            if (retValue == null) {
                throw new NoSuchRendererException("Cannot find a renderer named '" + name + "'");
            }
        } else {
            retValue = defaultRenderer;
        }

        return retValue;
    }

    /**
     * Sets the default renderer.
     *
     * @param renderer The default renderer.
     */
    public void setDefaultRenderer(Renderer renderer) {
        this.defaultRenderer = renderer;
    }

    /**
     * Registers a renderer.
     *
     * @param name The name of the renderer.
     * @param renderer The renderer to register.
     */
    public void registerRenderer(String name, Renderer renderer) {
        renderers.put(name, renderer);
    }
}
