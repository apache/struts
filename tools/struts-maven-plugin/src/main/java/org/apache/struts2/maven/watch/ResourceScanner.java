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

import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResourceScanner {

    private final List<WatchedRoot> roots;
    private final boolean addDefaultExcludes;

    public ResourceScanner(List<WatchedRoot> roots, boolean addDefaultExcludes) {
        this.roots = List.copyOf(roots);
        this.addDefaultExcludes = addDefaultExcludes;
    }

    public Map<SourceFile, FileStamp> scan() {
        Map<SourceFile, FileStamp> result = new LinkedHashMap<>();
        for (WatchedRoot root : roots) {
            File directory = root.directory().toFile();
            if (!directory.isDirectory()) {
                continue;
            }
            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir(directory);
            scanner.setIncludes(root.includes().isEmpty() ? new String[]{"**/**"}
                    : root.includes().toArray(new String[0]));
            scanner.setExcludes(root.excludes().toArray(new String[0]));
            if (addDefaultExcludes) {
                scanner.addDefaultExcludes();
            }
            scanner.scan();
            for (String relativePath : scanner.getIncludedFiles()) {
                File file = new File(directory, relativePath);
                result.put(new SourceFile(root, relativePath), new FileStamp(file.lastModified(), file.length()));
            }
        }
        return result;
    }
}
