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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.opensymphony.xwork2.util.DomHelper;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * StringAdapter adapts a Java String value to a DOM Element with the specified
 * property name containing the String's text.
 * e.g. a property <pre>String getFoo() { return "My Text!"; }</pre>
 * will appear in the result DOM as:
 * <foo>MyText!</foo>
 *
 * Subclasses may override the getStringValue() method in order to use StringAdapter
 * as a simplified custom XML adapter for Java types.  A subclass can enable XML
 * parsing of the value string via the setParseStringAsXML() method and then
 * override getStringValue() to return a String containing the custom formatted XML.
 *
 */
public class StringAdapter extends AbstractAdapterElement {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    boolean parseStringAsXML;

    public StringAdapter() {
    }

    public StringAdapter(AdapterFactory adapterFactory, AdapterNode parent, String propertyName, String value) {
        setContext(adapterFactory, parent, propertyName, value);
    }

    /**
     * Get the object to be adapted as a String value.
     * <p/>
     * This method can be overridden by subclasses that wish to use StringAdapter
     * as a simplified customizable XML adapter for Java types. A subclass can
     * enable parsing of the value string as containing XML text via the
     * setParseStringAsXML() method and then override getStringValue() to return a
     * String containing the custom formatted XML.
     */
    protected String getStringValue() {
        return getPropertyValue().toString();
    }

    protected List<Node> buildChildAdapters() {
        Node node;
        if (getParseStringAsXML()) {
            log.debug("parsing string as xml: " + getStringValue());
            // Parse the String to a DOM, then proxy that as our child
            node = DomHelper.parse(new InputSource(new StringReader(getStringValue())));
            node = getAdapterFactory().proxyNode(this, node);
        } else {
            log.debug("using string as is: " + getStringValue());
            // Create a Text node as our child
            node = new SimpleTextNode(getAdapterFactory(), this, "text", getStringValue());
        }

        List<Node> children = new ArrayList<Node>();
        children.add(node);
        return children;
    }

    /**
     * Is this StringAdapter to interpret its string values as containing
     * XML Text?
     *
     * @see #setParseStringAsXML(boolean)
     */
    public boolean getParseStringAsXML() {
        return parseStringAsXML;
    }

    /**
     * When set to true the StringAdapter will interpret its String value
     * as containing XML text and parse it to a DOM Element.  The new DOM
     * Element will be a child of this String element. (i.e. wrapped in an
     * element of the property name specified for this StringAdapter).
     *
     * @param parseStringAsXML
     * @see #getParseStringAsXML()
     */
    public void setParseStringAsXML(boolean parseStringAsXML) {
        this.parseStringAsXML = parseStringAsXML;
    }

}
