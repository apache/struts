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
package org.apache.struts2.quickstart;

import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;

import java.util.List;
import java.util.Map;

/**
 * To start a Jetty server used by the QuickStart application.
 */
public class JettyServer {
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

            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
