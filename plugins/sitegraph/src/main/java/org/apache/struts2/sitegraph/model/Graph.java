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

package org.apache.struts2.sitegraph.model;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 */
public class Graph extends SubGraph {
    private Set<Link> links;
    public static Map<String,SiteGraphNode> nodeMap = new LinkedHashMap<String,SiteGraphNode>();

    public Graph() {
        super("");
        this.links = new TreeSet<Link>();
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
        for (SubGraph subGraph : subGraphs) {
            subGraph.render(new IndentWriter(writer));
        }

        // render all the nodes
        for (SiteGraphNode siteGraphNode : nodes) {
            siteGraphNode.render(writer);
        }

        // finally, render the links
        for (Link link : links) {
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
            String prefix;
            if (ref.getParent() != null) {
                prefix = ref.getParent().getPrefix();
                location = prefix + "_" + location;
            }
        }

        location = location.replaceAll("[\\./\\-\\$\\{\\}]", "_");

        return nodeMap.get(location);
    }
}
