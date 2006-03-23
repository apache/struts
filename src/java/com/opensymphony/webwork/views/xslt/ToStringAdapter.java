/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.xslt;

import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="mailto:meier@meisterbohne.de">Philipp Meier</a>
 *         Date: 10.10.2003
 *         Time: 19:45:12
 */
public class ToStringAdapter extends DefaultElementAdapter {
    public ToStringAdapter(DOMAdapter rootAdapter, AdapterNode parent, String propertyName, Object value) {
        super(rootAdapter, parent, propertyName, value);
    }

    protected List buildChildrenAdapters() {
        List children = new ArrayList();
        children.add(new SimpleTextNode(getRootAdapter(), this, "text", getValue()));

        return children;
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

    public TypeInfo getSchemaTypeInfo() {
        return null;
    }

    public void setIdAttribute(String name, boolean isId) throws DOMException {
    }

    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
    }

    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
    }
}
