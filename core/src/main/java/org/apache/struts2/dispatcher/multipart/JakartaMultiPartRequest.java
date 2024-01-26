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
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletDiskFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Multipart form data request adapter for Jakarta Commons FileUpload package.
 */
public class JakartaMultiPartRequest extends AbstractMultiPartRequest<File> {

    private static final Logger LOG = LogManager.getLogger(JakartaMultiPartRequest.class);

    @Override
    protected void processUpload(HttpServletRequest request, String saveDir) throws IOException {
        String charset = StringUtils.isBlank(request.getCharacterEncoding())
                ? defaultEncoding
                : request.getCharacterEncoding();

        JakartaServletDiskFileUpload servletFileUpload =
                prepareServletFileUpload(Charset.forName(charset), Path.of(saveDir));

        for (DiskFileItem item : servletFileUpload.parseRequest(request)) {
            LOG.debug(() -> "Processing a form field: " + sanitizeNewlines(item.getFieldName()));
            if (item.isFormField()) {
                processNormalFormField(item, charset);
            } else {
                LOG.debug(() -> "Processing a file: " + sanitizeNewlines(item.getFieldName()));
                processFileField(item);
            }
        }
    }

    protected JakartaServletDiskFileUpload createJakartaFileUpload(Charset charset, Path saveDir) {
        DiskFileItemFactory.Builder builder = DiskFileItemFactory.builder();

        LOG.debug("Using file save directory: {}", saveDir);
        builder.setPath(saveDir);

        LOG.debug("Sets minimal buffer size to always write file to disk");
        builder.setBufferSize(1);

        LOG.debug("Using charset: {}", charset);
        builder.setCharset(charset);

        DiskFileItemFactory factory = builder.get();
        return new JakartaServletDiskFileUpload(factory);
    }


    protected void processNormalFormField(DiskFileItem item, String charset) throws IOException {
        try {
            LOG.debug("Item: {} is a normal form field", item.getName());
            Charset encoding = StringUtils.isBlank(charset) ? Charset.forName(defaultEncoding) : Charset.forName(charset);

            List<String> values;
            String fieldName = item.getFieldName();
            if (parameters.get(fieldName) != null) {
                values = parameters.get(fieldName);
            } else {
                values = new ArrayList<>();
            }

            String fieldValue = item.getString(encoding);
            if (exceedsMaxStringLength(fieldName, fieldValue)) {
                return;
            }
            if (item.getSize() == 0) {
                values.add(StringUtils.EMPTY);
            } else {
                values.add(fieldValue);
            }
            parameters.put(fieldName, values);
        } finally {
            item.delete();
        }
    }

    protected void processFileField(DiskFileItem item) {
        // Skip file uploads that don't have a file name - meaning that no file was selected.
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            LOG.debug(() -> "No file has been uploaded for the field: " + sanitizeNewlines(item.getFieldName()));
            return;
        }

        List<UploadedFile<File>> values;
        if (uploadedFiles.get(item.getFieldName()) != null) {
            values = uploadedFiles.get(item.getFieldName());
        } else {
            values = new ArrayList<>();
        }

        if (item.isInMemory()) {
            LOG.warn("Storing uploaded files just in memory isn't supported currently, skipping file: {}!", item.getName());
        } else {
            UploadedFile<File> uploadedFile = StrutsUploadedFile.Builder
                    .create(item.getPath().toFile())
                    .withOriginalName(item.getName())
                    .withContentType(item.getContentType())
                    .build();
            values.add(uploadedFile);
        }

        uploadedFiles.put(item.getFieldName(), values);
    }

}
