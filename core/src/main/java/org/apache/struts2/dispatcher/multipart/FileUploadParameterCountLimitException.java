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

import org.apache.commons.fileupload2.core.FileUploadException;

/**
 * Thrown when a multipart request contains more non-file form fields (parameters)
 * than allowed by {@code struts.multipart.maxParameterCount}.
 */
public class FileUploadParameterCountLimitException extends FileUploadException {

    private final long permitted;
    private final long actual;

    public FileUploadParameterCountLimitException(final String message, final long permitted, final long actual) {
        super(message);
        this.permitted = permitted;
        this.actual = actual;
    }

    public long getPermitted() {
        return permitted;
    }

    public long getActual() {
        return actual;
    }
}
