package com.opensymphony.webwork.sitegraph.model;

import java.io.IOException;

/**
 * User: plightbo
 * Date: Jun 26, 2005
 * Time: 4:49:14 PM
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
        return name.replaceAll("[\\.\\/\\-\\$\\{\\}]", "_");
    }

    public abstract String getColor();

    public int compareTo(Object o) {
        return name.compareTo(((SiteGraphNode) o).name);
    }
}
