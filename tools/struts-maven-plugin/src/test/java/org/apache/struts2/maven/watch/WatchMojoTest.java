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
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WatchMojoTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void backgroundWithoutEnabledDoesNotRun() {
        WatchMojo mojo = new WatchMojo();
        mojo.background = true;
        mojo.enabled = false;
        assertThat(mojo.shouldRun()).isFalse();
    }

    @Test
    public void backgroundWithEnabledRuns() {
        WatchMojo mojo = new WatchMojo();
        mojo.background = true;
        mojo.enabled = true;
        assertThat(mojo.shouldRun()).isTrue();
    }

    @Test
    public void blockingAlwaysRuns() {
        WatchMojo mojo = new WatchMojo();
        mojo.background = false;
        mojo.enabled = false;
        assertThat(mojo.shouldRun()).isTrue();
    }

    @Test
    public void multiModuleSessionFailsWithGuidance() {
        WatchMojo mojo = new WatchMojo();
        MavenSession session = mock(MavenSession.class);
        when(session.getProjects()).thenReturn(List.of(new MavenProject(), new MavenProject()));
        mojo.session = session;
        assertThatThrownBy(mojo::checkSingleModule)
                .isInstanceOf(MojoFailureException.class)
                .hasMessageContaining("-pl <module>");
    }

    @Test
    public void staleOutputsArePushedAtStart() throws Exception {
        Path src = temp.newFolder("src").toPath();
        Path out = temp.newFolder("out").toPath();
        File stale = write(src.resolve("stale.txt"), 2000);
        File fresh = write(src.resolve("fresh.txt"), 1000);
        File missing = write(src.resolve("missing.txt"), 1000);
        write(out.resolve("stale.txt"), 1000);
        write(out.resolve("fresh.txt"), 2000);

        WatchedRoot root = new WatchedRoot(src, List.of(), List.of(), null, false);
        Map<SourceFile, FileStamp> scan = new LinkedHashMap<>();
        for (File f : List.of(stale, fresh, missing)) {
            scan.put(new SourceFile(root, f.getName()), new FileStamp(f.lastModified(), f.length()));
        }
        List<String> pushed = new ArrayList<>();
        ResourcePusher pusher = new ResourcePusher() {
            public void push(SourceFile file) {
                pushed.add(file.relativePath());
            }

            public void delete(SourceFile file) {
            }
        };

        WatchMojo.pushStaleOutputs(scan, file -> out.resolve(file.relativePath()), pusher, (m, e) -> { });

        assertThat(pushed).containsExactlyInAnyOrder("stale.txt", "missing.txt");
    }

    @Test
    public void stalePushFailureIsReportedAndRemainingFilesArePushed() throws Exception {
        Path src = temp.newFolder("src2").toPath();
        Path out = temp.newFolder("out2").toPath();
        WatchedRoot root = new WatchedRoot(src, List.of(), List.of(), null, false);
        Map<SourceFile, FileStamp> scan = new LinkedHashMap<>();
        for (String name : List.of("bad.txt", "good.txt")) {
            File f = write(src.resolve(name), 1000);
            scan.put(new SourceFile(root, name), new FileStamp(f.lastModified(), f.length()));
        }
        List<String> pushed = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        ResourcePusher pusher = new ResourcePusher() {
            public void push(SourceFile file) throws java.io.IOException {
                if (file.relativePath().equals("bad.txt")) {
                    throw new java.io.IOException("locked");
                }
                pushed.add(file.relativePath());
            }

            public void delete(SourceFile file) {
            }
        };

        WatchMojo.pushStaleOutputs(scan, file -> out.resolve(file.relativePath()), pusher,
                (message, e) -> errors.add(message));

        assertThat(pushed).containsExactly("good.txt");
        assertThat(errors).containsExactly("Failed to push bad.txt");
    }

    @Test
    public void secondBackgroundWatcherForTheSameOutputDirectoryIsRefused() throws Exception {
        Path out = temp.newFolder("claimed").toPath();
        Path other = temp.newFolder("other").toPath();
        try {
            assertThat(WatchMojo.claimBackgroundWatcher(out)).isTrue();
            assertThat(WatchMojo.claimBackgroundWatcher(out.resolve("x").resolve(".."))).isFalse();
            assertThat(WatchMojo.claimBackgroundWatcher(other)).isTrue();

            WatchMojo.releaseBackgroundWatcher(out);

            assertThat(WatchMojo.claimBackgroundWatcher(out)).isTrue();
        } finally {
            WatchMojo.releaseBackgroundWatcher(out);
            WatchMojo.releaseBackgroundWatcher(other);
        }
    }

    @Test
    public void boundExecutionReturnsWhenAWatcherAlreadyRunsForTheOutputDirectory() throws Exception {
        Path out = temp.newFolder("running").toPath();
        WatchMojo mojo = new WatchMojo();
        mojo.background = true;
        mojo.enabled = true;
        MavenSession session = mock(MavenSession.class);
        when(session.getProjects()).thenReturn(List.of(new MavenProject()));
        mojo.session = session;
        mojo.project = new MavenProject();
        mojo.project.getBuild().setOutputDirectory(out.toString());
        Log log = mock(Log.class);
        mojo.setLog(log);
        WatchMojo.claimBackgroundWatcher(out);
        try {
            mojo.execute();
        } finally {
            WatchMojo.releaseBackgroundWatcher(out);
        }

        ArgumentCaptor<CharSequence> info = ArgumentCaptor.forClass(CharSequence.class);
        verify(log).info(info.capture());
        assertThat(info.getValue().toString()).contains("already watching");
        verify(log, never()).warn(any(CharSequence.class));
    }

    @Test
    public void backgroundTaskReportsEscapingThrowableAndReleasesItsClaim() throws Exception {
        Path out = temp.newFolder("dying").toPath();
        List<Throwable> reported = new ArrayList<>();
        Error failure = new LinkageError("boom");
        WatchMojo.claimBackgroundWatcher(out);

        WatchMojo.backgroundTask(() -> {
            throw failure;
        }, out, (message, e) -> reported.add(e)).run();

        assertThat(reported).containsExactly(failure);
        try {
            assertThat(WatchMojo.claimBackgroundWatcher(out)).isTrue();
        } finally {
            WatchMojo.releaseBackgroundWatcher(out);
        }
    }

    private File write(Path path, long lastModified) throws Exception {
        Files.writeString(path, "x");
        File file = path.toFile();
        assertThat(file.setLastModified(lastModified)).isTrue();
        return file;
    }
}
