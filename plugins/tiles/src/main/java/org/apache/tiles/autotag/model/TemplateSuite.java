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
 * It represents a suite of template classes.
 */
public class TemplateSuite {

    /**
     * The name of the suite.
     */
    private String name;

    /**
     * The documentation of this suite.
     */
    private String documentation;

    /**
     * The map of template classes.
     */
    private Map<String, TemplateClass> templateClasses;

    /**
     * Constructor.
     *
     * @param name The name of the suite.
     * @param documentation The documentation.
     */
    public TemplateSuite(String name, String documentation) {
        this(name, documentation, null);
    }

    /**
     * Constructor.
     *
     * @param name The name of the suite.
     * @param documentation The documentation.
     * @param classes The template classes.
     */
    public TemplateSuite(String name, String documentation,
            Iterable<? extends TemplateClass> classes) {
        this.name = name;
        this.documentation = documentation;
        templateClasses = new LinkedHashMap<String, TemplateClass>();
        if (classes != null) {
            for (TemplateClass templateClass : classes) {
                templateClasses.put(templateClass.getName(), templateClass);
            }
        }
    }

    /**
     * Returns the template suite name.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the documentation.
     *
     * @return The documentation.
     */
    public String getDocumentation() {
        return documentation;
    }

    /**
     * Adds a new template class.
     *
     * @param clazz The template class.
     */
    public void addTemplateClass(TemplateClass clazz) {
        templateClasses.put(clazz.getName(), clazz);
    }

    /**
     * Returns the template classes.
     *
     * @return The template classes.
     */
    public Collection<TemplateClass> getTemplateClasses() {
        return templateClasses.values();
    }

    /**
     * Returns a template class given its name.
     *
     * @param name The name of the class.
     * @return The template class instance.
     */
    public TemplateClass getTemplateClassByName(String name) {
        return templateClasses.get(name);
    }

    @Override
    public String toString() {
        return "TemplateSuite [name=" + name + ", documentation="
                + documentation + ", templateClasses=" + templateClasses + "]";
    }
}
