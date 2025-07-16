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

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.RequestContext;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletDiskFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.LocalizedMessage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.normalizeSpace;

/**
 * Multipart form data request adapter for Jakarta Commons FileUpload package.
 */
public class JakartaMultiPartRequest extends AbstractMultiPartRequest {

    private static final Logger LOG = LogManager.getLogger(JakartaMultiPartRequest.class);

    /**
     * List to track all DiskFileItem instances for proper cleanup
     */
    private final List<DiskFileItem> diskFileItems = new ArrayList<>();
    
    /**
     * List to track temporary files created for in-memory uploads
     */
    private final List<File> temporaryFiles = new ArrayList<>();

    @Override
    protected void processUpload(HttpServletRequest request, String saveDir) throws IOException {
        Charset charset = readCharsetEncoding(request);

        JakartaServletDiskFileUpload servletFileUpload =
                prepareServletFileUpload(charset, Path.of(saveDir));

        RequestContext requestContext = createRequestContext(request);
        
        for (DiskFileItem item : servletFileUpload.parseRequest(requestContext)) {
            // Track all DiskFileItem instances for cleanup
            diskFileItems.add(item);

            LOG.debug(() -> "Processing a form field: " + normalizeSpace(item.getFieldName()));
            if (item.isFormField()) {
                processNormalFormField(item, charset);
            } else {
                LOG.debug(() -> "Processing a file: " + normalizeSpace(item.getFieldName()));
                processFileField(item);
            }
        }
    }

    protected void processNormalFormField(DiskFileItem item, Charset charset) throws IOException {
        LOG.debug("Item: {} is a normal form field", normalizeSpace(item.getName()));

        List<String> values;
        String fieldName = item.getFieldName();
        if (parameters.get(fieldName) != null) {
            values = parameters.get(fieldName);
        } else {
            values = new ArrayList<>();
        }

        String fieldValue = item.getString(charset);
        if (exceedsMaxStringLength(fieldName, fieldValue)) {
            return;
        }
        if (item.getSize() == 0) {
            values.add(StringUtils.EMPTY);
        } else {
            values.add(fieldValue);
        }
        parameters.put(fieldName, values);
    }

    protected void processFileField(DiskFileItem item) {
        // Skip file uploads that don't have a file name - meaning that no file was selected.
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            LOG.debug(() -> "No file has been uploaded for the field: " + normalizeSpace(item.getFieldName()));
            return;
        }

        List<UploadedFile> values;
        if (uploadedFiles.get(item.getFieldName()) != null) {
            values = uploadedFiles.get(item.getFieldName());
        } else {
            values = new ArrayList<>();
        }

        if (item.isInMemory()) {
            LOG.debug("Creating temporary file representing in-memory uploaded item: {}", normalizeSpace(item.getFieldName()));
            try {
                File tempFile = File.createTempFile("struts_upload_", "_" + item.getName());
                tempFile.deleteOnExit(); // Ensure cleanup on JVM exit as fallback
                
                // Track the temporary file for explicit cleanup
                temporaryFiles.add(tempFile);

                // Write the in-memory content to the temporary file
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile)) {
                    fos.write(item.get());
                }

                UploadedFile uploadedFile = StrutsUploadedFile.Builder
                        .create(tempFile)
                        .withOriginalName(item.getName())
                        .withContentType(item.getContentType())
                        .withInputName(item.getFieldName())
                        .build();
                values.add(uploadedFile);

                LOG.debug("Created temporary file for in-memory uploaded item: {} at {}",
                         normalizeSpace(item.getName()), tempFile.getAbsolutePath());
            } catch (IOException e) {
                LOG.warn("Failed to create temporary file for in-memory uploaded item: {}",
                        normalizeSpace(item.getName()), e);
                
                // Add the error to the errors list for proper user feedback
                LocalizedMessage errorMessage = buildErrorMessage(e.getClass(), e.getMessage(), new Object[]{item.getName()});
                if (!errors.contains(errorMessage)) {
                    errors.add(errorMessage);
                }
            }
        } else {
            UploadedFile uploadedFile = StrutsUploadedFile.Builder
                    .create(item.getPath().toFile())
                    .withOriginalName(item.getName())
                    .withContentType(item.getContentType())
                    .withInputName(item.getFieldName())
                    .build();
            values.add(uploadedFile);
        }

        uploadedFiles.put(item.getFieldName(), values);
    }

    /**
     * Cleans up disk file items by deleting associated temporary files.
     * This method can be overridden by subclasses to customize cleanup behavior.
     */
    protected void cleanUpDiskFileItems() {
        LOG.debug("Clean up all DiskFileItem instances (both form fields and file uploads");
        for (DiskFileItem item : diskFileItems) {
            try {
                if (item.isInMemory()) {
                    LOG.debug("Cleaning up in-memory item: {}", normalizeSpace(item.getFieldName()));
                } else {
                    LOG.debug("Cleaning up disk item: {} at {}", normalizeSpace(item.getFieldName()), item.getPath());
                    if (item.getPath() != null && item.getPath().toFile().exists()) {
                        if (!item.getPath().toFile().delete()) {
                            LOG.warn("There was a problem attempting to delete temporary file: {}", item.getPath());
                        }
                    }
                }
            } catch (Exception e) {
                LOG.warn("Error cleaning up DiskFileItem: {}", normalizeSpace(item.getFieldName()), e);
            }
        }
    }

    /**
     * Cleans up temporary files created for in-memory uploads.
     * This method can be overridden by subclasses to customize cleanup behavior.
     */
    protected void cleanUpTemporaryFiles() {
        LOG.debug("Cleaning up {} temporary files created for in-memory uploads", temporaryFiles.size());
        for (File tempFile : temporaryFiles) {
            try {
                if (tempFile.exists()) {
                    LOG.debug("Deleting temporary file: {}", tempFile.getAbsolutePath());
                    if (!tempFile.delete()) {
                        LOG.warn("There was a problem attempting to delete temporary file: {}", tempFile.getAbsolutePath());
                    }
                } else {
                    LOG.debug("Temporary file already deleted: {}", tempFile.getAbsolutePath());
                }
            } catch (Exception e) {
                LOG.warn("Error cleaning up temporary file: {}", tempFile.getAbsolutePath(), e);
            }
        }
    }

    /**
     * Override cleanUp to ensure all DiskFileItem instances and temporary files are properly cleaned up
     */
    @Override
    public void cleanUp() {
        super.cleanUp();
        try {
            cleanUpDiskFileItems();
            cleanUpTemporaryFiles();
        } finally {
            diskFileItems.clear();
            temporaryFiles.clear();
        }
    }

}
