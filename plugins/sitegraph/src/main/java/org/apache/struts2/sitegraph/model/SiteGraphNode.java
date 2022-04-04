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

/**
 */
public abstract class SiteGraphNode implements Render, Comparable {
    private String name;
    private SubGraph parent;

    public SiteGraphNode(String name) {
        this.name = name;
    }

    public SubGraph getParent() {
        return parent;
    }

    public void setParent(SubGraph parent) {
        this.parent = parent;
    }

    public void render(IndentWriter writer) throws IOException {
        writer.write(getFullName() + " [label=\"" + name + "\",color=\"" + getColor() + "\"];");
    }

    public String getFullName() {
        String prefix = "";
        if (parent != null) {
            String parentPrefix = parent.getPrefix();
            if (!parentPrefix.equals("")) {
                prefix = parentPrefix + "_";
            }
        }
        return prefix + cleanName();
    }

    private String cleanName() {
        return name.replaceAll("[\\./\\-\\$\\{\\}]", "_");
    }

    public abstract String getColor();

    public int compareTo(Object o) {
        return name.compareTo(((SiteGraphNode) o).name);
    }
}
