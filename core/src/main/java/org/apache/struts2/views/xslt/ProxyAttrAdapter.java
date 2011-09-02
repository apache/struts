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

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.TypeInfo;

/**
 * ProxyAttrAdapter is a pass-through adapter for objects which already
 * implement the Attr interface.  All methods are proxied to the underlying
 * Node except node traversal (e.g. getParent()) related methods which
 * are implemented by the abstract adapter node to work with the parent adapter.
 */
public class ProxyAttrAdapter extends ProxyNodeAdapter implements Attr {

    public ProxyAttrAdapter(AdapterFactory factory, AdapterNode parent, Attr value) {
        super(factory, parent, value);
    }

    // convenience
    protected Attr attr() {
        return (Attr) getPropertyValue();
    }

    // Proxied Attr methods

    public String getName() {
        return attr().getName();
    }

    public boolean getSpecified() {
        return attr().getSpecified();
    }

    public String getValue() {
        return attr().getValue();
    }

    public void setValue(String string) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public Element getOwnerElement() {
        return (Element) getParent();
    }

    // DOM level 3

    public TypeInfo getSchemaTypeInfo() {
        throw operationNotSupported();
    }

    public boolean isId() {
        throw operationNotSupported();
    }

    // end DOM level 3

    // End Proxied Attr methods

    public String toString() {
        return "ProxyAttribute for: " + attr();
    }
}

