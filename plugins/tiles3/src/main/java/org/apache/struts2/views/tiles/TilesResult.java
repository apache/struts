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

import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.ServletDispatcherResult;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.access.TilesAccess;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.servlet.ServletRequest;
import org.apache.tiles.request.servlet.ServletUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * First implementation of Tiles 3 support
 *
 * Please follow the link to read more how to configure the result
 * http://stackoverflow.com/questions/13337938/how-to-integrate-struts-2-with-tiles-3
 *
 * or check the docs
 *
 * https://cwiki.apache.org/confluence/display/WW/Tiles+3+Plugin
 *
 *
 * @author Ken McWilliams
 */
public class TilesResult extends ServletDispatcherResult {

    public TilesResult() {
        super();
    }

    public TilesResult(String location) {
        super(location);
    }

    @Override
    public void doExecute(String location, ActionInvocation invocation) throws Exception {
        ServletContext context = ServletActionContext.getServletContext();
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();

        ApplicationContext applicationContext = ServletUtil.getApplicationContext(context);
        ServletRequest servletRequest = new ServletRequest(applicationContext, request, response);

        TilesContainer container = initTilesContainer(applicationContext, servletRequest);

        container.startContext(servletRequest);
        container.render(location, servletRequest);
    }

    protected TilesContainer initTilesContainer(ApplicationContext applicationContext, ServletRequest servletRequest) {
        TilesContainer container = TilesAccess.getContainer(applicationContext);
        TilesAccess.setCurrentContainer(servletRequest, container);
        return container;
    }

}
