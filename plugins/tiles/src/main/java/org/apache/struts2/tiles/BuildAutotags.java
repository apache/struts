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
package org.apache.struts2.tiles;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.tiles.autotag.freemarker.FMTemplateGeneratorFactory;
import org.apache.tiles.autotag.generate.TemplateGenerator;
import org.apache.tiles.autotag.generate.TemplateGeneratorBuilder;
import org.apache.tiles.autotag.jsp.JspTemplateGeneratorFactory;
import org.apache.tiles.autotag.model.TemplateSuite;
import org.apache.tiles.autotag.velocity.VelocityTemplateGeneratorFactory;
import org.apache.velocity.app.VelocityEngine;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Helper class for building/generating the classes and resources used in the
 * plugin.
 */
public class BuildAutotags {

    public BuildAutotags() {
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

        BuildAutotags me = new BuildAutotags();

        // Jsp classes
        me.buildJsp(args[0]);

        // Freemarker classes
        me.buildFreemarker(args[0]);

        // Velocity classes
        me.buildVelocity(args[0]);

    }

    /**
     * Build JSP tag classes and .tld file.
     * 
     * To build, change template-suite.xml as required and then run this program.
     * Copy the classes and .tld from the target autotag folder into the packageName
     * location, .tld to src/main/resources/META-INF/tld/tiles-jsp.tld
     *
     * @param outputDir the output dir
     */
    public void buildJsp(String outputDir) {

        // Default values
        String taglibURI = "http://tiles.apache.org/tags-tiles";
        String packageName = "org.apache.tiles.web.jsp.taglib";
        String requestClass = "org.apache.tiles.request.Request";
        String runtime = "org.apache.tiles.request.jsp.autotag.JspAutotagRuntime";
        // outputDir = "/target"

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("taglibURI", taglibURI);

        try {

            TemplateSuite suite;

            InputStream stream = getClass().getResourceAsStream("/META-INF/template-suite.xml");

            try {
                XStream xstream = new XStream(new DomDriver());
                xstream.allowTypes(new Class[] { org.apache.tiles.autotag.model.TemplateClass.class,
                        org.apache.tiles.autotag.model.TemplateSuite.class,
                        org.apache.tiles.autotag.model.TemplateParameter.class });
                suite = (TemplateSuite) xstream.fromXML(stream);
            } finally {
                stream.close();
            }

            Properties props = new Properties();
            InputStream propsStream = getClass().getResourceAsStream("/org/apache/tiles/autotag/velocity.properties");
            props.load(propsStream);
            propsStream.close();

            File classesOutputDirectory = new File(outputDir + "/generated-sources/autotag/classes");
            File resourcesOutputDirectory = new File(outputDir + "/generated-sources/autotag");

            TemplateGenerator generator = new JspTemplateGeneratorFactory(classesOutputDirectory,
                    resourcesOutputDirectory, new VelocityEngine(props), TemplateGeneratorBuilder.createNewInstance())
                    .createTemplateGenerator();

            generator.generate(packageName, suite, parameters, runtime, requestClass);

        } catch (Exception e) {
            // ignored
        }

    }

    /**
     * Builds the Freemarker classes.
     * 
     * To build, change template-suite.xml as required and then run this program.
     * Copy the classes from the target autotag folder into the packageName
     * location.
     *
     * @param outputDir the output dir
     */
    public void buildFreemarker(String outputDir) {

        // Default values
        String packageName = "org.apache.tiles.freemarker.template";
        String requestClass = "org.apache.tiles.request.Request";
        String runtime = "org.apache.tiles.request.freemarker.autotag.FreemarkerAutotagRuntime";
        // outputDir = "/target"

        try {

            TemplateSuite suite;

            InputStream stream = getClass().getResourceAsStream("/META-INF/template-suite.xml");

            try {
                XStream xstream = new XStream(new DomDriver());
                xstream.allowTypes(new Class[] { org.apache.tiles.autotag.model.TemplateClass.class,
                        org.apache.tiles.autotag.model.TemplateSuite.class,
                        org.apache.tiles.autotag.model.TemplateParameter.class });
                suite = (TemplateSuite) xstream.fromXML(stream);
            } finally {
                stream.close();
            }

            Properties props = new Properties();
            InputStream propsStream = getClass().getResourceAsStream("/org/apache/tiles/autotag/velocity.properties");
            props.load(propsStream);
            propsStream.close();

            File classesOutputDirectory = new File(outputDir + "/generated-sources/autotag/classes");

            TemplateGenerator generator = new FMTemplateGeneratorFactory(classesOutputDirectory,
                    new VelocityEngine(props), TemplateGeneratorBuilder.createNewInstance()).createTemplateGenerator();

            generator.generate(packageName, suite, null, runtime, requestClass);

        } catch (Exception e) {
            // ignored
        }

    }

    /**
     * Builds the velocity classes and velocity.properties.
     * 
     * To build, change template-suite.xml as required and then run this program.
     * Copy the classes from the target autotag folder into the packageName
     * location, and velocity.properties to
     * src/main/resources/META-INF/velocity.properties
     *
     * @param outputDir the output dir
     */
    public void buildVelocity(String outputDir) {

        // Default values
        String packageName = "org.apache.tiles.velocity.template";
        String requestClass = "org.apache.tiles.request.Request";
        String runtime = "org.apache.tiles.request.velocity.autotag.VelocityAutotagRuntime";
        // outputDir = "/target"

        try {

            TemplateSuite suite;

            InputStream stream = getClass().getResourceAsStream("/META-INF/template-suite.xml");

            try {
                XStream xstream = new XStream(new DomDriver());
                xstream.allowTypes(new Class[] { org.apache.tiles.autotag.model.TemplateClass.class,
                        org.apache.tiles.autotag.model.TemplateSuite.class,
                        org.apache.tiles.autotag.model.TemplateParameter.class });
                suite = (TemplateSuite) xstream.fromXML(stream);
            } finally {
                stream.close();
            }

            Properties props = new Properties();
            InputStream propsStream = getClass().getResourceAsStream("/org/apache/tiles/autotag/velocity.properties");
            props.load(propsStream);
            propsStream.close();

            File classesOutputDirectory = new File(outputDir + "/generated-sources/autotag/classes");
            File resourcesOutputDirectory = new File(outputDir + "/generated-sources/autotag");

            TemplateGenerator generator = new VelocityTemplateGeneratorFactory(classesOutputDirectory,
                    resourcesOutputDirectory, new VelocityEngine(props), TemplateGeneratorBuilder.createNewInstance())
                    .createTemplateGenerator();

            generator.generate(packageName, suite, null, runtime, requestClass);

        } catch (Exception e) {
            // ignored
        }

    }

}
