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
package org.apache.struts2.rest;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ModelDriven;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.handler.ContentTypeHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Uses the content handler to apply the request body to the action
 */
public class ContentTypeInterceptor extends AbstractInterceptor {

    private static final Logger LOG = LogManager.getLogger(ContentTypeInterceptor.class);

    public static final int DEFAULT_MAX_LENGTH = 2_097_152;

    private ContentTypeHandlerManager selector;
    private int maxLength = DEFAULT_MAX_LENGTH;

    @Inject
    public void setContentTypeHandlerSelector(ContentTypeHandlerManager selector) {
        this.selector = selector;
    }

    @Inject(value = RestConstants.REST_CONTENT_MAX_LENGTH, required = false)
    public void setMaxLength(String maxLength) {
        if (StringUtils.isBlank(maxLength)) {
            return;
        }
        int length;
        try {
            length = Integer.parseInt(maxLength.trim());
        } catch (NumberFormatException e) {
            LOG.warn("Ignoring non-numeric {} value: {}, keeping {}",
                    RestConstants.REST_CONTENT_MAX_LENGTH, maxLength, this.maxLength);
            return;
        }
        if (length < 1) {
            LOG.warn("Ignoring out-of-range {} value: {}, expected 1 or more, keeping {}",
                    RestConstants.REST_CONTENT_MAX_LENGTH, length, this.maxLength);
            return;
        }
        this.maxLength = length;
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        ContentTypeHandler handler = selector.getHandlerForRequest(request);

        Object target = invocation.getAction();
        if (target instanceof ModelDriven) {
            target = ((ModelDriven<?>)target).getModel();
        }

        if (request.getContentLength() > 0) {
            BoundedReader reader = new BoundedReader(openBodyReader(request), maxLength);
            try {
                handler.toObject(invocation, reader, target);
            } catch (Exception e) {
                if (reader.limitExceeded()) {
                    throw requestBodyTooLarge();
                }
                throw e;
            }
            if (reader.limitExceeded()) {
                throw requestBodyTooLarge();
            }
        }
        return invocation.invoke();
    }

    private RequestBodyTooLargeException requestBodyTooLarge() {
        return new RequestBodyTooLargeException("Request body exceeds maximum allowed length ("
                + maxLength + "). Use " + RestConstants.REST_CONTENT_MAX_LENGTH + " to increase the limit.");
    }

    private static InputStreamReader openBodyReader(HttpServletRequest request) throws IOException {
        String encoding = request.getCharacterEncoding();
        InputStream is = request.getInputStream();
        return encoding == null ? new InputStreamReader(is) : new InputStreamReader(is, encoding);
    }

    /**
     * Stops the handler at {@code struts.rest.content.maxLength} characters. The handler may wrap the
     * {@link IOException} thrown here in its own type, so {@link #intercept} consults
     * {@link #limitExceeded()} afterwards rather than relying on what propagates.
     */
    private static final class BoundedReader extends FilterReader {

        private final int limit;
        private long consumed;
        private boolean limitExceeded;

        BoundedReader(Reader in, int limit) {
            super(in);
            this.limit = limit;
        }

        @Override
        public int read() throws IOException {
            int c = super.read();
            if (c != -1) {
                consumed(1);
            }
            return c;
        }

        @Override
        public int read(char[] buf, int off, int len) throws IOException {
            int n = super.read(buf, off, len);
            if (n > 0) {
                consumed(n);
            }
            return n;
        }

        @Override
        public long skip(long n) throws IOException {
            long skipped = super.skip(n);
            if (skipped > 0) {
                consumed(skipped);
            }
            return skipped;
        }

        private void consumed(long n) throws IOException {
            consumed += n;
            if (consumed > limit) {
                limitExceeded = true;
                throw new IOException("Request body exceeds " + limit + " characters");
            }
        }

        boolean limitExceeded() {
            return limitExceeded;
        }
    }

}
