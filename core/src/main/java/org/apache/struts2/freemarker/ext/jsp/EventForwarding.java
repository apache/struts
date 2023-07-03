/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.freemarker.ext.jsp;

import freemarker.log.Logger;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextAttributeEvent;
import jakarta.servlet.ServletContextAttributeListener;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

/**
 * An instance of this class should be registered as a <tt>&lt;listener&gt;</tt> in
 * the <tt>web.xml</tt> descriptor in order to correctly dispatch events to
 * event listeners that are specified in TLD files.
 */
public class EventForwarding
    implements
        ServletContextAttributeListener,
        ServletContextListener,
        HttpSessionListener,
        HttpSessionAttributeListener {
    private static final Logger LOG = Logger.getLogger("freemarker.jsp");
    
    private static final String ATTR_NAME = EventForwarding.class.getName();
    
    private final List servletContextAttributeListeners = new ArrayList();
    private final List servletContextListeners = new ArrayList();
    private final List httpSessionAttributeListeners = new ArrayList();
    private final List httpSessionListeners = new ArrayList();

    void addListeners(List listeners) {
        for (Iterator iter = listeners.iterator(); iter.hasNext(); ) {
            addListener((EventListener) iter.next());
        }
    }
    
    private void addListener(EventListener listener) {
        boolean added = false;
        if (listener instanceof ServletContextAttributeListener) {
            addListener(servletContextAttributeListeners, listener);
            added = true;
        }
        if (listener instanceof ServletContextListener) {
            addListener(servletContextListeners, listener);
            added = true;
        }
        if (listener instanceof HttpSessionAttributeListener) {
            addListener(httpSessionAttributeListeners, listener);
            added = true;
        }
        if (listener instanceof HttpSessionListener) {
            addListener(httpSessionListeners, listener);
            added = true;
        }
        if (!added) {
            LOG.warn(
                "Listener of class " + listener.getClass().getName() +
                "wasn't registered as it doesn't implement any of the " +
                "recognized listener interfaces.");
        }
    }

    static EventForwarding getInstance(ServletContext context) {
        return (EventForwarding) context.getAttribute(ATTR_NAME);
    }
    private void addListener(List listeners, EventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }
    
    @Override
    public void attributeAdded(ServletContextAttributeEvent arg0) {
        synchronized (servletContextAttributeListeners) {
            int s = servletContextAttributeListeners.size();
            for (int i = 0; i < s; ++i) {
                ((ServletContextAttributeListener) servletContextAttributeListeners.get(i)).attributeAdded(arg0);
            }
        }
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent arg0) {
        synchronized (servletContextAttributeListeners) {
            int s = servletContextAttributeListeners.size();
            for (int i = 0; i < s; ++i) {
                ((ServletContextAttributeListener) servletContextAttributeListeners.get(i)).attributeRemoved(arg0);
            }
        }
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent arg0) {
        synchronized (servletContextAttributeListeners) {
            int s = servletContextAttributeListeners.size();
            for (int i = 0; i < s; ++i) {
                ((ServletContextAttributeListener) servletContextAttributeListeners.get(i)).attributeReplaced(arg0);
            }
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        arg0.getServletContext().setAttribute(ATTR_NAME, this);
        
        synchronized (servletContextListeners) {
            int s = servletContextListeners.size();
            for (int i = 0; i < s; ++i) {
                ((ServletContextListener) servletContextListeners.get(i)).contextInitialized(arg0);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        synchronized (servletContextListeners) {
            int s = servletContextListeners.size();
            for (int i = s - 1; i >= 0; --i) {
                ((ServletContextListener) servletContextListeners.get(i)).contextDestroyed(arg0);
            }
        }
    }

    @Override
    public void sessionCreated(HttpSessionEvent arg0) {
        synchronized (httpSessionListeners) {
            int s = httpSessionListeners.size();
            for (int i = 0; i < s; ++i) {
                ((HttpSessionListener) httpSessionListeners.get(i)).sessionCreated(arg0);
            }
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent arg0) {
        synchronized (httpSessionListeners) {
            int s = httpSessionListeners.size();
            for (int i = s - 1; i >= 0; --i) {
                ((HttpSessionListener) httpSessionListeners.get(i)).sessionDestroyed(arg0);
            }
        }
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent arg0) {
        synchronized (httpSessionAttributeListeners) {
            int s = httpSessionAttributeListeners.size();
            for (int i = 0; i < s; ++i) {
                ((HttpSessionAttributeListener) httpSessionAttributeListeners.get(i)).attributeAdded(arg0);
            }
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent arg0) {
        synchronized (httpSessionAttributeListeners) {
            int s = httpSessionAttributeListeners.size();
            for (int i = 0; i < s; ++i) {
                ((HttpSessionAttributeListener) httpSessionAttributeListeners.get(i)).attributeRemoved(arg0);
            }
        }
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent arg0) {
        synchronized (httpSessionAttributeListeners) {
            int s = httpSessionAttributeListeners.size();
            for (int i = 0; i < s; ++i) {
                ((HttpSessionAttributeListener) httpSessionAttributeListeners.get(i)).attributeReplaced(arg0);
            }
        }
    }
}
