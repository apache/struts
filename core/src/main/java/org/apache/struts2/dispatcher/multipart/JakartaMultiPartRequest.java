/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.dispatcher.multipart;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItem;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Multipart form data request adapter for Jakarta's file upload package.
 *
 */
public class JakartaMultiPartRequest extends MultiPartRequest {
    // maps parameter name -> List of FileItem objects
    private Map files = new HashMap();
    // maps parameter name -> List of param values
    private Map params = new HashMap();
    // any errors while processing this request
    private List errors = new ArrayList();

    /**
     * Creates a new request wrapper to handle multi-part data using methods adapted from Jason Pell's
     * multipart classes (see class description).
     *
     * @param maxSize        maximum size post allowed
     * @param saveDir        the directory to save off the file
     * @param servletRequest the request containing the multipart
     * @throws java.io.IOException  is thrown if encoding fails.
     */
    public JakartaMultiPartRequest(HttpServletRequest servletRequest, String saveDir, int maxSize)
            throws IOException {
        DiskFileItemFactory fac = new DiskFileItemFactory();
        fac.setSizeThreshold(0);
        if (saveDir != null) {
            fac.setRepository(new File(saveDir));
        }

        // Parse the request
        try {
            ServletFileUpload upload = new ServletFileUpload(fac);
            List items = upload.parseRequest(createRequestContext(servletRequest));

            for (int i = 0; i < items.size(); i++) {
                FileItem item = (FileItem) items.get(i);
                if (log.isDebugEnabled()) log.debug("Found item " + item.getFieldName());
                if (item.isFormField()) {
                    log.debug("Item is a normal form field");
                    List values;
                    if (params.get(item.getFieldName()) != null) {
                        values = (List) params.get(item.getFieldName());
                    } else {
                        values = new ArrayList();
                    }

                    // note: see http://jira.opensymphony.com/browse/WW-633
                    // basically, in some cases the charset may be null, so
                    // we're just going to try to "other" method (no idea if this
                    // will work)
                    String charset = servletRequest.getCharacterEncoding();
                    if (charset != null) {
                        values.add(item.getString(charset));
                    } else {
                        values.add(item.getString());
                    }
                    params.put(item.getFieldName(), values);
                } else {
                    log.debug("Item is a file upload");

                    List values;
                    if (files.get(item.getFieldName()) != null) {
                        values = (List) files.get(item.getFieldName());
                    } else {
                        values = new ArrayList();
                    }

                    values.add(item);
                    files.put(item.getFieldName(), values);
                }
            }
        } catch (FileUploadException e) {
            log.error(e);
            errors.add(e.getMessage());
        }
    }

    public Enumeration getFileParameterNames() {
        return Collections.enumeration(files.keySet());
    }

    public String[] getContentType(String fieldName) {
        List items = (List) files.get(fieldName);

        if (items == null) {
            return null;
        }

        List contentTypes = new ArrayList(items.size());
        for (int i = 0; i < items.size(); i++) {
            FileItem fileItem = (FileItem) items.get(i);
            contentTypes.add(fileItem.getContentType());
        }

        return (String[]) contentTypes.toArray(new String[contentTypes.size()]);
    }

    public File[] getFile(String fieldName) {
        List items = (List) files.get(fieldName);

        if (items == null) {
            return null;
        }

        List fileList = new ArrayList(items.size());
        for (int i = 0; i < items.size(); i++) {
            DiskFileItem fileItem = (DiskFileItem) items.get(i);
            fileList.add(fileItem.getStoreLocation());
        }

        return (File[]) fileList.toArray(new File[fileList.size()]);
    }

    public String[] getFileNames(String fieldName) {
        List items = (List) files.get(fieldName);

        if (items == null) {
            return null;
        }

        List fileNames = new ArrayList(items.size());
        for (int i = 0; i < items.size(); i++) {
            DiskFileItem fileItem = (DiskFileItem) items.get(i);
            fileNames.add(getCanonicalName(fileItem.getName()));
        }

        return (String[]) fileNames.toArray(new String[fileNames.size()]);
    }

    public String[] getFilesystemName(String fieldName) {
        List items = (List) files.get(fieldName);

        if (items == null) {
            return null;
        }

        List fileNames = new ArrayList(items.size());
        for (int i = 0; i < items.size(); i++) {
            DiskFileItem fileItem = (DiskFileItem) items.get(i);
            fileNames.add(fileItem.getStoreLocation().getName());
        }

        return (String[]) fileNames.toArray(new String[fileNames.size()]);
    }

    public String getParameter(String name) {
        List v = (List) params.get(name);
        if (v != null && v.size() > 0) {
            return (String) v.get(0);
        }

        return null;
    }

    public Enumeration getParameterNames() {
        return Collections.enumeration(params.keySet());
    }

    public String[] getParameterValues(String name) {
        List v = (List) params.get(name);
        if (v != null && v.size() > 0) {
            return (String[]) v.toArray(new String[v.size()]);
        }

        return null;
    }

    public List getErrors() {
        return errors;
    }

    /**
     * Returns the canonical name of the given file.
     *
     * @param filename  the given file
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
     * @param req  the request.
     * @return a new request context.
     */
    private RequestContext createRequestContext(final HttpServletRequest req) {
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
                return req.getInputStream();
            }
        };
    }

}
