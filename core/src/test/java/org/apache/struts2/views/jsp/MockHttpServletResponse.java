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
package org.apache.struts2.views.jsp;

import com.mockobjects.ExpectationCounter;
import com.mockobjects.ExpectationList;
import com.mockobjects.ExpectationValue;
import com.mockobjects.MapEntry;
import com.mockobjects.MockObject;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

public class MockHttpServletResponse  extends MockObject implements HttpServletResponse {
    private final ExpectationList myContentTypes = new ExpectationList("MockHttpServletResponse.setContentType");
    private final ExpectationList myHeaders = new ExpectationList("MockHttpServletResponse.setHeader");
    private final ExpectationCounter mySetStatusCalls = new ExpectationCounter("MockHttpServletResponse.setStatus");
    private final ExpectationList myRedirects = new ExpectationList("MockHttpServletResponse.sendRedirect");
    private MockServletOutputStream myOutputStream = new MockServletOutputStream();
    private final ExpectationValue myErrorCode = new ExpectationValue("MockHttpServletResponse.sendError");
    private final ExpectationValue myErrorMessage = new ExpectationValue("MockHttpServletResponse.sendError");
    private final ExpectationValue length = new ExpectationValue("MockHttpServletResponse.length");

    public MockHttpServletResponse() {
    }

    public void addCookie(Cookie arg1) {
        this.notImplemented();
    }

    public void addDateHeader(String arg1, long arg2) {
        this.notImplemented();
    }

    public void addHeader(String arg1, String arg2) {
        this.notImplemented();
    }

    public void addIntHeader(String arg1, int arg2) {
        this.notImplemented();
    }

    public boolean containsHeader(String arg1) {
        this.notImplemented();
        return false;
    }

    public String encodeRedirectUrl(String arg1) {
        this.notImplemented();
        return null;
    }

    public String encodeRedirectURL(String arg1) {
        this.notImplemented();
        return null;
    }

    public String encodeUrl(String arg1) {
        this.notImplemented();
        return null;
    }

    public String encodeURL(String arg1) {
        this.notImplemented();
        return null;
    }

    public void flushBuffer() throws IOException {
        this.notImplemented();
    }

    public int getBufferSize() {
        this.notImplemented();
        return 0;
    }

    public String getCharacterEncoding() {
        this.notImplemented();
        return null;
    }

    /**
     * @return
     */
    @Override
    public String getContentType() {
        return null;
    }

    public Locale getLocale() {
        this.notImplemented();
        return null;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return this.myOutputStream;
    }

    public String getOutputStreamContents() {
        return this.myOutputStream.getContents();
    }

    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(this.myOutputStream, true);
    }

    /**
     * @param s
     */
    @Override
    public void setCharacterEncoding(String s) {

    }

    public boolean isCommitted() {
        this.notImplemented();
        return false;
    }

    public void reset() {
        this.notImplemented();
    }

    public void resetBuffer() {
        this.notImplemented();
    }

    public void setExpectedError(int anErrorCode) {
        this.myErrorCode.setExpected(anErrorCode);
    }

    public void setExpectedError(int anErrorCode, String anErrorMessage) {
        this.setExpectedError(anErrorCode);
        this.myErrorMessage.setExpected(anErrorMessage);
    }

    public void setExpectedErrorNothing() {
        this.myErrorCode.setExpectNothing();
        this.myErrorMessage.setExpectNothing();
    }

    public void sendError(int anErrorCode) throws IOException {
        this.myErrorCode.setActual(anErrorCode);
    }

    public void sendError(int anErrorCode, String anErrorMessage) throws IOException {
        this.sendError(anErrorCode);
        this.myErrorMessage.setActual(anErrorMessage);
    }

    public void sendRedirect(String aURL) throws IOException {
        this.myRedirects.addActual(aURL);
    }

    public void setBufferSize(int arg1) {
        this.notImplemented();
    }

    public void setContentLength(int length) {
        this.length.setActual(length);
    }

    /**
     * @param l
     */
    @Override
    public void setContentLengthLong(long l) {

    }

    public void setExpectedContentLength(int length) {
        this.length.setExpected(length);
    }

    public void setContentType(String contentType) {
        this.myContentTypes.addActual(contentType);
    }

    public void setDateHeader(String arg1, long arg2) {
        this.notImplemented();
    }

    public void setExpectedContentType(String contentType) {
        this.myContentTypes.addExpected(contentType);
    }

    public void setExpectedHeader(String key, String value) {
        this.myHeaders.addExpected(new MapEntry(key, value));
    }

    public void setExpectedRedirect(String aURL) throws IOException {
        this.myRedirects.addExpected(aURL);
    }

    public void setExpectedSetStatusCalls(int callCount) {
        this.mySetStatusCalls.setExpected(callCount);
    }

    public void setHeader(String key, String value) {
        this.myHeaders.addActual(new MapEntry(key, value));
    }

    public void setIntHeader(String arg1, int arg2) {
        this.notImplemented();
    }

    public void setLocale(Locale arg1) {
        this.notImplemented();
    }

    public void setStatus(int status) {
        this.mySetStatusCalls.inc();
    }

    /**
     * @return
     */
    @Override
    public int getStatus() {
        return 0;
    }

    /**
     * @param s
     * @return
     */
    @Override
    public String getHeader(String s) {
        return null;
    }

    /**
     * @param s
     * @return
     */
    @Override
    public Collection<String> getHeaders(String s) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

    public void setStatus(int arg1, String arg2) {
        this.notImplemented();
    }

    public void setupOutputStream(MockServletOutputStream anOutputStream) {
        this.myOutputStream = anOutputStream;
    }
}
