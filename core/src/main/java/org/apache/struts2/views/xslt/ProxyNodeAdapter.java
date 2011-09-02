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

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * ProxyNodeAdapter is a read-only delegating adapter for objects which already
 * implement the Node interface.  All methods are proxied to the underlying
 * Node except getParent(), getNextSibling() and getPreviousSibling(), which
 * are implemented by the abstract adapter node to work with the parent adapter.
 */
public abstract class ProxyNodeAdapter extends AbstractAdapterNode {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public ProxyNodeAdapter(AdapterFactory factory, AdapterNode parent, Node value) {
        setContext(factory, parent, "document"/*propname unused*/, value);
        log.debug("proxied node is: " + value);
        log.debug("node class is: " + value.getClass());
        log.debug("node type is: " + value.getNodeType());
        log.debug("node name is: " + value.getNodeName());
    }

    /**
     * Get the proxied Node value
     */
    protected Node node() {
        return (Node) getPropertyValue();
    }

    /**
     * Get and adapter to wrap the proxied node.
     *
     * @param node
     */
    protected Node wrap(Node node) {
        return getAdapterFactory().proxyNode(this, node);
    }

    protected NamedNodeMap wrap(NamedNodeMap nnm) {
        return getAdapterFactory().proxyNamedNodeMap(this, nnm);
    }
    //protected NodeList wrap( NodeList nl ) { }

    //protected Node unwrap( Node child ) {
    //  return ((ProxyNodeAdapter)child).node();
    //}

    // Proxied Node methods

    public String getNodeName() {
        log.trace("getNodeName");
        return node().getNodeName();
    }

    public String getNodeValue() throws DOMException {
        log.trace("getNodeValue");
        return node().getNodeValue();
    }

    public short getNodeType() {
        if (log.isTraceEnabled())
            log.trace("getNodeType: " + getNodeName() + ": " + node().getNodeType());
        return node().getNodeType();
    }

    public NamedNodeMap getAttributes() {
        NamedNodeMap nnm = wrap(node().getAttributes());
        if (log.isTraceEnabled())
            log.trace("getAttributes: " + nnm);
        return nnm;
    }

    public boolean hasChildNodes() {
        log.trace("hasChildNodes");
        return node().hasChildNodes();
    }

    public boolean isSupported(String s, String s1) {
        log.trace("isSupported");
        // Is this ok?  What kind of features are they asking about?
        return node().isSupported(s, s1);
    }

    public String getNamespaceURI() {
        log.trace("getNamespaceURI");
        return node().getNamespaceURI();
    }

    public String getPrefix() {
        log.trace("getPrefix");
        return node().getPrefix();
    }

    public String getLocalName() {
        log.trace("getLocalName");
        return node().getLocalName();
    }

    public boolean hasAttributes() {
        log.trace("hasAttributes");
        return node().hasAttributes();
    }

    // End proxied Node methods

    public String toString() {
        return "ProxyNode for: " + node();
    }
}

