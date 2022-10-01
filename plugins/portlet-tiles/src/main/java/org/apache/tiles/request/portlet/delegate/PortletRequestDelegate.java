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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.portlet.delegate;

import org.apache.tiles.request.collection.ReadOnlyEnumerationMap;
import org.apache.tiles.request.portlet.extractor.ParameterExtractor;

import javax.portlet.PortletRequest;
import java.util.Map;

/**
 * Request delegate in case of simple Portlet request.
 */
public class PortletRequestDelegate implements RequestDelegate {

    /**
     * The request.
     */
    private final PortletRequest request;

    /**
     * <p>The lazily instantiated <code>Map</code> of request
     * parameter name-value.</p>
     */
    private Map<String, String> param = null;

    /**
     * <p>The lazily instantiated <code>Map</code> of request
     * parameter name-values.</p>
     */
    private Map<String, String[]> paramValues = null;

    /**
     * Constructor.
     *
     * @param request The request.
     */
    public PortletRequestDelegate(PortletRequest request) {
        this.request = request;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getParam() {
        if ((param == null) && (request != null)) {
            param = new ReadOnlyEnumerationMap<>(new ParameterExtractor(request));
        }
        return (param);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String[]> getParamValues() {
        if ((paramValues == null) && (request != null)) {
            paramValues = request.getParameterMap();
        }
        return (paramValues);
    }
}
