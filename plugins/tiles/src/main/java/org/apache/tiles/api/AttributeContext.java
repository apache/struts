/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.api;

import java.util.Map;
import java.util.Set;

/**
 * Encapsulation of the current state of execution.
 *
 * @since Tiles 2.0
 */
public interface AttributeContext {

    /**
     * Returns the attribute that will be used to render a template.
     *
     * @return The template attribute.
     * @since 2.1.2
     */
    Attribute getTemplateAttribute();

    /**
     * Sets the template attribute, that will be used to render the template
     * page.
     *
     * @param templateAttribute The template attribute.
     * @since 2.1.2
     */
    void setTemplateAttribute(Attribute templateAttribute);

    /**
     * Get associated preparer instance.
     *
     * @return The preparer name.
     * @since 2.1.0
     */
    String getPreparer();

    /**
     * Set associated preparer instance.
     *
     * @param url The preparer name.
     * @since 2.1.0
     */
    void setPreparer(String url);

    /**
     * Add all attributes to the context.
     *
     * @param newAttributes the attributes to be added.
     */
    void addAll(Map<String, Attribute> newAttributes);

    /**
     * Copies the cascaded attributes to this attribute context.
     *
     * @param parent The parent context to be used.
     * @since 2.1.0
     */
    void inheritCascadedAttributes(AttributeContext parent);

    /**
     * Copies all missing attributes from the <code>parent</code> attribute
     * context to this one.
     *
     * @param parent The attribute context to copy attributes from.
     * @since 2.1.0
     */
    void inherit(AttributeContext parent);

    /**
     * Retrieve the named attribute, either cascaded or not.
     *
     * @param name key name for the attribute.
     * @return Attribute associated with the given name.
     */
    Attribute getAttribute(String name);

    /**
     * Retrieve the attribute that has been defined in this context (i.e. not
     * cascaded).
     *
     * @param name key name for the attribute.
     * @return Attribute The local attribute associated with the given name, if
     * present, or <code>null</code> otherwise.
     * @since 2.1.0
     */
    Attribute getLocalAttribute(String name);

    /**
     * Retrieve the attribute that has been cascaded at upper levels.
     *
     * @param name key name for the attribute.
     * @return Attribute The cascaded attribute associated with the given name,
     * if present, or <code>null</code> otherwise.
     * @since 2.1.0
     */
    Attribute getCascadedAttribute(String name);

    /**
     * Returns the names of the local attributes, i.e. the one that have not
     * been cascaded.
     *
     * @return The local attribute names.
     * @since 2.1.0
     */
    Set<String> getLocalAttributeNames();

    /**
     * Returns the names of the cascaded attributes.
     *
     * @return The cascaded attribute names.
     * @since 2.1.0
     */
    Set<String> getCascadedAttributeNames();

    /**
     * Add the specified attribute. The attribute value will be available only
     * in the current context, i.e. it is like calling
     * {@link AttributeContext#putAttribute(String, Attribute, boolean)} with
     * <code>cascade = false</code>.
     *
     * @param name name of the attribute
     * @param value value of the attribute
     */
    void putAttribute(String name, Attribute value);

    /**
     * Add the specified attribute.
     *
     * @param name name of the attribute
     * @param value value of the attribute
     * @param cascade If <code>true</code>, the attribute value will be
     * available in all nested contexts. If <code>false</code>, it will be
     * available only in the current context.
     * @since 2.1.0
     */
    void putAttribute(String name, Attribute value, boolean cascade);

    /**
     * Clear the attributes.
     */
    void clear();
}
