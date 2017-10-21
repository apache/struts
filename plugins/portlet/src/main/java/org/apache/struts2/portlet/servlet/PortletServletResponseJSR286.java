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
package org.apache.struts2.portlet.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.portlet.MimeResponse;
import javax.portlet.PortletResponse;
import javax.portlet.ResourceResponse;
import javax.servlet.ServletOutputStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * PortletServletResponseJSR286.
 *
 * @author Rene Gielen
 */
public class PortletServletResponseJSR286 extends PortletServletResponse {

  private static final Logger LOG = LogManager.getLogger(PortletServletResponseJSR286.class);
  
    public PortletServletResponseJSR286( PortletResponse portletResponse ) {
        super(portletResponse);
    }


    public void flushBuffer() throws IOException {
        if(portletResponse instanceof MimeResponse) {
            ((MimeResponse)portletResponse).flushBuffer();
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public int getBufferSize() {
        if(portletResponse instanceof MimeResponse) {
            return ((MimeResponse)portletResponse).getBufferSize();
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public String getCharacterEncoding() {
        if(portletResponse instanceof MimeResponse) {
            return ((MimeResponse)portletResponse).getCharacterEncoding();
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public String getContentType() {
        if(portletResponse instanceof MimeResponse) {
            return ((MimeResponse)portletResponse).getContentType();
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public Locale getLocale() {
        if(portletResponse instanceof MimeResponse) {
            return ((MimeResponse)portletResponse).getLocale();
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if(portletResponse instanceof MimeResponse) {
            return new PortletServletOutputStream(((MimeResponse)portletResponse).getPortletOutputStream());
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public PrintWriter getWriter() throws IOException {
        if(portletResponse instanceof MimeResponse) {
            return ((MimeResponse)portletResponse).getWriter();
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public boolean isCommitted() {
        if(portletResponse instanceof MimeResponse) {
            return ((MimeResponse)portletResponse).isCommitted();
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public void reset() {
        if(portletResponse instanceof MimeResponse) {
            ((MimeResponse)portletResponse).reset();
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public void resetBuffer() {
        if(portletResponse instanceof MimeResponse) {
            ((MimeResponse)portletResponse).resetBuffer();
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public void setBufferSize(int size) {
        if(portletResponse instanceof MimeResponse) {
            ((MimeResponse)portletResponse).setBufferSize(size);
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public void setCharacterEncoding(String charset) {
        if(portletResponse instanceof ResourceResponse) {
            ((ResourceResponse)portletResponse).setCharacterEncoding(charset);
        }
        else {
          throw new IllegalStateException("Only allowed in resource phase");
        }
    }

    public void setContentLength(int len) {
        if(portletResponse instanceof ResourceResponse) {
            ((ResourceResponse)portletResponse).setContentLength(len);
        }
        else {
          throw new IllegalStateException("Only allowed in resource phase");
        }
    }

    public void setContentType(String type) {
        if(portletResponse instanceof MimeResponse) {
            ((MimeResponse)portletResponse).setContentType(type);
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public void setLocale(Locale loc) {
        if(portletResponse instanceof ResourceResponse) {
            ((ResourceResponse)portletResponse).setLocale(loc);
        }
        else {
          throw new IllegalStateException("Only allowed in resource phase");
        }
    }

}
