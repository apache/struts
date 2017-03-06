/*
 * $Id$
 *
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

import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.struts2.StrutsConstants;

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
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Multipart form data request adapter for Jakarta Commons Fileupload package.
 */
public class JakartaMultiPartRequest implements MultiPartRequest {

    static final Logger LOG = LoggerFactory.getLogger(JakartaMultiPartRequest.class);

    // maps parameter name -> List of FileItem objects
    protected Map<String, List<FileItem>> files = new HashMap<String, List<FileItem>>();

    // maps parameter name -> List of param values
    protected Map<String, List<String>> params = new HashMap<String, List<String>>();

    // any errors while processing this request
    protected List<String> errors = new ArrayList<String>();

    protected long maxSize;
    private Locale defaultLocale = Locale.ENGLISH;

    @Inject(StrutsConstants.STRUTS_MULTIPART_MAXSIZE)
    public void setMaxSize(String maxSize) {
        this.maxSize = Long.parseLong(maxSize);
    }

    @Inject
    public void setLocaleProvider(LocaleProvider provider) {
        defaultLocale = provider.getLocale();
    }

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
        } catch (FileUploadBase.SizeLimitExceededException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Request exceeded size limit!", e);
            }
            String errorMessage = buildErrorMessage(e, new Object[]{e.getPermittedSize(), e.getActualSize()});
            if (!errors.contains(errorMessage)) {
                errors.add(errorMessage);
            }
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Unable to parse request", e);
            }
            String errorMessage = buildErrorMessage(e, new Object[]{});
            if (!errors.contains(errorMessage)) {
                errors.add(errorMessage);
            }
        }
    }

    protected void setLocale(HttpServletRequest request) {
        if (defaultLocale == null) {
            defaultLocale = request.getLocale();
        }
    }

    protected String buildErrorMessage(Throwable e, Object[] args) {
        String errorKey = "struts.messages.upload.error." + e.getClass().getSimpleName();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Preparing error message for key: [#0]", errorKey);
        }
        if (LocalizedTextUtil.findText(this.getClass(), errorKey, defaultLocale, null, new Object[0]) == null) {
            return LocalizedTextUtil.findText(this.getClass(), "struts.messages.error.uploading", defaultLocale, null, new Object[] { e.getMessage() });
        } else {
            return LocalizedTextUtil.findText(this.getClass(), errorKey, defaultLocale, null, args);
        }
    }

    protected void processUpload(HttpServletRequest request, String saveDir) throws FileUploadException, UnsupportedEncodingException {
        for (FileItem item : parseRequest(request, saveDir)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Found item " + item.getFieldName());
            }
            if (item.isFormField()) {
                processNormalFormField(item, request.getCharacterEncoding());
            } else {
                processFileField(item);
            }
        }
    }

    protected void processFileField(FileItem item) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Item is a file upload");
        }

        // Skip file uploads that don't have a file name - meaning that no file was selected.
        if (item.getName() == null || item.getName().trim().length() < 1) {
            LOG.debug("No file has been uploaded for the field: " + item.getFieldName());
            return;
        }

        List<FileItem> values;
        if (files.get(item.getFieldName()) != null) {
            values = files.get(item.getFieldName());
        } else {
            values = new ArrayList<FileItem>();
        }

        values.add(item);
        files.put(item.getFieldName(), values);
    }

    protected void processNormalFormField(FileItem item, String charset) throws UnsupportedEncodingException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Item is a normal form field");
        }
        List<String> values;
        if (params.get(item.getFieldName()) != null) {
            values = params.get(item.getFieldName());
        } else {
            values = new ArrayList<String>();
        }

        // note: see http://jira.opensymphony.com/browse/WW-633
        // basically, in some cases the charset may be null, so
        // we're just going to try to "other" method (no idea if this
        // will work)
        if (charset != null) {
            values.add(item.getString(charset));
        } else {
            values.add(item.getString());
        }
        params.put(item.getFieldName(), values);
        item.delete();
    }

    protected List<FileItem> parseRequest(HttpServletRequest servletRequest, String saveDir) throws FileUploadException {
        DiskFileItemFactory fac = createDiskFileItemFactory(saveDir);
        ServletFileUpload upload = createServletFileUpload(fac);
        return upload.parseRequest(createRequestContext(servletRequest));
    }

    protected ServletFileUpload createServletFileUpload(DiskFileItemFactory fac) {
        ServletFileUpload upload = new ServletFileUpload(fac);
        upload.setSizeMax(maxSize);
        return upload;
    }

    protected DiskFileItemFactory createDiskFileItemFactory(String saveDir) {
        DiskFileItemFactory fac = new DiskFileItemFactory();
        // Make sure that the data is written to file
        fac.setSizeThreshold(0);
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

        List<String> contentTypes = new ArrayList<String>(items.size());
        for (FileItem fileItem : items) {
            contentTypes.add(fileItem.getContentType());
        }

        return contentTypes.toArray(new String[contentTypes.size()]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFile(java.lang.String)
     */
    public File[] getFile(String fieldName) {
        List<FileItem> items = files.get(fieldName);

        if (items == null) {
            return null;
        }

        List<File> fileList = new ArrayList<File>(items.size());
        for (FileItem fileItem : items) {
            File storeLocation = ((DiskFileItem) fileItem).getStoreLocation();
            if (fileItem.isInMemory() && storeLocation != null && !storeLocation.exists()) {
                try {
                    storeLocation.createNewFile();
                } catch (IOException e) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error("Cannot write uploaded empty file to disk: " + storeLocation.getAbsolutePath(), e);
                    }
                }
            }
            fileList.add(storeLocation);
        }

        return fileList.toArray(new File[fileList.size()]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFileNames(java.lang.String)
     */
    public String[] getFileNames(String fieldName) {
        List<FileItem> items = files.get(fieldName);

        if (items == null) {
            return null;
        }

        List<String> fileNames = new ArrayList<String>(items.size());
        for (FileItem fileItem : items) {
            fileNames.add(getCanonicalName(fileItem.getName()));
        }

        return fileNames.toArray(new String[fileNames.size()]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFilesystemName(java.lang.String)
     */
    public String[] getFilesystemName(String fieldName) {
        List<FileItem> items = files.get(fieldName);

        if (items == null) {
            return null;
        }

        List<String> fileNames = new ArrayList<String>(items.size());
        for (FileItem fileItem : items) {
            fileNames.add(((DiskFileItem) fileItem).getStoreLocation().getName());
        }

        return fileNames.toArray(new String[fileNames.size()]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameter(java.lang.String)
     */
    public String getParameter(String name) {
        List<String> v = params.get(name);
        if (v != null && v.size() > 0) {
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
        if (v != null && v.size() > 0) {
            return v.toArray(new String[v.size()]);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getErrors()
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Returns the canonical name of the given file.
     *
     * @param filename the given file
     * @return the canonical name of the given file
     */
    private String getCanonicalName(String filename) {
        int forwardSlash = filename.lastIndexOf("/");
        int backwardSlash = filename.lastIndexOf("\\");
        if (forwardSlash != -1 && forwardSlash > backwardSlash) {
            filename = filename.substring(forwardSlash + 1, filename.length());
        } else if (backwardSlash != -1 && backwardSlash >= forwardSlash) {
            filename = filename.substring(backwardSlash + 1, filename.length());
        }

        return filename;
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
                if (LOG.isDebugEnabled()) {
                    String msg = LocalizedTextUtil.findText(this.getClass(), "struts.messages.removing.file",
                            Locale.ENGLISH, "no.message.found", new Object[]{name, item});
                    LOG.debug(msg);
                }
                if (!item.isInMemory()) {
                    item.delete();
                }
            }
        }
    }

}
