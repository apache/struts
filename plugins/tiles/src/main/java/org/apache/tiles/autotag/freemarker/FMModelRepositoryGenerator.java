/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.autotag.freemarker;

import java.io.File;
import java.util.Map;

import org.apache.tiles.autotag.generate.AbstractTemplateSuiteGenerator;
import org.apache.tiles.autotag.model.TemplateSuite;
import org.apache.velocity.app.VelocityEngine;

/**
 * Generates the model repository, given the template suite.
 */
public class FMModelRepositoryGenerator extends AbstractTemplateSuiteGenerator {

    /**
     * Constructor.
     *
     * @param velocityEngine The Velocity engine.
     */
    public FMModelRepositoryGenerator(VelocityEngine velocityEngine) {
        super(velocityEngine);
    }

    @Override
    protected String getTemplatePath(File directory, String packageName,
            TemplateSuite suite, Map<String, String> parameters) {
        return "/org/apache/tiles/autotag/freemarker/repository.vm";
    }

    @Override
    protected String getFilename(File directory, String packageName,
            TemplateSuite suite, Map<String, String> parameters) {
        String name = suite.getName();
        return name.substring(0, 1).toUpperCase() + name.substring(1) + "FMModelRepository.java";
    }

    @Override
    protected String getDirectoryName(File directory, String packageName,
            TemplateSuite suite, Map<String, String> parameters) {
        return packageName.replaceAll("\\.", "/");
    }

}
