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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * ProxyElementAdapter is a pass-through adapter for objects which already
 * implement the Element interface.  All methods are proxied to the underlying
 * Node except getParent(), getNextSibling() and getPreviousSibling(), which
 * are implemented by the abstract adapter node to work with the parent adapter.
 *
 * Note: this class wants to be (extend) both an AbstractElementAdapter
 * and ProxyElementAdapter, but its proxy-ness is winning right now.
 */
public class ProxyElementAdapter extends ProxyNodeAdapter implements Element {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public ProxyElementAdapter(AdapterFactory factory, AdapterNode parent, Element value) {
        super(factory, parent, value);
    }

    /**
     * Get the proxied Element
     */
    protected Element element() {
        return (Element) getPropertyValue();
    }

    protected List<Node> buildChildAdapters() {
        List<Node> adapters = new ArrayList<Node>();
        NodeList children = node().getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            Node adapter = wrap(child);
            if (adapter != null) {
                log.debug("wrapped child node: " + child.getNodeName());
                adapters.add(adapter);
            }
        }
        return adapters;
    }

    // Proxied Element methods

    public String getTagName() {
        return element().getTagName();
    }

    public boolean hasAttribute(String name) {
        return element().hasAttribute(name);
    }

    public String getAttribute(String name) {
        return element().getAttribute(name);
    }

    public boolean hasAttributeNS(String namespaceURI, String localName) {
        return element().hasAttributeNS(namespaceURI, localName);
    }

    public Attr getAttributeNode(String name) {
        log.debug("wrapping attribute");
        return (Attr) wrap(element().getAttributeNode(name));
    }

    // I'm overriding this just for clarity.  The base impl is correct.
    public NodeList getElementsByTagName(String name) {
        return super.getElementsByTagName(name);
    }

    public String getAttributeNS(String namespaceURI, String localName) {
        return element().getAttributeNS(namespaceURI, localName);
    }

    public Attr getAttributeNodeNS(String namespaceURI, String localName) {
        return (Attr) wrap(element().getAttributeNodeNS(namespaceURI, localName));
    }

    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return super.getElementsByTagNameNS(namespaceURI, localName);
    }

    // Unsupported mutators of Element

    public void removeAttribute(String name) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public void setAttribute(String name, String value) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        throw new UnsupportedOperationException();
    }

    // end proxied Element methods

    // unsupported DOM level 3 methods

    public TypeInfo getSchemaTypeInfo() {
        throw operationNotSupported();
    }

    public void setIdAttribute(String string, boolean b) throws DOMException {
        throw operationNotSupported();
    }

    public void setIdAttributeNS(String string, String string1, boolean b) throws DOMException {
        throw operationNotSupported();
    }

    public void setIdAttributeNode(Attr attr, boolean b) throws DOMException {
        throw operationNotSupported();
    }

    // end DOM level 3 methods

    public String toString() {
        return "ProxyElement for: " + element();
    }
}
