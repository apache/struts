/*
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
package org.apache.struts2.result.xslt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * StringAdapter adapts a Java String value to a DOM Element with the specified
 * property name containing the String's text.
 * e.g. a property <code>String getFoo() { return "My Text!"; }</code>
 * will appear in the result DOM as:
 * &lt;foo&gt;MyText!&lt;/foo&gt;
 * </p>
 *
 * <p>
 * Subclasses may override the getStringValue() method in order to use StringAdapter
 * as a simplified custom XML adapter for Java types.
 * </p>
 */
public class StringAdapter extends AbstractAdapterElement {

    private static final Logger LOG = LogManager.getLogger(StringAdapter.class);

    public StringAdapter() {
    }

    public StringAdapter(AdapterFactory adapterFactory, AdapterNode parent, String propertyName, String value) {
        setContext(adapterFactory, parent, propertyName, value);
    }

    /**
     * Get the object to be adapted as a String value.
     *
     * <p>
     * This method can be overridden by subclasses that wish to use StringAdapter
     * as a simplified customizable XML adapter for Java types.
     * </p>
     *
     * @return the string value
     */
    protected String getStringValue() {
        return getPropertyValue().toString();
    }

    @Override
    protected List<Node> buildChildAdapters() {
        LOG.debug("using string as is: {}", getStringValue());
        Node node = new SimpleTextNode(getAdapterFactory(), this, "text", getStringValue());

        List<Node> children = new ArrayList<>();
        children.add(node);
        return children;
    }

    /**
     * @return always returns false
     * @deprecated This feature has been removed for security reasons (potential XML Entity Expansion attacks).
     *             This method now always returns false and will be removed in a future version.
     */
    @Deprecated(forRemoval = true, since = "7.2.0")
    public boolean getParseStringAsXML() {
        return false;
    }

    /**
     * @param parseStringAsXML ignored
     * @deprecated This feature has been removed for security reasons (potential XML Entity Expansion attacks).
     *             This method is now a no-op and will be removed in a future version.
     */
    @Deprecated(forRemoval = true, since = "7.2.0")
    public void setParseStringAsXML(boolean parseStringAsXML) {
        // no-op - feature removed for security reasons
    }

}
