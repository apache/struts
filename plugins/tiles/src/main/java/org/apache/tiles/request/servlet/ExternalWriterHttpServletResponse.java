/*
 * $Id: ExternalWriterHttpServletResponse.java 1306435 2012-03-28 15:39:11Z nlebas $
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

package org.apache.tiles.request.servlet;

import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * Wraps an HTTP response and overrides its print writer.
 *
 * @version $Rev: 1306435 $ $Date: 2012-03-29 02:39:11 +1100 (Thu, 29 Mar 2012) $
 */
public class ExternalWriterHttpServletResponse extends
        HttpServletResponseWrapper {

    /**
     * The print writer to use, instead of the response's one.
     */
    private PrintWriter writer;

    /**
     * Constructor.
     *
     * @param response The response to wrap.
     * @param writer The print writer to use, instead of the response's one.
     */
    public ExternalWriterHttpServletResponse(HttpServletResponse response, PrintWriter writer) {
        super(response);
        this.writer = writer;
    }

    /** {@inheritDoc} */
    @Override
    public PrintWriter getWriter() {
        return writer;
    }
}
