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
 * An encapsulation of the Tiles framework.  This interface is
 * used to expose tiles features to frameworks which leverage
 * it as a plugin.  It can alternately be used by web applications
 * which would like a programmatic interface.
 *
 * @since 2.0
 */
public interface TilesContainer {

    /**
     * Retrieve the container's context.
     *
     * @return current application context
     */
    ApplicationContext getApplicationContext();

    /**
     * Retrieve the attribute context of the current request.
     * @param request The request.
     * @return map of the attributes in the current attribute context.
     */
    AttributeContext getAttributeContext(Request request);

    /**
     * Starts a new context, where attribute values are stored independently of others.<br>
     * When the use of the contexts is finished, call{@link TilesContainer#endContext(Request)}
     *
     * @param request The request.
     * @return The newly created context.
     */
    AttributeContext startContext(Request request);

    /**
     * Ends a context, where attribute values are stored independently of others.<br>
     * It must be called after a {@link TilesContainer#startContext(Request)} call.
     *
     * @param request The request.
     */
    void endContext(Request request);

    /**
     * Renders the current context, as it is.
     * @param request The request.
     *
     * @since 2.1.0
     */
    void renderContext(Request request);

    /**
     * Executes a preparer.
     *
     * @param preparer The name of the preparer to execute.
     * @param request The request.
     */
    void prepare(String preparer, Request request);

    /**
     * Render the given tiles request.
     *
     * @param definition the current definition.
     * @param request The request.
     */
    void render(String definition, Request request);

    /**
     * Renders the specified definition.
     * @param definition The definition to render.
     * @param request The request context.
     */
    void render(Definition definition, Request request);

    /**
     * Render the given Attribute.
     *
     * @param attribute The attribute to render.
     * @param request The request.
     * @throws IOException If something goes wrong during writing to the output.
     * @since 2.1.2
     */
    void render(Attribute attribute, Request request)
        throws IOException;

    /**
     * Evaluates the given attribute.
     *
     * @param attribute The attribute to evaluate.
     * @param request The request.
     * @return The evaluated object.
     * @since 2.1.0
     */
    Object evaluate(Attribute attribute, Request request);

    /**
     * Returns a definition specifying its name.
     *
     * @param definitionName The name of the definition to find.
     * @param request The request context.
     * @return The definition, if found.
     */
    Definition getDefinition(String definitionName,
                             Request request);

    /**
     * Determine whether the definition exists.
     *
     * @param definition the name of the definition.
     * @param request The request.
     * @return true if the definition is found.
     */
    boolean isValidDefinition(String definition, Request request);
}
