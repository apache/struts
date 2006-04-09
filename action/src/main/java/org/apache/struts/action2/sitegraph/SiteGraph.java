/*
 * Created on Aug 12, 2004 by mgreer
 */
package org.apache.struts.action2.sitegraph;

import org.apache.struts.action2.sitegraph.renderers.DOTRenderer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

/**
 * // START SNIPPET: javadocs-intro
 * SiteGraph is a tool that renders out GraphViz-generated images depicting your
 * Struts-powered web application's flow. SiteGraph requires GraphViz be installed
 * and that the "dot" executable be in your command path. You can find GraphViz
 * at http://www.graphviz.org.
 * // END SNIPPET: javadocs-intro
 * <p/>
 * // START SNIPPET: javadocs-api
 * If you wish to use SiteGraph through its API rather than through the command line,
 * you can do that as well. All you need to do is create a new SiteGraph instance,
 * optionally specify a {@link Writer} to output the dot content to, and then call
 * {@link #prepare()}.
 * // END SNIPPET: javadocs-api
 */
public class SiteGraph {

    private static final Log LOG = LogFactory.getLog(SiteGraph.class);

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
        LOG.info("SiteGraph starting...");

        if (args.length != 8 && args.length != 6) {
            InputStream is = SiteGraph.class.getResourceAsStream("sitegraph-usage.txt");
            byte[] buffer = new byte[2048];
            int length = -1;
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

        // START SNIPPET: example-api
        SiteGraph siteGraph = new SiteGraph(configDir, views, output, namespace);
        siteGraph.prepare();
        siteGraph.render();
        // END SNIPPET: example-api
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
                throw new RuntimeException(e);
            }
        }

        XWorkConfigRetriever.setConfiguration(configDir, views.split("[, ]+"));
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
