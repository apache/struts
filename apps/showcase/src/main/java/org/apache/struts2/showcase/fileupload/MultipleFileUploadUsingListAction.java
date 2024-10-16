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
// START SNIPPET: entire-file
package org.apache.struts2.showcase.fileupload;

import org.apache.struts2.ActionSupport;
import org.apache.struts2.action.UploadedFilesAware;
import org.apache.struts2.dispatcher.multipart.UploadedFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Showcase action - multiple file upload using List
 */
public class MultipleFileUploadUsingListAction extends ActionSupport implements UploadedFilesAware {

    private List<UploadedFile> uploads = new ArrayList<>();

    public List<UploadedFile> getUpload() {
        return this.uploads;
    }

    @Override
    public void withUploadedFiles(List<UploadedFile> uploads) {
        this.uploads = uploads;
    }

    private List<String> getUploadFileNames() {
        return this.uploads.stream()
                .map(UploadedFile::getOriginalName)
                .collect(Collectors.toList());
    }

    private List<String> getUploadContentTypes() {
        return this.uploads.stream()
                .map(UploadedFile::getContentType)
                .collect(Collectors.toList());
    }

    public String upload() throws Exception {
        System.out.println("\n\n upload1");
        System.out.println("files:");
        for (UploadedFile u : uploads) {
            System.out.println("*** " + u + "\t" + u.length());
        }
        System.out.println("filenames:");
        for (String n : getUploadFileNames()) {
            System.out.println("*** " + n);
        }
        System.out.println("content types:");
        for (String c : getUploadContentTypes()) {
            System.out.println("*** " + c);
        }
        System.out.println("\n\n");
        return SUCCESS;
    }
}