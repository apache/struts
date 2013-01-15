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
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * AdapterFactory produces Node adapters for Java object types.
 * Adapter classes are generally instantiated dynamically via a no-args constructor
 * and populated with their context information via the AdapterNode interface.
 *
 * This factory supports proxying of generic DOM Node trees, allowing arbitrary
 * Node types to be mixed together.  You may simply return a Document or Node
 * type as an object property and it will appear as a sub-tree in the XML as
 * you'd expect. See #proxyNode().
 *
 * Customization of the result XML can be accomplished by providing
 * alternate adapters for Java types.  Adapters are associated with Java
 * types through the registerAdapterType() method.
 *
 * For example, since there is no default Date adapter, Date objects will be
 * rendered with the generic Bean introspecting adapter, producing output
 * like:
 * <pre>
     <date>
        <date>19</date>
        <day>1</day>
        <hours>0</hours>
        <minutes>7</minutes>
        <month>8</month>
        <seconds>4</seconds>
        <time>1127106424531</time>
        <timezoneOffset>300</timezoneOffset>
        <year>105</year>
    </date>
 * </pre>
 *
 * By extending the StringAdapter and overriding its normal behavior we can
 * create a custom Date formatter:
 *
 * <pre>
      public static class CustomDateAdapter extends StringAdapter {
        protected String getStringValue() {
            Date date = (Date)getPropertyValue();
            return DateFormat.getTimeInstance( DateFormat.FULL ).format( date );
        }
    }
 * </pre>
 *
 * Producing output like:
 *
<pre>
     <date>12:02:54 AM CDT</date>
 </pre>
 *
 * The StringAdapter (which is normally invoked only to adapt String values)
 * is a useful base for these kinds of customizations and can produce
 * structured XML output as well as plain text by setting its parseStringAsXML()
 * property to true.
 *
 * See provided examples.
 */
public class AdapterFactory {

    private Map<Class, Class> adapterTypes = new HashMap<Class, Class>();

    /**
     * Register an adapter type for a Java class type.
     *
     * @param type        the Java class type which is to be handled by the adapter.
     * @param adapterType The adapter class, which implements AdapterNode.
     */
    public void registerAdapterType(Class type, Class adapterType) {
        adapterTypes.put(type, adapterType);
    }

    /**
     * Create a top level Document adapter for the specified Java object.
     * The document will have a root element with the specified property name
     * and contain the specified Java object content.
     *
     * @param propertyName The name of the root document element
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public Document adaptDocument(String propertyName, Object propertyValue)
            throws IllegalAccessException, InstantiationException {
        //if ( propertyValue instanceof Document )
        //  return (Document)propertyValue;

        return new SimpleAdapterDocument(this, null, propertyName, propertyValue);
    }


    /**
     * Create an Node adapter for a child element.
     * Note that the parent of the created node must be an AdapterNode, however
     * the child node itself may be any type of Node.
     *
     * @see #adaptDocument( String, Object )
     */
    public Node adaptNode(AdapterNode parent, String propertyName, Object value) {
        Class adapterClass = getAdapterForValue(value);
        if (adapterClass != null)
            return constructAdapterInstance(adapterClass, parent, propertyName, value);

        // If the property is a Document, "unwrap" it to the root element
        if (value instanceof Document)
            value = ((Document) value).getDocumentElement();

        // If the property is already a Node, proxy it
        if (value instanceof Node)
            return proxyNode(parent, (Node) value);

        // Check other supported types or default to generic JavaBean introspecting adapter
        Class valueType = value.getClass();

        if (valueType.isArray())
            adapterClass = ArrayAdapter.class;
        else if (value instanceof String || value instanceof Number || value instanceof Boolean || valueType.isPrimitive())
            adapterClass = StringAdapter.class;
        else if (value instanceof Collection)
            adapterClass = CollectionAdapter.class;
        else if (value instanceof Map)
            adapterClass = MapAdapter.class;
        else
            adapterClass = BeanAdapter.class;

        return constructAdapterInstance(adapterClass, parent, propertyName, value);
    }

    /**
     * Construct a proxy adapter for a value that is an existing DOM Node.
     * This allows arbitrary DOM Node trees to be mixed in with our results.
     * The proxied nodes are read-only and currently support only
     * limited types of Nodes including Element, Text, and Attributes.  (Other
     * Node types may be ignored by the proxy and not appear in the result tree).
     * <p/>
     * // TODO:
     * NameSpaces are not yet supported.
     * <p/>
     * This method is primarily for use by the adapter node classes.
     */
    public Node proxyNode(AdapterNode parent, Node node) {
        // If the property is a Document, "unwrap" it to the root element
        if (node instanceof Document)
            node = ((Document) node).getDocumentElement();

        if (node == null)
            return null;
        if (node.getNodeType() == Node.ELEMENT_NODE)
            return new ProxyElementAdapter(this, parent, (Element) node);
        if (node.getNodeType() == Node.TEXT_NODE)
            return new ProxyTextNodeAdapter(this, parent, (Text) node);
        if (node.getNodeType() == Node.ATTRIBUTE_NODE)
            return new ProxyAttrAdapter(this, parent, (Attr) node);

        return null; // Unsupported Node type - ignore for now
    }

    public NamedNodeMap proxyNamedNodeMap(AdapterNode parent, NamedNodeMap nnm) {
        return new ProxyNamedNodeMap(this, parent, nnm);
    }

    /**
     * Create an instance of an adapter dynamically and set its context via
     * the AdapterNode interface.
     */
    private Node constructAdapterInstance(Class adapterClass, AdapterNode parent, String propertyName, Object propertyValue) {
        // Check to see if the class has a no-args constructor
        try {
            adapterClass.getConstructor(new Class []{});
        } catch (NoSuchMethodException e1) {
            throw new StrutsException("Adapter class: " + adapterClass
                    + " does not have a no-args consructor.");
        }

        try {
            AdapterNode adapterNode = (AdapterNode) adapterClass.newInstance();
            adapterNode.setAdapterFactory(this);
            adapterNode.setParent(parent);
            adapterNode.setPropertyName(propertyName);
            adapterNode.setPropertyValue(propertyValue);

            return adapterNode;

        } catch (IllegalAccessException e) {
            throw new StrutsException("Cannot adapt " + propertyValue + " (" + propertyName + ") :" + e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new StrutsException("Cannot adapt " + propertyValue + " (" + propertyName + ") :" + e.getMessage(), e);
        }
    }

    /**
     * Create an appropriate adapter for a null value.
     *
     * @param parent
     * @param propertyName
     */
    public Node adaptNullValue(AdapterNode parent, String propertyName) {
        return new StringAdapter(this, parent, propertyName, "null");
    }

    //TODO: implement Configuration option to provide additional adapter classes
    public Class getAdapterForValue(Object value) {
        return adapterTypes.get(value.getClass());
    }
}
