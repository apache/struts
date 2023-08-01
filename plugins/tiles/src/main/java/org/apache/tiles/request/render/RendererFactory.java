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

/**
 * <p>
 * Factory interface used to create/retrieve instances of the {@link Renderer} interface.
 * </p>
 *
 * <p>
 * This factory provides an extension point into the default tiles
 * implementation. Implementors wishing to provide per request initialization of
 * the AttributeRenderer (for instance) may provide a custom renderer.
 * </p>
 */
public interface RendererFactory {

    /**
     * Returns a renderer by its name.
     *
     * @param name The name of the renderer.
     * @return The renderer.
     */
    Renderer getRenderer(String name);
}
