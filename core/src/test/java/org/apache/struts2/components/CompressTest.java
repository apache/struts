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
package org.apache.struts2.components;

import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;

import java.io.StringWriter;
import java.util.Map;

public class CompressTest extends StrutsInternalTestCase {

    private ValueStack stack;
    private Map<String, Object> context;

    public void testCompressHtmlOutput() {
        Compress compress = new Compress(stack);

        String body = """
                <html>
                <head>
                    <title>File upload: result</title>
                </head>
                <body>
                    <h1>File upload: result</h1>
                </body>
                </html>
                """;

        StringWriter writer = new StringWriter();

        compress.setDevMode("false");
        compress.setForce("false");
        compress.end(writer, body);

        assertEquals("<html><head><title>File upload: result</title></head><body><h1>File upload: result</h1></body></html>", writer.toString());
    }

    public void testAvoidCompressingInDevModeHtmlOutput() {
        Compress compress = new Compress(stack);

        String body = """
                <html>
                <head>
                    <title>File upload: result</title>
                </head>
                <body>
                    <h1>File upload: result</h1>
                </body>
                </html>
                """;

        StringWriter writer = new StringWriter();

        compress.setDevMode("true");
        compress.end(writer, body);

        assertEquals(body, writer.toString());
    }

    public void testCompressHtmlOutputEvenInDevMode() {
        Compress compress = new Compress(stack);

        String body = """
                <html>
                <head>
                    <title>File upload: result</title>
                </head>
                <body>
                    <h1>File upload: result</h1>
                </body>
                </html>
                """;

        StringWriter writer = new StringWriter();

        compress.setDevMode("true");
        compress.setForce("true");
        compress.end(writer, body);

        assertEquals("<html><head><title>File upload: result</title></head><body><h1>File upload: result</h1></body></html>", writer.toString());
    }

    public void testCompressHtmlOutputEvenInDevModeAndForceIsExpression() {
        Compress compress = new Compress(stack);

        String body = """
                <html>
                <head>
                    <title>File upload: result</title>
                </head>
                <body>
                    <h1>File upload: result</h1>
                </body>
                </html>
                """;

        this.context.put("shouldCompress", Boolean.TRUE);

        compress.setDevMode("true");
        compress.setForce("shouldCompress");

        StringWriter writer = new StringWriter();
        compress.end(writer, body);

        assertEquals("<html><head><title>File upload: result</title></head><body><h1>File upload: result</h1></body></html>", writer.toString());
    }

    public void testCompressionDisabledGlobally() {
        Compress compress = new Compress(stack);

        String body = """
                <html>
                <head>
                    <title>File upload: result</title>
                </head>
                <body>
                    <h1>File upload: result</h1>
                </body>
                </html>
                """;

        StringWriter writer = new StringWriter();

        compress.setDevMode("false");
        compress.setCompressionEnabled("false");
        compress.end(writer, body);

        assertEquals(body, writer.toString());
    }

    public void testCompressionDisabledGloballyButForced() {
        Compress compress = new Compress(stack);

        String body = """
                <html>
                <head>
                    <title>File upload: result</title>
                </head>
                <body>
                    <h1>File upload: result</h1>
                </body>
                </html>
                """;

        StringWriter writer = new StringWriter();

        compress.setDevMode("false");
        compress.setCompressionEnabled("false");
        compress.setForce("true");
        compress.end(writer, body);

        assertEquals("<html><head><title>File upload: result</title></head><body><h1>File upload: result</h1></body></html>", writer.toString());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        stack = container.getInstance(ValueStackFactory.class).createValueStack();
        context = stack.getContext();
    }
}
