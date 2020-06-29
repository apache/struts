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
package org.apache.struts2.result.plain;

import javax.servlet.http.Cookie;

public class ResponseBuilder {

    public static final String CONTENT_TYPE = "Content-Type";

    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML = "text/html";
    public static final String APPLICATION_JSON = "application/json";

    private final BodyWriter body;
    private final HttpHeaders headers;
    private final HttpCookies cookies;

    public ResponseBuilder() {
        this.body = new BodyWriter();
        this.headers = new HttpHeaders().add(CONTENT_TYPE, TEXT_PLAIN + "; charset=UTF-8");
        this.cookies = new HttpCookies();
    }

    public ResponseBuilder write(String out) {
        body.write(out);
        return this;
    }

    public ResponseBuilder writeLine(String out) {
        body.writeLine(out);
        return this;
    }

    public ResponseBuilder withHeader(String name, String value) {
        headers.add(name, value);
        return this;
    }

    public ResponseBuilder withHeader(String name, Long value) {
        headers.add(name, value);
        return this;
    }

    public ResponseBuilder withHeader(String name, Integer value) {
        headers.add(name, value);
        return this;
    }

    public ResponseBuilder withContentTypeTextPlain() {
        headers.add(CONTENT_TYPE, TEXT_PLAIN + "; charset=UTF-8");
        return this;
    }

    public ResponseBuilder withContentTypeTextHtml() {
        headers.add(CONTENT_TYPE, TEXT_HTML + "; charset=UTF-8");
        return this;
    }

    public ResponseBuilder withContentTypeJson() {
        headers.add(CONTENT_TYPE, APPLICATION_JSON);
        return this;
    }

    public ResponseBuilder withContentType(String contentType) {
        headers.add(CONTENT_TYPE, contentType);
        return this;
    }

    public ResponseBuilder withCookie(String name, String value) {
        cookies.add(name, value);
        return this;
    }

    public Iterable<HttpHeader<String>> getStringHeaders() {
        return headers.getStringHeaders();
    }

    public Iterable<HttpHeader<Long>> getDateHeaders() {
        return headers.getDateHeaders();
    }

    public Iterable<HttpHeader<Integer>> getIntHeaders() {
        return headers.getIntHeaders();
    }

    public Iterable<Cookie> getCookies() {
        return cookies.getCookies();
    }

    public String getBody() {
        return body.getBody();
    }

}
