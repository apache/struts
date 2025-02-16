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
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
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
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.normalizeSpace;

/**
 * Multi-part form data request adapter for Jakarta Commons FileUpload package that
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

    protected JakartaServletDiskFileUpload createJakartaFileUpload(Charset charset, Path location) {
        DiskFileItemFactory.Builder builder = DiskFileItemFactory.builder();

        LOG.debug("Using file save directory: {}", location);
        builder.setPath(location);

        LOG.debug("Sets buffer size: {}", bufferSize);
        builder.setBufferSize(bufferSize);

        LOG.debug("Using charset: {}", charset);
        builder.setCharset(charset);

        DiskFileItemFactory factory = builder.get();
        return new JakartaServletDiskFileUpload(factory);
    }

    private String readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length; (length = inputStream.read(buffer)) != -1; ) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8);
    }

    /**
     * Processes the FileItem as a normal form field.
     *
     * @param fileItemInput a form field item input
     */
    protected void processFileItemAsFormField(FileItemInput fileItemInput) throws IOException {
        String fieldName = fileItemInput.getFieldName();
        String fieldValue = readStream(fileItemInput.getInputStream());

        if (exceedsMaxStringLength(fieldName, fieldValue)) {
            return;
        }

        List<String> values;
        if (parameters.containsKey(fieldName)) {
            values = parameters.get(fieldName);
        } else {
            values = new ArrayList<>();
            parameters.put(fieldName, values);
        }
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
     * Processes the FileItem as a file field.
     *
     * @param fileItemInput file item representing upload file
     * @param location      location
     */
    protected void processFileItemAsFileField(FileItemInput fileItemInput, Path location) throws IOException {
        // Skip file uploads that don't have a file name - meaning that no file was selected.
        if (fileItemInput.getName() == null || fileItemInput.getName().trim().isEmpty()) {
            LOG.debug(() -> "No file has been uploaded for the field: " + normalizeSpace(fileItemInput.getFieldName()));
            return;
        }

        if (exceedsMaxFiles(fileItemInput)) {
            return;
        }

        File file = createTemporaryFile(fileItemInput.getName(), location);
        streamFileToDisk(fileItemInput, file);

        Long currentFilesSize = maxSizeOfFiles != null ? actualSizeOfUploadedFiles() : null;
        if (maxSizeOfFiles != null && currentFilesSize + file.length() >= maxSizeOfFiles) {
            exceedsMaxSizeOfFiles(fileItemInput, file, currentFilesSize);
        } else {
            createUploadedFile(fileItemInput, file);
        }
    }

    /**
     * Creates a temporary file based on the given filename and location.
     *
     * @param fileName file name
     * @param location location
     * @return a temporary file based on the given filename and location
     */
    protected File createTemporaryFile(String fileName, Path location) {
        String uid = UUID.randomUUID().toString().replace("-", "_");
        File file = location.resolve("upload_" + uid + ".tmp").toFile();
        LOG.debug("Creating temporary file: {} (originally: {})", file.getName(), fileName);
        return file;
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
     * Create {@link UploadedFile} abstraction over uploaded file
     *
     * @param fileItemInput file item stream
     * @param file          the file
     */
    protected void createUploadedFile(FileItemInput fileItemInput, File file) {
        String fileName = fileItemInput.getName();
        String fieldName = fileItemInput.getFieldName();

        UploadedFile uploadedFile = StrutsUploadedFile.Builder
                .create(file)
                .withOriginalName(fileName)
                .withContentType(fileItemInput.getContentType())
                .withInputName(fileItemInput.getFieldName())
                .build();

        if (uploadedFiles.containsKey(fieldName)) {
            uploadedFiles.get(fieldName).add(uploadedFile);
        } else {
            List<UploadedFile> infos = new ArrayList<>();
            infos.add(uploadedFile);
            uploadedFiles.put(fieldName, infos);
        }
    }

}
