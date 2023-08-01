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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.portlet.delegate;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Exposes features of a response, if they are available.
 */
public interface ResponseDelegate {

    /**
     * Returns the output stream.
     *
     * @return The output stream.
     * @throws IOException If the underlying response causes a problem.
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Returns the print writer.
     *
     * @return The print writer.
     * @throws IOException If the underlying response causes a problem.
     */
    PrintWriter getPrintWriter() throws IOException;

    /**
     * Returns the writer.
     *
     * @return The writer.
     * @throws IOException If the underlying response causes a problem.
     */
    Writer getWriter() throws IOException;

    /**
     * Sets the content type of the response.
     *
     * @param contentType The content type.
     */
    void setContentType(String contentType);

    /**
     * Checks if the response is committed.
     *
     * @return <code>true</code> if the response is committed.
     */
    boolean isResponseCommitted();
}
