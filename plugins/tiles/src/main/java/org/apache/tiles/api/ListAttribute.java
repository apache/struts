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

import java.util.ArrayList;
import java.util.List;

/**
 * An attribute as a <code>List</code>.
 * This attribute associates a name with a list. The list can be found by the
 * property name.
 * Elements in list are retrieved using List methods.
 * This class is used to read configuration files.
 *
 * @since 2.1.0
 */
public class ListAttribute extends Attribute {

    /**
     * If true, the attribute will put the elements of the attribute with the
     * same name of the parent definition before the ones specified here. By
     * default, it is 'false'.
     */
    private boolean inherit = false;

    /**
     * Constructor.
     *
     * @since 2.1.0
     */
    public ListAttribute() {
        setValue(new ArrayList<Object>());
    }

    /**
     * Copy constructor.
     *
     * @param toCopy The list attribute to copy.
     * @since 2.1.3
     */
    public ListAttribute(ListAttribute toCopy) {
        super(toCopy);
        List<Attribute> attributesToCopy = toCopy.getValue();
        if (attributesToCopy != null) {
            List<Attribute> attributes = new ArrayList<>(attributesToCopy.size());
            for (Attribute attribute : attributesToCopy) {
                if (attribute != null) {
                    attributes.add(attribute.copy());
                } else {
                    attributes.add(null);
                }
            }
            setValue(attributes);
        }
        this.inherit = toCopy.inherit;
    }

    /**
     * Sets the list of the attributes that are elements of this attribute.
     *
     * @param attributes The attributes.
     * @since 3.0.0
     */
    public void setValue(List<Attribute> attributes) {
        super.setValue(attributes);
    }

    /**
     * Returns the list of the attributes that are elements of this attribute.
     *
     * @return The attributes.
     * @since 3.0.0
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Attribute> getValue() {
        return (List<Attribute>) super.getValue();
    }

    /**
     * Add an element in list.
     * We use a property to avoid rewriting a new class.
     *
     * @param element XmlAttribute to add.
     * @since 2.1.0
     */
    public void add(Attribute element) {
        getValue().add(element);
    }

    /**
     * If true, the attribute will put the elements of the attribute with the
     * same name of the parent definition before the ones specified here. By
     * default, it is 'false'
     *
     * @param inherit The "inherit" value.
     * @since 2.1.0
     */
    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    /**
     * If true, the attribute will put the elements of the attribute with the
     * same name of the parent definition before the ones specified here. By
     * default, it is 'false'
     *
     * @return inherit The "inherit" value.
     * @since 2.1.0
     */
    public boolean isInherit() {
        return inherit;
    }

    /**
     * Inherits elements present in a "parent" list attribute. The elements will
     * be put before the ones already present.
     *
     * @param parent The parent list attribute.
     * @since 2.1.0
     */
    public void inherit(ListAttribute parent) {
        List<Attribute> tempList = new ArrayList<>();
        tempList.addAll(parent.getValue());
        tempList.addAll(getValue());
        setValue(tempList);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ListAttribute)) {
            return false;
        }
        ListAttribute attribute = (ListAttribute) obj;
        return super.equals(attribute) && this.inherit == attribute.inherit;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return super.hashCode() + Boolean.valueOf(inherit).hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public ListAttribute copy() {
        return new ListAttribute(this);
    }
}
