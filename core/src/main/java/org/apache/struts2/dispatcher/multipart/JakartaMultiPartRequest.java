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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.normalizeSpace;

/**
 * Multipart form data request adapter for Jakarta Commons FileUpload package.
 * 
 * <p>This implementation provides secure handling of multipart requests with proper
 * resource management and cleanup. It tracks all temporary files created during
 * the upload process and ensures they are properly cleaned up to prevent
 * resource leaks and security vulnerabilities.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Automatic tracking and cleanup of temporary files</li>
 *   <li>Proper error handling with user-friendly error messages</li>
 *   <li>Support for both in-memory and disk-based file uploads</li>
 *   <li>Extensible cleanup mechanisms for customization</li>
 * </ul>
 * 
 * <p>Usage example:</p>
 * <pre>
 * JakartaMultiPartRequest multipartRequest = new JakartaMultiPartRequest();
 * try {
 *     multipartRequest.parse(request, "/tmp/uploads");
 *     // Process uploaded files
 *     for (String fieldName : multipartRequest.getFileParameterNames()) {
 *         List&lt;UploadedFile&gt; files = multipartRequest.getFile(fieldName);
 *         // Handle files
 *     }
 * } finally {
 *     multipartRequest.cleanUp(); // Always clean up resources
 * }
 * </pre>
 * 
 * @see AbstractMultiPartRequest
 * @see org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletDiskFileUpload
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

    /**
     * Processes the multipart upload request using Jakarta Commons FileUpload.
     * 
     * <p>This method handles the core upload processing by:</p>
     * <ol>
     *   <li>Reading the character encoding from the request</li>
     *   <li>Preparing the Jakarta servlet file upload handler</li>
     *   <li>Creating a request context for processing</li>
     *   <li>Iterating through all form items (fields and files)</li>
     *   <li>Processing each item appropriately based on its type</li>
     * </ol>
     * 
     * <p>All {@link org.apache.commons.fileupload2.core.DiskFileItem} instances
     * are automatically tracked for proper cleanup.</p>
     * 
     * @param request the HTTP servlet request containing the multipart data
     * @param saveDir the directory where uploaded files will be stored
     * @throws IOException if an error occurs during upload processing
     * @see #processNormalFormField(DiskFileItem, Charset)
     * @see #processFileField(DiskFileItem, String)
     */
    @Override
    protected void processUpload(HttpServletRequest request, String saveDir) throws IOException {
        Charset charset = readCharsetEncoding(request);

        JakartaServletDiskFileUpload servletFileUpload =
                prepareServletFileUpload(charset, Path.of(saveDir));

        RequestContext requestContext = createRequestContext(request);
        
        for (DiskFileItem item : servletFileUpload.parseRequest(requestContext)) {
            // Track all DiskFileItem instances for cleanup - this is critical for security
            // as it ensures temporary files are properly cleaned up even if processing fails
            diskFileItems.add(item);

            LOG.debug(() -> "Processing a form field: " + normalizeSpace(item.getFieldName()));
            if (item.isFormField()) {
                // Process regular form fields (text inputs, checkboxes, etc.)
                processNormalFormField(item, charset);
            } else {
                // Process file upload fields
                LOG.debug(() -> "Processing a file: " + normalizeSpace(item.getFieldName()));
                processFileField(item, saveDir);
            }
        }
    }

    /**
     * Processes a normal form field (non-file) from the multipart request.
     * 
     * <p>This method handles text form fields by:</p>
     * <ol>
     *   <li>Validating the field name is not null</li>
     *   <li>Extracting the field value using the specified charset</li>
     *   <li>Checking if the field value exceeds maximum string length</li>
     *   <li>Adding the value to the parameters map</li>
     * </ol>
     * 
     * <p>Fields with null names are skipped with a warning log message.</p>
     * <p>Empty form fields are stored as empty strings.</p>
     * 
     * @param item the disk file item representing the form field
     * @param charset the character set to use for decoding the field value
     * @throws IOException if an error occurs reading the field value
     * @see #exceedsMaxStringLength(String, String)
     */
    protected void processNormalFormField(DiskFileItem item, Charset charset) throws IOException {
        LOG.debug("Item: {} is a normal form field", normalizeSpace(item.getName()));

        String fieldName = item.getFieldName();
        if (fieldName == null) {
            LOG.warn("Form field has null fieldName, skipping");
            return;
        }
        
        List<String> values = parameters.computeIfAbsent(fieldName, k -> new ArrayList<>());

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

    /**
     * Processes a file field from the multipart request.
     * 
     * <p>This method handles file uploads by:</p>
     * <ol>
     *   <li>Validating the file name and field name are not null/empty</li>
     *   <li>Determining if the file is stored in memory or on disk</li>
     *   <li>For in-memory files: creating a temporary file and copying content</li>
     *   <li>For disk files: using the existing file directly</li>
     *   <li>Creating an {@link UploadedFile} abstraction</li>
     *   <li>Adding the file to the uploaded files collection</li>
     * </ol>
     * 
     * <p>Temporary files created for in-memory uploads are automatically
     * tracked for cleanup. Any errors during temporary file creation are
     * logged and added to the error list for user feedback.</p>
     * 
     * @param item the disk file item representing the uploaded file
     * @see #cleanUpTemporaryFiles()
     */
    protected void processFileField(DiskFileItem item, String saveDir) {
        // Skip file uploads that don't have a file name - meaning that no file was selected.
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            LOG.debug(() -> "No file has been uploaded for the field: " + normalizeSpace(item.getFieldName()));
            return;
        }

        String fieldName = item.getFieldName();
        if (fieldName == null) {
            LOG.warn("File field has null fieldName, skipping");
            return;
        }
        
        // Reject empty files (0 bytes) as they are not considered valid uploads
        if (rejectEmptyFile(item.getSize(), item.getName(), fieldName)) {
            return;
        }
        
        List<UploadedFile> values = uploadedFiles.computeIfAbsent(fieldName, k -> new ArrayList<>());

        if (item.isInMemory()) {
            LOG.debug(() -> "Creating temporary file representing in-memory uploaded item: " + normalizeSpace(item.getFieldName()));
            try {
                File tempFile = createTemporaryFile(item.getName(), Path.of(saveDir));
                
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

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Created temporary file for in-memory uploaded item: {} at {}",
                             normalizeSpace(item.getName()), tempFile.getAbsolutePath());
                }
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

        uploadedFiles.put(fieldName, values);
    }

    /**
     * Cleans up disk file items by deleting associated temporary files.
     * 
     * <p>This method iterates through all tracked {@link DiskFileItem} instances
     * and performs cleanup operations:</p>
     * <ul>
     *   <li>For in-memory items: logs cleanup (no files to delete)</li>
     *   <li>For disk items: deletes the associated temporary file</li>
     * </ul>
     * 
     * <p>This method is called automatically during {@link #cleanUp()} but can
     * be overridden by subclasses to customize cleanup behavior. All exceptions
     * are caught and logged to prevent cleanup failures from affecting the
     * overall cleanup process.</p>
     * 
     * @see #cleanUp()
     * @see #cleanUpTemporaryFiles()
     */
    protected void cleanUpDiskFileItems() {
        LOG.debug("Clean up all DiskFileItem instances (both form fields and file uploads");
        for (DiskFileItem item : diskFileItems) {
            try {
                if (item.isInMemory()) {
                    LOG.debug(() -> "Cleaning up in-memory item: " + normalizeSpace(item.getFieldName()));
                } else {
                    Path itemPath = item.getPath();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Cleaning up disk item: {} at {}", normalizeSpace(item.getFieldName()), itemPath);
                    }
                    if (!Files.deleteIfExists(itemPath)) {
                        LOG.warn("There was a problem attempting to delete uploaded file: {}", itemPath);
                    }
                }
            } catch (Exception e) {
                LOG.warn("Error cleaning up DiskFileItem: {}", normalizeSpace(item.getFieldName()), e);
            }
        }
    }

    /**
     * Cleans up temporary files created for in-memory uploads.
     * 
     * <p>This method deletes all temporary files that were created when
     * processing in-memory uploads. These files are created in
     * {@link #processFileField(DiskFileItem, String)} when an uploaded file is
     * stored in memory and needs to be written to disk.</p>
     * 
     * <p>The cleanup process:</p>
     * <ol>
     *   <li>Iterates through all tracked temporary files</li>
     *   <li>Checks if each file still exists</li>
     *   <li>Attempts to delete existing files</li>
     *   <li>Logs warnings for files that cannot be deleted</li>
     * </ol>
     * 
     * <p>This method can be overridden by subclasses to customize cleanup
     * behavior. All exceptions are caught and logged to ensure cleanup
     * continues even if individual file deletions fail.</p>
     * 
     * @see #cleanUp()
     * @see #cleanUpDiskFileItems()
     */
    protected void cleanUpTemporaryFiles() {
        LOG.debug("Cleaning up {} temporary files created for in-memory uploads", temporaryFiles.size());
        for (File tempFile : temporaryFiles) {
            try {
                if (!Files.deleteIfExists(tempFile.toPath())) {
                    LOG.warn("There was a problem attempting to delete temporary file: {}", tempFile.getAbsolutePath());
                }
            } catch (Exception e) {
                LOG.warn("Error cleaning up temporary file: {}", tempFile.getAbsolutePath(), e);
            }
        }
    }

    /**
     * Performs complete cleanup of all resources associated with this request.
     * 
     * <p>This method extends the parent cleanup functionality to ensure proper
     * cleanup of Jakarta-specific resources:</p>
     * <ol>
     *   <li>Calls parent cleanup to handle base class resources</li>
     *   <li>Cleans up all tracked {@link DiskFileItem} instances</li>
     *   <li>Cleans up all temporary files created for in-memory uploads</li>
     *   <li>Clears internal tracking collections</li>
     * </ol>
     * 
     * <p>This method is designed to be safe to call multiple times and will
     * not throw exceptions even if cleanup operations fail. All errors are
     * logged for debugging purposes.</p>
     * 
     * <p><strong>Important:</strong> This method should always be called in a
     * finally block to ensure resources are properly released, even if
     * exceptions occur during request processing.</p>
     * 
     * @see #cleanUpDiskFileItems()
     * @see #cleanUpTemporaryFiles()
     * @see AbstractMultiPartRequest#cleanUp()
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
