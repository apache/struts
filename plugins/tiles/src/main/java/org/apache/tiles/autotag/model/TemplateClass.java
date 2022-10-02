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
 * It represents a parsed template class.
 */
public class TemplateClass {

    /**
     * The class name.
     */
    private String name;

    /**
     * The name of the tag.
     */
    private String tagName;

    /**
     * The prefix of the tag class.
     */
    private String tagClassPrefix;

    /**
     * Documentation about this tag.
     */
    private String documentation;

    /**
     * The method that executes the template class.
     */
    private TemplateMethod executeMethod;

    /**
     * Constructor.
     *
     * @param name The name of the template class.
     */
    public TemplateClass(String name) {
        this(name, null, null, null);
    }

    /**
     * Constructor.
     *
     * @param name The name of the template class.
     * @param tagName The name of the tag.
     * @param tagClassPrefix The tag class prefix.
     * @param executeMethod The method that executes the template class.
     */
    public TemplateClass(String name, String tagName, String tagClassPrefix,
            TemplateMethod executeMethod) {
        this.name = name;
        this.tagName = tagName;
        this.tagClassPrefix = tagClassPrefix;
        this.executeMethod = executeMethod;
    }

    /**
     * The name of the parsed class.
     *
     * @return The name of the class.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the name of the class, without the package part.
     *
     * @return The simple class name.
     */
    public String getSimpleName() {
        int pos = name.lastIndexOf('.');
        if (pos >= 0) {
            return name.substring(pos + 1);
        }
        return name;
    }

    /**
     * Returns the tag name.
     *
     * @return The tag name.
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Returns the tag class prefix.
     *
     * @return The tag class prefix.
     */
    public String getTagClassPrefix() {
        return tagClassPrefix;
    }

    /**
     * Returns the documentation for this class.
     *
     * @return The documentation.
     */
    public String getDocumentation() {
        return documentation;
    }

    /**
     * Sets the documentation for this class.
     *
     * @param documentation The documentation.
     */
    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    /**
     * Returns the method that execute this class.
     *
     * @return The execute method.
     */
    public TemplateMethod getExecuteMethod() {
        return executeMethod;
    }

    /**
     * Returns the collection of regular parameters (no request, no body)
     * of the execute method.
     *
     * @return The regular parameters.
     */
    public Collection<TemplateParameter> getParameters() {
        Map<String, TemplateParameter> params = new LinkedHashMap<String, TemplateParameter>();
        fillRegularParameters(params, executeMethod);
        return params.values();
    }

    /**
     * Indicates that this class needs a tag body.
     *
     * @return <code>true</code> if tag body is needed.
     */
    public boolean hasBody() {
        return executeMethod.hasBody();
    }

    @Override
    public String toString() {
        return "TemplateClass [name=" + name + ", tagName=" + tagName
                + ", tagClassPrefix=" + tagClassPrefix + ", documentation="
                + documentation + ", executeMethod=" + executeMethod + "]";
    }

    /**
     * Creates regular parameters map.
     *
     * @param params The map to fill.
     * @param method The method to analyze.
     */
    private void fillRegularParameters(Map<String, TemplateParameter> params,
            TemplateMethod method) {
        if (method != null) {
            for (TemplateParameter param : method.getParameters()) {
                if (!param.isRequest() && !param.isBody()) {
                    params.put(param.getName(), param);
                }
            }
        }
    }
}
