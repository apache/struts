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

package org.apache.tiles.request.freemarker.autotag;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

/**
 * Utilities for FreeMarker usage in Tiles.
 */
public final class FreemarkerUtil {

    /**
     * Private constructor to avoid instantiation.
     */
    private FreemarkerUtil() {
    }

    /**
     * Unwraps a TemplateModel to extract an object.
     *
     * @param model The TemplateModel to unwrap.
     * @param defaultValue The default value, as specified in the template
     * model, or null if not specified.
     * @return The unwrapped object.
     */
    public static <T> T getAsObject(TemplateModel model, Class<T> type, T defaultValue) {
        try {
            T retValue = defaultValue;
            if (model != null) {
                @SuppressWarnings("unchecked")
                T value = (T) DeepUnwrap.unwrap(model);
                if (value != null) {
                    retValue = value;
                }
            }
            return retValue;
        } catch (TemplateModelException e) {
            throw new FreemarkerAutotagException("Cannot unwrap a model", e);
        }
    }
}
