/*
 * $Id: VelocityTemplateGeneratorFactory.java 1045345 2010-12-13 19:58:23Z apetrelli $
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
package org.apache.tiles.autotag.velocity;

import java.io.File;

import org.apache.tiles.autotag.generate.TemplateGenerator;
import org.apache.tiles.autotag.generate.TemplateGeneratorBuilder;
import org.apache.tiles.autotag.generate.TemplateGeneratorFactory;
import org.apache.velocity.app.VelocityEngine;

/**
 * Creates a template generator that generates code to build Velocity code
 * around template classes.
 *
 * @version $Rev: 1045345 $ $Date: 2010-12-13 14:58:23 -0500 (Mon, 13 Dec 2010) $
 */
public class VelocityTemplateGeneratorFactory implements
        TemplateGeneratorFactory {

    /**
     * Location of the file.
     */
    private File classesOutputDirectory;

    /**
     * Location of the file.
     */
    private File resourcesOutputDirectory;

    /**
     * The Velocity engine.
     */
    private VelocityEngine velocityEngine;

    /**
     * The template generator builder.
     */
    private TemplateGeneratorBuilder templateGeneratorBuilder;

    /**
     * Constructor.
     *
     * @param classesOutputDirectory The directory where classes will be generated.
     * @param resourcesOutputDirectory The directory where velocity.properties will be written.
     * @param velocityEngine The Velocity engine.
     * @param templateGeneratorBuilder The template generator builder.
     */
    public VelocityTemplateGeneratorFactory(File classesOutputDirectory,
            File resourcesOutputDirectory, VelocityEngine velocityEngine,
            TemplateGeneratorBuilder templateGeneratorBuilder) {
        this.classesOutputDirectory = classesOutputDirectory;
        this.resourcesOutputDirectory = resourcesOutputDirectory;
        this.velocityEngine = velocityEngine;
        this.templateGeneratorBuilder = templateGeneratorBuilder;
    }

    @Override
    public TemplateGenerator createTemplateGenerator() {
        return templateGeneratorBuilder
                .setClassesOutputDirectory(classesOutputDirectory)
                .setResourcesOutputDirectory(resourcesOutputDirectory)
                .addResourcesTemplateSuiteGenerator(
                        new VelocityPropertiesGenerator(velocityEngine))
                .addClassesTemplateClassGenerator(
                        new VelocityDirectiveGenerator(velocityEngine)).build();
    }

}
