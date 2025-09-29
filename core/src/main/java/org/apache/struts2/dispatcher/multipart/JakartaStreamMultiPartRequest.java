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
import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileUploadFileCountLimitException;
import org.apache.commons.fileupload2.core.FileUploadSizeException;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletDiskFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.LocalizedMessage;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.normalizeSpace;

/**
 * Multipart form data request adapter for Jakarta Commons FileUpload package that
 * leverages the streaming API rather than the traditional non-streaming API.
 * <p>
 * For more details see WW-3025
 *
 * @since 2.3.18
 */
public class JakartaStreamMultiPartRequest extends AbstractMultiPartRequest {

    private static final Logger LOG = LogManager.getLogger(JakartaStreamMultiPartRequest.class);

    /**
     * Processes the upload.
     *
     * @param request the servlet request
     * @param saveDir location of the save dir
     */
    @Override
    protected void processUpload(HttpServletRequest request, String saveDir) throws IOException {
        Charset charset = readCharsetEncoding(request);
        Path location = Path.of(saveDir);

        JakartaServletDiskFileUpload servletFileUpload =
                prepareServletFileUpload(charset, location);

        LOG.debug("Using Jakarta Stream API to process request");
        servletFileUpload.getItemIterator(request).forEachRemaining(item -> {
            if (item.isFormField()) {
                LOG.debug(() -> "Processing a form field: " + normalizeSpace(item.getFieldName()));
                processFileItemAsFormField(item);
            } else {
                LOG.debug(() -> "Processing a file: " + normalizeSpace(item.getFieldName()));
                processFileItemAsFileField(item, location);
            }
        });
    }

    /**
     * Reads the entire contents of an input stream into a string.
     *
     * <p>This method uses a buffered approach to efficiently read the stream
     * content without loading the entire stream into memory at once. It uses
     * try-with-resources to ensure proper cleanup of resources.</p>
     *
     * @param inputStream the input stream to read from
     * @return the stream contents as a UTF-8 string
     * @throws IOException if an error occurs reading the stream
     */
    private String readStream(InputStream inputStream) throws IOException {
        // Use try-with-resources to ensure ByteArrayOutputStream is properly closed
        try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024]; // 1KB buffer for efficient reading
            // Read the stream in chunks to avoid loading everything into memory at once
            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            // Convert to string using UTF-8 encoding
            return result.toString(StandardCharsets.UTF_8);
        }
    }

    /**
     * Processes a normal form field (non-file) from the multipart request using streaming API.
     *
     * <p>This method handles text form fields by:</p>
     * <ol>
     *   <li>Validating the field name is not null</li>
     *   <li>Reading the field value from the input stream</li>
     *   <li>Checking if the field value exceeds maximum string length</li>
     *   <li>Adding the value to the parameters collection</li>
     * </ol>
     *
     * <p>Fields with null names are skipped with a warning log message.</p>
     * <p>The streaming approach is more memory-efficient for large form data.</p>
     *
     * @param fileItemInput a form field item input from the streaming API
     * @throws IOException if an error occurs reading the input stream
     * @see #readStream(InputStream)
     * @see #exceedsMaxStringLength(String, String)
     */
    protected void processFileItemAsFormField(FileItemInput fileItemInput) throws IOException {
        String fieldName = fileItemInput.getFieldName();
        if (fieldName == null) {
            LOG.warn("Form field has null fieldName, skipping");
            return;
        }

        String fieldValue = readStream(fileItemInput.getInputStream());
        if (exceedsMaxStringLength(fieldName, fieldValue)) {
            return;
        }

        List<String> values = parameters.computeIfAbsent(fieldName, k -> new ArrayList<>());
        values.add(fieldValue);
    }

    /**
     * @return actual size of already uploaded files
     */
    protected Long actualSizeOfUploadedFiles() {
        return uploadedFiles.values().stream()
                .map(files -> files.stream().map(UploadedFile::length).reduce(0L, Long::sum))
                .reduce(0L, Long::sum);
    }

    private boolean exceedsMaxFiles(FileItemInput fileItemInput) {
        if (maxFiles != null && maxFiles == uploadedFiles.size()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cannot accept another file: {} as it will exceed max files: {}",
                        normalizeSpace(fileItemInput.getName()), maxFiles);
            }
            LocalizedMessage errorMessage = buildErrorMessage(
                    FileUploadFileCountLimitException.class,
                    String.format("File %s exceeds allowed maximum number of files %s",
                            fileItemInput.getName(), maxFiles),
                    new Object[]{maxFiles, uploadedFiles.size()}
            );
            if (!errors.contains(errorMessage)) {
                errors.add(errorMessage);
            }
            return true;
        }
        return false;
    }

    private void exceedsMaxSizeOfFiles(FileItemInput fileItemInput, File file, Long currentFilesSize) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("File: {} of size: {} exceeds allowed max size: {}, actual size of already uploaded files: {}",
                    normalizeSpace(fileItemInput.getName()), file.length(), maxSizeOfFiles, currentFilesSize
            );
        }
        LocalizedMessage errorMessage = buildErrorMessage(
                FileUploadSizeException.class,
                String.format("Size %s of file %s exceeds allowed max size %s", file.length(),
                        fileItemInput.getName(), maxSizeOfFiles),
                new Object[]{maxSizeOfFiles, currentFilesSize}
        );
        if (!errors.contains(errorMessage)) {
            errors.add(errorMessage);
        }
        if (!file.delete() && LOG.isWarnEnabled()) {
            LOG.warn("Cannot delete file: {} which exceeds maximum size: {} of all files!",
                    normalizeSpace(fileItemInput.getName()), maxSizeOfFiles);
        }
    }

    /**
     * Processes a file field from the multipart request using streaming API.
     *
     * <p>This method handles file uploads by:</p>
     * <ol>
     *   <li>Validating the file name and field name are not null/empty</li>
     *   <li>Checking if the upload exceeds maximum file count</li>
     *   <li>Creating a temporary file in the specified location</li>
     *   <li>Streaming the file content directly to disk</li>
     *   <li>Checking if the total size exceeds maximum allowed size</li>
     *   <li>Creating an {@link UploadedFile} abstraction or cleaning up on size exceeded</li>
     * </ol>
     *
     * <p>Files with null names or field names are skipped with appropriate logging.</p>
     * <p>The streaming approach is more memory-efficient for large file uploads
     * as it writes directly to disk rather than loading into memory first.</p>
     *
     * @param fileItemInput file item representing upload file from streaming API
     * @param location      the directory where temporary files will be created
     * @throws IOException if an error occurs during file processing
     * @see #createTemporaryFile(String, Path)
     * @see #streamFileToDisk(FileItemInput, File)
     * @see #createUploadedFile(FileItemInput, File)
     */
    protected void processFileItemAsFileField(FileItemInput fileItemInput, Path location) throws IOException {
        // Skip file uploads that don't have a file name - meaning that no file was selected.
        if (fileItemInput.getName() == null || fileItemInput.getName().trim().isEmpty()) {
            LOG.debug(() -> "No file has been uploaded for the field: " + normalizeSpace(fileItemInput.getFieldName()));
            return;
        }

        // Skip file uploads that don't have a field name
        if (fileItemInput.getFieldName() == null) {
            LOG.warn("File upload has null fieldName, skipping");
            return;
        }

        if (exceedsMaxFiles(fileItemInput)) {
            return;
        }

        File file = createTemporaryFile(fileItemInput.getName(), location);
        streamFileToDisk(fileItemInput, file);

        // Reject empty files (0 bytes) as they are not considered valid uploads
        if (rejectEmptyFile(file.length(), fileItemInput.getName(), fileItemInput.getFieldName())) {
            // Clean up the empty temporary file
            deleteFile(file.toPath());
            return;
        }

        Long currentFilesSize = maxSizeOfFiles != null ? actualSizeOfUploadedFiles() : null;
        if (maxSizeOfFiles != null && currentFilesSize + file.length() >= maxSizeOfFiles) {
            exceedsMaxSizeOfFiles(fileItemInput, file, currentFilesSize);
        } else {
            createUploadedFile(fileItemInput, file);
        }
    }

    /**
     * Streams the file upload stream to the specified file.
     *
     * @param fileItemInput file item input
     * @param file          the file
     */
    protected void streamFileToDisk(FileItemInput fileItemInput, File file) throws IOException {
        InputStream input = fileItemInput.getInputStream();
        try (OutputStream output = new BufferedOutputStream(Files.newOutputStream(file.toPath()), bufferSize)) {
            byte[] buffer = new byte[bufferSize];
            LOG.debug("Streaming file: {} using buffer size: {}", normalizeSpace(fileItemInput.getName()), bufferSize);
            for (int length; ((length = input.read(buffer)) > 0); ) {
                output.write(buffer, 0, length);
            }
        }
    }

    /**
     * Creates an {@link UploadedFile} abstraction over an uploaded file.
     *
     * <p>This method creates a wrapper around the uploaded file that provides
     * a consistent interface for accessing file information and content.
     * The created {@link UploadedFile} instance contains:</p>
     * <ul>
     *   <li>The original filename as provided by the client</li>
     *   <li>The content type (MIME type) if available</li>
     *   <li>The form field name that contained the file</li>
     *   <li>A reference to the temporary file on disk</li>
     * </ul>
     *
     * <p>The file is automatically added to the uploaded files collection,
     * grouped by field name to support multiple file uploads per field.</p>
     *
     * @param fileItemInput file item stream containing file metadata
     * @param file          the temporary file containing the uploaded content
     * @see UploadedFile
     * @see StrutsUploadedFile
     */
    protected void createUploadedFile(FileItemInput fileItemInput, File file) {
        String fileName = fileItemInput.getName();
        String fieldName = fileItemInput.getFieldName();

        // fieldName null check already done in processFileItemAsFileField
        UploadedFile uploadedFile = StrutsUploadedFile.Builder
                .create(file)
                .withOriginalName(fileName)
                .withContentType(fileItemInput.getContentType())
                .withInputName(fieldName)
                .build();

        List<UploadedFile> infos = uploadedFiles.computeIfAbsent(fieldName, key -> new ArrayList<>());
        infos.add(uploadedFile);
    }

}
