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
package org.apache.struts2.quickstart;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;

/**
 * To start a Jetty server used by the QuickStart application.
 */
public class JettyServer {
    /**
     * The system property name used to specify a directory of webapps.
     */
    public static final String WEBAPPS_DIR_PROPERTY = "webapps.dir";

    public static void startServer(int port, String context, List pathPriority, Map paths, String resolver) throws Exception {
        try {
            Server server = new Server();
            SocketListener socketListener = new SocketListener();
            socketListener.setPort(port);
            server.addListener(socketListener);

            WebApplicationContext ctx;
            if (resolver == null) {
                ctx = new MultiWebApplicationContext(pathPriority, paths);
            } else {
                ctx = new MultiWebApplicationContext(pathPriority, paths, resolver);
            }
            ctx.setClassLoader(Thread.currentThread().getContextClassLoader());
            ctx.setContextPath(context);
            server.addContext(null, ctx);

            // Add in extra webapps dir (see WW-1387)
            String webappsDir = System.getProperty(WEBAPPS_DIR_PROPERTY);
            if (webappsDir != null && new File(webappsDir).exists()) {
                server.addWebApplications(webappsDir);
            }

            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
