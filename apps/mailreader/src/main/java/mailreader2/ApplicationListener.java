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

package mailreader2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.struts.apps.mailreader.dao.impl.memory.MemoryUserDatabase;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * <p><code>ServletContextListener</code> that initializes and finalizes the
 * persistent storage of User and Subscription information for the Struts
 * Demonstration Application, using an in-memory database backed by an XML
 * file.</p>
 * <p/>
 * <p><strong>IMPLEMENTATION WARNING</strong> - If this web application is run
 * from a WAR file, or in another environment where reading and writing of the
 * web application resource is impossible, the initial contents will be copied
 * to a file in the web application temporary directory provided by the
 * container.  This is for demonstration purposes only - you should
 * <strong>NOT</strong> assume that files written here will survive a restart
 * of your servlet container.</p>
 * <p/>
 * <p>This class was borrowed from the Shale Mailreader. Changes were:</p>
 * <p/>
 * <ul>
 * <p/>
 * <li>Path to database.xml (under classes here). </li>
 * <p/>
 * <li>Class to store protocol list (an array here). </li>
 * <p/>
 * </ul>
 * <p>
 * DEVELOPMENT NOTE - Another approach would be to instantiate the database via Spring.
 * </p>
 */

public final class ApplicationListener implements ServletContextListener {

    // ------------------------------------------------------ Manifest Constants


    /**
     * <p>Appication scope attribute key under which the in-memory version of
     * our database is stored.</p>
     */
    public static final String DATABASE_KEY = "database";


    /**
     * <p>Application scope attribute key under which the valid selection
     * items for the protocol property is stored.</p>
     */
    public static final String PROTOCOLS_KEY = "protocols";

    // ------------------------------------------------------ Instance Variables


    /**
     * <p>The <code>ServletContext</code> for this web application.</p>
     */
    private ServletContext context = null;


    /**
     * The {@link MemoryUserDatabase} object we construct and make available.
     */
    private MemoryUserDatabase database = null;


    /**
     * <p>Logging output for this plug in instance.</p>
     */
    private Logger log = LoggerFactory.getLogger(this.getClass());

    // ------------------------------------------------------------- Properties


    /**
     * <p>The web application resource path of our persistent database storage
     * file.</p>
     */
    private String pathname = "/WEB-INF/database.xml";

    /**
     * <p>Return the application resource path to the database.</p>
     *
     * @return application resource path path to the database
     */
    public String getPathname() {
        return (this.pathname);
    }

    /**
     * <p>Set the application resource path to the database.</p>
     *
     * @param pathname to the database
     */
    public void setPathname(String pathname) {
        this.pathname = pathname;
    }

    // ------------------------------------------ ServletContextListener Methods


    /**
     * <p>Gracefully shut down this database, releasing any resources that
     * were allocated at initialization.</p>
     *
     * @param event ServletContextEvent to process
     */
    public void contextDestroyed(ServletContextEvent event) {

        log.info("Finalizing memory database plug in");

        if (database != null) {
            try {
                database.close();
            } catch (Exception e) {
                log.error("Closing memory database", e);
            }
        }

        context.removeAttribute(DATABASE_KEY);
        context.removeAttribute(PROTOCOLS_KEY);
        database = null;
        context = null;

    }


    /**
     * <p>Initialize and load our initial database from persistent
     * storage.</p>
     *
     * @param event The context initialization event
     */
    public void contextInitialized(ServletContextEvent event) {

        log.info("Initializing memory database plug in from '" +
                pathname + "'");

        // Remember our associated ServletContext
        this.context = event.getServletContext();

        // Construct a new database and make it available
        database = new MemoryUserDatabase();
        try {
            String path = calculatePath();
            if (log.isDebugEnabled()) {
                log.debug(" Loading database from '" + path + "'");
            }
            database.setPathname(path);
            database.open();
        } catch (Exception e) {
            log.error("Opening memory database", e);
            throw new IllegalStateException("Cannot load database from '" +
                    pathname + "': " + e);
        }
        context.setAttribute(DATABASE_KEY, database);

    }

    // -------------------------------------------------------- Private Methods


    /**
     * <p>Calculate and return an absolute pathname to the XML file to contain
     * our persistent storage information.</p>
     *
     * @throws Exception if an input/output error occurs
     */
    private String calculatePath() throws Exception {

        // Can we access the database via file I/O?
        String path = context.getRealPath(pathname);
        if (path != null) {
            return (path);
        }

        // Does a copy of this file already exist in our temporary directory
        File dir = (File)
                context.getAttribute("javax.servlet.context.tempdir");
        File file = new File(dir, "struts-example-database.xml");
        if (file.exists()) {
            return (file.getAbsolutePath());
        }

        // Copy the static resource to a temporary file and return its path
        InputStream is =
                context.getResourceAsStream(pathname);
        BufferedInputStream bis = new BufferedInputStream(is, 1024);
        FileOutputStream os =
                new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        byte buffer[] = new byte[1024];
        while (true) {
            int n = bis.read(buffer);
            if (n <= 0) {
                break;
            }
            bos.write(buffer, 0, n);
        }
        bos.close();
        bis.close();
        return (file.getAbsolutePath());

    }


}
