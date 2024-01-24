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
import org.apache.commons.fileupload2.core.AbstractFileUpload;
import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.struts2.dispatcher.LocalizedMessage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
     * Map between file fields and file data.
     */
    protected Map<String, List<UploadedFile>> uploadedFiles = new HashMap<>();

    /**
     * Map between non-file fields and values.
     */
    protected Map<String, List<String>> parameters = new HashMap<>();

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#cleanUp()
     */
    public void cleanUp() {
        LOG.debug("Performing File Upload temporary storage cleanup.");
        for (List<UploadedFile> uploadedFileList : uploadedFiles.values()) {
            for (UploadedFile uploadedFile : uploadedFileList) {
                LOG.debug("Deleting file '{}'.", uploadedFile.getName());
                if (!uploadedFile.delete()) {
                    LOG.warn("There was a problem attempting to delete file '{}'.", uploadedFile.getName());
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getContentType(java.lang.String)
     */
    public String[] getContentType(String fieldName) {
        List<UploadedFile> uploadedFileList = uploadedFiles.get(fieldName);
        if (uploadedFileList == null) {
            return null;
        }

        List<String> types = new ArrayList<>(uploadedFileList.size());
        for (UploadedFile uploadedFile : uploadedFileList) {
            types.add(uploadedFile.getContentType());
        }

        return types.toArray(new String[0]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFile(java.lang.String)
     */
    public UploadedFile[] getFile(String fieldName) {
        List<UploadedFile> uploadedFileList = uploadedFiles.get(fieldName);
        if (uploadedFileList == null) {
            return null;
        }
        return uploadedFileList.toArray(UploadedFile[]::new);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFileNames(java.lang.String)
     */
    public String[] getFileNames(String fieldName) {
        List<UploadedFile> uploadedFileList = uploadedFiles.get(fieldName);
        if (uploadedFileList == null) {
            return null;
        }

        List<String> names = new ArrayList<>(uploadedFileList.size());
        for (UploadedFile uploadedFile : uploadedFileList) {
            names.add(getCanonicalName(uploadedFile.getOriginalName()));
        }

        return names.toArray(new String[0]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFileParameterNames()
     */
    public Enumeration<String> getFileParameterNames() {
        return Collections.enumeration(uploadedFiles.keySet());
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFilesystemName(java.lang.String)
     */
    public String[] getFilesystemName(String fieldName) {
        List<UploadedFile> uploadedFileList = uploadedFiles.get(fieldName);
        if (uploadedFileList == null) {
            return null;
        }
        return uploadedFileList.stream().map(UploadedFile::getAbsolutePath).toArray(String[]::new);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameter(java.lang.String)
     */
    public String getParameter(String name) {
        List<String> values = parameters.get(name);
        if (values != null && !values.isEmpty()) {
            return values.get(0);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameterNames()
     */
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameterValues(java.lang.String)
     */
    public String[] getParameterValues(String name) {
        List<String> values = parameters.get(name);
        if (values != null && !values.isEmpty()) {
            return values.toArray(new String[0]);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#parse(jakarta.servlet.http.HttpServletRequest, java.lang.String)
     */
    public void parse(HttpServletRequest request, String saveDir) throws IOException {
        try {
            setLocale(request);
            processUpload(request, saveDir);
        } catch (Exception e) {
            LOG.debug("Error occurred during parsing of multi part request", e);
            LocalizedMessage errorMessage = buildErrorMessage(e, new Object[]{});
            if (!errors.contains(errorMessage)) {
                errors.add(errorMessage);
            }
        }
    }

    /**
     * Processes the upload.
     *
     * @param request the servlet request
     * @param saveDir location of the save dir
     */
    protected void processUpload(HttpServletRequest request, String saveDir) throws Exception {
        // Sanity check that the request is a multi-part/form-data request.
        if (!JakartaServletFileUpload.isMultipartContent(request)) {
            LOG.debug("Http request isn't: {}, stop processing", AbstractFileUpload.MULTIPART_FORM_DATA);
            return;
        }

        // Interface with Commons FileUpload API
        // Using the Streaming API
        JakartaServletFileUpload<DiskFileItem, DiskFileItemFactory> servletFileUpload = new JakartaServletFileUpload<>();
        if (maxSize != null) {
            LOG.debug("Applies max size: {} to file upload request", maxSize);
            servletFileUpload.setSizeMax(maxSize);
        }
        if (maxFiles != null) {
            LOG.debug("Applies max files number: {} to file upload request", maxFiles);
            servletFileUpload.setFileCountMax(maxFiles);
        }
        if (maxFileSize != null) {
            LOG.debug("Applies max size of single file: {} to file upload request", maxFileSize);
            servletFileUpload.setFileSizeMax(maxFileSize);
        }

        // Iterate the file items
        servletFileUpload.getItemIterator(request).forEachRemaining(item -> {
            if (item.isFormField()) {
                LOG.debug("Processing a form field: {}", sanitizeNewlines(item.getFieldName()));
                processFileItemAsFormField(item);
            } else {
                LOG.debug("Processing a file: {}", sanitizeNewlines(item.getFieldName()));
                processFileItemAsFileField(item, saveDir);
            }
        });
    }

    /**
     * Processes the FileItem as a normal form field.
     *
     * @param fileItemInput a form field item input
     */
    protected void processFileItemAsFormField(FileItemInput fileItemInput) {
        String fieldName = fileItemInput.getFieldName();
        try {
            List<String> values;

            String fieldValue = fileItemInput.getInputStream().toString();
            if (!parameters.containsKey(fieldName)) {
                values = new ArrayList<>();
                parameters.put(fieldName, values);
            } else {
                values = parameters.get(fieldName);
            }
            values.add(fieldValue);
        } catch (IOException e) {
            LOG.warn(new ParameterizedMessage("Failed to handle form field: '{}'", sanitizeNewlines(fieldName)), e);
        }
    }

    /**
     * Processes the FileItem as a file field.
     *
     * @param fileItemInput file item representing upload file
     * @param location      location
     */
    protected void processFileItemAsFileField(FileItemInput fileItemInput, String location) {
        // Skip file uploads that don't have a file name - meaning that no file was selected.
        if (fileItemInput.getName() == null || fileItemInput.getName().trim().isEmpty()) {
            LOG.debug("No file has been uploaded for the field: {}", sanitizeNewlines(fileItemInput.getFieldName()));
            return;
        }

        File file = null;
        try {
            // Create the temporary upload file.
            file = createTemporaryFile(fileItemInput.getName(), location);

            if (streamFileToDisk(fileItemInput, file)) {
                createUploadFile(fileItemInput, file);
            }
        } catch (IOException e) {
            if (file != null) {
                try {
                    if (!file.delete()) {
                        LOG.warn("Could not delete the file: {}", file.getAbsoluteFile());
                    }
                } catch (SecurityException se) {
                    LOG.warn("Failed to delete '{}' due to security exception above.", file.getName(), se);
                }
            }
        }
    }

    /**
     * Creates a temporary file based on the given filename and location.
     *
     * @param fileName file name
     * @param location location
     * @return a temporary file based on the given filename and location
     * @throws IOException in case of IO errors
     */
    protected File createTemporaryFile(String fileName, String location) throws IOException {
        String name = fileName
                .substring(fileName.lastIndexOf('/') + 1)
                .substring(fileName.lastIndexOf('\\') + 1);

        String prefix = name;
        String suffix = "";

        if (name.contains(".")) {
            prefix = name.substring(0, name.lastIndexOf('.'));
            suffix = name.substring(name.lastIndexOf('.'));
        }

        if (prefix.length() < 3) {
            prefix = UUID.randomUUID().toString();
        }

        File file = File.createTempFile(prefix + "_", suffix, new File(location));
        LOG.debug("Creating temporary file '{}' (originally '{}').", file.getName(), fileName);
        return file;
    }

    /**
     * Streams the file upload stream to the specified file.
     *
     * @param fileItemInput file item input
     * @param file          the file
     * @return true if stream was successfully
     * @throws IOException in case of IO errors
     */
    protected boolean streamFileToDisk(FileItemInput fileItemInput, File file) throws IOException {
        try (InputStream input = fileItemInput.getInputStream();
             OutputStream output = new BufferedOutputStream(Files.newOutputStream(file.toPath()), bufferSize)) {
            byte[] buffer = new byte[bufferSize];
            LOG.debug("Streaming file using buffer size: {}", bufferSize);
            for (int length; ((length = input.read(buffer)) > 0); ) {
                output.write(buffer, 0, length);
            }
        } catch (IOException e) {
            LOG.error(new ParameterizedMessage("Cannot write input file: {} into file stream: {}",
                    fileItemInput.getName(), file.getAbsolutePath()), e);
            return false;
        }
        return true;
    }

    /**
     * Create {@link UploadedFile} abstraction over uploaded file
     *
     * @param fileItemInput file item stream
     * @param file          the file
     */
    protected void createUploadFile(FileItemInput fileItemInput, File file) {
        // gather attributes from file upload stream.
        String fileName = fileItemInput.getName();
        String fieldName = fileItemInput.getFieldName();
        // create internal structure
        UploadedFile uploadedFile = StrutsUploadedFile.Builder
                .create(file)
                .withOriginalName(fileName)
                .withContentType(fileItemInput.getContentType())
                .build();
        // append or create new entry.
        if (!uploadedFiles.containsKey(fieldName)) {
            List<UploadedFile> infos = new ArrayList<>();
            infos.add(uploadedFile);
            uploadedFiles.put(fieldName, infos);
        } else {
            uploadedFiles.get(fieldName).add(uploadedFile);
        }
    }

}
