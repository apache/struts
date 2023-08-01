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
package org.apache.tiles.autotag.generate;


import java.util.Map;

import org.apache.tiles.autotag.model.TemplateSuite;

/**
 * Generates all the code for a template suite.
 */
public interface TemplateGenerator {

    /**
     * Generates the code.
     *
     * @param packageName The package name.
     * @param suite The template suite.
     * @param parameters Configuration parameters.
     * @param runtimeClass The RequestBuilder implementation.
     * @param requestClass The request class to use.
     */
    void generate(String packageName, TemplateSuite suite, Map<String, String> parameters, 
        String runtimeClass, String requestClass);

    /**
     * Indicates that this generator generates resources.
     *
     * @return <code>true</code> if the generator generates resources.
     */
    boolean isGeneratingResources();

    /**
     * Indicates that this generator generates classes.
     *
     * @return <code>true</code> if the generator generates classes.
     */
    boolean isGeneratingClasses();
}
