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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class FilteringResourcePusher implements ResourcePusher {

    private final MavenResourcesFiltering filtering;
    private final MavenProject project;
    private final MavenSession session;
    private final ResourcesPluginSettings settings;
    private final Path outputDirectory;

    public FilteringResourcePusher(MavenResourcesFiltering filtering, MavenProject project, MavenSession session,
                                   ResourcesPluginSettings settings, Path outputDirectory) {
        this.filtering = filtering;
        this.project = project;
        this.session = session;
        this.settings = settings;
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void push(SourceFile file) throws IOException {
        WatchedRoot root = file.root();
        Resource resource = new Resource();
        resource.setDirectory(root.directory().toString());
        resource.setIncludes(List.of(file.relativePath()));
        resource.setTargetPath(root.targetPath());
        resource.setFiltering(root.filtering());

        MavenResourcesExecution execution = new MavenResourcesExecution(
                List.of(resource), outputDirectory.toFile(), project, settings.getEncoding(),
                project.getBuild().getFilters(), List.of(), session);
        execution.setPropertiesEncoding(settings.getPropertiesEncoding());
        execution.setDelimiters(settings.getDelimiters(), settings.isUseDefaultDelimiters());
        execution.setEscapeString(settings.getEscapeString());
        execution.setNonFilteredFileExtensions(settings.getNonFilteredFileExtensions());
        execution.setAddDefaultExcludes(settings.isAddDefaultExcludes());
        execution.setOverwrite(true);
        try {
            filtering.filterResources(execution);
        } catch (MavenFilteringException e) {
            throw new IOException("Cannot filter " + file.relativePath(), e);
        }
    }

    @Override
    public void delete(SourceFile file) throws IOException {
        Files.deleteIfExists(outputPathOf(file));
    }

    public Path outputPathOf(SourceFile file) {
        String targetPath = file.root().targetPath();
        Path base = targetPath == null || targetPath.isEmpty() ? outputDirectory : outputDirectory.resolve(targetPath);
        return base.resolve(file.relativePath());
    }
}
