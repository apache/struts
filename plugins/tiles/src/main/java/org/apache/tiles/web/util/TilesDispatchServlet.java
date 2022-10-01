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
package org.apache.tiles.web.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tiles.api.AttributeContext;
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.api.access.TilesAccess;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.reflect.ClassUtil;
import org.apache.tiles.request.servlet.ServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Tiles dispatching servlet.  Used to invoke
 * a definition directly.
 */
public class TilesDispatchServlet extends HttpServlet {

    /**
     * Init parameter to define the key of the container to use.
     *
     * @since 2.1.2
     */
    public static final String CONTAINER_KEY_INIT_PARAMETER =
        "org.apache.tiles.web.util.TilesDispatchServlet.CONTAINER_KEY";

    /**
     * The logging object.
     */
    private static final Logger LOG = LogManager.getLogger(TilesDispatchServlet.class);

    /**
     * The key under which the container is stored.
     */
    private String containerKey;

    /**
     * The object that will mutate the attribute context so that it uses
     * different attributes.
     */
    private org.apache.tiles.web.util.AttributeContextMutator mutator;


    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws ServletException {
        super.init();

        containerKey = getServletConfig().getInitParameter(
            CONTAINER_KEY_INIT_PARAMETER);

        String temp = getInitParameter("mutator");
        if (temp != null) {
            try {
                mutator = (org.apache.tiles.web.util.AttributeContextMutator) ClassUtil.instantiate(temp);
            } catch (Exception e) {
                throw new ServletException("Unable to instantiate specified context mutator.", e);
            }
        } else {
            mutator = new DefaultMutator();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {

        ApplicationContext applicationContext = org.apache.tiles.request.servlet.ServletUtil
            .getApplicationContext(getServletContext());
        Request request = new ServletRequest(applicationContext,
            req, res);
        TilesContainer container = TilesAccess.getContainer(applicationContext,
            containerKey);
        mutator.mutate(container.getAttributeContext(request), req);
        String definition = getDefinitionName(req);
        if (LOG.isDebugEnabled()) {
            LOG.info("Dispatching to tile '{}'", definition);
        }
        container.render(definition, request);
    }

    /**
     * Returns the called definition name for the given request.
     *
     * @param request The request to parse.
     * @return The definition name to render.
     */
    protected String getDefinitionName(HttpServletRequest request) {
        String path = (String) request.getAttribute("javax.servlet.include.servlet_path");
        if (path == null) {
            path = request.getServletPath();
        }

        int start = path.startsWith("/") ? 1 : 0;
        int end = path.endsWith(".tiles") ? path.indexOf(".tiles") : path.length();

        return path.substring(start, end);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
        LOG.info("Tiles dispatch request received. Redirecting POST to GET.");
        doGet(req, res);
    }

    /**
     * Default no-op mutator.
     */
    static class DefaultMutator implements AttributeContextMutator {

        /**
         * {@inheritDoc}
         */
        public void mutate(AttributeContext context, javax.servlet.ServletRequest request) {
            // noop;
        }
    }
}
