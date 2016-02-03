/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.views.tiles;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.ServletDispatcherResult;
import org.apache.struts2.tiles.StrutsTilesAnnotationProcessor;
import org.apache.struts2.tiles.annotation.TilesDefinition;
import org.apache.tiles.Definition;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.TilesException;
import org.apache.tiles.mgmt.MutableTilesContainer;
import org.apache.tiles.servlet.context.ServletUtil;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


/**
 * <!-- START SNIPPET: description -->
 * Renders a view using struts-tiles.
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: webxml -->
 * In your web.xml file, you need to add a TilesListener.
 *
 * &lt;listener&gt;
 *      &lt;listener-class&gt;org.apache.struts2.tiles.StrutsTilesListener&lt;/listener-class&gt;
 * &lt;/listener&gt;
 * <!-- END SNIPPET: webxml -->
 *
 * <!-- START SNIPPET: strutsxml -->
 * In struts.xml, use type="tiles" on your &lt;result&gt;.
 *
 * &lt;action name="editUser" class="userAction" method="edit"&gt;
 *      &lt;result name="success" type="tiles"&gt;userForm&lt;/result&gt;
 *      &lt;result name="input" type="tiles"&gt;userList&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: strutsxml -->
 *
 *
 * <!-- START SNIPPET: packageconfig -->
 *
 * Making this result type the default for the current package.
 *
 * &lt;result-types&gt;
 *      &lt;result-type name="tiles"
 * class="org.apache.struts2.views.tiles.TilesResult" default="true" /&gt;
 * &lt;/result-types&gt;
 * <!-- END SNIPPET: packageconfig -->
 *
 *
 * <!-- START SNIPPET: tilesconfig -->
 * You have to configure tiles itself. Therefore you can add <code>tiles.xml</code> either 
 * to resources or WEB-INF. You may also use annotations like {@link TilesDefinition}.
 *
 * <!-- END SNIPPET: tilesconfig -->
 *
 */
public class TilesResult extends ServletDispatcherResult {

    private static final long serialVersionUID = -3806939435493086244L;

    private static final Logger LOG = LoggerFactory.getLogger(TilesResult.class);

    public TilesResult() {
        super();
    }

    public TilesResult(String location) {
        super(location);
    }

    /**
     * Dispatches to the given location. Does its forward via a RequestDispatcher. If the
     * dispatch fails a 404 error will be sent back in the http response.
     *
     * @param location the location to dispatch to.
     * @param invocation    the execution state of the action
     * @throws Exception if an error occurs. If the dispatch fails the error will go back via the
     *                   HTTP request.
     */
    public void doExecute(String location, ActionInvocation invocation) throws Exception {
        StrutsTilesAnnotationProcessor annotationProcessor = new StrutsTilesAnnotationProcessor();
        TilesDefinition tilesDefinition = null;
        Object action = invocation.getAction();
        String actionName = invocation.getInvocationContext().getName();

        if (StringUtils.isEmpty(location)) {
            LOG.trace("location not set -> action must have one @TilesDefinition");
            tilesDefinition = annotationProcessor.findAnnotation(action, null);
            String tileName = StringUtils.isNotEmpty(tilesDefinition.name()) ? tilesDefinition.name() : actionName;
            location = tileName;
            LOG.debug("using new location name '{}' and @TilesDefinition '{}'", location, tilesDefinition);
        }
        setLocation(location);

        ServletContext servletContext = ServletActionContext.getServletContext();

        TilesContainer container = ServletUtil.getContainer(servletContext);

        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();

        boolean definitionValid = false;
        try {
            LOG.debug("checking if tiles definition exists '{}'", location);
            definitionValid = container.isValidDefinition(location, request, response);
        } catch (TilesException e) {
            LOG.warn("got TilesException while checking if definiton exists, ignoring it", e);
        }
        if (!definitionValid) {
            if (tilesDefinition == null) {
                LOG.trace("tilesDefinition not found yet, searching in action");
                tilesDefinition = annotationProcessor.findAnnotation(action, location);
            }
            if (tilesDefinition != null) {
                Definition definition = annotationProcessor.buildTilesDefinition(location, tilesDefinition);
                if (container instanceof MutableTilesContainer) {
                    LOG.debug("registering tiles definition with name '{}'", definition.getName());
                    ((MutableTilesContainer)container).register(definition, request, response);
                } else {
                    LOG.error("cannot register tiles definition as tiles container is not mutable!");
                }
            } else {
                LOG.warn("could not find @TilesDefinition for action: {}", actionName);
            }
        }

        container.render(location, request, response);
    }
}
