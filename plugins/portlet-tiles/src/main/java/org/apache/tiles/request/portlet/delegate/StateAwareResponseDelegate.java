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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * A state aware response does not allow to access to the output stream and similar, so it is, essentially,
 * a feature blocker.
 */
public class StateAwareResponseDelegate implements ResponseDelegate {

    @Override
    public OutputStream getOutputStream() {
        throw new UnsupportedOperationException("No outputstream available for state-aware response");
    }

    @Override
    public PrintWriter getPrintWriter() {
        throw new UnsupportedOperationException("No outputstream available for state-aware response");
    }

    @Override
    public Writer getWriter() {
        throw new UnsupportedOperationException("No outputstream available for state-aware response");
    }

    @Override
    public boolean isResponseCommitted() {
        return false;
    }

    @Override
    public void setContentType(String contentType) {
        throw new UnsupportedOperationException("No outputstream available for state-aware response");
    }
}
