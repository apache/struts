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
package org.apache.struts2.maven.watch;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class StrutsReloadInspector {

    private static final String DEV_MODE = "struts.devMode";
    private static final String XML_RELOAD = "struts.configuration.xml.reload";
    private static final String I18N_RELOAD = "struts.i18n.reload";

    private final Map<String, String> settings = new HashMap<>();

    public StrutsReloadInspector(Path outputDirectory) {
        readXml(outputDirectory.resolve("struts.xml"));
        readProperties(outputDirectory.resolve("struts.properties"));
    }

    public List<String> warnings() {
        List<String> warnings = new ArrayList<>();
        String devMode = settings.get(DEV_MODE);
        if (devMode == null) {
            warnings.add("struts.devMode was not found in struts.xml or struts.properties; "
                    + "if it is set elsewhere, ignore this. Otherwise pushed changes may not be picked up.");
        }
        for (String key : new String[]{XML_RELOAD, I18N_RELOAD}) {
            String explicit = settings.get(key);
            String effective = explicit != null ? explicit : devMode;
            if (effective != null && !"true".equalsIgnoreCase(effective.trim())) {
                warnings.add(key + " is not true; pushed changes will not be picked up "
                        + "until the application is restarted. Set struts.devMode=true or " + key + "=true.");
            }
        }
        return warnings;
    }

    private void readXml(Path file) {
        Document document = parse(file);
        if (document == null) {
            return;
        }
        NodeList constants = document.getDocumentElement().getElementsByTagName("constant");
        for (int i = 0; i < constants.getLength(); i++) {
            Element constant = (Element) constants.item(i);
            if (constant.hasAttribute("name") && constant.hasAttribute("value")) {
                settings.put(constant.getAttribute("name"), constant.getAttribute("value"));
            }
        }
    }

    private Document parse(Path file) {
        if (!Files.isRegularFile(file)) {
            return null;
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader("")));
            try (InputStream in = Files.newInputStream(file)) {
                return builder.parse(in);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private void readProperties(Path file) {
        if (!Files.isRegularFile(file)) {
            return;
        }
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(file)) {
            properties.load(in);
        } catch (IOException | IllegalArgumentException e) {
            return;
        }
        for (String name : properties.stringPropertyNames()) {
            settings.put(name, properties.getProperty(name));
        }
    }
}
