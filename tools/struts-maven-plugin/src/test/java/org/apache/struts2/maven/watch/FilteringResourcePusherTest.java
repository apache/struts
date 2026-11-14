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

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.DefaultMavenFileFilter;
import org.apache.maven.shared.filtering.DefaultMavenResourcesFiltering;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonatype.plexus.build.incremental.ThreadBuildContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FilteringResourcePusherTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private Path source;
    private Path output;
    private MavenProject project;

    @Before
    public void setUp() throws IOException {
        source = temp.newFolder("src").toPath();
        output = temp.getRoot().toPath().resolve("classes");
        project = new MavenProject();
        project.getProperties().setProperty("greeting", "hi");
    }

    private FilteringResourcePusher pusher(Xpp3Dom configuration) {
        DefaultMavenResourcesFiltering filtering =
                new DefaultMavenResourcesFiltering(new DefaultMavenFileFilter(new ThreadBuildContext()), new ThreadBuildContext());
        MavenSession session = new MavenSession(null, new DefaultMavenExecutionRequest(), new DefaultMavenExecutionResult(), project);
        return new FilteringResourcePusher(filtering, project, session,
                ResourcesPluginSettings.from(configuration, "UTF-8"), output);
    }

    private SourceFile write(String relativePath, byte[] content, boolean filtering, String targetPath) throws IOException {
        Path file = source.resolve(relativePath);
        Files.createDirectories(file.getParent());
        Files.write(file, content);
        return new SourceFile(new WatchedRoot(source, List.of(), List.of(), targetPath, filtering), relativePath);
    }

    private SourceFile write(String relativePath, String content, boolean filtering) throws IOException {
        return write(relativePath, content.getBytes(StandardCharsets.UTF_8), filtering, null);
    }

    @Test
    public void pushCopiesUnfilteredFile() throws IOException {
        byte[] content = "name=${greeting}\n".getBytes(StandardCharsets.UTF_8);
        SourceFile file = write("a.properties", content, false, null);

        pusher(null).push(file);

        assertThat(output.resolve("a.properties")).hasBinaryContent(content);
    }

    @Test
    public void pushFiltersPlaceholdersFromProjectProperties() throws IOException {
        SourceFile file = write("a.properties", "name=${greeting}\n", true);

        pusher(null).push(file);

        assertThat(output.resolve("a.properties")).hasContent("name=hi");
    }

    @Test
    public void pushHonoursCustomDelimiter() throws IOException {
        Xpp3Dom configuration = new Xpp3Dom("configuration");
        Xpp3Dom delimiters = new Xpp3Dom("delimiters");
        Xpp3Dom delimiter = new Xpp3Dom("delimiter");
        delimiter.setValue("@");
        delimiters.addChild(delimiter);
        configuration.addChild(delimiters);
        Xpp3Dom useDefault = new Xpp3Dom("useDefaultDelimiters");
        useDefault.setValue("false");
        configuration.addChild(useDefault);
        SourceFile file = write("a.properties", "name=@greeting@ other=${greeting}\n", true);

        pusher(configuration).push(file);

        assertThat(output.resolve("a.properties")).hasContent("name=hi other=${greeting}");
    }

    @Test
    public void pushHonoursTargetPath() throws IOException {
        SourceFile file = write("a.xml", "<a>${greeting}</a>".getBytes(StandardCharsets.UTF_8), true, "conf");

        pusher(null).push(file);

        assertThat(output.resolve("conf/a.xml")).hasContent("<a>hi</a>");
        assertThat(pusher(null).outputPathOf(file)).isEqualTo(output.resolve("conf/a.xml"));
    }

    @Test
    public void pushCreatesMissingParentDirectories() throws IOException {
        SourceFile file = write("com/acme/FooAction.properties", "k=v\n", false);

        pusher(null).push(file);

        assertThat(output.resolve("com/acme/FooAction.properties")).exists();
    }

    @Test
    public void pushAfterOutputDirectoryDeletedRecreatesIt() throws IOException {
        SourceFile file = write("a.properties", "k=v\n", false);
        FilteringResourcePusher pusher = pusher(null);
        pusher.push(file);
        Files.delete(output.resolve("a.properties"));
        Files.delete(output);

        pusher.push(file);

        assertThat(output.resolve("a.properties")).exists();
    }

    @Test
    public void binaryFileInFilteredResourceIsCopiedUnchanged() throws IOException {
        Xpp3Dom configuration = new Xpp3Dom("configuration");
        Xpp3Dom extensions = new Xpp3Dom("nonFilteredFileExtensions");
        Xpp3Dom extension = new Xpp3Dom("nonFilteredFileExtension");
        extension.setValue("png");
        extensions.addChild(extension);
        configuration.addChild(extensions);
        byte[] png = {(byte) 0x89, 'P', 'N', 'G', '$', '{', 'g', 'r', 'e', 'e', 't', 'i', 'n', 'g', '}', (byte) 0xff};
        SourceFile file = write("logo.png", png, true, null);

        pusher(configuration).push(file);

        assertThat(output.resolve("logo.png")).hasBinaryContent(png);
    }

    @Test
    public void deleteRemovesOutputFile() throws IOException {
        SourceFile file = write("a.properties", "k=v\n", false);
        FilteringResourcePusher pusher = pusher(null);
        pusher.push(file);

        pusher.delete(file);

        assertThat(output.resolve("a.properties")).doesNotExist();
    }

    @Test
    public void deleteOfMissingOutputIsANoOp() throws IOException {
        SourceFile file = write("a.properties", "k=v\n", false);

        pusher(null).delete(file);

        assertThat(output.resolve("a.properties")).doesNotExist();
    }
}
