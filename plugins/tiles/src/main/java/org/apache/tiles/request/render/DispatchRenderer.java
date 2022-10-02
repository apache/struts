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

import java.io.IOException;

import org.apache.tiles.request.Request;
import org.apache.tiles.request.DispatchRequest;
import org.apache.tiles.request.RequestWrapper;

/**
 * Renders an attribute that contains a reference to a template.
 */
public class DispatchRenderer implements Renderer {

    /** {@inheritDoc} */
    @Override
    public void render(String path, Request request) throws IOException {
        if (path == null) {
            throw new CannotRenderException("Cannot dispatch a null path");
        }
        DispatchRequest dispatchRequest = getDispatchRequest(request);
        if (dispatchRequest == null) {
            throw new CannotRenderException("Cannot dispatch outside of a web environment");
        }

        dispatchRequest.dispatch(path);
    }

    /** {@inheritDoc} */
    public boolean isRenderable(String path, Request request) {
        return path != null && getDispatchRequest(request) != null && path.startsWith("/");
    }

    private DispatchRequest getDispatchRequest(Request request) {
        Request result = request;
        while (!(result instanceof DispatchRequest) && result instanceof RequestWrapper) {
            result = ((RequestWrapper) result).getWrappedRequest();
        }
        if (!(result instanceof DispatchRequest)) {
            result = null;
        }
        return (DispatchRequest) result;
    }
}
