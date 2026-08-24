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
package org.apache.struts2.components;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

/**
 * The kind of HTML form control a {@link UIBean} renders, used to decide which HTML5 constraint
 * attributes are legal on it.
 * <p>
 * This models the <em>control</em> rather than the {@code type} attribute, because {@code textarea}
 * and {@code select} have no {@code type} attribute yet still accept {@code required}.
 *
 * @since 7.4.0
 */
public enum HtmlControlType {

    TEXT, SEARCH, TEL, PASSWORD, EMAIL, URL,
    NUMBER, RANGE,
    DATE, MONTH, WEEK, TIME, DATETIME_LOCAL,
    CHECKBOX, RADIO, FILE, HIDDEN, SELECT,
    TEXTAREA,
    OTHER;

    private static final Set<HtmlControlType> TEXT_ENTRY = EnumSet.of(TEXT, SEARCH, TEL, PASSWORD, EMAIL, URL);
    private static final Set<HtmlControlType> NUMERIC = EnumSet.of(NUMBER, RANGE);
    private static final Set<HtmlControlType> TEMPORAL = EnumSet.of(DATE, MONTH, WEEK, TIME, DATETIME_LOCAL);

    /**
     * Resolves a raw {@code type} attribute value. Never throws: the attribute is OGNL-evaluated, so at
     * runtime it can be any string. Anything unrecognised becomes {@link #OTHER}, which supports no
     * constraints at all — so an unknown control degrades to emitting nothing.
     */
    public static HtmlControlType from(String type) {
        if (type == null) {
            return OTHER;
        }
        String normalised = type.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        if (normalised.isEmpty()) {
            return OTHER;
        }
        try {
            return valueOf(normalised);
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }

    public boolean supportsPattern() {
        return TEXT_ENTRY.contains(this);
    }

    public boolean supportsLength() {
        return TEXT_ENTRY.contains(this) || this == TEXTAREA;
    }

    public boolean supportsRange() {
        return NUMERIC.contains(this) || TEMPORAL.contains(this);
    }
}
