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

import org.w3c.dom.Node;

/**
 */
public interface AdapterNode extends Node {

    /**
     * The adapter factory that created this node.
     */
    AdapterFactory getAdapterFactory();

    /**
     * The adapter factory that created this node.
     */
    void setAdapterFactory(AdapterFactory factory);

    /**
     * The parent adapter node of this node. Note that our parent must be another adapter node, but our children may be any
     * kind of Node.
     */
    AdapterNode getParent();

    /**
     * The parent adapter node of this node. Note that our parent must be another adapter node, but our children may be any
     * kind of Node.
     */
    void setParent(AdapterNode parent);

    /**
     * The child node before the specified sibling
     */
    Node getChildBefore(Node thisNode);

    /**
     * The child node after the specified sibling
     */
    Node getChildAfter(Node thisNode);

    /**
     * The name of the Java object (property) that we are adapting
     */
    String getPropertyName();

    /**
     * The name of the Java object (property) that we are adapting
     */
    void setPropertyName(String name);

    /**
     * The Java object (property) that we are adapting
     */
    Object getPropertyValue();

    /** The Java object (property) that we are adapting */
    void setPropertyValue(Object prop );
}
