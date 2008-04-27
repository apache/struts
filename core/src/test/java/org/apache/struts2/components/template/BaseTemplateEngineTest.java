/*
 * $Id$
 *
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

package org.apache.struts2.components.template;

import java.io.File;
import java.net.URL;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Test case for BaseTemplateEngine
 */
public class BaseTemplateEngineTest extends TestCase {

public void testGetThemePropsThroughFileSystem() throws Exception {

        URL dummyResourceUrl = getClass().getResource("dummy.properties");
        File dummyResourceFile = new File(dummyResourceUrl.getFile().replaceAll("%20", " "));
        String themePropertiesDir = dummyResourceFile.getParent();

        System.out.println("dummy resource url="+dummyResourceUrl);
        System.out.println("resource file="+dummyResourceFile);
        System.out.println("theme properties dir="+themePropertiesDir);

        assertTrue(dummyResourceFile.exists());
        assertNotNull(themePropertiesDir);

        Template template = new Template(themePropertiesDir, "theme1", "template1");

        TemplateEngine templateEngine = new InnerBaseTemplateEngine("themeThroughFileSystem.properties");
        Map propertiesMap = templateEngine.getThemeProps(template);

        assertNotNull(propertiesMap);
        assertTrue(propertiesMap.size() > 0);

    }

    public void testGetThemePropsThroughClasspath() throws Exception {

        Template template = new Template("org/apache/struts2/components/template", "theme1", "template2");
        TemplateEngine templateEngine = new InnerBaseTemplateEngine("themeThroughClassPath.properties");
        Map propertiesMap = templateEngine.getThemeProps(template);

        assertNotNull(propertiesMap);
        assertTrue(propertiesMap.size() > 0);
    }

    public class InnerBaseTemplateEngine extends BaseTemplateEngine {

        private String themePropertiesFileName;

        public InnerBaseTemplateEngine(String themePropertiesFileName) {
            this.themePropertiesFileName = themePropertiesFileName;
        }

        protected String getSuffix() {
            return "ftl";
        }

        public void renderTemplate(TemplateRenderingContext templateContext) throws Exception {
        }

        protected String getThemePropertiesFileName() {
            return this.themePropertiesFileName;
        }
    }
}
