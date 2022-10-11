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

import org.apache.tiles.autotag.generate.TemplateGenerator;
import org.apache.tiles.autotag.generate.TemplateGeneratorBuilder;
import org.apache.tiles.autotag.jsp.JspTemplateGeneratorFactory;
import org.apache.tiles.autotag.model.TemplateSuite;
import org.apache.velocity.app.VelocityEngine;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Helper class for building the JSP tag classes and .tld file.
 */
public class BuildJspAutotags {

    public BuildJspAutotags() {
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

        BuildJspAutotags me = new BuildJspAutotags();

        me.build(args[0], args[1], args[2], args[3], args[4]);

    }

    /**
     * Build
     *
     * @param taglibURI    the taglib URI
     * @param packageName  the package name
     * @param requestClass the request class
     * @param jspRuntime   the jsp runtime
     * @param outputDir    the output dir
     */
    public void build(String taglibURI, String packageName, String requestClass, String jspRuntime, String outputDir) {

        // Default values
        // taglibURI = "org.apache.tiles.autotag.jsp.runtime.Runtime";
        // packageName = "org.apache.tiles.web.jsp.taglib";
        // requestClass = "org.apache.tiles.request.Request";
        // jspRuntime = "org.apache.tiles.autotag.jsp.runtime.Runtime";
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

            generator.generate(packageName, suite, parameters, jspRuntime, requestClass);

        } catch (Exception e) {
            // ignored
        }

    }

}
