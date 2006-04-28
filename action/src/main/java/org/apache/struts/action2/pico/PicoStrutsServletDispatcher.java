/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.apache.struts.action2.pico;

import org.apache.struts.action2.dispatcher.ServletDispatcher;
import com.opensymphony.xwork.ActionProxyFactory;
import org.nanocontainer.nanowar.ServletRequestContainerLauncher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Extension to the standard Struts ServletDispatcher that instantiates 
 * a new container in the request scope for each request and disposes of it 
 * correctly at the end of the request.
 * <p/>
 * To use, replace the Struts ServletDispatcher in web.xml with this.
 *
 * @deprecated Use {@link PicoFilterDispatcher}
 */
public class PicoStrutsServletDispatcher extends ServletDispatcher {

    public PicoStrutsServletDispatcher() {
        super();
        ActionProxyFactory.setFactory(new PicoActionProxyFactory());
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ServletRequestContainerLauncher containerLauncher = new ServletRequestContainerLauncher(getServletContext(), request);
        try {
            containerLauncher.startContainer();
            // process the servlet using Struts
            super.service(request, response);
        } finally {
            containerLauncher.killContainer();
        }
    }
}
