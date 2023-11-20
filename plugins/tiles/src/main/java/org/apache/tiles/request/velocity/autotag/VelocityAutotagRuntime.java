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
package org.apache.tiles.request.velocity.autotag;

import java.io.Writer;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.tiles.autotag.core.runtime.AutotagRuntime;
import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.servlet.ServletUtil;
import org.apache.tiles.request.velocity.VelocityRequest;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.ASTBlock;
import org.apache.velocity.runtime.parser.node.ASTMap;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.tools.view.ViewContext;

/**
 * A Runtime for implementing Velocity Directives.
 */
public class VelocityAutotagRuntime extends Directive implements AutotagRuntime<Request> {
    private InternalContextAdapter context;
    private Writer writer;
    private Node node;
    private Map<String, Object> params;

    @Override
    public Request createRequest() {
        ViewContext viewContext = (ViewContext) context.getInternalUserContext();
        HttpServletRequest request = viewContext.getRequest();
        HttpServletResponse response = viewContext.getResponse();
        ServletContext servletContext = viewContext.getServletContext();
        return VelocityRequest.createVelocityRequest(ServletUtil.getApplicationContext(servletContext), request,
                response, context, writer);
    }

    /** {@inheritDoc} */
    @Override
    public ModelBody createModelBody() {
        ASTBlock block = (ASTBlock) node.jjtGetChild(1);
        return new VelocityModelBody(context, block, writer);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String name, Class<T> type, T defaultValue) {
        if (params == null) {
            ASTMap astMap = (ASTMap) node.jjtGetChild(0);
            params = (Map<String, Object>) astMap.value(context);
        }
        T result = (T) params.get(name);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public int getType() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) {
        this.context = context;
        this.writer = writer;
        this.node = node;
        return false;
    }

}
