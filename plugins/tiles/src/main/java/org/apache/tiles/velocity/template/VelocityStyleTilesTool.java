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

package org.apache.tiles.velocity.template;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletContext;

import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.AttributeContext;
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.api.access.TilesAccess;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.servlet.ServletUtil;
import org.apache.tiles.request.velocity.VelocityRequest;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.Renderable;

/**
 * Tiles Tool to be used "the classic way".
 *
 * @since 2.2.0
 */
public class VelocityStyleTilesTool extends ContextHolder {

    /**
     * Returns an attribute.
     *
     * @param key The name of the attribute to get.
     * @return The Attribute.
     * @since 2.2.0
     */
    public Attribute get(String key) {
        Request velocityRequest = createVelocityRequest(getServletContext(), null);
        TilesContainer container = TilesAccess.getCurrentContainer(velocityRequest);
        AttributeContext attributeContext = container.getAttributeContext(velocityRequest);
        Attribute attribute = attributeContext.getAttribute(key);
        return attribute;
    }

    /**
     * Creates a new empty attribute.
     *
     * @return The created attribute.
     * @since 2.2.0
     */
    public Attribute createAttribute() {
        return new Attribute();
    }

    /**
     * Creates an attribute that is a copy of the one passed as a parameter.
     *
     * @param attribute The attribute to copy.
     * @return The copied attribute.
     * @since 2.2.0
     */
    public Attribute clone(Attribute attribute) {
        return new Attribute(attribute);
    }

    /**
     * Creates an attribute that represents a template.
     *
     * @param template The template.
     * @return The attribute.
     * @since 2.2.0
     */
    public Attribute createTemplateAttribute(String template) {
        return Attribute.createTemplateAttribute(template);
    }

    /**
     * Renders an attribute.
     *
     * @param attribute The attribute to render.
     * @return The renderable object, ready to be rendered.
     * @since 2.2.0
     */
    public Renderable render(final Attribute attribute) {
        return new AbstractDefaultToStringRenderable(getVelocityContext(), null, getResponse(), getRequest()) {

            public boolean render(InternalContextAdapter context, Writer writer) throws IOException {
                Request velocityRequest = createVelocityRequest(getServletContext(), writer);
                TilesContainer container = TilesAccess.getCurrentContainer(velocityRequest);
                container.render(attribute, velocityRequest);
                return true;
            }

        };
    }

    /**
     * Renders a definition. It can be used in conjunction with
     * {@link #startAttributeContext()} and {@link #endAttributeContext()} to
     * customize appearance.
     *
     * @param definitionName The name of the definition to render.
     * @return The renderable that renders the definition.
     * @since 2.2.0
     */
    public Renderable renderDefinition(final String definitionName) {
        return new AbstractDefaultToStringRenderable(getVelocityContext(), null, getResponse(), getRequest()) {

            public boolean render(InternalContextAdapter context, Writer writer) {
                Request velocityRequest = createVelocityRequest(getServletContext(), writer);
                TilesContainer container = TilesAccess.getCurrentContainer(velocityRequest);
                container.render(definitionName, velocityRequest);
                return true;
            }

        };
    }

    /**
     * Renders the current attribute context. It can be used in conjunction with
     * {@link #startAttributeContext()} and {@link #endAttributeContext()} to
     * customize appearance.
     *
     * @return The renderable that renders the current attribute context.
     * @since 2.2.0
     */
    public Renderable renderAttributeContext() {
        return new AbstractDefaultToStringRenderable(getVelocityContext(), null, getResponse(), getRequest()) {

            public boolean render(InternalContextAdapter context, Writer writer) {
                Request velocityRequest = createVelocityRequest(getServletContext(), writer);
                TilesContainer container = TilesAccess.getCurrentContainer(velocityRequest);
                container.renderContext(velocityRequest);
                return true;
            }

        };
    }

    /**
     * Starts the attribute context. Remember to call {@link #endAttributeContext()}
     * when finished!
     *
     * @return The started attribute context, ready to be customized.
     * @since 2.2.0
     */
    public AttributeContext startAttributeContext() {
        Request velocityRequest = createVelocityRequest(getServletContext(), null);
        TilesContainer container = TilesAccess.getCurrentContainer(velocityRequest);
        return container.startContext(velocityRequest);
    }

    /**
     * Ends the current attribute context. To be called after
     * {@link #startAttributeContext()}.
     *
     * @return The tool itself.
     * @since 2.2.0
     */
    public VelocityStyleTilesTool endAttributeContext() {
        Request velocityRequest = createVelocityRequest(getServletContext(), null);
        TilesContainer container = TilesAccess.getCurrentContainer(velocityRequest);
        container.endContext(velocityRequest);
        return this;
    }

    /**
     * Returns the current attribute context.
     *
     * @return The current attribute context.
     * @since 2.2.0
     */
    public AttributeContext getAttributeContext() {
        Request velocityRequest = createVelocityRequest(getServletContext(), null);
        TilesContainer container = TilesAccess.getCurrentContainer(velocityRequest);
        return container.getAttributeContext(velocityRequest);
    }

    /**
     * Sets the current container for the current request.
     *
     * @param containerKey The key of the container to set as "current" for the
     *                     current request.
     * @return The tool itself.
     * @since 2.2.0
     */
    public VelocityStyleTilesTool setCurrentContainer(String containerKey) {
        Request velocityRequest = createVelocityRequest(getServletContext(), null);
        TilesAccess.setCurrentContainer(velocityRequest, containerKey);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "";
    }

    /**
     * Creates a Velocity request.
     *
     * @param servletContext The servlet context.
     * @param writer         The writer.
     * @return The created request.
     */
    protected Request createVelocityRequest(ServletContext servletContext, Writer writer) {
        return VelocityRequest.createVelocityRequest(ServletUtil.getApplicationContext(servletContext), getRequest(),
                getResponse(), getVelocityContext(), writer);
    }
}
