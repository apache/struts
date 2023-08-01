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
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.velocity.TilesVelocityException;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.Renderable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Renderable that provides a default implementation of Renderable#toString()
 * and allows access to parameters and context objects.
 *
 * @since 2.2.0
 */
public abstract class AbstractDefaultToStringRenderable implements Renderable {

    /**
     * The Velocity context.
     *
     * @since 2.2.0
     */
    protected final Context velocityContext;

    /**
     * The parameters used in the current tool call.
     *
     * @since 2.2.0
     */
    protected final Map<String, Object> params;

    /**
     * The HTTP response.
     *
     * @since 2.2.0
     */
    protected final HttpServletResponse response;

    /**
     * The HTTP request.
     *
     * @since 2.2.0
     */
    protected final HttpServletRequest request;

    /**
     * The logging object.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Constructor.
     *
     * @param velocityContext The Velocity context.
     * @param params          The parameters used in the current tool call.
     * @param response        The HTTP response.
     * @param request         The HTTP request.
     * @since 2.2.0
     */
    public AbstractDefaultToStringRenderable(Context velocityContext, Map<String, Object> params,
            HttpServletResponse response, HttpServletRequest request) {
        this.velocityContext = velocityContext;
        this.params = params;
        this.response = response;
        this.request = request;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        try {
            render(null, writer);
        } catch (MethodInvocationException e) {
            throw new TilesVelocityException("Cannot invoke method when rendering", e);
        } catch (ParseErrorException e) {
            throw new TilesVelocityException("Cannot parse when rendering", e);
        } catch (ResourceNotFoundException e) {
            throw new TilesVelocityException("Cannot find resource when rendering", e);
        } catch (IOException e) {
            throw new TilesVelocityException("I/O exception when rendering", e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                log.error("Error when closing a StringWriter, the impossible happened!", e);
            }
        }
        return writer.toString();
    }
}
