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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class ResourceWatcher {

    private final ResourceScanner scanner;
    private final ResourcePusher pusher;
    private final List<Path> filterFiles;
    private final Consumer<String> info;
    private final BiConsumer<String, Exception> error;

    private Map<SourceFile, FileStamp> previous = Map.of();
    private Map<SourceFile, FileStamp> acted = Map.of();
    private Map<Path, FileStamp> previousFilterStamps = Map.of();
    private Map<Path, FileStamp> actedFilterStamps = Map.of();

    public ResourceWatcher(ResourceScanner scanner, ResourcePusher pusher, List<Path> filterFiles,
                           Consumer<String> info, BiConsumer<String, Exception> error) {
        this.scanner = scanner;
        this.pusher = pusher;
        this.filterFiles = List.copyOf(filterFiles);
        this.info = info;
        this.error = error;
    }

    public void start() {
        previous = scanner.scan();
        acted = previous;
        previousFilterStamps = filterStamps();
        actedFilterStamps = previousFilterStamps;
    }

    public void pollOnce() {
        Map<SourceFile, FileStamp> current;
        try {
            current = scanner.scan();
        } catch (RuntimeException e) {
            error.accept("Failed to scan resources", e);
            return;
        }
        Map<Path, FileStamp> currentFilterStamps = filterStamps();
        Set<SourceFile> handled = new HashSet<>();

        Set<SourceFile> paths = new HashSet<>(current.keySet());
        paths.addAll(previous.keySet());
        paths.addAll(acted.keySet());
        Map<SourceFile, FileStamp> nextActed = new HashMap<>(acted);
        for (SourceFile file : paths) {
            FileStamp now = current.get(file);
            if (Objects.equals(now, previous.get(file)) && !Objects.equals(now, acted.get(file))) {
                if (now == null) {
                    nextActed.remove(file);
                    delete(file);
                } else {
                    nextActed.put(file, now);
                    push(file);
                    handled.add(file);
                }
            }
        }

        if (currentFilterStamps.equals(previousFilterStamps) && !currentFilterStamps.equals(actedFilterStamps)) {
            actedFilterStamps = currentFilterStamps;
            for (SourceFile file : current.keySet()) {
                if (file.root().filtering() && !handled.contains(file)) {
                    push(file);
                }
            }
        }

        acted = nextActed;
        previous = current;
        previousFilterStamps = currentFilterStamps;
    }

    public void run(Duration interval, BooleanSupplier keepRunning) {
        while (keepRunning.getAsBoolean()) {
            pollOnce();
            try {
                Thread.sleep(interval.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void push(SourceFile file) {
        try {
            pusher.push(file);
            info.accept("Pushed " + file.relativePath());
        } catch (IOException | RuntimeException e) {
            error.accept("Failed to push " + file.relativePath(), e);
        }
    }

    private void delete(SourceFile file) {
        try {
            pusher.delete(file);
            info.accept("Deleted " + file.relativePath());
        } catch (IOException | RuntimeException e) {
            error.accept("Failed to delete " + file.relativePath(), e);
        }
    }

    private Map<Path, FileStamp> filterStamps() {
        Map<Path, FileStamp> stamps = new HashMap<>();
        for (Path path : filterFiles) {
            File file = path.toFile();
            if (file.isFile()) {
                stamps.put(path, new FileStamp(file.lastModified(), file.length()));
            }
        }
        return stamps;
    }
}
