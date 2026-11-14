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
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourcesPluginSettingsTest {

    @Test
    public void defaultsWhenNoConfiguration() {
        ResourcesPluginSettings settings = ResourcesPluginSettings.from(null, "UTF-8");

        assertThat(settings.getEncoding()).isEqualTo("UTF-8");
        assertThat(settings.getDelimiters()).isEmpty();
        assertThat(settings.isUseDefaultDelimiters()).isTrue();
        assertThat(settings.isAddDefaultExcludes()).isTrue();
        assertThat(settings.getNonFilteredFileExtensions()).isEmpty();
        assertThat(settings.getEscapeString()).isNull();
    }

    @Test
    public void readsAllFilteringSettings() throws Exception {
        Xpp3Dom configuration = Xpp3DomBuilder.build(new StringReader(
                "<configuration><encoding>ISO-8859-1</encoding>"
                        + "<propertiesEncoding>UTF-8</propertiesEncoding>"
                        + "<delimiters><delimiter>@</delimiter></delimiters>"
                        + "<useDefaultDelimiters>false</useDefaultDelimiters>"
                        + "<escapeString>\\</escapeString>"
                        + "<nonFilteredFileExtensions><nonFilteredFileExtension>png</nonFilteredFileExtension></nonFilteredFileExtensions>"
                        + "<addDefaultExcludes>false</addDefaultExcludes></configuration>"));

        ResourcesPluginSettings settings = ResourcesPluginSettings.from(configuration, "UTF-8");

        assertThat(settings.getEncoding()).isEqualTo("ISO-8859-1");
        assertThat(settings.getPropertiesEncoding()).isEqualTo("UTF-8");
        assertThat(settings.getDelimiters()).containsExactly("@");
        assertThat(settings.isUseDefaultDelimiters()).isFalse();
        assertThat(settings.getEscapeString()).isEqualTo("\\");
        assertThat(settings.getNonFilteredFileExtensions()).isEqualTo(List.of("png"));
        assertThat(settings.isAddDefaultExcludes()).isFalse();
    }

    @Test
    public void executionConfigurationOverridesPluginConfiguration() throws Exception {
        Plugin plugin = new Plugin();
        plugin.setGroupId("org.apache.maven.plugins");
        plugin.setArtifactId("maven-resources-plugin");
        plugin.setConfiguration(Xpp3DomBuilder.build(new StringReader(
                "<configuration><encoding>A</encoding></configuration>")));
        PluginExecution execution = new PluginExecution();
        execution.setId("default-resources");
        execution.setConfiguration(Xpp3DomBuilder.build(new StringReader(
                "<configuration><encoding>B</encoding></configuration>")));
        plugin.addExecution(execution);
        MavenProject project = new MavenProject();
        project.getBuild().addPlugin(plugin);

        Xpp3Dom effective = ResourcesPluginSettings.effectiveResourcesConfiguration(project);

        assertThat(effective.getChild("encoding").getValue()).isEqualTo("B");
    }
}
