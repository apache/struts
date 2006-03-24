/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.xslt;

import org.apache.commons.logging.LogFactory;
import org.w3c.dom.*;


/**
 * @author <a href="mailto:meier@meisterbohne.de">Philipp Meier</a>
 * @author Mike Mosiewicz
 * @author Rainer Hermanns
 *         Date: 10.10.2003
 *         Time: 19:46:43
 */
public abstract class DefaultAdapterNode implements Node, AdapterNode {
    private static final NodeList EMPTY_NODELIST = new NodeList() {
        public Node item(int i) {
            return null;
        }

        public int getLength() {
            return 0;
        }
    };

    private AdapterNode parent;
    private DOMAdapter rootAdapter;
    private Object value;
    private String propertyName;

    public DefaultAdapterNode(DOMAdapter rootAdapter, AdapterNode parent, String propertyName, Object value) {
        //assert rootAdapter != null : "rootAdapter == null";
        this.rootAdapter = rootAdapter;

        //assert parent != null : "parent == null";
        this.parent = parent;

        //assert propertyName != null : "propertyName == null";
        this.propertyName = propertyName;
        this.value = value;

        if (LogFactory.getLog(getClass()).isDebugEnabled()) {
            LogFactory.getLog(getClass()).debug("Creating " + this);
        }
    }

    public int getDepth() {
        if (parent == null) return 0;
        return parent.getDepth() + 1;
    }

    public String getPath() {
        StringBuffer buf = new StringBuffer();
        getPath(buf);
        return buf.toString();
    }


    /**
     * @param buf
     */
    public void getPath(StringBuffer buf) {
        if (parent != null)
            ((DefaultAdapterNode) parent).getPath(buf);
        buf.append('/').append(this.propertyName);
    }

    public NamedNodeMap getAttributes() {
        return null;
    }

    public NodeList getChildNodes() {
        return EMPTY_NODELIST;
    }

    public Node getFirstChild() {
        return null;
    }

    public Node getLastChild() {
        return null;
    }

    public String getLocalName() {
        return null;
    }

    public String getNamespaceURI() {
        return null;
    }

    public Node getNextSibling() {
        return (getParentAdapterNode() != null) ? getParentAdapterNode().getNextSibling(this) : null;
    }

    public Node getNextSibling(AdapterNode child) {
        return null;
    }

    public void setNodeValue(String string) throws DOMException {
        operationNotSupported();
    }

    public String getNodeValue() throws DOMException {
        operationNotSupported();

        return null;
    }

    public Document getOwnerDocument() {
        return null;
    }

    public AdapterNode getParentAdapterNode() {
        return parent;
    }

    public Node getParentNode() {
        return parent;
    }

    public void setPrefix(String string) throws DOMException {
        operationNotSupported();
    }

    public String getPrefix() {
        return null;
    }

    public Node getPreviousSibling() {
        return null;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public DOMAdapter getRootAdapter() {
        return rootAdapter;
    }

    public boolean isSupported(String string, String string1) {
        operationNotSupported();

        return false;
    }

    public Object getValue() {
        return value;
    }

    public Node appendChild(Node node) throws DOMException {
        operationNotSupported();

        return null;
    }

    public Node cloneNode(boolean b) {
        operationNotSupported();

        return null;
    }

    public boolean equals(Object other) {
        if (other == this) return true;
        if (other instanceof AdapterNode) {
            AdapterNode otherNode = (AdapterNode) other;
            return
                    getRootAdapter().equals(otherNode.getRootAdapter())
                            & getPropertyName().equals(otherNode.getPropertyName())
                            & ((getValue() != null) ? getValue().equals(otherNode.getValue()) : (otherNode.getValue() == null))
                            & ((getParentAdapterNode() != null) ? getParentAdapterNode().equals(otherNode.getParentAdapterNode()) : (otherNode.getParentAdapterNode() == null));
        } else {
            return false;
        }
    }

    public boolean hasAttributes() {
        return false;
    }

    public boolean hasChildNodes() {
        return false;
    }

    public int hashCode() {
        /*
         *  This code have to deal with possible circular references
         */
        return
                (
                        (
                                getParentAdapterNode() != null &&
                                        getParentAdapterNode().getPropertyName() != null)
                                ?
                                (getParentAdapterNode().getPropertyName().hashCode() * 41)
                                :
                                0
                )
                        + (getPropertyName().hashCode() * 43

                );
    }

    public Node insertBefore(Node node, Node node1) throws DOMException {
        operationNotSupported();

        return null;
    }

    public void normalize() {
        operationNotSupported();
    }

    public Node removeChild(Node node) throws DOMException {
        operationNotSupported();

        return null;
    }

    public Node replaceChild(Node node, Node node1) throws DOMException {
        operationNotSupported();

        return null;
    }

    protected void operationNotSupported() {
        throw new RuntimeException("Operation not supported.");
    }


    public boolean isElementContentWhitespace() {
        return false;
    }

    public String getWholeText() {
        return null;
    }

    public Text replaceWholeText(String content) throws DOMException {
        return null;
    }

    public String getBaseURI() {
        return null;
    }

    public short compareDocumentPosition(Node other) throws DOMException {
        return 0;
    }

    public String getTextContent() throws DOMException {
        return null;
    }

    public void setTextContent(String textContent) throws DOMException {
    }

    public boolean isSameNode(Node other) {
        return false;
    }

    public String lookupPrefix(String namespaceURI) {
        return null;
    }

    public boolean isDefaultNamespace(String namespaceURI) {
        return false;
    }

    public String lookupNamespaceURI(String prefix) {
        return null;
    }

    public boolean isEqualNode(Node arg) {
        return false;
    }

    public Object getFeature(String feature, String version) {
        return null;
    }

    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return null;
    }

    public Object getUserData(String key) {
        return null;
    }

    public String getInputEncoding() {
        return null;
    }

    public String getXmlEncoding() {
        return null;
    }

    public boolean getXmlStandalone() {
        return false;
    }

    public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
    }

    public String getXmlVersion() {
        return null;
    }

    public void setXmlVersion(String xmlVersion) throws DOMException {
    }

    public boolean getStrictErrorChecking() {
        return false;
    }

    public void setStrictErrorChecking(boolean strictErrorChecking) {
    }

    public String getDocumentURI() {
        return null;
    }

    public void setDocumentURI(String documentURI) {
    }

    public Node adoptNode(Node source) throws DOMException {
        return null;
    }

    public DOMConfiguration getDomConfig() {
        return null;
    }

    public void normalizeDocument() {
    }

    public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getClass().getName() + "(" + getPropertyName() + "," + value.toString() + ")";
    }
}
