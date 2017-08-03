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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Handles transferring content to and from objects for a specific content type
 */
public interface ContentTypeHandler {
    
    /**
     * Populates an object using data from the input stream
     * @param in The input stream, usually the body of the request
     * @param target The target, usually the action class
     * @throws IOException If unable to write to the output stream
     *
     * @deprecated use version which requires {@link ActionInvocation}
     */
    @Deprecated
    void toObject(Reader in, Object target) throws IOException;

    void toObject(ActionInvocation invocation, Reader in, Object target) throws IOException;

    /**
     * Writes content to the stream
     * 
     * @param obj The object to write to the stream, usually the Action class
     * @param resultCode The original result code
     * @param stream The output stream, usually the response
     * @return The new result code
     * @throws IOException If unable to write to the output stream
     *
     * @deprecated use version which requires {@link ActionInvocation}
     */
    @Deprecated
    String fromObject(Object obj, String resultCode, Writer stream) throws IOException;

    String fromObject(ActionInvocation invocation, Object obj, String resultCode, Writer stream) throws IOException;

    /**
     * Gets the content type for this handler
     * 
     * @return The mime type
     */
    String getContentType();
    
    /**
     * Gets the extension this handler supports
     * 
     * @return The extension
     */
    String getExtension();
}
