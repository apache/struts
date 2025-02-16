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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.normalizeSpace;

/**
 * Multipart form data request adapter for Jakarta Commons FileUpload package.
 */
public class JakartaMultiPartRequest extends AbstractMultiPartRequest {

    private static final Logger LOG = LogManager.getLogger(JakartaMultiPartRequest.class);

    @Override
    protected void processUpload(HttpServletRequest request, String saveDir) throws IOException {
        Charset charset = readCharsetEncoding(request);

        JakartaServletDiskFileUpload servletFileUpload =
                prepareServletFileUpload(charset, Path.of(saveDir));

        for (DiskFileItem item : servletFileUpload.parseRequest(request)) {
            LOG.debug(() -> "Processing a form field: " + normalizeSpace(item.getFieldName()));
            if (item.isFormField()) {
                processNormalFormField(item, charset);
            } else {
                LOG.debug(() -> "Processing a file: " + normalizeSpace(item.getFieldName()));
                processFileField(item);
            }
        }
    }

    @Override
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

    protected void processNormalFormField(DiskFileItem item, Charset charset) throws IOException {
        LOG.debug("Item: {} is a normal form field", normalizeSpace(item.getName()));

        List<String> values;
        String fieldName = item.getFieldName();
        if (parameters.get(fieldName) != null) {
            values = parameters.get(fieldName);
        } else {
            values = new ArrayList<>();
        }

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

    protected void processFileField(DiskFileItem item) {
        // Skip file uploads that don't have a file name - meaning that no file was selected.
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            LOG.debug(() -> "No file has been uploaded for the field: " + normalizeSpace(item.getFieldName()));
            return;
        }

        List<UploadedFile> values;
        if (uploadedFiles.get(item.getFieldName()) != null) {
            values = uploadedFiles.get(item.getFieldName());
        } else {
            values = new ArrayList<>();
        }

        if (item.isInMemory()) {
            LOG.warn(() -> "Storing uploaded files just in memory isn't supported currently, skipping file: %s!".formatted(normalizeSpace(item.getName())));
        } else {
            UploadedFile uploadedFile = StrutsUploadedFile.Builder
                    .create(item.getPath().toFile())
                    .withOriginalName(item.getName())
                    .withContentType(item.getContentType())
                    .withInputName(item.getFieldName())
                    .build();
            values.add(uploadedFile);
        }

        uploadedFiles.put(item.getFieldName(), values);
    }

}
