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
package org.apache.tiles.api;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;

import java.io.IOException;

/**
 * Wraps a Tiles container to allow easy decoration.
 */
public class TilesContainerWrapper implements TilesContainer {

    /**
     * The container to wrap.
     */
    protected TilesContainer container;

    /**
     * Constructor.
     *
     * @param container The container to wrap.
     */
    public TilesContainerWrapper(TilesContainer container) {
        this.container = container;
        if (container == null) {
            throw new NullPointerException("The wrapped container must be not null");
        }
    }

    @Override
    public void endContext(Request request) {
        container.endContext(request);
    }

    @Override
    public Object evaluate(Attribute attribute, Request request) {
        return container.evaluate(attribute, request);
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return container.getApplicationContext();
    }

    @Override
    public AttributeContext getAttributeContext(Request request) {
        return container.getAttributeContext(request);
    }

    @Override
    public Definition getDefinition(String definitionName, Request request) {
        return container.getDefinition(definitionName, request);
    }

    @Override
    public boolean isValidDefinition(String definition, Request request) {
        return container.isValidDefinition(definition, request);
    }

    @Override
    public void prepare(String preparer, Request request) {
        container.prepare(preparer, request);
    }

    @Override
    public void render(String definition, Request request) {
        container.render(definition, request);
    }

    @Override
    public void render(Definition definition, Request request) {
        container.render(definition, request);
    }

    @Override
    public void render(Attribute attribute, Request request) throws IOException {
        container.render(attribute, request);
    }

    @Override
    public void renderContext(Request request) {
        container.renderContext(request);
    }

    @Override
    public AttributeContext startContext(Request request) {
        return container.startContext(request);
    }
}
