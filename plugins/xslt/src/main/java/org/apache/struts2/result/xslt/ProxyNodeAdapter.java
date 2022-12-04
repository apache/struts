/*
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
package org.apache.struts2.result.xslt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * ProxyNodeAdapter is a read-only delegating adapter for objects which already
 * implement the Node interface.  All methods are proxied to the underlying
 * Node except getParent(), getNextSibling() and getPreviousSibling(), which
 * are implemented by the abstract adapter node to work with the parent adapter.
 */
public abstract class ProxyNodeAdapter extends AbstractAdapterNode {

    private static final Logger LOG = LogManager.getLogger(ProxyNodeAdapter.class);

    protected ProxyNodeAdapter(AdapterFactory factory, AdapterNode parent, Node value) {
        setContext(factory, parent, "document", value);
        LOG.debug("Proxied node is: {}", value);
        LOG.debug("Node class is: {}", value.getClass());
        LOG.debug("Node type is: {}", value.getNodeType());
        LOG.debug("Node name is: {}", value.getNodeName());
    }

    /**
     * @return the proxied Node value
     */
    protected Node node() {
        return (Node) getPropertyValue();
    }

    /**
     * @param node the node
     * @return adapter to wrap the proxied node.
     */
    protected Node wrap(Node node) {
        return getAdapterFactory().proxyNode(this, node);
    }

    protected NamedNodeMap wrap(NamedNodeMap nnm) {
        return getAdapterFactory().proxyNamedNodeMap(this, nnm);
    }

    @Override
    public String getNodeName() {
        LOG.trace("getNodeName");
        return node().getNodeName();
    }

    @Override
    public String getNodeValue() throws DOMException {
        LOG.trace("getNodeValue");
        return node().getNodeValue();
    }

    @Override
    public short getNodeType() {
        if (LOG.isTraceEnabled()) {
            LOG.trace("getNodeType: {}:{}", getNodeName(), node().getNodeType());
        }
        return node().getNodeType();
    }

    @Override
    public NamedNodeMap getAttributes() {
        NamedNodeMap nnm = wrap(node().getAttributes());
        LOG.trace("getAttributes: {}", nnm);
        return nnm;
    }

    @Override
    public boolean hasChildNodes() {
        LOG.trace("hasChildNodes");
        return node().hasChildNodes();
    }

    @Override
    public boolean isSupported(String s, String s1) {
        LOG.trace("isSupported");
        return node().isSupported(s, s1);
    }

    @Override
    public String getNamespaceURI() {
        LOG.trace("getNamespaceURI");
        return node().getNamespaceURI();
    }

    @Override
    public String getPrefix() {
        LOG.trace("getPrefix");
        return node().getPrefix();
    }

    @Override
    public String getLocalName() {
        LOG.trace("getLocalName");
        return node().getLocalName();
    }

    @Override
    public boolean hasAttributes() {
        LOG.trace("hasAttributes");
        return node().hasAttributes();
    }

    @Override
    public String toString() {
        return "ProxyNode for: " + node();
    }
}

