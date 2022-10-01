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
package org.apache.tiles.autotag.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * It represents a parsed method in a parsed template class.
 */
public class TemplateMethod {

    /**
     * The name of the method.
     */
    private String name;

    /**
     * Documentation about the method.
     */
    private String documentation;

    /**
     * The map of parameters.
     */
    private Map<String, TemplateParameter> parameters;

    /**
     * Constructor.
     *
     * @param name The name of the method.
     * @param parameters The map of parameters.
     */
    public TemplateMethod(String name,
            Iterable<? extends TemplateParameter> parameters) {
        this.name = name;
        this.parameters = new LinkedHashMap<String, TemplateParameter>();
        for (TemplateParameter parameter : parameters) {
            this.parameters.put(parameter.getName(), parameter);
        }
    }

    /**
     * Returns the name of the method.
     *
     * @return The name of the method.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the documentation for this method.
     *
     * @return The documentation.
     */
    public String getDocumentation() {
        return documentation;
    }

    /**
     * Sets the documentation for this method.
     *
     * @param documentation The documentation.
     */
    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    /**
     * Returns the parameters of this method.
     *
     * @return The parameters.
     */
    public Collection<TemplateParameter> getParameters() {
        return parameters.values();
    }

    /**
     * Returns a parameter given its name.
     *
     * @param name The name of the parameter.
     * @return The parameter.
     */
    public TemplateParameter getParameterByName(String name) {
        return parameters.get(name);
    }

    /**
     * Indicates that this method needs a tag body.
     *
     * @return <code>true</code> if tag body is needed.
     */
    public boolean hasBody() {
        if (parameters.size() >= 2) {
            for (TemplateParameter param : parameters.values()) {
                if (param.isBody()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "TemplateMethod [name=" + name + ", documentation="
                + documentation + ", parameters=" + parameters + "]";
    }
}
