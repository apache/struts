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

import com.opensymphony.xwork2.ActionInvocation;

import java.io.Writer;
import java.io.IOException;
import java.io.Reader;

/**
 * Handles the default content type for requests that originate from a browser's HTML form
 *
 * content-type: application/x-www-form-urlencoded
 *
 * This handler is intended for requests only, not for responses
 *
 * {@link http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4}
 *
 */
public class FormUrlEncodedHandler extends  AbstractContentTypeHandler {

    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

    public String fromObject(ActionInvocation invocation, Object obj, String resultCode, Writer out) throws IOException {
        throw new IOException("Conversion from Object to '"+getContentType()+"' is not supported");
    }

    /**
     * No transformation is required as the framework handles this data
     *
     * @param in The input stream, usually the body of the request
     * @param target The target, usually the action class
     */
    public void toObject(ActionInvocation invocation, Reader in, Object target) {
    }

    /**
     * @return The extension is not used by this handler
     */
    public String getExtension() {
        return null;
    }

    public String getContentType() {
        return CONTENT_TYPE;
    }
}
