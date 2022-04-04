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
package org.apache.struts2.views.java;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;

/**
 * Map of tag attributes, used for rendering the tags
 */
public class Attributes extends LinkedHashMap<String, String> {

    private static final long serialVersionUID = 4103241472140545630L; 

    public Attributes add(String key, String value) {
        return add(key, value, true);
    }

    public Attributes add(String key, String value, boolean encode) {
        put(key, (encode ? StringUtils.defaultString(StringEscapeUtils.escapeHtml4(value)) : value));
        return this;
    }

    /**
     * Add a key/value pair to the attributes only if the value is not null. Value
     * is html encoded
     * @param attrName attribute name
     * @param paramValue value of attribute
     * @return this
     */
    public Attributes addIfExists(String attrName, Object paramValue) {
        return addIfExists(attrName, paramValue, true);
    }

    /**
     * Add a key/value pair to the attributes only if the value is not null.
     * @param attrName attribute name
     * @param paramValue value of attribute
     * @param encode html encode the value
     * @return this
     */
    public Attributes addIfExists(String attrName, Object paramValue, boolean encode) {
        if (paramValue != null) {
            String val = paramValue.toString();
            if (StringUtils.isNotBlank(val))
                put(attrName, (encode ? StringUtils.defaultString(StringEscapeUtils.escapeHtml4(val)) : val));
        }
        return this;
    }

    /**
     * Add a key/value pair to the attributes only if the value is not null and is a boolean with a
     * value of 'true'. Value is html encoded
     * @param attrName attribute name
     * @param paramValue value of attribute
     * @return this
     */
    public Attributes addIfTrue(String attrName, Object paramValue) {
        if (paramValue != null) {
            if ((paramValue instanceof Boolean && ((Boolean) paramValue).booleanValue()) ||
                    (Boolean.valueOf(paramValue.toString()).booleanValue())) {
                put(attrName, attrName);
            }
        }
        return this;
    }

    /**
     * Add a key/value pair to the attributes, if the value is null, it will be set as an empty string.
     * Value is html encoded.
     * @param attrName attribute name
     * @param paramValue value of attribute
     * @return this
     */
    public Attributes addDefaultToEmpty(String attrName, Object paramValue) {
        return addDefaultToEmpty(attrName, paramValue, true);
    }

    /**
     * Add a key/value pair to the attributes, if the value is null, it will be set as an empty string.
     * @param attrName attribute name
     * @param paramValue value of attribute
     * @param encode html encode the value
     * @return this
     */
    public Attributes addDefaultToEmpty(String attrName, Object paramValue, boolean encode) {
        if (paramValue != null) {
            String val = paramValue.toString();
            put(attrName, (encode ? StringUtils.defaultString(StringEscapeUtils.escapeHtml4(val)) : val));
        } else {
            put(attrName, "");
        }
        return this;
    }
}
