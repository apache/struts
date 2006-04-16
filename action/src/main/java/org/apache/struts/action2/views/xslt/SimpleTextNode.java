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
package org.apache.struts.action2.views.xslt;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


/**
 * 
 */
public class SimpleTextNode extends DefaultAdapterNode implements Text, AdapterNode {

    public SimpleTextNode(DOMAdapter rootAdapter, AdapterNode parent, String propertyName, Object value) {
        super(rootAdapter, parent, propertyName, value);
    }


    public void setData(String string) throws DOMException {
        throw new RuntimeException("Operation not supported");
    }

    public String getData() throws DOMException {
        return getStringValue();
    }

    public int getLength() {
        return getStringValue().length();
    }

    public String getNodeName() {
        return "#text";
    }

    public short getNodeType() {
        return Node.TEXT_NODE;
    }

    public String getNodeValue() throws DOMException {
        return getStringValue();
    }

    public void appendData(String string) throws DOMException {
        throw new RuntimeException("Operation not supported");
    }

    public void deleteData(int i, int i1) throws DOMException {
        throw new RuntimeException("Operation not supported");
    }

    public void insertData(int i, String string) throws DOMException {
        throw new RuntimeException("Operation not supported");
    }

    public void replaceData(int i, int i1, String string) throws DOMException {
        throw new RuntimeException("Operation not supported");
    }

    public Text splitText(int i) throws DOMException {
        throw new RuntimeException("Operation not supported");
    }

    public String substringData(int beginIndex, int endIndex) throws DOMException {
        return getStringValue().substring(beginIndex, endIndex);
    }

    private String getStringValue() {
        return getValue().toString();
    }
}
