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

import org.apache.commons.fileupload.FileItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.LocalizedMessage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Pure Servlet API 3.1 based implementation
 */
public class ServletMultiPartRequest extends AbstractMultiPartRequest {

    private static final Logger LOG = LogManager.getLogger(ServletMultiPartRequest.class);

    private Map<String, List<FileData>> uploadedFiles = new HashMap<>();
    private Map<String, List<String>> parameters = new HashMap<>();

    @Override
    public void parse(HttpServletRequest request, String saveDir) throws IOException {
        try {
            if (isSizeLimitExceeded(request)) {
                applySizeLimitExceededError(request);
                return;
            }
            parseParts(request, saveDir);
        } catch (ServletException e) {
            LOG.warn("Error occurred during parsing of multi part request", e);
            LocalizedMessage errorMessage = buildErrorMessage(e, new Object[]{e.getMessage()});
            if (!errors.contains(errorMessage)) {
                errors.add(errorMessage);
            }
        }
    }

    private void parseParts(HttpServletRequest request, String saveDir) throws IOException, ServletException {
        Collection<Part> parts = request.getParts();
        if (parts.isEmpty()) {
            LocalizedMessage error = buildErrorMessage(new IOException(), new Object[]{"No boundary defined!"});
            if (!errors.contains(error)) {
                errors.add(error);
            }
            return;
        }
        for (Part part : parts) {
            if (part.getSubmittedFileName() == null) { // normal field
                LOG.debug("Ignoring a normal form field: {}", part.getName());
            } else { // file upload
                LOG.debug("Storing file: {} in save dir: {}", part.getSubmittedFileName(), saveDir);
                parseFile(part, saveDir);
            }
        }
    }

    private boolean isSizeLimitExceeded(HttpServletRequest request) {
        if (request.getContentLength() > -1) {
            return maxSizeProvided && request.getContentLength() > maxSize;
        } else {
            LOG.debug("Request Content Length is: {} which means the size overflows 2 GB!", request.getContentLength());
            return true;
        }
    }

    private void applySizeLimitExceededError(HttpServletRequest request) {
        String exceptionMessage = "Request size: " + request.getContentLength() + " exceeded maximum size limit: " + maxSize;
        SizeLimitExceededException exception = new SizeLimitExceededException(exceptionMessage);
        LocalizedMessage message = buildErrorMessage(exception, new Object[]{request.getContentLength(), maxSize});
        if (!errors.contains(message)) {
            errors.add(message);
        }
    }

    private void parseFile(Part part, String saveDir) throws IOException {
        File file = extractFile(part, saveDir);
        List<FileData> data = uploadedFiles.get(part.getName());
        if (data == null) {
            data = new ArrayList<>();
        }
        data.add(new FileData(file, part.getContentType(), part.getSubmittedFileName()));
        uploadedFiles.put(part.getName(), data);
    }

    private File extractFile(Part part, String saveDir) throws IOException {
        String name = part.getSubmittedFileName()
            .substring(part.getSubmittedFileName().lastIndexOf('/') + 1)
            .substring(part.getSubmittedFileName().lastIndexOf('\\') + 1);

        String prefix = name;
        String suffix = "";

        if (name.contains(".")) {
            prefix = name.substring(0, name.lastIndexOf('.'));
            suffix = name.substring(name.lastIndexOf('.'));
        }

        if (prefix.length() < 3) {
            prefix = UUID.randomUUID().toString();
        }

        File tempFile = File.createTempFile(prefix + "_", suffix, new File(saveDir));
        LOG.debug("Stored file: {} as temporary file: {}", part.getSubmittedFileName(), tempFile.getName());
        return tempFile;
    }

    @Override
    public Enumeration<String> getFileParameterNames() {
        return Collections.enumeration(uploadedFiles.keySet());
    }

    @Override
    public String[] getContentType(String fieldName) {
        List<FileData> fileData = uploadedFiles.get(fieldName);
        if (fileData == null) {
            LOG.debug("No file data for: {}", fieldName);
            return null;
        }
        return fileData.stream().map(FileData::getContentType).toArray(String[]::new);
    }

    @Override
    public UploadedFile[] getFile(String fieldName) {
        List<FileData> fileData = uploadedFiles.get(fieldName);
        if (fileData == null) {
            LOG.debug("No file data for: {}", fieldName);
            return null;
        }

        return fileData.stream().map(data -> new StrutsUploadedFile(data.getFile())).toArray(StrutsUploadedFile[]::new);
    }

    @Override
    public String[] getFileNames(String fieldName) {
        List<FileData> fileData = uploadedFiles.get(fieldName);
        if (fileData == null) {
            LOG.debug("No file data for: {}", fieldName);
            return null;
        }

        return fileData.stream().map(FileData::getOriginalName).toArray(String[]::new);
    }

    @Override
    public String[] getFilesystemName(String fieldName) {
        List<FileData> fileData = uploadedFiles.get(fieldName);
        if (fileData == null) {
            LOG.debug("No file data for: {}", fieldName);
            return null;
        }

        return fileData.stream().map(data -> data.getFile().getName()).toArray(String[]::new);
    }

    @Override
    public String getParameter(String name) {
        List<String> params = parameters.get(name);
        if (params != null && params.size() > 0) {
            return params.get(0);
        }
        LOG.debug("Ignoring parameter: {}", name);
        return null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        List<String> v = parameters.get(name);
        if (v != null && v.size() > 0) {
            return v.toArray(new String[0]);
        }

        LOG.debug("Ignoring values for parameter: {}", name);
        return null;
    }

    @Override
    public void cleanUp() {
        for (List<FileData> fileData : uploadedFiles.values()) {
            for (FileData data : fileData) {
                LOG.debug("Removing file: {} {}", data.getOriginalName(), data.getFile().getAbsolutePath());
                if (!data.getFile().delete()) {
                    LOG.warn("Couldn't delete file: {}", data.getFile().getAbsolutePath());
                }
            }
        }
        uploadedFiles = new HashMap<>();
        parameters = new HashMap<>();
    }

    public static class FileData implements Serializable {

        private final File file;
        private final String contentType;
        private final String originalName;

        public FileData(File file, String contentType, String originalName) {
            this.file = file;
            this.contentType = contentType;
            this.originalName = originalName;
        }

        public File getFile() {
            return file;
        }

        public String getContentType() {
            return contentType;
        }

        public String getOriginalName() {
            return originalName;
        }
    }

    public static class SizeLimitExceededException extends Exception {
        public SizeLimitExceededException(String message) {
            super(message);
        }
    }
}
