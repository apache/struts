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

import java.nio.file.Path;
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
    private final List<String> filters;
    private final boolean useBuildFilters;
    private final boolean escapeWindowsPaths;
    private final boolean supportMultiLineFiltering;
    private final boolean fileNameFiltering;
    private final String outputDirectory;
    private final boolean skip;

    private ResourcesPluginSettings(Xpp3Dom configuration, String projectEncoding) {
        this.encoding = text(configuration, "encoding", projectEncoding);
        this.propertiesEncoding = text(configuration, "propertiesEncoding", null);
        this.delimiters = new LinkedHashSet<>(texts(configuration, "delimiters"));
        this.useDefaultDelimiters = flag(configuration, "useDefaultDelimiters", "true");
        this.escapeString = text(configuration, "escapeString", null);
        this.nonFilteredFileExtensions = Collections.unmodifiableList(texts(configuration, "nonFilteredFileExtensions"));
        this.addDefaultExcludes = flag(configuration, "addDefaultExcludes", "true");
        this.filters = Collections.unmodifiableList(texts(configuration, "filters"));
        this.useBuildFilters = flag(configuration, "useBuildFilters", "true");
        this.escapeWindowsPaths = flag(configuration, "escapeWindowsPaths", "true");
        this.supportMultiLineFiltering = flag(configuration, "supportMultiLineFiltering", "false");
        this.fileNameFiltering = flag(configuration, "fileNameFiltering", "false");
        this.outputDirectory = text(configuration, "outputDirectory", null);
        this.skip = flag(configuration, "skip", "false");
    }

    public static ResourcesPluginSettings from(Xpp3Dom configuration, String projectEncoding) {
        return new ResourcesPluginSettings(configuration, projectEncoding);
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

    private static boolean flag(Xpp3Dom configuration, String name, String defaultValue) {
        return Boolean.parseBoolean(text(configuration, name, defaultValue));
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

    public List<Path> filterFiles(List<String> buildFilters, Path basedir) {
        List<String> combined = new ArrayList<>();
        if (useBuildFilters) {
            combined.addAll(buildFilters);
        }
        combined.addAll(filters);
        return combined.stream().map(basedir::resolve).toList();
    }

    public boolean isEscapeWindowsPaths() {
        return escapeWindowsPaths;
    }

    public boolean isSupportMultiLineFiltering() {
        return supportMultiLineFiltering;
    }

    public boolean isFileNameFiltering() {
        return fileNameFiltering;
    }

    public List<String> warnings() {
        List<String> warnings = new ArrayList<>();
        if (outputDirectory != null) {
            warnings.add("maven-resources-plugin <outputDirectory> " + outputDirectory
                    + " is ignored; struts:watch writes to the project's build output directory");
        }
        if (skip) {
            warnings.add("maven-resources-plugin <skip> is true; struts:watch pushes resources regardless");
        }
        return warnings;
    }
}
