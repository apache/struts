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

import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public final class ResourcesPluginSettings {

    private static final String RESOURCES_PLUGIN_KEY = "org.apache.maven.plugins:maven-resources-plugin";
    private static final String RESOURCES_EXECUTION_ID = "default-resources";

    private final String encoding;
    private final String propertiesEncoding;
    private final LinkedHashSet<String> delimiters;
    private final boolean useDefaultDelimiters;
    private final String escapeString;
    private final List<String> nonFilteredFileExtensions;
    private final boolean addDefaultExcludes;

    private ResourcesPluginSettings(String encoding, String propertiesEncoding, LinkedHashSet<String> delimiters,
                                    boolean useDefaultDelimiters, String escapeString,
                                    List<String> nonFilteredFileExtensions, boolean addDefaultExcludes) {
        this.encoding = encoding;
        this.propertiesEncoding = propertiesEncoding;
        this.delimiters = delimiters;
        this.useDefaultDelimiters = useDefaultDelimiters;
        this.escapeString = escapeString;
        this.nonFilteredFileExtensions = Collections.unmodifiableList(nonFilteredFileExtensions);
        this.addDefaultExcludes = addDefaultExcludes;
    }

    public static ResourcesPluginSettings from(Xpp3Dom configuration, String projectEncoding) {
        return new ResourcesPluginSettings(
                text(configuration, "encoding", projectEncoding),
                text(configuration, "propertiesEncoding", null),
                new LinkedHashSet<>(texts(configuration, "delimiters")),
                flag(configuration, "useDefaultDelimiters"),
                text(configuration, "escapeString", null),
                texts(configuration, "nonFilteredFileExtensions"),
                flag(configuration, "addDefaultExcludes"));
    }

    public static Xpp3Dom effectiveResourcesConfiguration(MavenProject project) {
        Plugin plugin = project.getBuild().getPluginsAsMap().get(RESOURCES_PLUGIN_KEY);
        if (plugin == null) {
            return null;
        }
        Xpp3Dom pluginConfiguration = (Xpp3Dom) plugin.getConfiguration();
        PluginExecution execution = plugin.getExecutionsAsMap().get(RESOURCES_EXECUTION_ID);
        Xpp3Dom executionConfiguration = execution == null ? null : (Xpp3Dom) execution.getConfiguration();
        if (executionConfiguration == null) {
            return pluginConfiguration;
        }
        return Xpp3Dom.mergeXpp3Dom(new Xpp3Dom(executionConfiguration), pluginConfiguration);
    }

    private static String text(Xpp3Dom configuration, String name, String fallback) {
        Xpp3Dom child = configuration == null ? null : configuration.getChild(name);
        return child == null || child.getValue() == null ? fallback : child.getValue();
    }

    private static boolean flag(Xpp3Dom configuration, String name) {
        return Boolean.parseBoolean(text(configuration, name, "true"));
    }

    private static List<String> texts(Xpp3Dom configuration, String name) {
        List<String> values = new ArrayList<>();
        Xpp3Dom parent = configuration == null ? null : configuration.getChild(name);
        if (parent != null) {
            for (Xpp3Dom child : parent.getChildren()) {
                if (child.getValue() != null) {
                    values.add(child.getValue());
                }
            }
        }
        return values;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getPropertiesEncoding() {
        return propertiesEncoding;
    }

    public LinkedHashSet<String> getDelimiters() {
        return new LinkedHashSet<>(delimiters);
    }

    public boolean isUseDefaultDelimiters() {
        return useDefaultDelimiters;
    }

    public String getEscapeString() {
        return escapeString;
    }

    public List<String> getNonFilteredFileExtensions() {
        return nonFilteredFileExtensions;
    }

    public boolean isAddDefaultExcludes() {
        return addDefaultExcludes;
    }
}
