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
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileUploadByteCountLimitException;
import org.apache.commons.fileupload2.core.FileUploadContentTypeException;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.apache.commons.fileupload2.core.FileUploadFileCountLimitException;
import org.apache.commons.fileupload2.core.FileUploadSizeException;
import org.apache.commons.fileupload2.core.RequestContext;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletDiskFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.LocalizedMessage;
import org.apache.struts2.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.normalizeSpace;

/**
 * Abstract class with some helper methods, it should be used
 * when starting development of another implementation of {@link MultiPartRequest}
 */
public abstract class AbstractMultiPartRequest implements MultiPartRequest {

    protected static final String STRUTS_MESSAGES_UPLOAD_ERROR_PARAMETER_TOO_LONG_KEY = "struts.messages.upload.error.parameter.too.long";

    private static final Logger LOG = LogManager.getLogger(AbstractMultiPartRequest.class);

    /**
     * Verified once per JVM: whether the commons-fileupload2 API on the classpath matches what
     * Struts compiled against. Guards against a mismatched milestone resolving at runtime.
     */
    private static volatile boolean fileUploadApiVerified;

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
     * Specifies the maximum number of non-file form fields (parameters) in one request.
     */
    protected Long maxParameterCount;

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
     * @param maxParameterCount Injects the Struts maximum number of non-file form fields.
     */
    @Inject(StrutsConstants.STRUTS_MULTIPART_MAX_PARAMETER_COUNT)
    public void setMaxParameterCount(String maxParameterCount) {
        this.maxParameterCount = Long.parseLong(maxParameterCount);
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
    protected JakartaServletDiskFileUpload createJakartaFileUpload(Charset charset, Path saveDir) {
        DiskFileItemFactory.Builder builder = DiskFileItemFactory.builder();

        LOG.debug("Using file save directory: {}", saveDir);
        builder.setPath(saveDir);

        LOG.debug("Sets buffer size: {}", bufferSize);
        builder.setBufferSize(bufferSize);

        LOG.debug("Using charset: {}", charset);
        builder.setCharset(charset);

        DiskFileItemFactory factory = builder.get();
        return new JakartaServletDiskFileUpload(factory);
    }

    protected JakartaServletDiskFileUpload prepareServletFileUpload(Charset charset, Path saveDir) {
        ensureFileUploadApiVerified();
        JakartaServletDiskFileUpload servletFileUpload = createJakartaFileUpload(charset, saveDir);

        if (maxSize != null) {
            LOG.debug("Applies max size: {} to file upload request", maxSize);
            servletFileUpload.setMaxSize(maxSize);
        }
        if (maxFiles != null && maxParameterCount != null) {
            long maxParts = maxFiles + maxParameterCount;
            LOG.debug("Applies total parts backstop: {} to file upload request", maxParts);
            servletFileUpload.setMaxFileCount(maxParts);
        }
        if (maxFileSize != null) {
            LOG.debug("Applies max size of single file: {} to file upload request", maxFileSize);
            servletFileUpload.setMaxFileSize(maxFileSize);
        }
        return servletFileUpload;
    }

    /**
     * Verifies once per JVM that the commons-fileupload2 API on the classpath matches what Struts
     * compiled against, failing fast with an actionable message instead of a deep-stack
     * {@link NoSuchMethodError} when a mismatched milestone is resolved.
     */
    private static void ensureFileUploadApiVerified() {
        if (!fileUploadApiVerified) {
            verifyFileUploadApi(JakartaServletDiskFileUpload.class);
            fileUploadApiVerified = true;
        }
    }

    /**
     * Probes {@code uploadClass} for the size-limit setters Struts invokes in
     * {@link #prepareServletFileUpload}. Package-private for testing.
     *
     * @param uploadClass the file upload class to verify
     * @throws StrutsException if any required method is absent, indicating a binary-incompatible
     *                         commons-fileupload2 version on the classpath
     */
    static void verifyFileUploadApi(Class<?> uploadClass) {
        for (String method : new String[]{"setMaxSize", "setMaxFileCount", "setMaxFileSize"}) {
            try {
                uploadClass.getMethod(method, long.class);
            } catch (NoSuchMethodException e) {
                throw new StrutsException(String.format(
                        "Incompatible Apache Commons FileUpload on the classpath: %s.%s(long) is missing. " +
                                "Detected commons-fileupload2-core version [%s] and commons-fileupload2-jakarta-servlet6 version [%s]. " +
                                "Align commons-fileupload2-core with commons-fileupload2-jakarta-servlet6 (use the same release for both).",
                        uploadClass.getName(), method,
                        implementationVersion(AbstractFileUpload.class),
                        implementationVersion(uploadClass)), e);
            }
        }
    }

    private static String implementationVersion(Class<?> clazz) {
        Package pkg = clazz.getPackage();
        String version = pkg != null ? pkg.getImplementationVersion() : null;
        return version != null ? version : "unknown";
    }

    protected RequestContext createRequestContext(HttpServletRequest request) {
        return new StrutsRequestContext(request);
    }

    protected boolean exceedsMaxStringLength(String fieldName, String fieldValue) {
        if (maxStringLength != null && fieldValue.length() > maxStringLength) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Form field: {} of size: {} bytes exceeds limit of: {}.",
                        normalizeSpace(fieldName), fieldValue.length(), maxStringLength);
            }
            LocalizedMessage localizedMessage = new LocalizedMessage(this.getClass(),
                    STRUTS_MESSAGES_UPLOAD_ERROR_PARAMETER_TOO_LONG_KEY, null,
                    new Object[]{fieldName, maxStringLength, fieldValue.length()});
            addErrorIfAbsent(localizedMessage);
            return true;
        }
        return false;
    }

    /**
     * Fail-closed guard: throws when accepting another file would exceed {@link #maxFiles}.
     * A negative {@link #maxFiles} means "no limit", matching commons-fileupload2's own
     * {@code fileCountMax = -1} convention (see {@code AbstractFileUpload.setFileCountMax}).
     *
     * @param currentFileCount number of files already accepted in this request
     * @param fileName         name of the file being considered (for logging)
     */
    protected void enforceMaxFiles(int currentFileCount, String fileName) throws FileUploadFileCountLimitException {
        if (maxFiles != null && maxFiles >= 0 && currentFileCount >= maxFiles) {
            LOG.debug("Cannot accept another file: {} as it would exceed max files: {}", normalizeSpace(fileName), maxFiles);
            throw new FileUploadFileCountLimitException(
                    String.format("Request exceeds allowed number of files, permitted: %s", maxFiles),
                    maxFiles, currentFileCount + 1L);
        }
    }

    /**
     * Fail-closed guard: throws when accepting another form field would exceed {@link #maxParameterCount}.
     * A negative {@link #maxParameterCount} means "no limit", matching the same convention as
     * {@link #maxFiles}.
     *
     * @param currentParameterCount number of form fields already accepted in this request
     * @param fieldName             name of the field being considered (for logging)
     */
    protected void enforceMaxParameterCount(int currentParameterCount, String fieldName) throws FileUploadParameterCountLimitException {
        if (maxParameterCount != null && maxParameterCount >= 0 && currentParameterCount >= maxParameterCount) {
            LOG.debug("Cannot accept another parameter: {} as it would exceed max parameter count: {}", normalizeSpace(fieldName), maxParameterCount);
            throw new FileUploadParameterCountLimitException(
                    String.format("Request exceeds allowed number of parameters, permitted: %s", maxParameterCount),
                    maxParameterCount, currentParameterCount + 1L);
        }
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
            LOG.warn("Error parsing the multi-part request!", e);
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
            } else if (e instanceof FileUploadParameterCountLimitException ex) {
                exClass = ex.getClass();
                args = new Object[]{ex.getPermitted(), ex.getActual()};
            }

            LocalizedMessage errorMessage = buildErrorMessage(exClass, e.getMessage(), args);
            addErrorIfAbsent(errorMessage);
            clearCollectedData();
        } catch (IOException e) {
            LOG.warn("Unable to parse request", e);
            LocalizedMessage errorMessage = buildErrorMessage(e.getClass(), e.getMessage(), new Object[]{});
            addErrorIfAbsent(errorMessage);
        }
    }

    private void addErrorIfAbsent(LocalizedMessage errorMessage) {
        if (!errors.contains(errorMessage)) {
            errors.add(errorMessage);
        }
    }

    /**
     * Fail-closed: discards everything collected so far so a rejected request exposes
     * no partial parameters or files to the action. Deletes partial upload files first
     * to avoid leaking temporary files.
     */
    private void clearCollectedData() {
        for (List<UploadedFile> files : uploadedFiles.values()) {
            for (UploadedFile file : files) {
                if (file.isFile() && !file.delete()) {
                    LOG.warn("Could not delete partial upload file: {}", file.getName());
                }
            }
        }
        uploadedFiles.clear();
        parameters.clear();
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

    /**
     * Creates a secure temporary file in the specified directory using UUID-based naming.
     * This method ensures files are created in a controlled location rather than the
     * system temporary directory, reducing security risks.
     *
     * @param fileName the original filename for logging purposes
     * @param location the directory where the temporary file should be created
     * @return a new temporary file in the specified location
     */
    protected File createTemporaryFile(String fileName, Path location) {
        String uid = UUID.randomUUID().toString().replace("-", "_");
        File file = location.resolve("upload_" + uid + ".tmp").toFile();
        LOG.debug("Creating temporary file: {} (originally: {})", file.getName(), fileName);
        return file;
    }

    /**
     * Validates that an uploaded file is not empty (0 bytes) and adds an error if it is.
     *
     * <p>Empty file uploads are rejected as they are not considered valid uploads.
     * This validation ensures consistent behavior across all multipart implementations
     * and provides proper user feedback when empty files are uploaded.</p>
     *
     * <p>When an empty file is detected:</p>
     * <ul>
     *   <li>A debug log message is written with field name and filename</li>
     *   <li>A localized error message is created and added to the errors list</li>
     *   <li>The method returns true to indicate the file should be rejected</li>
     * </ul>
     *
     * @param fileSize  the size of the uploaded file in bytes
     * @param fileName  the original filename of the uploaded file
     * @param fieldName the form field name containing the file upload
     * @return true if the file is empty and should be rejected, false otherwise
     * @see #buildErrorMessage(Class, String, Object[])
     */
    protected boolean rejectEmptyFile(long fileSize, String fileName, String fieldName) {
        if (fileSize == 0) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Rejecting empty file upload for field: {} with filename: {}",
                        normalizeSpace(fieldName), normalizeSpace(fileName));
            }
            LocalizedMessage errorMessage = buildErrorMessage(
                    IllegalArgumentException.class,
                    "Empty files are not allowed",
                    new Object[]{fileName, fieldName}
            );
            addErrorIfAbsent(errorMessage);
            return true;
        }
        return false;
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

    /**
     * Delete file if exists, reports warning if file cannot be deleted
     * 
     * @param filePath a file to delete
     */
    protected void deleteFile(Path filePath) {
        if (Files.exists(filePath)) {
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                LOG.warn("Failed to delete file: {}", filePath, e);
            }
        }
    }
}
