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

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.ServletDispatcherResult;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.access.TilesAccess;

import com.opensymphony.xwork2.ActionInvocation;

/**
 * <!-- START SNIPPET: description -->
 * Renders a view using struts-tiles.
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: webxml -->
 * In your web.xml file, you need to add a servlet entry for TilesServlet to load the tiles
 * definitions into the ServletContext.
 *
 * &lt;servlet&gt;
 *      &lt;servlet-name&gt;tiles&lt;/servlet-name&gt;
 *      &lt;servlet-class&gt;org.apache.tiles.servlets.TilesServlet&lt;/servlet-class&gt;
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;definitions-config&lt;/param-name&gt;
 *          &lt;param-value&gt;/WEB-INF/tiles-config.xml&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *      &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;
 * &lt;/servlet&gt;
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
 */
public class TilesResult extends ServletDispatcherResult {

    private static final long serialVersionUID = -3806939435493086244L;

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
        setLocation(location);

        ServletContext servletContext = ServletActionContext.getServletContext();
        TilesContainer container = TilesAccess.getContainer(servletContext);

        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();

        container.render(location, request, response);
    }
}
