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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

@Mojo(name = "watch", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, threadSafe = true)
public class WatchMojo extends AbstractMojo {

    static final String MULTI_MODULE_MESSAGE =
            "struts:watch supports a single module; run it in the module that owns the resources, e.g. -pl <module>";

    private static final Set<Path> BACKGROUND_WATCHERS = ConcurrentHashMap.newKeySet();

    /**
     * Milliseconds between scans of the resource directories.
     */
    @Parameter(property = "struts.watch.pollInterval", defaultValue = "500")
    long pollInterval = 500;

    /**
     * Run the watcher on a daemon thread and return, so it can be bound to a lifecycle phase.
     * Requires struts.watch=true to do anything.
     */
    @Parameter(property = "struts.watch.background", defaultValue = "false")
    boolean background;

    /**
     * Switches background watching on. Consulted only when background is true.
     */
    @Parameter(property = "struts.watch", defaultValue = "false")
    boolean enabled;

    /**
     * Seconds after which a blocking run stops; 0 means run until interrupted.
     */
    @Parameter(property = "struts.watch.timeout", defaultValue = "0")
    long timeout;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    MavenSession session;

    @Inject
    MavenResourcesFiltering resourcesFiltering;

    @Override
    public void execute() throws MojoFailureException {
        if (!shouldRun()) {
            getLog().debug("struts:watch is in background mode and struts.watch is not true; not watching");
            return;
        }
        checkSingleModule();

        Path outputDirectory = new File(project.getBuild().getOutputDirectory()).toPath();
        if (background) {
            watchInBackground(outputDirectory);
            return;
        }
        ResourceWatcher watcher = prepareWatcher(outputDirectory);
        getLog().info("Watching " + project.getResources().size() + " resource root(s); press Ctrl+C to stop");
        long deadline = timeout > 0 ? System.nanoTime() + Duration.ofSeconds(timeout).toNanos() : 0;
        BooleanSupplier keepRunning = () -> deadline == 0 || System.nanoTime() - deadline < 0;
        watcher.run(Duration.ofMillis(pollInterval), keepRunning);
    }

    private void watchInBackground(Path outputDirectory) {
        if (!claimBackgroundWatcher(outputDirectory)) {
            getLog().info("struts:watch is already watching " + outputDirectory + " in this JVM; not starting another");
            return;
        }
        try {
            ResourceWatcher watcher = prepareWatcher(outputDirectory);
            Duration interval = Duration.ofMillis(pollInterval);
            Runnable task = backgroundTask(() -> watcher.run(interval, () -> true), outputDirectory,
                    (message, e) -> getLog().error(message, e));
            Thread thread = new Thread(task, "struts-watch");
            thread.setDaemon(true);
            thread.start();
        } catch (RuntimeException | Error e) {
            releaseBackgroundWatcher(outputDirectory);
            throw e;
        }
        getLog().info("Watching " + project.getResources().size() + " resource root(s) in the background");
    }

    private ResourceWatcher prepareWatcher(Path outputDirectory) {
        Path basedir = project.getBasedir().toPath();
        for (String warning : new StrutsReloadInspector(outputDirectory).warnings()) {
            getLog().warn(warning);
        }

        ResourcesPluginSettings settings = ResourcesPluginSettings.from(
                ResourcesPluginSettings.effectiveResourcesConfiguration(project),
                project.getProperties().getProperty("project.build.sourceEncoding"));
        for (String warning : settings.warnings()) {
            getLog().warn(warning);
        }
        List<Path> filterFiles = settings.filterFiles(project.getBuild().getFilters(), basedir);
        List<WatchedRoot> roots = project.getResources().stream()
                .map(resource -> WatchedRoot.of(resource, basedir))
                .toList();
        ResourceScanner scanner = new ResourceScanner(roots, settings.isAddDefaultExcludes());
        FilteringResourcePusher filteringPusher =
                new FilteringResourcePusher(resourcesFiltering, project, session, settings, outputDirectory,
                        filterFiles);
        ResourcePusher pusher = loggingPusher(filteringPusher, basedir);

        pushStaleOutputs(scanner.scan(), filteringPusher::outputPathOf, pusher,
                (message, e) -> getLog().error(message, e));

        ResourceWatcher watcher = new ResourceWatcher(scanner, pusher, filterFiles,
                message -> getLog().debug(message), (message, e) -> getLog().error(message, e));
        watcher.start();
        return watcher;
    }

    static boolean claimBackgroundWatcher(Path outputDirectory) {
        return BACKGROUND_WATCHERS.add(registryKey(outputDirectory));
    }

    static void releaseBackgroundWatcher(Path outputDirectory) {
        BACKGROUND_WATCHERS.remove(registryKey(outputDirectory));
    }

    private static Path registryKey(Path outputDirectory) {
        return outputDirectory.toAbsolutePath().normalize();
    }

    static Runnable backgroundTask(Runnable work, Path outputDirectory, BiConsumer<String, Throwable> error) {
        return () -> {
            try {
                work.run();
            } catch (Throwable e) {
                error.accept("struts:watch background watcher for " + outputDirectory + " stopped", e);
            } finally {
                releaseBackgroundWatcher(outputDirectory);
            }
        };
    }

    boolean shouldRun() {
        return !background || enabled;
    }

    void checkSingleModule() throws MojoFailureException {
        if (session.getProjects().size() > 1) {
            throw new MojoFailureException(MULTI_MODULE_MESSAGE);
        }
    }

    static void pushStaleOutputs(Map<SourceFile, FileStamp> scan, Function<SourceFile, Path> outputPathOf,
                                 ResourcePusher pusher, BiConsumer<String, Exception> error) {
        for (Map.Entry<SourceFile, FileStamp> entry : scan.entrySet()) {
            File output = outputPathOf.apply(entry.getKey()).toFile();
            if (!output.isFile() || output.lastModified() < entry.getValue().lastModified()) {
                try {
                    pusher.push(entry.getKey());
                } catch (IOException | RuntimeException e) {
                    error.accept("Failed to push " + entry.getKey().relativePath(), e);
                }
            }
        }
    }

    private ResourcePusher loggingPusher(FilteringResourcePusher delegate, Path basedir) {
        return new ResourcePusher() {
            @Override
            public void push(SourceFile file) throws IOException {
                delegate.push(file);
                getLog().info("pushed " + file.relativePath() + " -> "
                        + basedir.relativize(delegate.outputPathOf(file)));
            }

            @Override
            public void delete(SourceFile file) throws IOException {
                delegate.delete(file);
                getLog().info("deleted " + file.relativePath());
            }
        };
    }
}
