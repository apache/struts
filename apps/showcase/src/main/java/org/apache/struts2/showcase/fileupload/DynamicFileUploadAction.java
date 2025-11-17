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
package org.apache.struts2.showcase.fileupload;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ActionSupport;
import org.apache.struts2.action.UploadedFilesAware;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.apache.struts2.interceptor.parameter.StrutsParameter;

import java.util.List;

/**
 * <p>
 * Demonstrates dynamic file upload validation using WithLazyParams.
 * This action shows how file upload validation rules can be determined
 * at runtime based on action properties, session data, or other dynamic values.
 * </p>
 *
 * <p>
 * The validation parameters (allowedTypes, allowedExtensions, maximumSize)
 * are set dynamically in the prepare() method and then referenced in struts.xml
 * using ${...} expressions. This allows the same action to enforce different
 * validation rules based on runtime conditions.
 * </p>
 *
 * <p>
 * This example demonstrates two use cases:
 * </p>
 * <ul>
 * <li><strong>Document Upload:</strong> Accepts PDF and Word documents up to 5MB</li>
 * <li><strong>Image Upload:</strong> Accepts JPEG and PNG images up to 2MB</li>
 * </ul>
 *
 * @see org.apache.struts2.interceptor.WithLazyParams
 * @see org.apache.struts2.interceptor.ActionFileUploadInterceptor
 */
public class DynamicFileUploadAction extends ActionSupport implements UploadedFilesAware {

    private static final Logger LOG = LogManager.getLogger(DynamicFileUploadAction.class);

    private UploadedFile uploadedFile;
    private String contentType;
    private String fileName;
    private String originalName;
    private String inputName;
    private String uploadType = "document";

    private transient UploadConfig uploadConfig;

    @Override
    public String input() {
        prepareUploadConfig(uploadType);
        return INPUT;
    }

    public String upload() {
        if (uploadedFile == null) {
            addActionError("Please select a file to upload");
            return INPUT;
        }

        return SUCCESS;
    }

    @Override
    public void withUploadedFiles(List<UploadedFile> uploadedFiles) {
        if (!uploadedFiles.isEmpty()) {
            LOG.info("Uploaded file: {}", uploadedFiles.get(0));
            this.uploadedFile = uploadedFiles.get(0);
            this.fileName = uploadedFile.getName();
            this.contentType = uploadedFile.getContentType();
            this.originalName = uploadedFile.getOriginalName();
            this.inputName = uploadedFile.getInputName();
        }
    }

    // Getters and Setters

    public String getContentType() {
        return contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getInputName() {
        return inputName;
    }

    public Object getUploadedFile() {
        return uploadedFile != null ? uploadedFile.getContent() : null;
    }

    public long getUploadSize() {
        return uploadedFile != null ? uploadedFile.length() : 0;
    }

    public String getUploadType() {
        return uploadType;
    }

    @StrutsParameter
    public void setUploadType(String uploadType) {
        this.uploadType = uploadType;
        prepareUploadConfig(uploadType);
    }

    private void prepareUploadConfig(String uploadType) {
        uploadConfig = new UploadConfig();
        LOG.debug("Configure validation rules based on upload type: {}", uploadType);
        if ("image".equals(uploadType)) {
            // Image upload configuration
            uploadConfig.setAllowedMimeTypes("image/jpeg,image/png");
            uploadConfig.setAllowedExtensions(".jpg,.jpeg,.png");
            uploadConfig.setMaxFileSize(2097152L); // 2MB
            uploadConfig.setDescription("images (JPEG, PNG)");
        } else {
            // Document upload configuration (default)
            uploadConfig.setAllowedMimeTypes("application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            uploadConfig.setAllowedExtensions(".pdf,.doc,.docx");
            uploadConfig.setMaxFileSize(5242880L); // 5MB
            uploadConfig.setDescription("documents (PDF, Word)");
        }
    }

    /**
     * Returns the upload configuration object.
     * This is used in struts.xml with ${uploadConfig.allowedMimeTypes} expressions.
     */
    public UploadConfig getUploadConfig() {
        return uploadConfig;
    }

    /**
     * Configuration holder for dynamic file upload validation rules.
     */
    public static class UploadConfig {
        private String allowedMimeTypes;
        private String allowedExtensions;
        private Long maxFileSize;
        private String description;

        public String getAllowedMimeTypes() {
            return allowedMimeTypes;
        }

        public void setAllowedMimeTypes(String allowedMimeTypes) {
            this.allowedMimeTypes = allowedMimeTypes;
        }

        public String getAllowedExtensions() {
            return allowedExtensions;
        }

        public void setAllowedExtensions(String allowedExtensions) {
            this.allowedExtensions = allowedExtensions;
        }

        public Long getMaxFileSize() {
            return maxFileSize;
        }

        public void setMaxFileSize(Long maxFileSize) {
            this.maxFileSize = maxFileSize;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * Returns a human-readable string representation of the max file size.
         */
        public String getMaxFileSizeFormatted() {
            if (maxFileSize == null) {
                return "unlimited";
            }

            if (maxFileSize < 1024) {
                return maxFileSize + " bytes";
            } else if (maxFileSize < 1024 * 1024) {
                return (maxFileSize / 1024) + " KB";
            } else {
                return (maxFileSize / (1024 * 1024)) + " MB";
            }
        }
    }
}
