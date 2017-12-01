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

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.LocalizedMessage;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

/**
 * Multi-part form data request adapter for Jakarta Commons FileUpload package that
 * leverages the streaming API rather than the traditional non-streaming API.
 *
 * For more details see WW-3025
 *
 * @author Chris Cranford
 * @since 2.3.18
 */
public class JakartaStreamMultiPartRequest extends AbstractMultiPartRequest {

    static final Logger LOG = LogManager.getLogger(JakartaStreamMultiPartRequest.class);

    /**
     * Map between file fields and file data.
     */
    protected Map<String, List<FileInfo>> fileInfos = new HashMap<>();

    /**
     * Map between non-file fields and values.
     */
    protected Map<String, List<String>> parameters = new HashMap<>();

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#cleanUp()
     */
    public void cleanUp() {
        LOG.debug("Performing File Upload temporary storage cleanup.");
        for (List<FileInfo> fileInfoList : fileInfos.values()) {
            for (FileInfo fileInfo : fileInfoList) {
                File file = fileInfo.getFile();
                LOG.debug("Deleting file '{}'.", file.getName());
                if (!file.delete()) {
                    LOG.warn("There was a problem attempting to delete file '{}'.", file.getName());
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getContentType(java.lang.String)
     */
    public String[] getContentType(String fieldName) {
        List<FileInfo> infos = fileInfos.get(fieldName);
        if (infos == null) {
            return null;
        }

        List<String> types = new ArrayList<>(infos.size());
        for (FileInfo fileInfo : infos) {
            types.add(fileInfo.getContentType());
        }

        return types.toArray(new String[types.size()]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFile(java.lang.String)
     */
    public UploadedFile[] getFile(String fieldName) {
        List<FileInfo> infos = fileInfos.get(fieldName);
        if (infos == null) {
            return null;
        }

        List<UploadedFile> files = new ArrayList<>(infos.size());
        for (FileInfo fileInfo : infos) {
            files.add(new StrutsUploadedFile(fileInfo.getFile()));
        }

        return files.toArray(new UploadedFile[files.size()]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFileNames(java.lang.String)
     */
    public String[] getFileNames(String fieldName) {
        List<FileInfo> infos = fileInfos.get(fieldName);
        if (infos == null) {
            return null;
        }

        List<String> names = new ArrayList<>(infos.size());
        for (FileInfo fileInfo : infos) {
            names.add(getCanonicalName(fileInfo.getOriginalName()));
        }

        return names.toArray(new String[names.size()]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFileParameterNames()
     */
    public Enumeration<String> getFileParameterNames() {
        return Collections.enumeration(fileInfos.keySet());
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFilesystemName(java.lang.String)
     */
    public String[] getFilesystemName(String fieldName) {
        List<FileInfo> infos = fileInfos.get(fieldName);
        if (infos == null) {
            return null;
        }

        List<String> names = new ArrayList<>(infos.size());
        for (FileInfo fileInfo : infos) {
            names.add(fileInfo.getFile().getName());
        }

        return names.toArray(new String[names.size()]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameter(java.lang.String)
     */
    public String getParameter(String name) {
        List<String> values = parameters.get(name);
        if (values != null && values.size() > 0) {
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
        if (values != null && values.size() > 0) {
            return values.toArray(new String[values.size()]);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#parse(javax.servlet.http.HttpServletRequest, java.lang.String)
     */
    public void parse(HttpServletRequest request, String saveDir) throws IOException {
        try {
            setLocale(request);
            processUpload(request, saveDir);
        } catch (Exception e) {
            LOG.warn("Error occurred during parsing of multi part request", e);
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
     * @throws Exception
     */
    protected void processUpload(HttpServletRequest request, String saveDir) throws Exception {

        // Sanity check that the request is a multi-part/form-data request.
        if (ServletFileUpload.isMultipartContent(request)) {

            // Sanity check on request size.
            boolean requestSizePermitted = isRequestSizePermitted(request);

            // Interface with Commons FileUpload API
            // Using the Streaming API
            ServletFileUpload servletFileUpload = new ServletFileUpload();
            if (maxSizeProvided) {
                servletFileUpload.setSizeMax(maxSize);
            }
            FileItemIterator i = servletFileUpload.getItemIterator(request);

            // Iterate the file items
            while (i.hasNext()) {
                try {
                    FileItemStream itemStream = i.next();

                    // If the file item stream is a form field, delegate to the
                    // field item stream handler
                    if (itemStream.isFormField()) {
                        processFileItemStreamAsFormField(itemStream);
                    }

                    // Delegate the file item stream for a file field to the
                    // file item stream handler, but delegation is skipped
                    // if the requestSizePermitted check failed based on the
                    // complete content-size of the request.
                    else {

                        // prevent processing file field item if request size not allowed.
                        // also warn user in the logs.
                        if (!requestSizePermitted) {
                            addFileSkippedError(itemStream.getName(), request);
                            LOG.warn("Skipped stream '{}', request maximum size ({}) exceeded.", itemStream.getName(), maxSize);
                            continue;
                        }

                        processFileItemStreamAsFileField(itemStream, saveDir);
                    }
                } catch (IOException e) {
                    LOG.warn("Error occurred during process upload", e);
                }
            }
        }
    }

    /**
     * Defines whether the request allowed based on content length.
     *
     * @param request the servlet request
     * @return true if request size is permitted
     */
    protected boolean isRequestSizePermitted(HttpServletRequest request) {
        // if maxSize is specified as -1, there is no sanity check and it's
        // safe to return true for any request, delegating the failure
        // checks later in the upload process.
        if (maxSize == -1 || request == null) {
            return true;
        }

        return request.getContentLength() < maxSize;
    }

    /**
     * @param request the servlet request
     * @return the request content length.
     */
    protected long getRequestSize(HttpServletRequest request) {
        long requestSize = 0;
        if (request != null) {
            requestSize = request.getContentLength();
        }

        return requestSize;
    }

    /**
     * Add a file skipped message notification for action messages.
     *
     * @param fileName file name
     * @param request the servlet request
     */
    protected void addFileSkippedError(String fileName, HttpServletRequest request) {
        String exceptionMessage = "Skipped file " + fileName + "; request size limit exceeded.";
        FileSizeLimitExceededException exception = new FileUploadBase.FileSizeLimitExceededException(exceptionMessage, getRequestSize(request), maxSize);
        LocalizedMessage message = buildErrorMessage(exception, new Object[]{fileName, getRequestSize(request), maxSize});
        if (!errors.contains(message)) {
            errors.add(message);
        }
    }

    /**
     * Processes the FileItemStream as a Form Field.
     *
     * @param itemStream file item stream
     */
    protected void processFileItemStreamAsFormField(FileItemStream itemStream) {
        String fieldName = itemStream.getFieldName();
        try {
            List<String> values;
            String fieldValue = Streams.asString(itemStream.openStream());
            if (!parameters.containsKey(fieldName)) {
                values = new ArrayList<>();
                parameters.put(fieldName, values);
            } else {
                values = parameters.get(fieldName);
            }
            values.add(fieldValue);
        } catch (IOException e) {
            LOG.warn("Failed to handle form field '{}'.", fieldName, e);
        }
    }

    /**
     * Processes the FileItemStream as a file field.
     *
     * @param itemStream file item stream
     * @param location location
     */
    protected void processFileItemStreamAsFileField(FileItemStream itemStream, String location) {
        // Skip file uploads that don't have a file name - meaning that no file was selected.
        if (itemStream.getName() == null || itemStream.getName().trim().length() < 1) {
            LOG.debug("No file has been uploaded for the field: {}", itemStream.getFieldName());
            return;
        }

        File file = null;
        try {
            // Create the temporary upload file.
            file = createTemporaryFile(itemStream.getName(), location);

            if (streamFileToDisk(itemStream, file)) {
                createFileInfoFromItemStream(itemStream, file);
            }
        } catch (IOException e) {
            if (file != null) {
                try {
                    file.delete();
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
     * @return temporary file based on the given filename and location
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
     * @param itemStream file item stream
     * @param file the file
     * @return true if stream was successfully
     * @throws IOException in case of IO errors
     */
    protected boolean streamFileToDisk(FileItemStream itemStream, File file) throws IOException {
        boolean result = false;
        try (InputStream input = itemStream.openStream();
                OutputStream output = new BufferedOutputStream(new FileOutputStream(file), bufferSize)) {
            byte[] buffer = new byte[bufferSize];
            LOG.debug("Streaming file using buffer size {}.", bufferSize);
            for (int length = 0; ((length = input.read(buffer)) > 0); ) {
                output.write(buffer, 0, length);
            }
            result = true;
        }
        return result;
    }

    /**
     * Creates an internal <code>FileInfo</code> structure used to pass information
     * to the <code>FileUploadInterceptor</code> during the interceptor stack
     * invocation process.
     *
     * @param itemStream file item stream
     * @param file the file
     */
    protected void createFileInfoFromItemStream(FileItemStream itemStream, File file) {
        // gather attributes from file upload stream.
        String fileName = itemStream.getName();
        String fieldName = itemStream.getFieldName();
        // create internal structure
        FileInfo fileInfo = new FileInfo(file, itemStream.getContentType(), fileName);
        // append or create new entry.
        if (!fileInfos.containsKey(fieldName)) {
            List<FileInfo> infos = new ArrayList<>();
            infos.add(fileInfo);
            fileInfos.put(fieldName, infos);
        } else {
            fileInfos.get(fieldName).add(fileInfo);
        }
    }

    /**
     * Internal data structure used to store a reference to information needed
     * to later pass post processing data to the <code>FileUploadInterceptor</code>.
     *
     * @since 7.0.0
     */
     public static class FileInfo implements Serializable {

        private static final long serialVersionUID = 1083158552766906037L;

        private File file;
        private String contentType;
        private String originalName;

        /**
         * Default constructor.
         *
         * @param file the file
         * @param contentType content type
         * @param originalName original file name
         */
        public FileInfo(File file, String contentType, String originalName) {
            this.file = file;
            this.contentType = contentType;
            this.originalName = originalName;
        }

        /**
         * @return the file
         */
        public File getFile() {
            return file;
        }

        /**
         * @return content type
         */
        public String getContentType() {
            return contentType;
        }

        /**
         * @return original file name
         */
        public String getOriginalName() {
            return originalName;
        }
    }

}
