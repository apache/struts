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
import org.apache.commons.fileupload2.core.FileUploadByteCountLimitException;
import org.apache.commons.fileupload2.core.FileUploadContentTypeException;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.apache.commons.fileupload2.core.FileUploadFileCountLimitException;
import org.apache.commons.fileupload2.core.FileUploadSizeException;
import org.apache.commons.fileupload2.core.RequestContext;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.LocalizedMessage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Multipart form data request adapter for Jakarta Commons Fileupload package.
 */
public class JakartaMultiPartRequest extends AbstractMultiPartRequest {

    private static final Logger LOG = LogManager.getLogger(JakartaMultiPartRequest.class);

    protected Map<String, List<UploadedFile>> uploadedFiles = new HashMap<>();

    /**
     * Keeps info about normal form fields
     */
    protected Map<String, List<String>> params = new HashMap<>();

    /**
     * Creates a new request wrapper to handle multipart data using methods adapted from Jason Pell's
     * multipart classes (see class description).
     *
     * @param saveDir the directory to save off the file
     * @param request the request containing the multipart
     * @throws java.io.IOException is thrown if encoding fails.
     */
    public void parse(HttpServletRequest request, String saveDir) throws IOException {
        try {
            setLocale(request);
            processUpload(request, saveDir);
        } catch (FileUploadException e) {
            LOG.debug("Request exceeded size limit!", e);
            LocalizedMessage errorMessage;
            if (e instanceof FileUploadByteCountLimitException) {
                FileUploadByteCountLimitException ex = (FileUploadByteCountLimitException) e;
                errorMessage = buildErrorMessage(e, new Object[]{
                        ex.getFieldName(), ex.getFileName(), ex.getPermitted(), ex.getActualSize()
                });
            } else if (e instanceof FileUploadFileCountLimitException) {
                FileUploadFileCountLimitException ex = (FileUploadFileCountLimitException) e;
                errorMessage = buildErrorMessage(e, new Object[]{
                        ex.getPermitted(), ex.getActualSize()
                });
            } else if (e instanceof FileUploadSizeException) {
                FileUploadSizeException ex = (FileUploadSizeException) e;
                errorMessage = buildErrorMessage(e, new Object[]{
                        ex.getPermitted(), ex.getActualSize()
                });
            } else if (e instanceof FileUploadContentTypeException) {
                FileUploadContentTypeException ex = (FileUploadContentTypeException) e;
                errorMessage = buildErrorMessage(e, new Object[]{
                        ex.getContentType()
                });
            } else {
                errorMessage = buildErrorMessage(e, new Object[]{});
            }

            if (!errors.contains(errorMessage)) {
                errors.add(errorMessage);
            }
        } catch (Exception e) {
            LOG.debug("Unable to parse request", e);
            LocalizedMessage errorMessage = buildErrorMessage(e, new Object[]{});
            if (!errors.contains(errorMessage)) {
                errors.add(errorMessage);
            }
        }
    }

    protected void processUpload(HttpServletRequest request, String saveDir) throws IOException {
        if (!JakartaServletFileUpload.isMultipartContent(request)) {
            LOG.debug("Http request isn't: {}, stop processing", AbstractFileUpload.MULTIPART_FORM_DATA);
            return;
        }
        for (DiskFileItem item : parseRequest(request, saveDir)) {
            LOG.debug("Processing a form field: {}", sanitizeNewlines(item.getFieldName()));
            if (item.isFormField()) {
                processNormalFormField(item, request.getCharacterEncoding());
            } else {
                LOG.debug("Processing a file: {}", sanitizeNewlines(item.getFieldName()));
                processFileField(item);
            }
        }
    }

    protected void processFileField(DiskFileItem item) {
        // Skip file uploads that don't have a file name - meaning that no file was selected.
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            LOG.debug("No file has been uploaded for the field: {}", sanitizeNewlines(item.getFieldName()));
            return;
        }

        List<UploadedFile> values;
        if (uploadedFiles.get(item.getFieldName()) != null) {
            values = uploadedFiles.get(item.getFieldName());
        } else {
            values = new ArrayList<>();
        }

        UploadedFile uploadedFile = StrutsUploadedFile.Builder
                .create(item.getPath().toFile())
                .withOriginalName(item.getName())
                .withContentType(item.getContentType())
                .build();
        values.add(uploadedFile);

        uploadedFiles.put(item.getFieldName(), values);
    }

    protected void processNormalFormField(DiskFileItem item, String charset) throws IOException {
        try {
            LOG.debug("Item is a normal form field");
            Charset encoding = Charset.forName(charset);

            List<String> values;
            if (params.get(item.getFieldName()) != null) {
                values = params.get(item.getFieldName());
            } else {
                values = new ArrayList<>();
            }

            long size = item.getSize();
            if (size > maxStringLength) {
                LOG.debug("Form field: {} of size: {} bytes exceeds limit of: {}.", sanitizeNewlines(item.getFieldName()), size, maxStringLength);
                LocalizedMessage localizedMessage = new LocalizedMessage(this.getClass(),
                        STRUTS_MESSAGES_UPLOAD_ERROR_PARAMETER_TOO_LONG_KEY, null,
                        new Object[]{item.getFieldName(), maxStringLength, size});
                if (!errors.contains(localizedMessage)) {
                    errors.add(localizedMessage);
                }
                return;
            }
            if (size == 0) {
                values.add(StringUtils.EMPTY);
            } else {
                values.add(item.getString(encoding));
            }
            params.put(item.getFieldName(), values);
        } finally {
            item.delete();
        }
    }

    protected List<DiskFileItem> parseRequest(HttpServletRequest servletRequest, String saveDir) throws FileUploadException {
        DiskFileItemFactory fileItemFactory = createDiskFileItemFactory(saveDir);
        JakartaServletFileUpload<DiskFileItem, DiskFileItemFactory> upload = createServletFileUpload(fileItemFactory);
        return upload.parseRequest(createRequestContext(servletRequest));
    }

    protected JakartaServletFileUpload<DiskFileItem, DiskFileItemFactory> createServletFileUpload(DiskFileItemFactory fileItemFactory) {
        JakartaServletFileUpload<DiskFileItem, DiskFileItemFactory> servletFileUpload = new JakartaServletFileUpload<>(fileItemFactory);
        if (maxSize != null) {
            servletFileUpload.setSizeMax(maxSize);
        }
        if (maxFiles != null) {
            servletFileUpload.setFileCountMax(maxFiles);
        }
        if (maxFileSize != null) {
            servletFileUpload.setFileSizeMax(maxFileSize);
        }
        return servletFileUpload;
    }

    protected DiskFileItemFactory createDiskFileItemFactory(String saveDir) {
        DiskFileItemFactory.Builder builder = DiskFileItemFactory.builder();
        if (saveDir != null) {
            LOG.debug("Using file save directory: {}", saveDir);
            builder.setPath(saveDir);
        }
        // sets minimal buffer size to always write file to disk
        builder.setBufferSize(1);
        return builder.get();
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFileParameterNames()
     */
    public Enumeration<String> getFileParameterNames() {
        return Collections.enumeration(uploadedFiles.keySet());
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getContentType(java.lang.String)
     */
    public String[] getContentType(String fieldName) {
        List<UploadedFile> uploadedFilesList = uploadedFiles.get(fieldName);

        if (uploadedFilesList == null) {
            return null;
        }
        return uploadedFilesList.stream().map(UploadedFile::getContentType).toArray(String[]::new);
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
        return uploadedFileList.stream()
                .map(file -> getCanonicalName(file.getName()))
                .toArray(String[]::new);
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
        List<String> paramValue = params.get(name);
        if (paramValue != null && !paramValue.isEmpty()) {
            return paramValue.get(0);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameterNames()
     */
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(params.keySet());
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameterValues(java.lang.String)
     */
    public String[] getParameterValues(String name) {
        List<String> v = params.get(name);
        if (v != null && !v.isEmpty()) {
            return v.toArray(new String[0]);
        }

        return null;
    }

    /**
     * Creates a RequestContext needed by Jakarta Commons Upload.
     *
     * @param req the request.
     * @return a new request context.
     */
    protected RequestContext createRequestContext(final HttpServletRequest req) {
        return new RequestContext() {
            public String getCharacterEncoding() {
                return req.getCharacterEncoding();
            }

            public String getContentType() {
                return req.getContentType();
            }

            public long getContentLength() {
                return req.getContentLength();
            }

            public InputStream getInputStream() throws IOException {
                InputStream in = req.getInputStream();
                if (in == null) {
                    throw new IOException("Missing content in the request");
                }
                return req.getInputStream();
            }
        };
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#cleanUp()
     */
    public void cleanUp() {
        Set<String> names = uploadedFiles.keySet();
        names.forEach(name -> {
            List<UploadedFile> uploadedFileList = uploadedFiles.get(name);
            uploadedFileList.forEach(file -> {
                LOG.debug("Removing file: {}", file.getName());
                file.delete();
            });
        });
    }

}
