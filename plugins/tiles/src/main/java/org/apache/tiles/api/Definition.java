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
import java.util.Objects;

/**
 * A definition, i.e. a template with (completely or not) filled attributes.
 * Attributes of a template can be defined with the help of this class.<br>
 * It can be used as a data transfer object used for registering new
 * definitions with the Container.
 *
 * @since Tiles 2.0
 */
public class Definition extends BasicAttributeContext {
    /**
     * Extends attribute value.
     */
    protected String inherit;
    /**
     * Definition name.
     */
    protected String name = null;

    /**
     * Constructor.
     */
    public Definition() {
    }

    /**
     * Copy Constructor.
     * Create a new definition initialized with parent definition.
     * Do a shallow copy : attributes are shared between copies, but not the Map
     * containing attributes.
     *
     * @param definition The definition to copy.
     */
    public Definition(Definition definition) {
        super(definition);
        this.name = definition.name;
        this.inherit = definition.inherit;
    }

    /**
     * Constructor.
     *
     * @param name              The name of the definition.
     * @param templateAttribute The template attribute of the definition.
     * @param attributes        The attribute map of the definition.
     * @since 2.1.2
     */
    public Definition(String name, Attribute templateAttribute,
                      Map<String, Attribute> attributes) {
        super(attributes);
        this.name = name;
        this.templateAttribute = templateAttribute;
    }

    /**
     * Access method for the name property.
     *
     * @return the current value of the name property
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param aName the new value of the name property
     */
    public void setName(String aName) {
        name = aName;
    }

    /**
     * Set extends.
     *
     * @param name Name of the extended definition.
     */
    public void setExtends(String name) {
        inherit = name;
    }

    /**
     * Get extends.
     *
     * @return Name of the extended definition.
     */
    public String getExtends() {
        return inherit;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Definition)) {
            return false;
        }
        Definition def = (Definition) obj;
        return Objects.equals(name, def.name) && Objects.equals(inherit, def.inherit) && super.equals(def);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(name) + Objects.hashCode(inherit) + super.hashCode();
    }

    /**
     * Get extends flag.
     *
     * @return <code>true</code> if this definition extends another.
     */
    public boolean isExtending() {
        return inherit != null;
    }

    /**
     * Returns a description of the attributes.
     *
     * @return A string representation of the content of this definition.
     */
    @Override
    public String toString() {
        return "{name="
            + name
            + ", template="
            + (templateAttribute != null ? templateAttribute.getValue() : "<null>")
            + ", role="
            + (templateAttribute != null ? templateAttribute.getRoles() : "<null>")
            + ", preparerInstance="
            + preparer
            + ", attributes="
            + attributes
            + "}";
    }
}
