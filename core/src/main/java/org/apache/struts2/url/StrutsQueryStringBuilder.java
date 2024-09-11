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
package org.apache.struts2.url;

import org.apache.struts2.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class StrutsQueryStringBuilder implements QueryStringBuilder {

    private static final Logger LOG = LogManager.getLogger(StrutsQueryStringBuilder.class);

    private final UrlEncoder encoder;

    @Inject
    public StrutsQueryStringBuilder(UrlEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public void build(Map<String, Object> params, StringBuilder link, String paramSeparator) {
        if (params == null || params.isEmpty()) {
            LOG.debug("Params are empty, skipping building the query string");
            return;
        }

        LOG.debug("Building query string out of: {} parameters", params.size());
        StringBuilder queryString = new StringBuilder();

        // Set params
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Iterable) {
                for (Object o : (Iterable<?>) value) {
                    appendParameterSubstring(queryString, paramSeparator, name, o);
                }
            } else if (value instanceof Object[] array) {
                for (Object o : array) {
                    appendParameterSubstring(queryString, paramSeparator, name, o);
                }
            } else {
                appendParameterSubstring(queryString, paramSeparator, name, value);
            }
        }

        if (!queryString.isEmpty()) {
            if (!link.toString().contains("?")) {
                link.append("?");
            } else {
                link.append(paramSeparator);
            }
            link.append(queryString);
        }
    }

    private void appendParameterSubstring(StringBuilder queryString, String paramSeparator, String name, Object value) {
        if (!queryString.isEmpty()) {
            queryString.append(paramSeparator);
        }

        String encodedName = encoder.encode(name);
        queryString.append(encodedName);

        queryString.append('=');
        if (value != null) {
            String encodedValue = encoder.encode(value.toString());
            queryString.append(encodedValue);
        }
    }

}
