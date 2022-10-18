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

package org.apache.tiles.request.velocity.render;

import java.io.IOException;

import org.apache.tiles.request.Request;
import org.apache.tiles.request.render.CannotRenderException;
import org.apache.tiles.request.render.Renderer;
import org.apache.tiles.request.servlet.ServletRequest;
import org.apache.tiles.request.servlet.ServletUtil;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityView;

/**
 * Attribute renderer for rendering Velocity templates as attributes. It is
 * available only to Servlet-based environment. It uses VelocityView to render
 * the response. To initialize it correctly, call #setParameter(String, String)
 * for all the parameters that you want to set, and then call #commit().
 */
public class VelocityRenderer implements Renderer {

    /**
     * The VelocityView object to use.
     */
    private VelocityView velocityView;

    /**
     * Constructor.
     *
     * @param velocityView The Velocity view manager.
     */
    public VelocityRenderer(VelocityView velocityView) {
        this.velocityView = velocityView;
    }

    @Override
    public void render(String path, Request request) throws IOException {
        if (path == null) {
            throw new CannotRenderException("Cannot dispatch a null path");
        }

        ServletRequest servletRequest = ServletUtil.getServletRequest(request);
        // then get a context
        Context context = velocityView.createContext(servletRequest.getRequest(), servletRequest.getResponse());

        // get the template
        Template template = velocityView.getTemplate((String) path);

        // merge the template and context into the writer
        velocityView.merge(template, context, request.getWriter());
    }

    public boolean isRenderable(String path, Request request) {
        return path != null && path.startsWith("/") && path.endsWith(".vm");
    }
}
