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
package org.apache.struts2.showcase.fileupload;

import org.apache.struts2.ActionSupport;
import org.apache.struts2.action.UploadedFilesAware;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.apache.struts2.interceptor.parameter.StrutsParameter;

import java.util.List;

/**
 * Show case File Upload example's action. <code>FileUploadAction</code>
 */
public class FileUploadAction extends ActionSupport implements UploadedFilesAware {

    private String contentType;
    private UploadedFile uploadedFile;
    private String fileName;
    private String caption;
    private String originalName;
    private String inputName;

    public String input() throws Exception {
        return SUCCESS;
    }

    public String upload() throws Exception {
        return SUCCESS;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getInputName() {
        return inputName;
    }

    public Object getUploadedFile() {
        return uploadedFile.getContent();
    }

    public String getCaption() {
        return caption;
    }

    @StrutsParameter
    public void setCaption(String caption) {
        this.caption = caption;
    }

    public long getUploadSize() {
        if (uploadedFile != null) {
            return uploadedFile.length();
        } else {
            return 0;
        }
    }

    @Override
    public void withUploadedFiles(List<UploadedFile> uploadedFiles) {
        this.uploadedFile = uploadedFiles.get(0);
        this.fileName = uploadedFile.getName();
        this.contentType = uploadedFile.getContentType();
        this.originalName = uploadedFile.getOriginalName();
        this.inputName = uploadedFile.getInputName();
    }
}
