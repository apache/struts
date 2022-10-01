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
package org.apache.tiles.request.freemarker.render;

import org.apache.tiles.request.freemarker.servlet.SharedVariableLoaderFreemarkerServlet;

import javax.servlet.http.HttpServletRequest;

/**
 * Extends {@link SharedVariableLoaderFreemarkerServlet} to use the attribute value as the template name.
 */
public class AttributeValueFreemarkerServlet extends SharedVariableLoaderFreemarkerServlet {

    /**
     * Holds the value that should be used as the template name.
     */
    private final ThreadLocal<String> valueHolder = new ThreadLocal<>();

    /**
     * Sets the value to use as the template name.
     *
     * @param value The template name.
     */
    public void setValue(String value) {
        valueHolder.set(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String requestUrlToTemplatePath(HttpServletRequest request) {
        return valueHolder.get();
    }
}
