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
import java.util.LinkedList;
import java.util.List;

import org.apache.struts2.StrutsException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * AbstractAdapterNode is the base for childAdapters that expose a read-only view
 * of a Java object as a DOM Node.  This class implements the core parent-child
 * and sibling node traversal functionality shared by all adapter type nodes
 * and used in proxy node support.
 *
 * @see AbstractAdapterElement
 */
public abstract class AbstractAdapterNode implements AdapterNode {

    private static final NamedNodeMap EMPTY_NAMEDNODEMAP =
            new NamedNodeMap() {
                public int getLength() {
                    return 0;
                }

                public Node item(int index) {
                    return null;
                }

                public Node getNamedItem(String name) {
                    return null;
                }

                public Node removeNamedItem(String name) throws DOMException {
                    return null;
                }

                public Node setNamedItem(Node arg) throws DOMException {
                    return null;
                }

                public Node setNamedItemNS(Node arg) throws DOMException {
                    return null;
                }

                public Node getNamedItemNS(String namespaceURI, String localName) {
                    return null;
                }

                public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
                    return null;
                }
            };

    private List<Node> childAdapters;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    // The domain object that we are adapting
    private Object propertyValue;
    private String propertyName;
    private AdapterNode parent;
    private AdapterFactory adapterFactory;


    public AbstractAdapterNode() {
        if (LoggerFactory.getLogger(getClass()).isDebugEnabled()) {
            LoggerFactory.getLogger(getClass()).debug("Creating " + this);
        }
    }

    /**
     *
     * @param adapterFactory
     * @param parent
     * @param propertyName
     * @param value
     */
    protected void setContext(AdapterFactory adapterFactory, AdapterNode parent, String propertyName, Object value) {
        setAdapterFactory(adapterFactory);
        setParent(parent);
        setPropertyName(propertyName);
        setPropertyValue(value);
    }

    /**
     * subclasses override to produce their children
     *
     * @return List of child adapters.
     */
    protected List<Node> buildChildAdapters() {
        return new ArrayList<Node>();
    }

    /**
     * Lazily initialize child childAdapters
     */
    protected List<Node> getChildAdapters() {
        if (childAdapters == null) {
            childAdapters = buildChildAdapters();
        }
        return childAdapters;
    }

    public Node getChildBeforeOrAfter(Node child, boolean before) {
        log.debug("getChildBeforeOrAfter: ");
        List adapters = getChildAdapters();
        if (log.isDebugEnabled()) {
            log.debug("childAdapters = " + adapters);
            log.debug("child = " + child);
        }
        int index = adapters.indexOf(child);
        if (index < 0)
            throw new StrutsException(child + " is no child of " + this);
        int siblingIndex = before ? index - 1 : index + 1;
        return ((0 < siblingIndex) && (siblingIndex < adapters.size())) ?
                ((Node) adapters.get(siblingIndex)) : null;
    }

    public Node getChildAfter(Node child) {
        log.trace("getChildafter");
        return getChildBeforeOrAfter(child, false/*after*/);
    }

    public Node getChildBefore(Node child) {
        log.trace("getchildbefore");
        return getChildBeforeOrAfter(child, true/*after*/);
    }

    public NodeList getElementsByTagName(String tagName) {
        if (tagName.equals("*")) {
            return getChildNodes();
        } else {
            LinkedList<Node> filteredChildren = new LinkedList<Node>();

            for (Node adapterNode : getChildAdapters()) {
                if (adapterNode.getNodeName().equals(tagName)) {
                    filteredChildren.add(adapterNode);
                }
            }

            return new SimpleNodeList(filteredChildren);
        }
    }

    public NodeList getElementsByTagNameNS(String string, String string1) {
        // TODO:
        return null;
    }

    // Begin Node methods

    public NamedNodeMap getAttributes() {
        return EMPTY_NAMEDNODEMAP;
    }

    public NodeList getChildNodes() {
        NodeList nl = new SimpleNodeList(getChildAdapters());
        if (log.isDebugEnabled())
            log.debug("getChildNodes for tag: "
                    + getNodeName() + " num children: " + nl.getLength());
        return nl;
    }

    public Node getFirstChild() {
        return (getChildNodes().getLength() > 0) ? getChildNodes().item(0) : null;
    }

    public Node getLastChild() {
        return (getChildNodes().getLength() > 0) ? getChildNodes().item(getChildNodes().getLength() - 1) : null;
    }


    public String getLocalName() {
        return null;
    }

    public String getNamespaceURI() {
        return null;
    }

    public void setNodeValue(String string) throws DOMException {
        throw operationNotSupported();
    }

    public String getNodeValue() throws DOMException {
        throw operationNotSupported();
    }

    public Document getOwnerDocument() {
        return null;
    }

    public Node getParentNode() {
        log.trace("getParentNode");
        return getParent();
    }

    public AdapterNode getParent() {
        return parent;
    }

    public void setParent(AdapterNode parent) {
        this.parent = parent;
    }

    public Object getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(Object prop) {
        this.propertyValue = prop;
    }

    public void setPrefix(String string) throws DOMException {
        throw operationNotSupported();
    }

    public String getPrefix() {
        return null;
    }

    public Node getNextSibling() {
        Node next = getParent().getChildAfter(this);
        if (log.isTraceEnabled()) {
            log.trace("getNextSibling on " + getNodeName() + ": "
                    + ((next == null) ? "null" : next.getNodeName()));
        }

        return next;
    }

    public Node getPreviousSibling() {
        return getParent().getChildBefore(this);
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String name) {
        this.propertyName = name;
    }

    public AdapterFactory getAdapterFactory() {
        return adapterFactory;
    }

    public void setAdapterFactory(AdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    public boolean isSupported(String string, String string1) {
        throw operationNotSupported();
    }

    public Node appendChild(Node node) throws DOMException {
        throw operationNotSupported();
    }

    public Node cloneNode(boolean b) {
        log.trace("cloneNode");
        throw operationNotSupported();
    }

    public boolean hasAttributes() {
        return false;
    }

    public boolean hasChildNodes() {
        return false;
    }

    public Node insertBefore(Node node, Node node1) throws DOMException {
        throw operationNotSupported();
    }

    public void normalize() {
        log.trace("normalize");
        throw operationNotSupported();
    }

    public Node removeChild(Node node) throws DOMException {
        throw operationNotSupported();
    }

    public Node replaceChild(Node node, Node node1) throws DOMException {
        throw operationNotSupported();
    }

    // Begin DOM 3 methods

    public boolean isDefaultNamespace(String string) {
        throw operationNotSupported();
    }

    public String lookupNamespaceURI(String string) {
        throw operationNotSupported();
    }

    public String getNodeName() {
        throw operationNotSupported();
    }

    public short getNodeType() {
        throw operationNotSupported();
    }

    public String getBaseURI() {
        throw operationNotSupported();
    }

    public short compareDocumentPosition(Node node) throws DOMException {
        throw operationNotSupported();
    }

    public String getTextContent() throws DOMException {
        throw operationNotSupported();
    }

    public void setTextContent(String string) throws DOMException {
        throw operationNotSupported();

    }

    public boolean isSameNode(Node node) {
        throw operationNotSupported();
    }

    public String lookupPrefix(String string) {
        throw operationNotSupported();
    }

    public boolean isEqualNode(Node node) {
        throw operationNotSupported();
    }

    public Object getFeature(String string, String string1) {
        throw operationNotSupported();
    }

    public Object setUserData(String string, Object object, UserDataHandler userDataHandler) {
        throw operationNotSupported();
    }

    public Object getUserData(String string) {
        throw operationNotSupported();
    }

    // End node methods

    protected StrutsException operationNotSupported() {
        return new StrutsException("Operation not supported.");
    }

    public String toString() {
        return getClass() + ": " + getNodeName() + " parent=" + getParentNode();
    }
}
