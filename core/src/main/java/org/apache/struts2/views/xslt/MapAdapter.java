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
package org.apache.struts2.views.xslt;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MapAdapter adapters a java.util.Map type to an XML DOM with the following
 * structure:
 * <pre>
 *  &lt;myMap&gt;
 *      &lt;entry&gt;
 *          &lt;key&gt;...&lt;/key&gt;
 *          &lt;value&gt;...&lt;/value&gt;
 *      &lt;/entry&gt;
 *      ...
 *  &lt;/myMap&gt;
 * </pre>
 */
public class MapAdapter extends AbstractAdapterElement {

    public MapAdapter() { }

    public MapAdapter(AdapterFactory adapterFactory, AdapterNode parent, String propertyName, Map value) {
        setContext( adapterFactory, parent, propertyName, value );
    }

    public Map map() {
        return (Map)getPropertyValue();
    }

    protected List<Node> buildChildAdapters() {
        List<Node> children = new ArrayList<>(map().entrySet().size());

        for (Object o : map().entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            Object key = entry.getKey();
            Object value = entry.getValue();
            EntryElement child = new EntryElement(getAdapterFactory(), this, "entry", key, value);
            children.add(child);
        }

        return children;
    }

    static class EntryElement extends AbstractAdapterElement {
        Object key, value;

        public EntryElement(  AdapterFactory adapterFactory,
                              AdapterNode parent, String propertyName, Object key, Object value ) {
            setContext( adapterFactory, parent, propertyName, null/*we have two values*/ );
            this.key = key;
            this.value = value;
        }

        protected List<Node> buildChildAdapters() {
            List<Node> children = new ArrayList<>();
            children.add( getAdapterFactory().adaptNode( this, "key", key ) );
            children.add( getAdapterFactory().adaptNode( this, "value", value ) );
            return children;
        }
    }
}
