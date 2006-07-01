/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.views.xslt;

import org.w3c.dom.*;


/**
 * 
 */
public class DocumentAdapter extends DefaultAdapterNode implements Document {

    private BeanAdapter rootElement;


    public DocumentAdapter(DOMAdapter rootAdapter, AdapterNode parent, String propertyName, Object value) {
        super(rootAdapter, parent, propertyName, value);
        rootElement = new BeanAdapter(getRootAdapter(), this, getPropertyName(), getValue());
    }


    public NodeList getChildNodes() {
        return new NodeList() {
            public Node item(int i) {
                return rootElement;
            }

            public int getLength() {
                return 1;
            }
        };
    }

    public int getDepth() {
        return 0;
    }

    public void getPath(StringBuffer buf) {
        //nothing - empty subpath
    }
    public DocumentType getDoctype() {
        return null;
    }

    public Element getDocumentElement() {
        return rootElement;
    }

    public Element getElementById(String string) {
        return null;
    }

    public NodeList getElementsByTagName(String string) {
        return null;
    }

    public NodeList getElementsByTagNameNS(String string, String string1) {
        return null;
    }

    public Node getFirstChild() {
        return rootElement;
    }

    public DOMImplementation getImplementation() {
        return null;
    }

    public Node getLastChild() {
        return rootElement;
    }

    public Node getNextSibling(AdapterNode value) {
        return null;
    }

    public String getNodeName() {
        return "#document";
    }

    public short getNodeType() {
        return Node.DOCUMENT_NODE;
    }

    public Attr createAttribute(String string) throws DOMException {
        return null;
    }

    public Attr createAttributeNS(String string, String string1) throws DOMException {
        return null;
    }

    public CDATASection createCDATASection(String string) throws DOMException {
        return null;
    }

    public Comment createComment(String string) {
        return null;
    }

    public DocumentFragment createDocumentFragment() {
        return null;
    }

    public Element createElement(String string) throws DOMException {
        return null;
    }

    public Element createElementNS(String string, String string1) throws DOMException {
        return null;
    }

    public EntityReference createEntityReference(String string) throws DOMException {
        return null;
    }

    public ProcessingInstruction createProcessingInstruction(String string, String string1) throws DOMException {
        return null;
    }

    public Text createTextNode(String string) {
        return null;
    }

    public boolean hasChildNodes() {
        return true;
    }

    public Node importNode(Node node, boolean b) throws DOMException {
        return null;
    }

    public boolean isWhitespaceInElementContent() {
        return true;
    }
}
