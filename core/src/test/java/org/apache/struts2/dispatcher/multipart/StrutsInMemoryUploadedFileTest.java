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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    @Test
    public void isSerializableWhenNotMaterialized() throws IOException, ClassNotFoundException {
        UploadedFile file = build("payload".getBytes(UTF_8));

        byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(file);
            bytes = bos.toByteArray();
        }

        UploadedFile restored;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            restored = (UploadedFile) ois.readObject();
        }

        assertThat(restored.length()).isEqualTo(7L);
        assertThat(restored.getContentType()).isEqualTo("text/plain");
        assertThat(restored.getName()).startsWith("upload_").endsWith(".tmp");
        try (InputStream in = restored.getInputStream()) {
            assertThat(new String(in.readAllBytes(), UTF_8)).isEqualTo("payload");
        }
    }
}
