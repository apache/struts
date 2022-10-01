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

import javax.portlet.MimeResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Response delegate in case of {@link MimeResponse}.
 */
public class MimeResponseDelegate implements ResponseDelegate {

    /**
     * The response.
     */
    private final MimeResponse response;

    /**
     * Constructor.
     *
     * @param response The response.
     */
    public MimeResponseDelegate(MimeResponse response) {
        this.response = response;
    }

    /** {@inheritDoc} */
    public OutputStream getOutputStream() throws IOException {
        return response.getPortletOutputStream();
    }

    /** {@inheritDoc} */
    public PrintWriter getPrintWriter() throws IOException {
        return response.getWriter();
    }

    /** {@inheritDoc} */
    public Writer getWriter() throws IOException {
        return response.getWriter();
    }

    /** {@inheritDoc} */
    public boolean isResponseCommitted() {
        return response.isCommitted();
    }

    /** {@inheritDoc} */
    public void setContentType(String contentType) {
        response.setContentType(contentType);
    }
}
