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

import org.apache.struts2.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload2.core.FileUploadByteCountLimitException;
import org.apache.commons.fileupload2.core.FileUploadContentTypeException;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.apache.commons.fileupload2.core.FileUploadFileCountLimitException;
import org.apache.commons.fileupload2.core.FileUploadSizeException;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletDiskFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.LocalizedMessage;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class with some helper methods, it should be used
 * when starting development of another implementation of {@link MultiPartRequest}
 */
public abstract class AbstractMultiPartRequest implements MultiPartRequest {

    protected static final String STRUTS_MESSAGES_UPLOAD_ERROR_PARAMETER_TOO_LONG_KEY = "struts.messages.upload.error.parameter.too.long";

    private static final Logger LOG = LogManager.getLogger(AbstractMultiPartRequest.class);

    /**
     * Defines the internal buffer size used during streaming operations.
     */
    public static final int BUFFER_SIZE = 10240;

    /**
     * Internal list of raised errors to be passed to the Struts2 framework.
     */
    protected List<LocalizedMessage> errors = new ArrayList<>();

    /**
     * Specifies the maximum size of the entire request.
     */
    protected Long maxSize;

    /**
     * Specifies the maximum size of all the uploaded files.
     */
    protected Long maxSizeOfFiles;

    /**
     * Specifies the maximum number of files in one request.
     */
    protected Long maxFiles;

    /**
     * Specifies the maximum length of a string parameter in a multipart request.
     */
    protected Long maxStringLength;

    /**
     * Specifies the maximum size per a file in the request.
     */
    protected Long maxFileSize;

    /**
     * Specifies the buffer size to use during streaming.
     */
    protected int bufferSize = BUFFER_SIZE;

    /**
     * Defines default encoding to encode data from request used if not provided with request
     */
    protected String defaultEncoding;

    /**
     * Map between file fields and file data.
     */
    protected Map<String, List<UploadedFile>> uploadedFiles = new HashMap<>();

    /**
     * Map between non-file fields and values.
     */
    protected Map<String, List<String>> parameters = new HashMap<>();

    /**
     * @param bufferSize Sets the buffer size to be used.
     */
    @Inject(value = StrutsConstants.STRUTS_MULTIPART_BUFFER_SIZE, required = false)
    public void setBufferSize(String bufferSize) {
        this.bufferSize = Integer.parseInt(bufferSize);
    }

    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setDefaultEncoding(String enc) {
        this.defaultEncoding = enc;
    }

    /**
     * @param maxSize Injects the Struts multipart request maximum size.
     */
    @Inject(StrutsConstants.STRUTS_MULTIPART_MAX_SIZE)
    public void setMaxSize(String maxSize) {
        this.maxSize = Long.parseLong(maxSize);
    }

    /**
     * @param maxSizeOfFiles Injects the Struts maximum size of all uploaded files.
     */
    @Inject(value = StrutsConstants.STRUTS_MULTIPART_MAX_SIZE_OF_FILES, required = false)
    public void setMaxSizeOfFiles(String maxSizeOfFiles) {
        this.maxSizeOfFiles = Long.parseLong(maxSizeOfFiles);
    }

    /**
     * @param maxFiles Injects the Struts maximum size of an individual file uploaded.
     */
    @Inject(StrutsConstants.STRUTS_MULTIPART_MAX_FILES)
    public void setMaxFiles(String maxFiles) {
        this.maxFiles = Long.parseLong(maxFiles);
    }

    /**
     * @param maxFileSize Injects the Struts maximum number of files, which can be uploaded.
     */
    @Inject(value = StrutsConstants.STRUTS_MULTIPART_MAX_FILE_SIZE, required = false)
    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = Long.parseLong(maxFileSize);
    }

    /**
     * @param maxStringLength Injects the Struts maximum size of single form field.
     */
    @Inject(StrutsConstants.STRUTS_MULTIPART_MAX_STRING_LENGTH)
    public void setMaxStringLength(String maxStringLength) {
        this.maxStringLength = Long.parseLong(maxStringLength);
    }

    /**
     * Process the request extract file upload data
     *
     * @param request current {@link HttpServletRequest}
     * @param saveDir a temporary directory to store files
     */
    protected abstract void processUpload(HttpServletRequest request, String saveDir) throws IOException;

    /**
     * @param request multipart request
     * @return character encoding from request or {@link #defaultEncoding}
     */
    protected Charset readCharsetEncoding(HttpServletRequest request) {
        String charsetStr = StringUtils.isBlank(request.getCharacterEncoding())
                ? defaultEncoding
                : request.getCharacterEncoding();

        return Charset.forName(charsetStr);
    }

    /**
     * Creates an instance of {@link JakartaServletDiskFileUpload} used by the parser to extract uploaded files
     *
     * @param charset used charset from incoming request
     * @param saveDir a temporary folder to store uploaded files (not always needed)
     */
    protected abstract JakartaServletDiskFileUpload createJakartaFileUpload(Charset charset, Path saveDir);

    protected JakartaServletDiskFileUpload prepareServletFileUpload(Charset charset, Path saveDir) {
        JakartaServletDiskFileUpload servletFileUpload = createJakartaFileUpload(charset, saveDir);

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
        return servletFileUpload;
    }

    protected boolean exceedsMaxStringLength(String fieldName, String fieldValue) {
        if (maxStringLength != null && fieldValue.length() > maxStringLength) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Form field: {} of size: {} bytes exceeds limit of: {}.",
                        sanitizeNewlines(fieldName), fieldValue.length(), maxStringLength);
            }
            LocalizedMessage localizedMessage = new LocalizedMessage(this.getClass(),
                    STRUTS_MESSAGES_UPLOAD_ERROR_PARAMETER_TOO_LONG_KEY, null,
                    new Object[]{fieldName, maxStringLength, fieldValue.length()});
            if (!errors.contains(localizedMessage)) {
                errors.add(localizedMessage);
            }
            return true;
        }
        return false;
    }

    /**
     * Processes the upload.
     *
     * @param request the servlet request
     * @param saveDir location of the save dir
     */
    public void parse(HttpServletRequest request, String saveDir) throws IOException {
        try {
            processUpload(request, saveDir);
        } catch (FileUploadException e) {
            LOG.debug("Error parsing the multi-part request!", e);
            Class<? extends Throwable> exClass = FileUploadException.class;
            Object[] args = new Object[]{};

            if (e instanceof FileUploadByteCountLimitException ex) {
                exClass = ex.getClass();
                args = new Object[]{ex.getFieldName(), ex.getFileName(), ex.getPermitted(), ex.getActualSize()};
            } else if (e instanceof FileUploadFileCountLimitException ex) {
                exClass = ex.getClass();
                args = new Object[]{ex.getPermitted(), ex.getActualSize()};
            } else if (e instanceof FileUploadSizeException ex) {
                exClass = ex.getClass();
                args = new Object[]{ex.getPermitted(), ex.getActualSize()};
            } else if (e instanceof FileUploadContentTypeException ex) {
                exClass = ex.getClass();
                args = new Object[]{ex.getContentType()};
            }

            LocalizedMessage errorMessage = buildErrorMessage(exClass, e.getMessage(), args);
            if (!errors.contains(errorMessage)) {
                errors.add(errorMessage);
            }
        } catch (IOException e) {
            LOG.debug("Unable to parse request", e);
            LocalizedMessage errorMessage = buildErrorMessage(e.getClass(), e.getMessage(), new Object[]{});
            if (!errors.contains(errorMessage)) {
                errors.add(errorMessage);
            }
        }
    }

    /**
     * Build error message.
     *
     * @param exceptionClass a class of the exception
     * @param defaultMessage a default message to use
     * @param args           arguments
     * @return error message
     */
    protected LocalizedMessage buildErrorMessage(Class<? extends Throwable> exceptionClass, String defaultMessage, Object[] args) {
        String errorKey = "struts.messages.upload.error." + exceptionClass.getSimpleName();
        LOG.debug("Preparing error message for key: [{}]", errorKey);

        return new LocalizedMessage(this.getClass(), errorKey, defaultMessage, args);
    }

    /**
     * @param originalFileName file name
     * @return the canonical name based on the supplied filename
     */
    protected String getCanonicalName(final String originalFileName) {
        return FilenameUtils.getName(originalFileName);
    }

    /**
     * @deprecated since 7.0.1, use {@link StringUtils#normalizeSpace(String)} instead
     */
    @Deprecated
    protected String sanitizeNewlines(String before) {
        return before.replaceAll("\\R", "_");
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getErrors()
     */
    @Override
    public List<LocalizedMessage> getErrors() {
        return errors;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFileParameterNames()
     */
    @Override
    public Enumeration<String> getFileParameterNames() {
        return Collections.enumeration(uploadedFiles.keySet());
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getContentType(java.lang.String)
     */
    @Override
    public String[] getContentType(String fieldName) {
        return uploadedFiles.getOrDefault(fieldName, Collections.emptyList()).stream()
                .map(UploadedFile::getContentType)
                .toArray(String[]::new);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFile(java.lang.String)
     */
    @Override
    public UploadedFile[] getFile(String fieldName) {
        return uploadedFiles.getOrDefault(fieldName, Collections.emptyList())
                .toArray(UploadedFile[]::new);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFileNames(java.lang.String)
     */
    @Override
    public String[] getFileNames(String fieldName) {
        return uploadedFiles.getOrDefault(fieldName, Collections.emptyList()).stream()
                .map(file -> getCanonicalName(file.getOriginalName()))
                .toArray(String[]::new);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFilesystemName(java.lang.String)
     */
    @Override
    public String[] getFilesystemName(String fieldName) {
        return uploadedFiles.getOrDefault(fieldName, Collections.emptyList()).stream()
                .map(UploadedFile::getAbsolutePath)
                .toArray(String[]::new);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameter(java.lang.String)
     */
    @Override
    public String getParameter(String name) {
        List<String> paramValue = parameters.getOrDefault(name, Collections.emptyList());
        if (!paramValue.isEmpty()) {
            return paramValue.get(0);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameterNames()
     */
    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameterValues(java.lang.String)
     */
    @Override
    public String[] getParameterValues(String name) {
        List<String> values = parameters.get(name);
        if (values == null) {
            return null;
        }
        return values.toArray(new String[0]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#cleanUp()
     */
    @Override
    public void cleanUp() {
        try {
            LOG.debug("Performing File Upload temporary storage cleanup.");
            for (List<UploadedFile> uploadedFileList : uploadedFiles.values()) {
                for (UploadedFile uploadedFile : uploadedFileList) {
                    if (uploadedFile.isFile()) {
                        LOG.debug("Deleting file: {}", uploadedFile.getName());
                        if (!uploadedFile.delete()) {
                            LOG.warn("There was a problem attempting to delete file: {}", uploadedFile.getName());
                        }
                    } else {
                        LOG.debug("File: {} already deleted", uploadedFile.getName());
                    }
                }
            }
        } finally {
            uploadedFiles = new HashMap<>();
            parameters = new HashMap<>();
        }
    }

}
