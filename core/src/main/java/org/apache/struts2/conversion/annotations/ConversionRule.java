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
package org.apache.struts2.conversion.annotations;

import org.apache.struts2.conversion.impl.DefaultObjectTypeDeterminer;

/**
 * <code>ConversionRule</code>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public enum ConversionRule {

    PROPERTY, COLLECTION, MAP, KEY, KEY_PROPERTY, ELEMENT, CREATE_IF_NULL;

    /**
     * The prefix a conversion mapping key carries for this rule, as read back by
     * {@link DefaultObjectTypeDeterminer}. {@code PROPERTY} and {@code MAP} have no prefix of their
     * own: map and collection metadata is read through the {@code Key_} and {@code Element_} keys.
     *
     * <p>{@code COLLECTION} deliberately derives {@code Collection_}, the deprecated spelling that
     * {@link DefaultObjectTypeDeterminer} still falls back to (and logs an INFO about) for
     * compatibility with existing annotations. Prefer {@link #ELEMENT}, whose {@code Element_}
     * prefix is the current form.</p>
     *
     * @return the mapping key prefix, never null; an empty string when the rule has none
     * @since 7.3.0
     */
    public String prefix() {
        return switch (this) {
            case COLLECTION -> DefaultObjectTypeDeterminer.DEPRECATED_ELEMENT_PREFIX;
            case CREATE_IF_NULL -> DefaultObjectTypeDeterminer.CREATE_IF_NULL_PREFIX;
            case ELEMENT -> DefaultObjectTypeDeterminer.ELEMENT_PREFIX;
            case KEY -> DefaultObjectTypeDeterminer.KEY_PREFIX;
            case KEY_PROPERTY -> DefaultObjectTypeDeterminer.KEY_PROPERTY_PREFIX;
            case PROPERTY, MAP -> "";
        };
    }

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }
}

