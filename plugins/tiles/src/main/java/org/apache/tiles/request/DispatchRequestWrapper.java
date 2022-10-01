/*
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
package org.apache.tiles.request;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tiles.request.attribute.Addable;

/**
 * Delegate for ease of customization.
 */
public class DispatchRequestWrapper extends AbstractRequest implements RequestWrapper {

    /**
     * The wrapper request context object.
     */
    private final DispatchRequest context;

    /**
     * Constructor.
     *
     * @param context The request context to wrap.
     */
    public DispatchRequestWrapper(DispatchRequest context) {
        this.context = context;
    }

    /** {@inheritDoc} */
    public DispatchRequest getWrappedRequest() {
        return context;
    }

    /** {@inheritDoc} */
    public Map<String, String> getHeader() {
        return context.getHeader();
    }

    /** {@inheritDoc} */
    public Map<String, String[]> getHeaderValues() {
        return context.getHeaderValues();
    }

    /** {@inheritDoc} */
    public Addable<String> getResponseHeaders() {
        return context.getResponseHeaders();
    }

    /** {@inheritDoc} */
    public Map<String, Object> getContext(String scope) {
        return context.getContext(scope);
    }

    /** {@inheritDoc} */
    public List<String> getAvailableScopes() {
        return context.getAvailableScopes();
    }

    /** {@inheritDoc} */
    public ApplicationContext getApplicationContext() {
        return context.getApplicationContext();
    }

    /** {@inheritDoc} */
    public void dispatch(String path) throws IOException {
        context.dispatch(path);
    }

    /** {@inheritDoc} */
    public void include(String path) throws IOException {
        context.include(path);
    }

    /** {@inheritDoc} */
    public OutputStream getOutputStream() throws IOException {
        return context.getOutputStream();
    }

    /** {@inheritDoc} */
    public Writer getWriter() throws IOException {
        return context.getWriter();
    }

    /** {@inheritDoc} */
    public PrintWriter getPrintWriter() throws IOException {
        return context.getPrintWriter();
    }

    /** {@inheritDoc} */
    public boolean isResponseCommitted() {
        return context.isResponseCommitted();
    }

    /** {@inheritDoc} */
    public void setContentType(String contentType) {
        context.setContentType(contentType);
    }

    /** {@inheritDoc} */
    public Map<String, String> getParam() {
        return context.getParam();
    }

    /** {@inheritDoc} */
    public Map<String, String[]> getParamValues() {
        return context.getParamValues();
    }

    /** {@inheritDoc} */
    public Locale getRequestLocale() {
        return context.getRequestLocale();
    }

    /** {@inheritDoc} */
    public boolean isUserInRole(String role) {
        return context.isUserInRole(role);
    }
}
