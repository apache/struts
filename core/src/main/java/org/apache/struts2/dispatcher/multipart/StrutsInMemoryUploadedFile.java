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
