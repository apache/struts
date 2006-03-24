/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.xslt;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;


/**
 * @author <a href="mailto:meier@meisterbohne.de">Philipp Meier</a>
 *         Date: 10.10.2003
 *         Time: 20:40:44
 */
public class CollectionNodeList implements NodeList {

    private List nodes;


    public CollectionNodeList(List nodes) {
        this.nodes = nodes;
    }


    public int getLength() {
        return nodes.size();
    }

    public Node item(int i) {
        return (Node) nodes.get(i);
    }
}
