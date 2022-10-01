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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.apache.tiles.autotag.core.AutotagRuntimeException;
import org.apache.tiles.autotag.model.TemplateSuite;
import org.apache.tiles.autotag.tool.StringTool;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * A base template suite generator.
 */
public abstract class AbstractTemplateSuiteGenerator implements TemplateSuiteGenerator {

    /**
     * The velocity engine.
     */
    private VelocityEngine velocityEngine;

    /**
     * Constructor.
     *
     * @param velocityEngine The Velocity engine.
     */
    public AbstractTemplateSuiteGenerator(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    @Override
    public void generate(File directory, String packageName, TemplateSuite suite, Map<String, String> parameters) {
        File dir = new File(directory, getDirectoryName(directory, packageName, suite, parameters));
        dir.mkdirs();
        File file = new File(dir, getFilename(dir, packageName, suite, parameters));
        VelocityContext context = new VelocityContext();
        context.put("packageName", packageName);
        context.put("suite", suite);
        context.put("stringTool", new StringTool());
        context.put("parameters", parameters);
        try {
            file.createNewFile();
            Template template = velocityEngine.getTemplate(getTemplatePath(dir,
                    packageName, suite, parameters));
            Writer writer = new FileWriter(file);
            try {
                template.merge(context, writer);
            } finally {
                writer.close();
            }
        } catch (ResourceNotFoundException e) {
            throw new AutotagRuntimeException("Cannot find template resource", e);
        } catch (ParseErrorException e) {
            throw new AutotagRuntimeException("The template resource is not parseable", e);
        } catch (IOException e) {
            throw new AutotagRuntimeException(
                    "I/O Exception when generating file", e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new AutotagRuntimeException(
                    "Another generic exception while parsing the template resource",
                    e);
        }
    }

    /**
     * Calculates and returns the template path.
     *
     * @param directory The directory where the file will be written.
     * @param packageName The name of the package.
     * @param suite The template suite.
     * @param parameters The map of parameters.
     * @return The template path.
     */
    protected abstract String getTemplatePath(File directory,
            String packageName, TemplateSuite suite,
            Map<String, String> parameters);

    /**
     * Calculates and returns the filename of the generated file.
     *
     * @param directory The directory where the file will be written.
     * @param packageName The name of the package.
     * @param suite The template suite.
     * @param parameters The map of parameters.
     * @return The template path.
     */
    protected abstract String getFilename(File directory, String packageName,
            TemplateSuite suite, Map<String, String> parameters);

    /**
     * Calculates and returns the directory where the file will be written..
     *
     * @param directory The directory where the file will be written.
     * @param packageName The name of the package.
     * @param suite The template suite.
     * @param parameters The map of parameters.
     * @return The template path.
     */
    protected abstract String getDirectoryName(File directory,
            String packageName, TemplateSuite suite,
            Map<String, String> parameters);
}
