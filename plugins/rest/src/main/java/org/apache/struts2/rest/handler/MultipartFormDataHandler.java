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
package org.apache.struts2.rest.handler;

import java.io.Writer;
import java.io.IOException;
import java.io.Reader;

/**
 * Handles the default content type for requests that originate from a browser's HTML form 
 * with a file upload and multipart/from-data encoding
 *
 * content-type: multipart/form-data
 *
 * This handler is intended for requests only, not for responses
 *
 * {@link http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4}
 *
 */
public class MultipartFormDataHandler implements ContentTypeHandler {

    public static final String CONTENT_TYPE = "multipart/form-data";

    public String fromObject(Object obj, String resultCode, Writer out) throws IOException {
        throw new IOException("Conversion from Object to '"+getContentType()+"' is not supported");
    }

    /** No transformation is required as the framework handles this data */
    public void toObject(Reader in, Object target) {
    }

    /**
     * The extension is not used by this handler
     * @return
     */
    public String getExtension() {
        return null;
    }

    public String getContentType() {
        return CONTENT_TYPE;
    }
}
