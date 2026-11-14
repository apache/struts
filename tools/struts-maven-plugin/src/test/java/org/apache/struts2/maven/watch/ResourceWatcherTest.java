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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceWatcherTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private final WatchedRoot plain = new WatchedRoot(Path.of("plain"), List.of(), List.of(), null, false);
    private final WatchedRoot filtered = new WatchedRoot(Path.of("filtered"), List.of(), List.of(), null, true);

    private final List<String> pushed = new ArrayList<>();
    private final List<String> deleted = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();
    private final List<String> failOn = new ArrayList<>();

    private final ResourcePusher pusher = new ResourcePusher() {
        @Override
        public void push(SourceFile file) throws IOException {
            if (failOn.contains(file.relativePath())) {
                throw new IOException("boom " + file.relativePath());
            }
            pushed.add(file.relativePath());
        }

        @Override
        public void delete(SourceFile file) {
            deleted.add(file.relativePath());
        }
    };

    private static class FakeScanner extends ResourceScanner {
        private final Deque<Map<SourceFile, FileStamp>> scans = new ArrayDeque<>();

        FakeScanner() {
            super(List.of(), true);
        }

        FakeScanner add(Map<SourceFile, FileStamp> scan) {
            scans.add(scan);
            return this;
        }

        @Override
        public Map<SourceFile, FileStamp> scan() {
            return scans.size() > 1 ? scans.poll() : scans.peek();
        }
    }

    private Map<SourceFile, FileStamp> scan(WatchedRoot root, Object... pathAndStamp) {
        Map<SourceFile, FileStamp> result = new LinkedHashMap<>();
        for (int i = 0; i < pathAndStamp.length; i += 2) {
            result.put(new SourceFile(root, (String) pathAndStamp[i]),
                    new FileStamp(((Number) pathAndStamp[i + 1]).longValue(), 1));
        }
        return result;
    }

    private ResourceWatcher watcher(FakeScanner scanner, List<Path> filterFiles) {
        return new ResourceWatcher(scanner, pusher, filterFiles, m -> { }, (m, e) -> errors.add(m));
    }

    private void drive(ResourceWatcher watcher, int polls) {
        watcher.start();
        for (int i = 0; i < polls; i++) {
            watcher.pollOnce();
        }
    }

    @Test
    public void changeIsPushedOnceAfterTwoStableScans() {
        FakeScanner scanner = new FakeScanner().add(scan(plain, "a", 1)).add(scan(plain, "a", 2));
        drive(watcher(scanner, List.of()), 3);

        assertThat(pushed).containsExactly("a");
        assertThat(deleted).isEmpty();
    }

    @Test
    public void changeIsNotPushedAfterOnlyOneScan() {
        FakeScanner scanner = new FakeScanner().add(scan(plain, "a", 1)).add(scan(plain, "a", 2));
        drive(watcher(scanner, List.of()), 1);

        assertThat(pushed).isEmpty();
    }

    @Test
    public void deletionIsMirroredAfterTwoStableScans() {
        FakeScanner scanner = new FakeScanner().add(scan(plain, "a", 1)).add(scan(plain));
        drive(watcher(scanner, List.of()), 2);

        assertThat(deleted).containsExactly("a");
        assertThat(pushed).isEmpty();
    }

    @Test
    public void deleteFollowedByRecreateBeforeStableIsAPushNotADelete() {
        FakeScanner scanner = new FakeScanner().add(scan(plain, "a", 1)).add(scan(plain))
                .add(scan(plain, "a", 2));
        drive(watcher(scanner, List.of()), 3);

        assertThat(deleted).isEmpty();
        assertThat(pushed).containsExactly("a");
    }

    @Test
    public void unchangedFilesAreNeverPushed() {
        FakeScanner scanner = new FakeScanner().add(scan(plain, "a", 1));
        drive(watcher(scanner, List.of()), 3);

        assertThat(pushed).isEmpty();
        assertThat(deleted).isEmpty();
    }

    @Test
    public void filterFileChangeRepushesFilteredResourcesOnly() throws Exception {
        Path filterFile = temp.newFile("filter.properties").toPath();
        Files.writeString(filterFile, "k=v");
        Map<SourceFile, FileStamp> files = scan(filtered, "f", 1);
        files.putAll(scan(plain, "u", 1));
        FakeScanner scanner = new FakeScanner().add(files);
        ResourceWatcher watcher = watcher(scanner, List.of(filterFile));
        watcher.start();

        Files.writeString(filterFile, "k=changed-longer");
        watcher.pollOnce();
        assertThat(pushed).isEmpty();
        watcher.pollOnce();

        assertThat(pushed).containsExactly("f");
        watcher.pollOnce();
        assertThat(pushed).containsExactly("f");
    }

    @Test
    public void pusherFailureIsReportedAndWatchingContinues() {
        failOn.add("a");
        FakeScanner scanner = new FakeScanner().add(scan(plain, "a", 1, "b", 1))
                .add(scan(plain, "a", 2, "b", 1)).add(scan(plain, "a", 2, "b", 2))
                .add(scan(plain, "a", 2, "b", 2));
        drive(watcher(scanner, List.of()), 3);

        assertThat(errors).hasSize(1);
        assertThat(errors.get(0)).contains("a");
        assertThat(pushed).containsExactly("b");
    }

    @Test
    public void runStopsWhenKeepRunningTurnsFalse() {
        FakeScanner scanner = new FakeScanner().add(scan(plain, "a", 1));
        ResourceWatcher watcher = watcher(scanner, List.of());
        AtomicInteger remaining = new AtomicInteger(3);
        watcher.start();

        watcher.run(Duration.ofMillis(10), () -> remaining.decrementAndGet() >= 0);

        assertThat(remaining.get()).isNegative();
    }
}
