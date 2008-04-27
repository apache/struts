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

import java.util.Arrays;
import java.util.List;

import org.apache.struts2.StrutsException;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * SimpleAdapterDocument adapted a Java object and presents it as
 * a Document.  This class represents the Document container and uses
 * the AdapterFactory to produce a child adapter for the wrapped object.
 * The adapter produced must be of an Element type or an exception is thrown.
 *
 * Note: in theory we could base this on AbstractAdapterElement and then allow
 * the wrapped object to be a more general Node type.  We would just use
 * ourselves as the root element.  However I don't think this is an issue as
 * people expect Documents to wrap Elements.
 */
public class SimpleAdapterDocument extends AbstractAdapterNode implements Document {

    private Element rootElement;

    public SimpleAdapterDocument(
            AdapterFactory adapterFactory, AdapterNode parent, String propertyName, Object value) {
        setContext(adapterFactory, parent, propertyName, value);

    }

    public void setPropertyValue(Object prop) {
        super.setPropertyValue(prop);
        rootElement = null; // recreate the root element
    }

    /**
     * Lazily construct the root element adapter from the value object.
     */
    private Element getRootElement() {
        if (rootElement != null)
            return rootElement;

        Node node = getAdapterFactory().adaptNode(
                this, getPropertyName(), getPropertyValue());
        if (node instanceof Element)
            rootElement = (Element) node;
        else
            throw new StrutsException(
                    "Document adapter expected to wrap an Element type.  Node is not an element:" + node);

        return rootElement;
    }

    protected List<Node> getChildAdapters() {
        return Arrays.asList(new Node[]{getRootElement()});
    }

    public NodeList getChildNodes() {
        return new NodeList() {
            public Node item(int i) {
                return getRootElement();
            }

            public int getLength() {
                return 1;
            }
        };
    }

    public DocumentType getDoctype() {
        return null;
    }

    public Element getDocumentElement() {
        return getRootElement();
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
        return getRootElement();
    }

    public DOMImplementation getImplementation() {
        return null;
    }

    public Node getLastChild() {
        return getRootElement();
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

    public Node getChildAfter(Node child) {
        return null;
    }

    public Node getChildBefore(Node child) {
        return null;
    }

    // DOM level 3

    public String getInputEncoding() {
        throw operationNotSupported();
    }

    public String getXmlEncoding() {
        throw operationNotSupported();
    }

    public boolean getXmlStandalone() {
        throw operationNotSupported();
    }

    public void setXmlStandalone(boolean b) throws DOMException {
        throw operationNotSupported();
    }

    public String getXmlVersion() {
        throw operationNotSupported();
    }

    public void setXmlVersion(String string) throws DOMException {
        throw operationNotSupported();
    }

    public boolean getStrictErrorChecking() {
        throw operationNotSupported();
    }

    public void setStrictErrorChecking(boolean b) {
        throw operationNotSupported();
    }

    public String getDocumentURI() {
        throw operationNotSupported();
    }

    public void setDocumentURI(String string) {
        throw operationNotSupported();
    }

    public Node adoptNode(Node node) throws DOMException {
        throw operationNotSupported();
    }

    public DOMConfiguration getDomConfig() {
        throw operationNotSupported();
    }

    public void normalizeDocument() {
        throw operationNotSupported();
    }

    public Node renameNode(Node node, String string, String string1) throws DOMException {
        return null;
    }
    // end DOM level 3
}
