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

import java.io.File;

public class StrutsUploadedFile implements UploadedFile {

    private final File file;
    private final String contentType;
    private final String originalName;
    private final String inputName;

    private StrutsUploadedFile(File file, String contentType, String originalName, String inputName) {
        this.file = file;
        this.contentType = contentType;
        this.originalName = originalName;
        this.inputName = inputName;
    }

    @Override
    public Long length() {
        return file.length();
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public boolean isFile() {
        return file.isFile();
    }

    @Override
    public boolean delete() {
        return file.delete();
    }

    @Override
    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    @Override
    public File getContent() {
        return file;
    }

    @Override
    public String getContentType() {
        return this.contentType;
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
        return "StrutsUploadedFile{" +
            "contentType='" + contentType + '\'' +
            ", originalName='" + originalName + '\'' +
            ", inputName='" + inputName + '\'' +
            '}';
    }

    public static class Builder {
        private final File file;
        private String contentType;
        private String originalName;
        private String inputName;

        private Builder(File file) {
            this.file = file;
        }

        public static Builder create(File file) {
            return new Builder(file);
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
            return new StrutsUploadedFile(this.file, this.contentType, this.originalName, this.inputName);
        }
    }
}
