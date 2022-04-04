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
import java.util.ArrayList;
import java.util.List;

/**
 */
public class SubGraph implements Render {
    protected String name;
    protected SubGraph parent;
    protected List<SubGraph> subGraphs;
    protected List<SiteGraphNode> nodes;

    public SubGraph(String name) {
        this.name = name;
        this.subGraphs = new ArrayList<SubGraph>();
        this.nodes = new ArrayList<SiteGraphNode>();
    }

    public String getName() {
        return name;
    }

    public void addSubGraph(SubGraph subGraph) {
        subGraph.setParent(this);
        subGraphs.add(subGraph);
    }

    public void setParent(SubGraph parent) {
        this.parent = parent;
    }

    public void addNode(SiteGraphNode node) {
        node.setParent(this);
        Graph.nodeMap.put(node.getFullName(), node);
        nodes.add(node);
    }

    public void render(IndentWriter writer) throws IOException {
        // write the header
        writer.write("subgraph cluster_" + getPrefix() + " {", true);
        writer.write("color=grey;");
        writer.write("fontcolor=grey;");
        writer.write("label=\"" + name + "\";");

        // write out the subgraphs
        for (SubGraph subGraph : subGraphs) {
            subGraph.render(new IndentWriter(writer));
        }

        // write out the actions
        for (SiteGraphNode siteGraphNode : nodes) {
            siteGraphNode.render(writer);
        }

        // .. footer
        writer.write("}", true);
    }

    public String getPrefix() {
        if (parent == null) {
            return name;
        } else {
            String prefix = parent.getPrefix();
            if (prefix.equals("")) {
                return name;
            } else {
                return prefix + "_" + name;
            }
        }
    }

    public SubGraph create(String namespace) {
        if (namespace.equals("")) {
            return this;
        }

        String[] parts = namespace.split("/");
        SubGraph last = this;
        for (String part : parts) {
            if (part.equals("")) {
                continue;
            }

            SubGraph subGraph = findSubGraph(part);
            if (subGraph == null) {
                subGraph = new SubGraph(part);
                last.addSubGraph(subGraph);
            }

            last = subGraph;
        }

        return last;
    }

    private SubGraph findSubGraph(String name) {
        for (SubGraph subGraph : subGraphs) {
            if (subGraph.getName().equals(name)) {
                return subGraph;
            }
        }

        return null;
    }
}
