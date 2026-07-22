# WW-5413 In-memory Multipart Upload Optimization — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Stop eagerly writing small (in-memory) multipart uploads to a temporary file; keep them in memory and materialize a file only when a caller demands one, while `getContent()` still returns a `java.io.File` for full backward compatibility.

**Architecture:** Add a `default InputStream getInputStream()` to the `UploadedFile` interface (the type-safe, no-disk read path). Introduce `StrutsInMemoryUploadedFile`, a byte-array-backed `UploadedFile` that lazily writes a temp file only on `getContent()`/`getAbsolutePath()`. Rewire `JakartaMultiPartRequest.processFileField()` to build it for `item.isInMemory()`, and drop the now-obsolete eager-write / `temporaryFiles` bookkeeping (cleanup rides the existing `AbstractMultiPartRequest.cleanUp()` `delete()` loop).

**Tech Stack:** Java (Struts core module), Apache Commons FileUpload2 2.0.0-M5, JUnit 4 + AssertJ, Maven.

## Global Constraints

- **Commit prefix:** every commit message MUST start with `WW-5413` followed by a conventional type, e.g. `WW-5413 feat(core): ...`.
- **`getContent()` runtime type MUST remain `java.io.File`** for every `UploadedFile` implementation — never return `byte[]` from it. The bytes-without-a-file path is `getInputStream()` only.
- **Core tests are JUnit 4** (`org.junit.Test`, AssertJ). Do NOT use JUnit 5. New standalone test classes must NOT extend `XWorkTestCase` (irrelevant here) — plain JUnit 4 classes run fine under Surefire.
- **Secure temp-file naming:** materialized files MUST use the pattern `upload_<uuid>.tmp` in the provided save directory; the user-supplied original filename MUST NOT influence the on-disk name.
- **Backward compatibility:** `UploadedFile.getInputStream()` MUST be a `default` method so third-party implementations keep compiling.
- **Build/test command:** `mvn test -DskipAssembly -pl core -Dtest=ClassName#methodName` (single test) or `-Dtest=ClassName` (whole class).

---

## File Structure

- `core/src/main/java/org/apache/struts2/dispatcher/multipart/UploadedFile.java` — **modify**: add `default InputStream getInputStream()`.
- `core/src/main/java/org/apache/struts2/dispatcher/multipart/StrutsUploadedFile.java` — **modify**: override `getInputStream()` to stream the backing file.
- `core/src/main/java/org/apache/struts2/dispatcher/multipart/StrutsInMemoryUploadedFile.java` — **create**: byte-array-backed, lazily-materializing `UploadedFile`.
- `core/src/main/java/org/apache/struts2/dispatcher/multipart/JakartaMultiPartRequest.java` — **modify**: build `StrutsInMemoryUploadedFile` for in-memory items; remove eager write, `temporaryFiles`, `cleanUpTemporaryFiles()`.
- `core/src/test/java/org/apache/struts2/dispatcher/multipart/UploadedFileTest.java` — **create**: interface default-method test.
- `core/src/test/java/org/apache/struts2/dispatcher/multipart/StrutsUploadedFileTest.java` — **create**: `getInputStream()` override test.
- `core/src/test/java/org/apache/struts2/dispatcher/multipart/StrutsInMemoryUploadedFileTest.java` — **create**: full lazy-materialization unit tests.
- `core/src/test/java/org/apache/struts2/dispatcher/multipart/JakartaMultiPartRequestTest.java` — **modify**: remove 5 tests that reference removed internals; add integration tests for the new behavior.

---

## Task 1: Streaming accessor on the File-backed path

Adds `getInputStream()` to the interface (default) and overrides it in the existing `File`-backed implementation.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/dispatcher/multipart/UploadedFile.java`
- Modify: `core/src/main/java/org/apache/struts2/dispatcher/multipart/StrutsUploadedFile.java`
- Test: `core/src/test/java/org/apache/struts2/dispatcher/multipart/UploadedFileTest.java` (create)
- Test: `core/src/test/java/org/apache/struts2/dispatcher/multipart/StrutsUploadedFileTest.java` (create)

**Interfaces:**
- Produces: `UploadedFile.getInputStream() throws IOException` returning an `InputStream` over the file's bytes. Default impl: `File` content → `FileInputStream`; `byte[]` content → `ByteArrayInputStream`; otherwise `IOException`.

- [ ] **Step 1: Write the failing interface default-method test**

Create `core/src/test/java/org/apache/struts2/dispatcher/multipart/UploadedFileTest.java`:

```java
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
package org.apache.struts2.dispatcher.multipart;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class UploadedFileTest {

    @Test
    public void defaultGetInputStreamReadsByteArrayContent() throws IOException {
        UploadedFile file = new UploadedFile() {
            public Long length() { return 3L; }
            public String getName() { return "x"; }
            public String getOriginalName() { return "x"; }
            public boolean isFile() { return false; }
            public boolean delete() { return true; }
            public String getAbsolutePath() { return null; }
            public Object getContent() { return "abc".getBytes(UTF_8); }
            public String getContentType() { return "text/plain"; }
            public String getInputName() { return "file"; }
        };

        try (InputStream in = file.getInputStream()) {
            assertThat(new String(in.readAllBytes(), UTF_8)).isEqualTo("abc");
        }
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=UploadedFileTest`
Expected: COMPILE FAILURE — `getInputStream()` is not defined on `UploadedFile`.

- [ ] **Step 3: Add the default method to the interface**

In `UploadedFile.java`, add these imports below `import java.io.Serializable;`:

```java
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
```

Add this method inside the interface (e.g. after `getInputName()`):

```java
    /**
     * Streams the uploaded content without forcing it to disk. Implementations backed by
     * in-memory bytes can return the bytes directly; file-backed implementations stream the
     * file. The default reads whatever {@link #getContent()} exposes.
     *
     * @return an input stream over the uploaded content
     * @throws IOException if the content cannot be read
     * @since 7.3.0
     */
    default InputStream getInputStream() throws IOException {
        Object content = getContent();
        if (content instanceof File file) {
            return new FileInputStream(file);
        }
        if (content instanceof byte[] bytes) {
            return new ByteArrayInputStream(bytes);
        }
        throw new IOException("No content stream available for " + getName());
    }
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn test -DskipAssembly -pl core -Dtest=UploadedFileTest`
Expected: PASS.

- [ ] **Step 5: Write the failing StrutsUploadedFile override test**

Create `core/src/test/java/org/apache/struts2/dispatcher/multipart/StrutsUploadedFileTest.java`:

```java
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
package org.apache.struts2.dispatcher.multipart;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class StrutsUploadedFileTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void getInputStreamReadsFileContent() throws IOException {
        File backing = tempFolder.newFile("upload_test.tmp");
        Files.writeString(backing.toPath(), "hello");

        UploadedFile file = StrutsUploadedFile.Builder.create(backing).build();

        try (InputStream in = file.getInputStream()) {
            assertThat(new String(in.readAllBytes(), UTF_8)).isEqualTo("hello");
        }
    }
}
```

- [ ] **Step 6: Run test to verify it passes via the interface default**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsUploadedFileTest`
Expected: PASS (the default method already handles the `File` branch).

- [ ] **Step 7: Add an explicit override in StrutsUploadedFile**

In `StrutsUploadedFile.java`, replace the import block:

```java
import java.io.File;
```

with:

```java
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
```

Add this method (e.g. after `getContent()`):

```java
    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }
```

- [ ] **Step 8: Run both tests to verify they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=UploadedFileTest,StrutsUploadedFileTest`
Expected: PASS.

- [ ] **Step 9: Commit**

```bash
git add core/src/main/java/org/apache/struts2/dispatcher/multipart/UploadedFile.java \
        core/src/main/java/org/apache/struts2/dispatcher/multipart/StrutsUploadedFile.java \
        core/src/test/java/org/apache/struts2/dispatcher/multipart/UploadedFileTest.java \
        core/src/test/java/org/apache/struts2/dispatcher/multipart/StrutsUploadedFileTest.java
git commit -m "WW-5413 feat(core): add UploadedFile.getInputStream() streaming accessor"
```

---

## Task 2: `StrutsInMemoryUploadedFile` (lazy materialization)

The byte-array-backed implementation. This is the core of the optimization.

**Files:**
- Create: `core/src/main/java/org/apache/struts2/dispatcher/multipart/StrutsInMemoryUploadedFile.java`
- Test: `core/src/test/java/org/apache/struts2/dispatcher/multipart/StrutsInMemoryUploadedFileTest.java` (create)

**Interfaces:**
- Consumes: `UploadedFile` (incl. `getInputStream()` from Task 1), `org.apache.struts2.StrutsException`.
- Produces:
  - `StrutsInMemoryUploadedFile.Builder.create(byte[] content, java.nio.file.Path saveDir)` → `Builder`
  - `Builder.withContentType(String).withOriginalName(String).withInputName(String).build()` → `UploadedFile`
  - Behavior: `getInputStream()` → `ByteArrayInputStream` (no disk); `getContent()`/`getAbsolutePath()` write once to `saveDir/upload_<uuid>.tmp` and cache; `isFile()` false until materialized; `getName()` returns the pre-chosen `upload_<uuid>.tmp`; `delete()` removes the materialized file (no-op, returns `true`, if never materialized).

- [ ] **Step 1: Write the failing unit tests**

Create `core/src/test/java/org/apache/struts2/dispatcher/multipart/StrutsInMemoryUploadedFileTest.java`:

```java
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
package org.apache.struts2.dispatcher.multipart;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class StrutsInMemoryUploadedFileTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private Path saveDir;

    @Before
    public void setUp() {
        saveDir = tempFolder.getRoot().toPath();
    }

    private UploadedFile build(byte[] content) {
        return StrutsInMemoryUploadedFile.Builder
                .create(content, saveDir)
                .withOriginalName("orig.txt")
                .withContentType("text/plain")
                .withInputName("file")
                .build();
    }

    @Test
    public void getInputStreamReturnsBytesWithoutWritingFile() throws IOException {
        UploadedFile file = build("hello".getBytes(UTF_8));

        try (InputStream in = file.getInputStream()) {
            assertThat(new String(in.readAllBytes(), UTF_8)).isEqualTo("hello");
        }

        assertThat(file.isFile()).isFalse();
        assertThat(tempFolder.getRoot().listFiles()).isEmpty();
    }

    @Test
    public void getContentMaterializesFileExactlyOnce() {
        UploadedFile file = build("data".getBytes(UTF_8));

        File first = (File) file.getContent();
        File second = (File) file.getContent();

        assertThat(first).exists().hasContent("data");
        assertThat(second).isSameAs(first);
        assertThat(tempFolder.getRoot().listFiles()).hasSize(1);
    }

    @Test
    public void getAbsolutePathMaterializesFile() {
        UploadedFile file = build("data".getBytes(UTF_8));

        String path = file.getAbsolutePath();

        assertThat(new File(path)).exists().hasContent("data");
        assertThat(file.isFile()).isTrue();
    }

    @Test
    public void isFileFalseBeforeMaterializationTrueAfter() {
        UploadedFile file = build("x".getBytes(UTF_8));

        assertThat(file.isFile()).isFalse();
        file.getContent();
        assertThat(file.isFile()).isTrue();
    }

    @Test
    public void lengthAndMetadataDoNotMaterialize() {
        UploadedFile file = build("abcd".getBytes(UTF_8));

        assertThat(file.length()).isEqualTo(4L);
        assertThat(file.getContentType()).isEqualTo("text/plain");
        assertThat(file.getOriginalName()).isEqualTo("orig.txt");
        assertThat(file.getInputName()).isEqualTo("file");
        assertThat(file.getName()).startsWith("upload_").endsWith(".tmp");

        assertThat(file.isFile()).isFalse();
        assertThat(tempFolder.getRoot().listFiles()).isEmpty();
    }

    @Test
    public void deleteRemovesMaterializedFile() {
        UploadedFile file = build("x".getBytes(UTF_8));
        File materialized = (File) file.getContent();
        assertThat(materialized).exists();

        assertThat(file.delete()).isTrue();
        assertThat(materialized).doesNotExist();
    }

    @Test
    public void deleteIsNoOpWhenNotMaterialized() {
        UploadedFile file = build("x".getBytes(UTF_8));

        assertThat(file.delete()).isTrue();
        assertThat(tempFolder.getRoot().listFiles()).isEmpty();
    }

    @Test
    public void maliciousOriginalNameDoesNotLeakIntoTempName() {
        UploadedFile file = StrutsInMemoryUploadedFile.Builder
                .create("x".getBytes(UTF_8), saveDir)
                .withOriginalName("../../etc/passwd")
                .build();

        file.getContent(); // materialize

        assertThat(file.getName()).startsWith("upload_").endsWith(".tmp");
        assertThat(file.getName()).doesNotContain("..").doesNotContain("/").doesNotContain("\\");
        assertThat(new File(saveDir.toFile(), file.getName())).exists();
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsInMemoryUploadedFileTest`
Expected: COMPILE FAILURE — `StrutsInMemoryUploadedFile` does not exist.

- [ ] **Step 3: Implement `StrutsInMemoryUploadedFile`**

Create `core/src/main/java/org/apache/struts2/dispatcher/multipart/StrutsInMemoryUploadedFile.java`:

```java
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
package org.apache.struts2.dispatcher.multipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * In-memory backed {@link UploadedFile} for small multipart uploads that Commons FileUpload kept
 * in memory ({@code DiskFileItem.isInMemory() == true}).
 *
 * <p>The content is held as a byte array and is written to a temporary file only the first time a
 * caller demands a {@link File} through {@link #getContent()} or {@link #getAbsolutePath()} (lazy
 * materialization). Callers reading through {@link #getInputStream()} never touch the disk. The
 * temporary file uses the secure {@code upload_<uuid>.tmp} naming and ignores the user-supplied
 * original filename.</p>
 *
 * @since 7.3.0
 */
public class StrutsInMemoryUploadedFile implements UploadedFile {

    private static final Logger LOG = LogManager.getLogger(StrutsInMemoryUploadedFile.class);

    private final byte[] content;
    private final Path saveDir;
    private final String name;
    private final String contentType;
    private final String originalName;
    private final String inputName;

    private transient File materializedFile;

    private StrutsInMemoryUploadedFile(byte[] content, Path saveDir, String contentType,
                                       String originalName, String inputName) {
        this.content = content;
        this.saveDir = saveDir;
        this.contentType = contentType;
        this.originalName = originalName;
        this.inputName = inputName;
        this.name = "upload_" + UUID.randomUUID().toString().replace("-", "_") + ".tmp";
    }

    private synchronized File materialize() {
        if (materializedFile == null) {
            File target = saveDir.resolve(name).toFile();
            try {
                Files.write(target.toPath(), content);
            } catch (IOException e) {
                throw new StrutsException("Could not materialize in-memory uploaded file: " + name, e);
            }
            materializedFile = target;
            LOG.debug("Materialized in-memory uploaded item to {}", target.getAbsolutePath());
        }
        return materializedFile;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public Long length() {
        return (long) content.length;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isFile() {
        return materializedFile != null && materializedFile.isFile();
    }

    @Override
    public boolean delete() {
        if (materializedFile != null) {
            return materializedFile.delete();
        }
        return true;
    }

    @Override
    public String getAbsolutePath() {
        return materialize().getAbsolutePath();
    }

    @Override
    public File getContent() {
        return materialize();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getOriginalName() {
        return originalName;
    }

    @Override
    public String getInputName() {
        return inputName;
    }

    @Override
    public String toString() {
        return "StrutsInMemoryUploadedFile{" +
            "contentType='" + contentType + '\'' +
            ", originalName='" + originalName + '\'' +
            ", inputName='" + inputName + '\'' +
            ", size=" + content.length +
            '}';
    }

    public static class Builder {
        private final byte[] content;
        private final Path saveDir;
        private String contentType;
        private String originalName;
        private String inputName;

        private Builder(byte[] content, Path saveDir) {
            this.content = content;
            this.saveDir = saveDir;
        }

        public static Builder create(byte[] content, Path saveDir) {
            return new Builder(content, saveDir);
        }

        public Builder withContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder withOriginalName(String originalName) {
            this.originalName = originalName;
            return this;
        }

        public Builder withInputName(String inputName) {
            this.inputName = inputName;
            return this;
        }

        public UploadedFile build() {
            return new StrutsInMemoryUploadedFile(content, saveDir, contentType, originalName, inputName);
        }
    }
}
```

> Note: `org.apache.struts2.StrutsException` is the same exception class used in `AbstractMultiPartRequest`. Confirm the import resolves; it is an unchecked `RuntimeException`.

- [ ] **Step 4: Run tests to verify they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsInMemoryUploadedFileTest`
Expected: PASS (all 8 tests).

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/dispatcher/multipart/StrutsInMemoryUploadedFile.java \
        core/src/test/java/org/apache/struts2/dispatcher/multipart/StrutsInMemoryUploadedFileTest.java
git commit -m "WW-5413 feat(core): add lazily-materializing StrutsInMemoryUploadedFile"
```

---

## Task 3: Rewire `JakartaMultiPartRequest` and clean up obsolete internals

Switch the in-memory branch to `StrutsInMemoryUploadedFile`, remove the eager write and `temporaryFiles` bookkeeping, and update the existing tests that reference the removed internals so the suite compiles and passes.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/dispatcher/multipart/JakartaMultiPartRequest.java`
- Modify: `core/src/test/java/org/apache/struts2/dispatcher/multipart/JakartaMultiPartRequestTest.java`

**Interfaces:**
- Consumes: `StrutsInMemoryUploadedFile.Builder` (Task 2), existing `StrutsUploadedFile.Builder`.
- Produces: `JakartaMultiPartRequest.processFileField(DiskFileItem, String)` no longer creates temp files eagerly; the `temporaryFiles` field and `cleanUpTemporaryFiles()` method are removed.

- [ ] **Step 1: Replace the in-memory branch in `processFileField`**

In `JakartaMultiPartRequest.java`, replace the entire `if (item.isInMemory()) { ... } else { ... }` block (the eager `FileOutputStream` write path) with:

```java
        if (item.isInMemory()) {
            LOG.debug(() -> "Keeping in-memory uploaded item without writing to disk: " + normalizeSpace(item.getFieldName()));
            UploadedFile uploadedFile = StrutsInMemoryUploadedFile.Builder
                    .create(item.get(), Path.of(saveDir))
                    .withOriginalName(item.getName())
                    .withContentType(item.getContentType())
                    .withInputName(item.getFieldName())
                    .build();
            values.add(uploadedFile);
        } else {
            UploadedFile uploadedFile = StrutsUploadedFile.Builder
                    .create(item.getPath().toFile())
                    .withOriginalName(item.getName())
                    .withContentType(item.getContentType())
                    .withInputName(item.getFieldName())
                    .build();
            values.add(uploadedFile);
        }
```

- [ ] **Step 2: Remove the `temporaryFiles` field**

Delete this field and its Javadoc (the block around lines 83–86):

```java
    /**
     * List to track temporary files created for in-memory uploads
     */
    private final List<File> temporaryFiles = new ArrayList<>();
```

- [ ] **Step 3: Remove `cleanUpTemporaryFiles()` and its call**

Delete the entire `cleanUpTemporaryFiles()` method (its Javadoc + body). In `cleanUp()`, remove the `cleanUpTemporaryFiles();` line and the `temporaryFiles.clear();` line, leaving:

```java
    @Override
    public void cleanUp() {
        super.cleanUp();
        try {
            cleanUpDiskFileItems();
        } finally {
            diskFileItems.clear();
        }
    }
```

- [ ] **Step 4: Fix imports**

Remove `import java.io.File;` (no longer used in this class). Leave `java.nio.file.Path` (used by `Path.of(saveDir)`). If the compiler reports any other now-unused import (e.g. `java.nio.file.Files`), remove it too. Keep `StringUtils` (still used by `processNormalFormField`).

- [ ] **Step 5: Remove the 5 obsolete tests**

In `JakartaMultiPartRequestTest.java`, delete these test methods entirely — they reference the removed `temporaryFiles` field / `cleanUpTemporaryFiles()` method, or assert the removed parse-time eager-write behavior. Their coverage is replaced by Task 2 (secure naming, materialization) and Task 4 (no eager write, cleanup):
  - `temporaryFileCleanupForInMemoryUploads`
  - `cleanupMethodsCanBeOverridden`
  - `temporaryFileCreationFailureAddsError`
  - `temporaryFilesCreatedInSaveDirectory`
  - `secureTemporaryFileNaming`

Leave all other tests untouched (`temporaryFileCreationErrorsAreNotDuplicated`, `cleanupIsIdempotent`, `endToEndMultipartProcessingWithCleanup`, `inMemoryVsDiskFileHandling`, `processNormalFormFieldHandlesNullFieldName`, `processFileFieldHandlesNullFieldName`, `diskFileItemCleanupCoverage`, `errorDuplicationPrevention`, `processFileFieldHandlesEmptyFileName`). After deleting, remove any imports left unused by the deletions (e.g. `java.lang.reflect.Field` if no longer referenced).

- [ ] **Step 6: Run the whole class to verify it compiles and passes**

Run: `mvn test -DskipAssembly -pl core -Dtest=JakartaMultiPartRequestTest`
Expected: PASS. In particular `inMemoryVsDiskFileHandling` still passes — its small-file assertion goes through `getContent()`, which now materializes the file on demand and returns a `File`.

- [ ] **Step 7: Commit**

```bash
git add core/src/main/java/org/apache/struts2/dispatcher/multipart/JakartaMultiPartRequest.java \
        core/src/test/java/org/apache/struts2/dispatcher/multipart/JakartaMultiPartRequestTest.java
git commit -m "WW-5413 refactor(core): drop eager temp-file write for in-memory uploads"
```

---

## Task 4: Integration tests for the deferred-write behavior

Prove end-to-end that in-memory uploads are not written to disk until content is demanded, are readable via the stream path without a write, and are cleaned up after materialization.

**Files:**
- Modify: `core/src/test/java/org/apache/struts2/dispatcher/multipart/JakartaMultiPartRequestTest.java`

**Interfaces:**
- Consumes: the parse pipeline from Task 3, `UploadedFile.getInputStream()` from Task 1.

- [ ] **Step 1: Add the imports the new test needs**

In `JakartaMultiPartRequestTest.java`, ensure these imports are present (add any missing):

```java
import java.io.InputStream;
```

(`java.io.File`, `java.nio.charset.StandardCharsets`, and `static org.assertj.core.api.Assertions.assertThat` are already imported.)

- [ ] **Step 2: Write the failing integration test**

Add this test method to `JakartaMultiPartRequestTest`:

```java
    @Test
    public void inMemoryUploadIsNotWrittenToDiskUntilContentRequested() throws IOException {
        // given - a small file that Commons FileUpload keeps in memory
        String content = formFile("file1", "test1.csv", "a,b,c,d") +
                endline + "--" + boundary + "--";
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        // when
        multiPart.parse(mockRequest, tempDir);

        UploadedFile file = multiPart.getFile("file1")[0];

        // then - nothing written to disk right after parse
        assertThat(file.isFile()).isFalse();

        // and - content is readable via the stream path without materializing
        try (InputStream in = file.getInputStream()) {
            assertThat(new String(in.readAllBytes(), StandardCharsets.UTF_8)).isEqualTo("a,b,c,d");
        }
        assertThat(file.isFile()).isFalse();

        // and - getContent() materializes a real file on demand
        File materialized = (File) file.getContent();
        assertThat(materialized).exists().hasContent("a,b,c,d");
        assertThat(file.isFile()).isTrue();

        // and - cleanUp removes the materialized file
        multiPart.cleanUp();
        assertThat(materialized).doesNotExist();
    }
```

- [ ] **Step 3: Run it to verify it passes**

Run: `mvn test -DskipAssembly -pl core -Dtest=JakartaMultiPartRequestTest#inMemoryUploadIsNotWrittenToDiskUntilContentRequested`
Expected: PASS.

- [ ] **Step 4: Run the full multipart test package as a regression check**

Run: `mvn test -DskipAssembly -pl core -Dtest='org.apache.struts2.dispatcher.multipart.*'`
Expected: PASS (all multipart tests, including `JakartaStreamMultiPartRequestTest` and `AbstractMultiPartRequestApiCheckTest`).

- [ ] **Step 5: Commit**

```bash
git add core/src/test/java/org/apache/struts2/dispatcher/multipart/JakartaMultiPartRequestTest.java
git commit -m "WW-5413 test(core): cover deferred-write behavior for in-memory uploads"
```

---

## Final verification

- [ ] **Run the core module's dispatcher tests** to catch any converter/interceptor fallout from the interface change:

Run: `mvn test -DskipAssembly -pl core -Dtest='org.apache.struts2.dispatcher.**,org.apache.struts2.interceptor.**,org.apache.struts2.conversion.**'`
Expected: PASS. Pay attention to `UploadedFileConverter`-related tests (legacy `File`-typed action property path) — a small upload now reaches the converter as a `StrutsInMemoryUploadedFile` whose `getContent()` returns a materialized `File`, so conversion must still succeed.

---

## Notes on the spec's error-handling trade-off (§4)

The spec deliberately accepts that a materialization **write failure** now surfaces as an unchecked `StrutsException` during consumption (via `getContent()`/`getAbsolutePath()`), rather than as a parse-time `LocalizedMessage`. This is why the old `temporaryFileCreationFailureAddsError` test is removed rather than rewritten: the parse-time graceful-degradation path for in-memory items no longer exists. No new test asserts the exception, since it only fires on genuine filesystem failures in the save directory.
