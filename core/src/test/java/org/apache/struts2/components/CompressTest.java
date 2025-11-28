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

import static org.junit.Assert.assertNotEquals;

public class CompressTest extends StrutsInternalTestCase {

    private ValueStack stack;
    private Map<String, Object> context;
    private Compress compress;

    public void testCompressHtmlOutput() {
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

        String expected = "<html><head><title>File upload: result</title></head><body><h1>File upload: result</h1></body></html>";
        assertEquals(expected, writer.toString());
    }

    public void testCompressHtmlOutputSimpleAlgorithm() {
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
        compress.end(writer, body);

        assertEquals("<html><head><title>File upload: result</title></head><body><h1>File upload: result</h1></body></html>", writer.toString());
    }

    public void testAvoidCompressingInDevModeHtmlOutput() {
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

        String expected = "<html><head><title>File upload: result</title></head><body><h1>File upload: result</h1></body></html>";
        assertEquals(expected, writer.toString());
    }

    public void testCompressionDisabledGlobally() {
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

        String expected = "<html><head><title>File upload: result</title></head><body><h1>File upload: result</h1></body></html>";
        assertEquals(expected, writer.toString());
    }

    public void testContentWithCRLineBreaks() {

        String body = "<html>\r<head>\r<title>Test</title>\r</head>\r</html>";

        StringWriter writer = new StringWriter();

        compress.setDevMode("false");
        compress.end(writer, body);

        String expected = "<html><head><title>Test</title></head></html>";
        assertEquals(expected, writer.toString());
    }

    public void testContentWithLFLineBreaks() {
        String body = "<html>\n<head>\n<title>Test</title>\n</head>\n</html>";

        StringWriter writer = new StringWriter();

        compress.setDevMode("false");
        compress.end(writer, body);

        String expected = "<html><head><title>Test</title></head></html>";
        assertEquals(expected, writer.toString());
    }

    public void testContentWithCRLFLineBreaks() {
        String body = "<html>\r\n<head>\r\n<title>Test</title>\r\n</head>\r\n</html>";

        StringWriter writer = new StringWriter();

        compress.setDevMode("false");
        compress.end(writer, body);

        String expected = "<html><head><title>Test</title></head></html>";
        assertEquals(expected, writer.toString());
    }

    public void testContentWithMixedLineBreaks() {
        String body = "<html>\r\n<head>\n<title>Test</title>\r</head>\r\n</html>";

        StringWriter writer = new StringWriter();

        compress.setDevMode("false");
        compress.end(writer, body);

        String expected = "<html><head><title>Test</title></head></html>";
        assertEquals(expected, writer.toString());
    }

    public void testEmptyBody() {
        String body = "";

        StringWriter writer = new StringWriter();

        compress.setDevMode("false");
        compress.end(writer, body);

        assertEquals("", writer.toString());
    }

    public void testWhitespaceOnlyBody() {
        String body = "   \n\n\n   ";

        StringWriter writer = new StringWriter();

        compress.setDevMode("false");
        compress.end(writer, body);

        assertEquals("", writer.toString()); // Leading/trailing whitespace removed
    }

    public void testMaxSizeLimit() {
        // Create body larger than default maxSize (10MB)
        StringBuilder largeBody = new StringBuilder();
        largeBody.append("<html><body>");
        largeBody.append("x".repeat(11_000_000));
        largeBody.append("</body></html>");

        StringWriter writer = new StringWriter();
        compress.setDevMode("false");
        compress.setMaxSize("10485760"); // 10MB

        compress.end(writer, largeBody.toString());

        // Should return original content without compression
        assertEquals(largeBody.toString(), writer.toString());
    }

    public void testMaxSizeDisabled() {
        // Create body with whitespace that will be compressed
        StringBuilder largeBody = new StringBuilder();
        largeBody.append("<html>\n<body>\n");
        largeBody.append("    <p>Content with whitespace</p>\n".repeat(1_000_000));
        largeBody.append("</body>\n</html>");

        StringWriter writer = new StringWriter();
        compress.setDevMode("false");
        // Don't set maxSize - defaults to null (no limit)

        compress.end(writer, largeBody.toString());

        // Should compress even large content when limit is disabled
        assertNotEquals("Content should be compressed when limit is disabled", largeBody.toString(), writer.toString());
    }

    public void testLogTruncation() {
        String longBody = "x".repeat(500);

        compress.setLogMaxLength("200");

        // Test that processing doesn't throw exceptions with long content
        StringWriter writer = new StringWriter();
        compress.setDevMode("false");
        compress.end(writer, longBody);

        // Should process without errors
        assertNotNull(writer.toString());
    }

    public void testVeryLargeInputSafety() {
        // Create input larger than 50MB hard limit
        StringBuilder hugeBody = new StringBuilder();
        hugeBody.append("<html><body>");
        hugeBody.append("x".repeat(60_000_000));
        hugeBody.append("</body></html>");

        StringWriter writer = new StringWriter();
        compress.setDevMode("false");
        // Don't set maxSize - defaults to null (no config limit)

        compress.end(writer, hugeBody.toString());

        // Should return original content due to hard limit in compressWhitespace
        assertEquals(hugeBody.toString(), writer.toString());
    }

    public void testInvalidMaxSizeConfiguration() {
        // Test non-numeric value - should disable limit and compress
        String largeBodyStr = "<html>\n<body>\n" +
                "    <p>Content with whitespace</p>\n".repeat(1_000) +
                "</body>\n</html>";

        compress.setDevMode("false");
        compress.setMaxSize("invalid");

        StringWriter writer = new StringWriter();
        compress.end(writer, largeBodyStr);

        // Should compress when limit is disabled (invalid value)
        assertNotEquals("Content should be compressed when limit is disabled (invalid value)", largeBodyStr, writer.toString());
    }

    public void testValidMaxSizeConfiguration() {
        // Test valid value - create body to exceed 5MB limit
        StringBuilder hugeBody = new StringBuilder();
        hugeBody.append("<html><body>");
        hugeBody.append("x".repeat(6_000_000));
        hugeBody.append("</body></html>");

        compress.setDevMode("false");
        compress.setMaxSize("5242880"); // 5MB

        StringWriter writer = new StringWriter();
        compress.end(writer, hugeBody.toString());

        // Should skip compression for content exceeding 5MB
        assertEquals(hugeBody.toString(), writer.toString());
    }

    public void testInvalidLogMaxLengthConfiguration() {
        // Test negative value - should use default (200) and work normally
        compress.setLogMaxLength("-1");

        String body = "<html><body>Test</body></html>";
        StringWriter writer = new StringWriter();
        compress.setDevMode("false");
        compress.end(writer, body);

        assertNotNull(writer.toString());
        assertEquals("<html><body>Test</body></html>", writer.toString());
    }

    public void testValidLogMaxLengthConfiguration() {
        // Test valid value - should work normally
        compress.setLogMaxLength("500");

        String body = "<html><body>Test</body></html>";
        StringWriter writer = new StringWriter();
        compress.setDevMode("false");
        compress.end(writer, body);

        assertNotNull(writer.toString());
        assertEquals("<html><body>Test</body></html>", writer.toString());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        stack = container.getInstance(ValueStackFactory.class).createValueStack();
        context = stack.getContext();
        compress = new Compress(stack);
        stack.push(compress);
    }
}
