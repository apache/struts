/*
 *  Copyright (c) 2002-2006 by OpenSymphony
 *  All rights reserved.
 */
package com.opensymphony.webwork.quickstart;

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
