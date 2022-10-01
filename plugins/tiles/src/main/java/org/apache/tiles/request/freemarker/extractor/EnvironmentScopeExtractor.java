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
package org.apache.tiles.request.freemarker.extractor;

import freemarker.core.Environment;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;
import org.apache.tiles.request.attribute.AttributeExtractor;
import org.apache.tiles.request.freemarker.FreemarkerRequestException;

import java.util.Collections;
import java.util.Enumeration;

/**
 * Extract attributes from {@link Environment} objects as a scope.
 */
public class EnvironmentScopeExtractor implements AttributeExtractor {

    /**
     * The environment.
     */
    private final Environment request;

    /**
     * Constructor.
     *
     * @param request The environment.
     */
    public EnvironmentScopeExtractor(Environment request) {
        this.request = request;
    }

    @Override
    public void removeValue(String name) {
        request.setVariable(name, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enumeration<String> getKeys() {
        try {
            return Collections.<String> enumeration(request.getKnownVariableNames());
        } catch (TemplateModelException e) {
            throw new FreemarkerRequestException("Cannot iterate variable names correctly", e);
        }
    }

    @Override
    public Object getValue(String key) {
        try {
            TemplateModel variable = request.getVariable(key);
            if (variable != null) {
                return DeepUnwrap.unwrap(variable);
            }
            return null;
        } catch (TemplateModelException e) {
            throw new FreemarkerRequestException("Cannot get attribute with name '" + key + "'", e);
        }
    }

    @Override
    public void setValue(String key, Object value) {
        try {
            TemplateModel model = request.getObjectWrapper().wrap(value);
            request.setVariable(key, model);
        } catch (TemplateModelException e) {
            throw new FreemarkerRequestException("Error when wrapping an object setting the '" + key + "' attribute", e);
        }
    }
}
