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

package org.apache.struts2.views.xslt;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * A NamedNodeMap that wraps the Nodes returned in their proxies.
 *
 * Note: Since maps have no guaranteed order we don't need to worry about identity
 * here as we do with "child" adapters.  In that case we need to preserve identity
 * in order to support finding the next/previous siblings.
 */
public class ProxyNamedNodeMap implements NamedNodeMap {

    private NamedNodeMap nodes;
    private AdapterFactory adapterFactory;
    private AdapterNode parent;

    public ProxyNamedNodeMap(AdapterFactory factory, AdapterNode parent, NamedNodeMap nodes) {
        this.nodes = nodes;
        this.adapterFactory = factory;
        this.parent = parent;
    }

    protected Node wrap(Node node) {
        return adapterFactory.proxyNode(parent, node);
    }

    public int getLength() {
        return nodes.getLength();
    }

    public Node item(int index) {
        return wrap(nodes.item(index));
    }

    public Node getNamedItem(String name) {
        return wrap(nodes.getNamedItem(name));
    }

    public Node removeNamedItem(String name) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public Node setNamedItem(Node arg) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public Node setNamedItemNS(Node arg) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public Node getNamedItemNS(String namespaceURI, String localName) {
        return wrap(nodes.getNamedItemNS(namespaceURI, localName));
    }

    public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }
}
