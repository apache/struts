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

import org.apache.tiles.request.Request;

import java.io.IOException;

/**
 * An object that can render a path, depending on the request passed as a parameter.
 */
public interface Renderer {

    /**
     * Renders a path.
     *
     * @param path The path to render.
     * @param request The Tiles request context.
     * @throws IOException If something goes wrong during rendition.
     */
    void render(String path, Request request) throws IOException;

    /**
     * Checks if this renderer can render a path. Note that this does not mean
     * it is the <strong>best</strong> renderer available, but checks only its capability.
     *
     * @param path The path to be rendered.
     * @param request The request context.
     * @return <code>true</code> if this renderer can render the attribute.
     */
    boolean isRenderable(String path, Request request);
}
