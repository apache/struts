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
import org.w3c.dom.Text;

/**
 * ProxyTextNodeAdapter is a pass-through adapter for objects which already
 * implement the Text interface.  All methods are proxied to the underlying
 * Node except getParent(), getNextSibling() and getPreviousSibling(), which
 * are implemented by the abstract adapter node to work with the parent adapter.
 */
public class ProxyTextNodeAdapter extends ProxyNodeAdapter implements Text {

    public ProxyTextNodeAdapter(AdapterFactory factory, AdapterNode parent, Text value) {
        super(factory, parent, value);
    }

    // convenience
    Text text() {
        return (Text) getPropertyValue();
    }

    public String toString() {
        return "ProxyTextNode for: " + text();
    }

    public Text splitText(int offset) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public int getLength() {
        return text().getLength();
    }

    public void deleteData(int offset, int count) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public String getData() throws DOMException {
        return text().getData();
    }

    public String substringData(int offset, int count) throws DOMException {
        return text().substringData(offset, count);
    }

    public void replaceData(int offset, int count, String arg) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public void insertData(int offset, String arg) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public void appendData(String arg) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public void setData(String data) throws DOMException {
        throw new UnsupportedOperationException();
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
}

