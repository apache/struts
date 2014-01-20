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

package org.apache.struts2.sitegraph;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.StrutsException;
import org.apache.struts2.sitegraph.renderers.DOTRenderer;

import java.io.*;

/**
 * <!-- START SNIPPET: javadocs-intro -->
 * SiteGraph is a tool that renders out GraphViz-generated images depicting your
 * Struts-powered web application's flow. SiteGraph requires GraphViz be installed
 * and that the "dot" executable be in your command path. You can find GraphViz
 * at http://www.graphviz.org.
 * <!-- END SNIPPET: javadocs-intro -->
 * <p/>
 * <!-- START SNIPPET: javadocs-api -->
 * If you wish to use SiteGraph through its API rather than through the command line,
 * you can do that as well. All you need to do is create a new SiteGraph instance,
 * optionally specify a {@link Writer} to output the dot content to, and then call
 * {@link #prepare()}.
 * <!-- END SNIPPET: javadocs-api -->
 */
public class SiteGraph {

    private static final Logger LOG = LoggerFactory.getLogger(SiteGraph.class);

    private String configDir;
    private String views;
    private String output;
    private String namespace;
    private Writer writer;

    public SiteGraph(String configDir, String views, String output, String namespace) {
        this.configDir = configDir;
        this.views = views;
        this.output = output;
        this.namespace = namespace;
    }

    public static void main(String[] args) throws IOException {
        if (LOG.isInfoEnabled()) {
            LOG.info("SiteGraph starting...");
        }

        if (args.length != 8 && args.length != 6) {
            InputStream is = SiteGraph.class.getResourceAsStream("sitegraph-usage.txt");
            byte[] buffer = new byte[2048];
            int length;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            is.close();
            baos.close();

            String usage = baos.toString();
            System.out.println(usage.replaceAll("//.*", ""));
            return;
        }

        String configDir = getArg(args, "config");
        String views = getArg(args, "views");
        String output = getArg(args, "output");
        String namespace = getArg(args, "ns");

        /** <!-- START SNIPPET: example-api --> */
        SiteGraph siteGraph = new SiteGraph(configDir, views, output, namespace);
        siteGraph.prepare();
        siteGraph.render();
        /** <!-- END SNIPPET: example-api --> */
    }

    private static String getArg(String[] args, String arg) {
        for (int i = 0; i < args.length; i++) {
            if (("-" + arg).equals(args[i]) && ((i + 1) < args.length)) {
                return args[i + 1];
            }
        }

        return "";
    }

    /**
     * Prepares the dot generated content and writes out to the provided writer
     * object. If no writer has been given, that a {@link FileWriter} pointing to "out.dot"
     * in the specified output directly shall be used.
     */
    public void prepare() {
        if (writer == null) {
            try {
                writer = new FileWriter(output + "/out.dot");
            } catch (IOException e) {
                throw new StrutsException(e);
            }
        }

        StrutsConfigRetriever.setConfiguration(configDir, views.split("[,]+"));
        DOTRenderer renderer = new DOTRenderer(writer);
        renderer.render(namespace);
    }

    /**
     * Invokes the dot command, cause GraphViz to render out.dot in the form of out.gif,
     * located in the specified output directory. If an error occurs during this process,
     * the error is logged and the method completes without throwing an exception.
     */
    public void render() {
        try {
            Runtime.getRuntime().exec("dot -o" + output + "/out.gif -Tgif " + output + "/out.dot");
        } catch (IOException e) {
            LOG.error("Could not invoke dot", e);
        }
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }
}
