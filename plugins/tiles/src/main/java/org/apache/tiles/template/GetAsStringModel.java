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
package org.apache.tiles.template;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.api.access.TilesAccess;
import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.autotag.core.runtime.annotation.Parameter;
import org.apache.tiles.request.Request;

import java.io.IOException;
import java.io.Writer;
import java.util.Deque;

/**
 * <p>
 * <strong> Render the value of the specified template attribute to the current
 * Writer</strong>
 * </p>
 *
 * <p>
 * Retrieve the value of the specified template attribute property, and render
 * it to the current Writer as a String. The usual toString() conversions is
 * applied on found value.
 * </p>
 *
 * @since 2.2.0
 */
public class GetAsStringModel {

    /**
     * The logging object.
     */
    private static final Logger LOG = LogManager.getLogger(GetAsStringModel.class);

    /**
     * The attribute resolver to use.
     */
    private final AttributeResolver attributeResolver;
    
    /**
     * Constructor that uses the defaut attribute resolver.
     *
     * @since 3.0.0
     */
    public GetAsStringModel() {
        this(new DefaultAttributeResolver());
    }

    /**
     * Constructor.
     *
     * @param attributeResolver The attribute resolver to use.
     * @since 2.2.0
     */
    public GetAsStringModel(AttributeResolver attributeResolver) {
        this.attributeResolver = attributeResolver;
    }

    /**
     * Executes the operation.
     *
     * @param ignore           If <code>true</code>, if an exception happens during
     *                         rendering, of if the attribute is null, the problem will be ignored.
     * @param preparer         The preparer to invoke before rendering the attribute.
     * @param role             A comma-separated list of roles. If present, the attribute
     *                         will be rendered only if the current user belongs to one of the roles.
     * @param defaultValue     The default value of the attribute. To use only if
     *                         the attribute was not computed.
     * @param defaultValueRole The default comma-separated list of roles. To use
     *                         only if the attribute was not computed.
     * @param defaultValueType The default type of the attribute. To use only if
     *                         the attribute was not computed.
     * @param name             The name of the attribute.
     * @param value            The attribute to use immediately, if not null.
     * @param request          The request.
     * @param modelBody        The body.
     * @throws IOException If an I/O error happens during rendering.
     * @since 2.2.0
     */
    public void execute(
        boolean ignore,
        String preparer,
        String role,
        Object defaultValue,
        String defaultValueRole,
        String defaultValueType,
        @Parameter(required = true) String name,
        Attribute value,
        Request request,
        ModelBody modelBody
    ) throws IOException {
        TilesContainer container = TilesAccess.getCurrentContainer(request);
        Deque<Object> composeStack = ComposeStackUtil.getComposeStack(request);
        Attribute attribute = resolveAttribute(
            container,
            ignore,
            preparer,
            role,
            defaultValue,
            defaultValueRole,
            defaultValueType,
            name,
            value,
            request
        );
        if (attribute != null) {
            composeStack.push(attribute);
        }
        modelBody.evaluateWithoutWriting();
        container = TilesAccess.getCurrentContainer(request);
        Writer writer = request.getWriter();
        if (attribute != null) {
            attribute = (Attribute) composeStack.pop();
        }
        renderAttribute(attribute, container, writer, ignore, request);
    }

    /**
     * Resolves the attribute. and starts the context.
     *
     * @param container        The Tiles container to use.
     * @param ignore           If <code>true</code>, if an exception happens during
     *                         rendering, of if the attribute is null, the problem will be ignored.
     * @param preparer         The preparer to invoke before rendering the attribute.
     * @param role             A comma-separated list of roles. If present, the attribute
     *                         will be rendered only if the current user belongs to one of the roles.
     * @param defaultValue     The default value of the attribute. To use only if
     *                         the attribute was not computed.
     * @param defaultValueRole The default comma-separated list of roles. To use
     *                         only if the attribute was not computed.
     * @param defaultValueType The default type of the attribute. To use only if
     *                         the attribute was not computed.
     * @param name             The name of the attribute.
     * @param value            The attribute to use immediately, if not null.
     * @param request          The request.
     * @return The resolved attribute.
     */
    private Attribute resolveAttribute(
        TilesContainer container,
        boolean ignore,
        String preparer,
        String role,
        Object defaultValue,
        String defaultValueRole,
        String defaultValueType,
        String name,
        Attribute value,
        Request request
    ) {
        if (preparer != null) {
            container.prepare(preparer, request);
        }
        Attribute attribute = attributeResolver.computeAttribute(
            container,
            value,
            name,
            role,
            ignore,
            defaultValue,
            defaultValueRole,
            defaultValueType,
            request
        );
        container.startContext(request);
        return attribute;
    }

    /**
     * Renders the attribute as a string.
     *
     * @param attribute The attribute to use, previously resolved.
     * @param container The Tiles container to use.
     * @param writer    The writer into which the attribute will be written.
     * @param ignore    If <code>true</code>, if an exception happens during
     *                  rendering, of if the attribute is null, the problem will be ignored.
     * @param request   The request.
     * @throws IOException If an I/O error happens during rendering.
     */
    private void renderAttribute(
        Attribute attribute,
        TilesContainer container,
        Writer writer,
        boolean ignore,
        Request request
    ) throws IOException {
        try {
            if (attribute == null && ignore) {
                return;
            }
            Object value = container.evaluate(attribute, request);
            if (value != null) {
                writer.write(value.toString());
            }
        } catch (IOException | RuntimeException e) {
            if (!ignore) {
                throw e;
            } else if (LOG.isDebugEnabled()) {
                LOG.debug("Ignoring exception", e);
            }
        } finally {
            container.endContext(request);
        }
    }
}
