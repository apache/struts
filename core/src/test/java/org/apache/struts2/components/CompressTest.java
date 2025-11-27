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

        String expected = """
                <html>
                <head>
                <title>File upload: result</title>
                </head>
                <body>
                <h1>File upload: result</h1>
                </body>
                </html>
                """.stripTrailing();
        assertEquals(expected, writer.toString());
    }

    public void testCompressSingleLineHtmlOutput() {
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
        compress.setSingleLine("true");
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
        compress.setSingleLine("true");
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

        String expected = """
                <html>
                <head>
                <title>File upload: result</title>
                </head>
                <body>
                <h1>File upload: result</h1>
                </body>
                </html>
                """.stripTrailing();
        assertEquals(expected, writer.toString());
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

        String expected = """
                <html>
                <head>
                <title>File upload: result</title>
                </head>
                <body>
                <h1>File upload: result</h1>
                </body>
                </html>
                """.stripTrailing();
        assertEquals(expected, writer.toString());
    }

    public void testSingleLineAsExpression() {
        Compress compress = new Compress(stack);

        String body = """
                <html>
                <head>
                    <title>Test</title>
                </head>
                </html>
                """;

        this.context.put("shouldUseSingleLine", Boolean.TRUE);

        StringWriter writer = new StringWriter();

        compress.setDevMode("false");
        compress.setSingleLine("shouldUseSingleLine");
        compress.end(writer, body);

        assertEquals("<html><head><title>Test</title></head></html>", writer.toString());
    }

    public void testContentWithCRLineBreaks() {
        Compress compress = new Compress(stack);

        String body = "<html>\r<head>\r<title>Test</title>\r</head>\r</html>";

        StringWriter writer = new StringWriter();

        compress.setDevMode("false");
        compress.end(writer, body);

        String expected = "<html>\n<head>\n<title>Test</title>\n</head>\n</html>";
        assertEquals(expected, writer.toString());
    }

    public void testContentWithLFLineBreaks() {
        Compress compress = new Compress(stack);

        String body = "<html>\n<head>\n<title>Test</title>\n</head>\n</html>";

        StringWriter writer = new StringWriter();

        compress.setDevMode("false");
        compress.end(writer, body);

        String expected = "<html>\n<head>\n<title>Test</title>\n</head>\n</html>";
        assertEquals(expected, writer.toString());
    }

    public void testContentWithCRLFLineBreaks() {
        Compress compress = new Compress(stack);

        String body = "<html>\r\n<head>\r\n<title>Test</title>\r\n</head>\r\n</html>";

        StringWriter writer = new StringWriter();

        compress.setDevMode("false");
        compress.end(writer, body);

        String expected = "<html>\n<head>\n<title>Test</title>\n</head>\n</html>";
        assertEquals(expected, writer.toString());
    }

    public void testContentWithMixedLineBreaks() {
        Compress compress = new Compress(stack);

        String body = "<html>\r\n<head>\n<title>Test</title>\r</head>\r\n</html>";

        StringWriter writer = new StringWriter();

        compress.setDevMode("false");
        compress.end(writer, body);

        String expected = "<html>\n<head>\n<title>Test</title>\n</head>\n</html>";
        assertEquals(expected, writer.toString());
    }

    public void testEmptyBody() {
        Compress compress = new Compress(stack);

        String body = "";

        StringWriter writer = new StringWriter();

        compress.setDevMode("false");
        compress.end(writer, body);

        assertEquals("", writer.toString());
    }

    public void testWhitespaceOnlyBody() {
        Compress compress = new Compress(stack);

        String body = "   \n\n\n   ";

        StringWriter writer = new StringWriter();

        compress.setDevMode("false");
        compress.end(writer, body);

        assertEquals("", writer.toString()); // Leading/trailing whitespace removed
    }

    public void testSingleLineWithWhitespaceOnlyBody() {
        Compress compress = new Compress(stack);

        String body = "   \n\n\n   ";

        StringWriter writer = new StringWriter();

        compress.setDevMode("false");
        compress.setSingleLine("true");
        compress.end(writer, body);

        assertEquals("", writer.toString()); // All whitespace removed in single-line mode
    }

    public void testMaxSizeLimit() {
        Compress compress = new Compress(stack);

        // Create body larger than default maxSize (10MB)
        StringBuilder largeBody = new StringBuilder();
        largeBody.append("<html><body>");
        for (int i = 0; i < 11_000_000; i++) { // ~11MB
            largeBody.append("x");
        }
        largeBody.append("</body></html>");

        StringWriter writer = new StringWriter();
        compress.setDevMode("false");
        compress.setMaxSize("10485760"); // 10MB

        compress.end(writer, largeBody.toString());

        // Should return original content without compression
        assertEquals(largeBody.toString(), writer.toString());
    }

    public void testMaxSizeDisabled() {
        Compress compress = new Compress(stack);

        StringBuilder largeBody = new StringBuilder();
        largeBody.append("<html><body>");
        for (int i = 0; i < 11_000_000; i++) {
            largeBody.append("x");
        }
        largeBody.append("</body></html>");

        StringWriter writer = new StringWriter();
        compress.setDevMode("false");
        // Don't set maxSize - defaults to null (no limit)

        compress.end(writer, largeBody.toString());

        // Should compress even large content when limit is disabled
        assertTrue("Content should be compressed when limit is disabled", 
                   !largeBody.toString().equals(writer.toString()));
    }

    public void testLogTruncation() {
        Compress compress = new Compress(stack);

        StringBuilder longBody = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            longBody.append("x");
        }

        compress.setLogMaxLength("200");

        // Test that processing doesn't throw exceptions with long content
        StringWriter writer = new StringWriter();
        compress.setDevMode("false");
        compress.end(writer, longBody.toString());

        // Should process without errors
        assertNotNull(writer.toString());
    }

    public void testVeryLargeInputSafety() {
        Compress compress = new Compress(stack);

        // Create input larger than 50MB hard limit
        StringBuilder hugeBody = new StringBuilder();
        hugeBody.append("<html><body>");
        for (int i = 0; i < 60_000_000; i++) { // ~60MB
            hugeBody.append("x");
        }
        hugeBody.append("</body></html>");

        StringWriter writer = new StringWriter();
        compress.setDevMode("false");
        // Don't set maxSize - defaults to null (no config limit)

        compress.end(writer, hugeBody.toString());

        // Should return original content due to hard limit in compressWhitespace
        assertEquals(hugeBody.toString(), writer.toString());
    }

    public void testInvalidMaxSizeConfiguration() {
        Compress compress = new Compress(stack);

        // Test negative value - should disable limit (null)
        compress.setMaxSize("-1");
        // Verify behavior: large content should be processed when limit is disabled
        StringBuilder largeBody = new StringBuilder();
        largeBody.append("<html><body>");
        for (int i = 0; i < 11_000_000; i++) {
            largeBody.append("x");
        }
        largeBody.append("</body></html>");
        StringWriter writer = new StringWriter();
        compress.setDevMode("false");
        compress.end(writer, largeBody.toString());
        // Should compress when limit is disabled
        assertTrue("Content should be compressed when limit is disabled (negative value)", 
                   !largeBody.toString().equals(writer.toString()));

        // Test non-numeric value - should disable limit
        compress.setMaxSize("invalid");
        writer = new StringWriter();
        compress.end(writer, largeBody.toString());
        // Should compress when limit is disabled
        assertTrue("Content should be compressed when limit is disabled (invalid value)", 
                   !largeBody.toString().equals(writer.toString()));

        // Test valid value
        compress.setMaxSize("5242880"); // 5MB
        writer = new StringWriter();
        compress.end(writer, largeBody.toString());
        // Should skip compression for content exceeding 5MB
        assertEquals(largeBody.toString(), writer.toString());
    }

    public void testInvalidLogMaxLengthConfiguration() {
        Compress compress = new Compress(stack);

        // Test negative value - should use default (200)
        compress.setLogMaxLength("-1");
        // Verify behavior: processing should work normally
        String body = "<html><body>Test</body></html>";
        StringWriter writer = new StringWriter();
        compress.setDevMode("false");
        compress.end(writer, body);
        assertNotNull(writer.toString());

        // Test non-numeric value - should use default (200)
        compress.setLogMaxLength("invalid");
        writer = new StringWriter();
        compress.end(writer, body);
        assertNotNull(writer.toString());

        // Test valid value
        compress.setLogMaxLength("500");
        writer = new StringWriter();
        compress.end(writer, body);
        assertNotNull(writer.toString());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        stack = container.getInstance(ValueStackFactory.class).createValueStack();
        context = stack.getContext();
    }
}
