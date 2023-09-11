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

import org.apache.commons.fileupload.FileCountLimitExceededException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.LocalizedMessage;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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

    static final Logger LOG = LogManager.getLogger(JakartaMultiPartRequest.class);

    // maps parameter name -> List of FileItem objects
    protected Map<String, List<FileItem>> files = new HashMap<>();

    // maps parameter name -> List of param values
    protected Map<String, List<String>> params = new HashMap<>();

    /**
     * Creates a new request wrapper to handle multi-part data using methods adapted from Jason Pell's
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
            if (e instanceof FileUploadBase.SizeLimitExceededException) {
                FileUploadBase.SizeLimitExceededException ex = (FileUploadBase.SizeLimitExceededException) e;
                errorMessage = buildErrorMessage(e, new Object[]{ex.getPermittedSize(), ex.getActualSize()});
            } else if (e instanceof FileUploadBase.FileSizeLimitExceededException) {
                FileUploadBase.FileSizeLimitExceededException ex = (FileUploadBase.FileSizeLimitExceededException) e;
                errorMessage = buildErrorMessage(e, new Object[]{ex.getFileName(), ex.getPermittedSize(), ex.getActualSize()});
            } else if (e instanceof FileCountLimitExceededException) {
                FileCountLimitExceededException ex = (FileCountLimitExceededException) e;
                errorMessage = buildErrorMessage(e, new Object[]{ex.getLimit()});
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

    protected void processUpload(HttpServletRequest request, String saveDir) throws FileUploadException, UnsupportedEncodingException {
        if (ServletFileUpload.isMultipartContent(request)) {
            for (FileItem item : parseRequest(request, saveDir)) {
                LOG.debug("Found file item: [{}]", item.getFieldName());
                if (item.isFormField()) {
                    processNormalFormField(item, request.getCharacterEncoding());
                } else {
                    processFileField(item);
                }
            }
        }
    }

    protected void processFileField(FileItem item) {
        LOG.debug("Item is a file upload");

        // Skip file uploads that don't have a file name - meaning that no file was selected.
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            LOG.debug("No file has been uploaded for the field: {}", item.getFieldName());
            return;
        }

        List<FileItem> values;
        if (files.get(item.getFieldName()) != null) {
            values = files.get(item.getFieldName());
        } else {
            values = new ArrayList<>();
        }

        values.add(item);
        files.put(item.getFieldName(), values);
    }

    protected void processNormalFormField(FileItem item, String charset) throws UnsupportedEncodingException {
        try {
            LOG.debug("Item is a normal form field");

            List<String> values;
            if (params.get(item.getFieldName()) != null) {
                values = params.get(item.getFieldName());
            } else {
                values = new ArrayList<>();
            }

            long size = item.getSize();
            if (size == 0) {
                values.add(StringUtils.EMPTY);
            } else if (size > maxStringLength) {
                String errorKey = "struts.messages.upload.error.parameter.too.long";
                LocalizedMessage localizedMessage = new LocalizedMessage(this.getClass(), errorKey, null,
                    new Object[]{item.getFieldName(), maxStringLength, size});

                if (!errors.contains(localizedMessage)) {
                    errors.add(localizedMessage);
                }
                return;

            } else if (charset != null) {
                values.add(item.getString(charset));
            } else {
                // note: see https://issues.apache.org/jira/browse/WW-633
                // basically, in some cases the charset may be null, so
                // we're just going to try to "other" method (no idea if this
                // will work)
                values.add(item.getString());
            }
            params.put(item.getFieldName(), values);
        } finally {
            item.delete();
        }
    }

    protected List<FileItem> parseRequest(HttpServletRequest servletRequest, String saveDir) throws FileUploadException {
        DiskFileItemFactory fac = createDiskFileItemFactory(saveDir);
        ServletFileUpload upload = createServletFileUpload(fac);
        return upload.parseRequest(createRequestContext(servletRequest));
    }

    protected ServletFileUpload createServletFileUpload(DiskFileItemFactory fac) {
        ServletFileUpload upload = new ServletFileUpload(fac);
        if (maxSize != null) {
            upload.setSizeMax(maxSize);
        }
        if (maxFiles != null) {
            upload.setFileCountMax(maxFiles);
        }
        if (maxFileSize != null) {
            upload.setFileSizeMax(maxFileSize);
        }
        return upload;
    }

    protected DiskFileItemFactory createDiskFileItemFactory(String saveDir) {
        DiskFileItemFactory fac = new DiskFileItemFactory();
        // Make sure that the data is written to file, even if the file is empty.
        fac.setSizeThreshold(-1);
        if (saveDir != null) {
            fac.setRepository(new File(saveDir));
        }
        return fac;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFileParameterNames()
     */
    public Enumeration<String> getFileParameterNames() {
        return Collections.enumeration(files.keySet());
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getContentType(java.lang.String)
     */
    public String[] getContentType(String fieldName) {
        List<FileItem> items = files.get(fieldName);

        if (items == null) {
            return null;
        }

        List<String> contentTypes = new ArrayList<>(items.size());
        for (FileItem fileItem : items) {
            contentTypes.add(fileItem.getContentType());
        }

        return contentTypes.toArray(new String[0]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFile(java.lang.String)
     */
    public UploadedFile[] getFile(String fieldName) {
        List<FileItem> items = files.get(fieldName);

        if (items == null) {
            return null;
        }

        List<UploadedFile> fileList = new ArrayList<>(items.size());
        for (FileItem fileItem : items) {
            DiskFileItem diskFileItem = (DiskFileItem) fileItem;
            File storeLocation = diskFileItem.getStoreLocation();

            // Ensure file exists even if it is empty.
            if (diskFileItem.getSize() == 0 && storeLocation != null && !storeLocation.exists()) {
                try {
                    storeLocation.createNewFile();
                } catch (IOException e) {
                    LOG.error("Cannot write uploaded empty file to disk: {}", storeLocation.getAbsolutePath(), e);
                }
            }
            fileList.add(new StrutsUploadedFile(storeLocation));
        }

        return fileList.toArray(new UploadedFile[0]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFileNames(java.lang.String)
     */
    public String[] getFileNames(String fieldName) {
        List<FileItem> items = files.get(fieldName);

        if (items == null) {
            return null;
        }

        List<String> fileNames = new ArrayList<>(items.size());
        for (FileItem fileItem : items) {
            fileNames.add(getCanonicalName(fileItem.getName()));
        }

        return fileNames.toArray(new String[0]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFilesystemName(java.lang.String)
     */
    public String[] getFilesystemName(String fieldName) {
        List<FileItem> items = files.get(fieldName);

        if (items == null) {
            return null;
        }

        List<String> fileNames = new ArrayList<>(items.size());
        for (FileItem fileItem : items) {
            fileNames.add(((DiskFileItem) fileItem).getStoreLocation().getName());
        }

        return fileNames.toArray(new String[0]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameter(java.lang.String)
     */
    public String getParameter(String name) {
        List<String> v = params.get(name);
        if (v != null && !v.isEmpty()) {
            return v.get(0);
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

            public int getContentLength() {
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
        Set<String> names = files.keySet();
        for (String name : names) {
            List<FileItem> items = files.get(name);
            for (FileItem item : items) {
                LOG.debug("Removing file {} {}", name, item);
                if (!item.isInMemory()) {
                    item.delete();
                }
            }
        }
    }

}
