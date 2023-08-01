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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tiles.autotag.generate.BasicTemplateGenerator.TCGeneratorDirectoryPair;
import org.apache.tiles.autotag.generate.BasicTemplateGenerator.TSGeneratorDirectoryPair;

/**
 * Builds a TemplateGenerator.
 */
public class TemplateGeneratorBuilder {

    /**
     * The template suite generators.
     */
    private List<TSGeneratorDirectoryPair> templateSuiteGenerators;

    /**
     * The template class generators.
     */
    private List<TCGeneratorDirectoryPair> templateClassGenerators;

    /**
     * Indicates that this generator generates resources.
     */
    private boolean generatingResources = false;

    /**
     * Indicates that this generator generates classes.
     */
    private boolean generatingClasses = false;

    /**
     * The classes output directory.
     */
    private File classesOutputDirectory;

    /**
     * The resources output directory.
     */
    private File resourcesOutputDirectory;

    /**
     * Constructor.
     */
    private TemplateGeneratorBuilder() {
        templateSuiteGenerators = new ArrayList<BasicTemplateGenerator.TSGeneratorDirectoryPair>();
        templateClassGenerators = new ArrayList<BasicTemplateGenerator.TCGeneratorDirectoryPair>();
    }

    /**
     * Creates a new instance of the builder.
     *
     * @return A new instance of the builder.
     */
    public static TemplateGeneratorBuilder createNewInstance() {
        return new TemplateGeneratorBuilder();
    }

    /**
     * Sets the classes output directory.
     *
     * @param classesOutputDirectory The classes output directory.
     * @return This instance.
     */
    public TemplateGeneratorBuilder setClassesOutputDirectory(File classesOutputDirectory) {
        this.classesOutputDirectory = classesOutputDirectory;
        return this;
    }

    /**
     * Sets the resources output directory.
     *
     * @param resourcesOutputDirectory The resources output directory.
     * @return This instance.
     */
    public TemplateGeneratorBuilder setResourcesOutputDirectory(File resourcesOutputDirectory) {
        this.resourcesOutputDirectory = resourcesOutputDirectory;
        return this;
    }

    /**
     * Adds a new template suite generator to generate classes.
     *
     * @param generator The generator to add.
     * @return This instance.
     */
    public TemplateGeneratorBuilder addClassesTemplateSuiteGenerator(TemplateSuiteGenerator generator) {
        if (classesOutputDirectory == null) {
            throw new NullPointerException(
                    "Classes output directory not specified, call 'setClassesOutputDirectory' first");
        }
        templateSuiteGenerators.add(new TSGeneratorDirectoryPair(
                classesOutputDirectory, generator));
        generatingClasses = true;
        return this;
    }

    /**
     * Adds a new template class generator to generate classes.
     *
     * @param generator The generator to add.
     * @return This instance.
     */
    public TemplateGeneratorBuilder addClassesTemplateClassGenerator(TemplateClassGenerator generator) {
        if (classesOutputDirectory == null) {
            throw new NullPointerException(
                    "Classes output directory not specified, call 'setClassesOutputDirectory' first");
        }
        templateClassGenerators.add(new TCGeneratorDirectoryPair(
                classesOutputDirectory, generator));
        generatingClasses = true;
        return this;
    }

    /**
     * Adds a new template suite generator to generate resources.
     *
     * @param generator The generator to add.
     * @return This instance.
     */
    public TemplateGeneratorBuilder addResourcesTemplateSuiteGenerator(TemplateSuiteGenerator generator) {
        if (resourcesOutputDirectory == null) {
            throw new NullPointerException(
                    "Resources output directory not specified, call 'setClassesOutputDirectory' first");
        }
        templateSuiteGenerators.add(new TSGeneratorDirectoryPair(
                resourcesOutputDirectory, generator));
        generatingResources = true;
        return this;
    }

    /**
     * Adds a new template class generator to generate resources.
     *
     * @param generator The generator to add.
     * @return This instance.
     */
    public TemplateGeneratorBuilder addResourcesTemplateClassGenerator(TemplateClassGenerator generator) {
        if (resourcesOutputDirectory == null) {
            throw new NullPointerException(
                    "Resources output directory not specified, call 'setClassesOutputDirectory' first");
        }
        templateClassGenerators.add(new TCGeneratorDirectoryPair(
                resourcesOutputDirectory, generator));
        generatingResources = true;
        return this;
    }

    /**
     * Builds and returns a new template generator.
     *
     * @return The new template generator.
     */
    public TemplateGenerator build() {
        return new BasicTemplateGenerator(templateSuiteGenerators,
                templateClassGenerators, generatingClasses, generatingResources);
    }

}
