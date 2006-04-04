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
package org.apache.struts.action2.sitegraph.model;

import java.io.IOException;
import java.util.*;

/**
 * User: plightbo
 * Date: Jun 26, 2005
 * Time: 4:58:30 PM
 */
public class Graph extends SubGraph {
    private Set links;
    public static Map nodeMap = new LinkedHashMap();

    public Graph() {
        super("");
        this.links = new TreeSet();
    }

    public void addLink(Link link) {
        links.add(link);
    }

    public void render(IndentWriter writer) throws IOException {
        // write out the header
        writer.write("digraph mygraph {", true);
        writer.write("fontsize=10;");
        writer.write("fontname=helvetica;");
        writer.write("node [fontsize=10, fontname=helvetica, style=filled, shape=rectangle]");
        writer.write("edge [fontsize=10, fontname=helvetica]");

        // render all the subgraphs
        for (Iterator iterator = subGraphs.iterator(); iterator.hasNext();) {
            SubGraph subGraph = (SubGraph) iterator.next();
            subGraph.render(new IndentWriter(writer));
        }

        // render all the nodes
        for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
            SiteGraphNode siteGraphNode = (SiteGraphNode) iterator.next();
            siteGraphNode.render(writer);
        }

        // finally, render the links
        for (Iterator iterator = links.iterator(); iterator.hasNext();) {
            Link link = (Link) iterator.next();
            link.render(writer);
        }

        // and now the footer
        writer.write("}", true);
    }

    public SiteGraphNode findNode(String location, SiteGraphNode ref) {
        if (location.startsWith("/")) {
            location = location.substring(1);
        } else {
            // not absolute, so use the reference node
            String prefix = null;
            if (ref.getParent() != null) {
                prefix = ref.getParent().getPrefix();
                location = prefix + "_" + location;
            }
        }

        location = location.replaceAll("[\\.\\/\\-\\$\\{\\}]", "_");

        return (SiteGraphNode) nodeMap.get(location);
    }
}
