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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceScannerTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private WatchedRoot root(Path dir, List<String> includes, List<String> excludes) {
        return new WatchedRoot(dir, includes, excludes, null, false);
    }

    private Set<String> paths(Map<SourceFile, FileStamp> scan) {
        return scan.keySet().stream().map(SourceFile::relativePath)
                .map(p -> p.replace(File.separatorChar, '/')).collect(Collectors.toSet());
    }

    @Test
    public void listsFilesHonouringIncludesAndExcludes() throws Exception {
        Path dir = temp.newFolder("res").toPath();
        Files.createDirectories(dir.resolve("sub"));
        Files.writeString(dir.resolve("a.properties"), "a=1");
        Files.writeString(dir.resolve("sub/b.properties"), "b=1");
        Files.writeString(dir.resolve("c.txt"), "c");
        Files.writeString(dir.resolve("sub/skip.properties"), "s");

        ResourceScanner scanner = new ResourceScanner(
                List.of(root(dir, List.of("**/*.properties"), List.of("**/skip.*"))), true);

        Map<SourceFile, FileStamp> scan = scanner.scan();

        assertThat(paths(scan)).containsExactlyInAnyOrder("a.properties", "sub/b.properties");
        assertThat(scan.get(new SourceFile(root(dir, List.of("**/*.properties"), List.of("**/skip.*")), "a.properties")).size())
                .isEqualTo(3);
    }

    @Test
    public void emptyIncludesMeansEverything() throws Exception {
        Path dir = temp.newFolder("res").toPath();
        Files.writeString(dir.resolve("a.txt"), "a");
        Files.writeString(dir.resolve("b.xml"), "b");

        ResourceScanner scanner = new ResourceScanner(List.of(root(dir, List.of(), List.of())), true);

        assertThat(paths(scanner.scan())).containsExactlyInAnyOrder("a.txt", "b.xml");
    }

    @Test
    public void skipsDefaultExcludesWhenEnabled() throws Exception {
        Path dir = temp.newFolder("res").toPath();
        Files.writeString(dir.resolve("a.txt"), "a");
        Files.writeString(dir.resolve(".DS_Store"), "x");
        Files.writeString(dir.resolve("a.txt~"), "x");

        assertThat(paths(new ResourceScanner(List.of(root(dir, List.of(), List.of())), true).scan()))
                .containsExactly("a.txt");
        assertThat(paths(new ResourceScanner(List.of(root(dir, List.of(), List.of())), false).scan()))
                .contains("a.txt", ".DS_Store", "a.txt~");
    }

    @Test
    public void missingRootDirectoryYieldsNoFiles() {
        Path dir = temp.getRoot().toPath().resolve("absent");

        assertThat(new ResourceScanner(List.of(root(dir, List.of(), List.of())), true).scan()).isEmpty();
    }

    @Test
    public void overlappingRootsAreListedInRootOrder() throws Exception {
        Path dir = temp.newFolder("res").toPath();
        Files.writeString(dir.resolve("a.txt"), "a");
        WatchedRoot first = new WatchedRoot(dir, List.of(), List.of(), "first", false);
        WatchedRoot second = new WatchedRoot(dir, List.of(), List.of(), "second", false);

        Map<SourceFile, FileStamp> scan = new ResourceScanner(List.of(first, second), true).scan();

        assertThat(scan.keySet()).containsExactly(new SourceFile(first, "a.txt"), new SourceFile(second, "a.txt"));
    }
}
