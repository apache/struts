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

import org.apache.struts2.StrutsException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


/**
 *
 */
public class SimpleTextNode extends AbstractAdapterNode implements Node, Text {

    public SimpleTextNode(AdapterFactory rootAdapterFactory, AdapterNode parent, String propertyName, Object value) {
        setContext(rootAdapterFactory, parent, propertyName, value);
    }

    protected String getStringValue() {
        return getPropertyValue().toString();
    }

    public void setData(String string) throws DOMException {
        throw new StrutsException("Operation not supported");
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
        throw new StrutsException("Operation not supported");
    }

    public void deleteData(int i, int i1) throws DOMException {
        throw new StrutsException("Operation not supported");
    }

    public void insertData(int i, String string) throws DOMException {
        throw new StrutsException("Operation not supported");
    }

    public void replaceData(int i, int i1, String string) throws DOMException {
        throw new StrutsException("Operation not supported");
    }

    public Text splitText(int i) throws DOMException {
        throw new StrutsException("Operation not supported");
    }

    public String substringData(int beginIndex, int endIndex) throws DOMException {
        return getStringValue().substring(beginIndex, endIndex);
    }

    // DOM level 3

    public boolean isElementContentWhitespace() {
        throw operationNotSupported();
    }

    public String getWholeText() {
        throw operationNotSupported();
    }

    public Text replaceWholeText(String string) throws DOMException {
        throw operationNotSupported();
    }
    // end DOM level 3

}
