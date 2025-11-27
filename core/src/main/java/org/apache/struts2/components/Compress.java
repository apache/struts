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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.io.Writer;

/**
 * <p>
 * Used to compress HTML output. Just wrap a given section with the tag.
 * </p>
 *
 * <p>
 * <b>Security considerations:</b>
 * </p>
 * <ul>
 *   <li>Body content is truncated in log messages to prevent sensitive data exposure</li>
 *   <li>Maximum size limit prevents DoS attacks via large inputs (configurable via struts.compress.maxSize)</li>
 *   <li>Regex operations include safeguards against ReDoS attacks</li>
 * </ul>
 *
 * <p>
 * Configurable attributes are:
 * </p>
 *
 * <ul>
 *    <li>force (true/false) - always compress output, this can be useful in DevMode as devMode disables compression</li>
 *    <li>singleLine (true/false) - compress to a single space instead of a line break</li>
 * </ul>
 *
 * <p><b>Examples</b></p>
 * <pre>
 *  <!-- START SNIPPET: example -->
 *  &lt;s:compress&gt;
 *    &lt;s:form action="submit"&gt;
 *    &lt;s:text name="name" /&gt;
 *    ...
 *    &lt;/s:form&gt;
 *  &lt;/s:compress&gt;
 *  <!-- END SNIPPET: example -->
 * </pre>
 *
 * <p>Uses conditional compression depending on action</p>
 * <pre>
 *  <!-- START SNIPPET: example -->
 *  &lt;s:compress force="shouldCompress"&gt;
 *    &lt;s:form action="submit"&gt;
 *    &lt;s:text name="name" /&gt;
 *    ...
 *    &lt;/s:form&gt;
 *  &lt;/s:compress&gt;
 *  <!-- END SNIPPET: example -->
 * </pre>
 * "shouldCompress" is a field with getter define on action used in expression evaluation
 *
 * @since 7.2.0
 */
@StrutsTag(name = "compress", tldTagClass = "org.apache.struts2.views.jsp.CompressTag",
        description = "Compress wrapped content")
public class Compress extends Component {

    private static final Logger LOG = LogManager.getLogger(Compress.class);

    private String force;
    private String singleLine;
    private boolean compressionEnabled = true;
    private Long maxSize = null;
    private int logMaxLength = 200;

    public Compress(ValueStack stack) {
        super(stack);
    }

    @Inject(value = StrutsConstants.STRUTS_COMPRESS_ENABLED, required = false)
    public void setCompressionEnabled(String compressionEnabled) {
        this.compressionEnabled = BooleanUtils.toBoolean(compressionEnabled);
    }

    @Inject(value = StrutsConstants.STRUTS_COMPRESS_MAX_SIZE, required = false)
    public void setMaxSize(String maxSize) {
        if (maxSize != null && !maxSize.trim().isEmpty()) {
            try {
                this.maxSize = Long.parseLong(maxSize.trim());
                if (this.maxSize < 0) {
                    LOG.warn("Invalid maxSize value: {}, must be >= 0. Disabling size limit.", maxSize);
                    this.maxSize = null;
                }
            } catch (NumberFormatException e) {
                LOG.warn("Invalid maxSize value: {}, must be a valid number. Disabling size limit.", maxSize, e);
                this.maxSize = null;
            }
        }
    }

    @Inject(value = StrutsConstants.STRUTS_COMPRESS_LOG_MAX_LENGTH, required = false)
    public void setLogMaxLength(String logMaxLength) {
        if (logMaxLength != null && !logMaxLength.trim().isEmpty()) {
            try {
                int length = Integer.parseInt(logMaxLength.trim());
                if (length < 0) {
                    LOG.warn("Invalid logMaxLength value: {}, must be >= 0. Using default: 200.", logMaxLength);
                    this.logMaxLength = 200;
                } else {
                    this.logMaxLength = length;
                }
            } catch (NumberFormatException e) {
                LOG.warn("Invalid logMaxLength value: {}, must be a valid number. Using default: 200.", logMaxLength, e);
                this.logMaxLength = 200;
            }
        }
    }

    @Override
    public boolean end(Writer writer, String body) {
        // Check size limit before processing
        if (exceedsMaxSize(body) && compressionEnabled) {
            LOG.warn("Body size: {} exceeds maximum allowed size: {}, skipping compression", 
                     body.length(), maxSize);
            return super.end(writer, body, true);
        }

        Object forceValue = findValue(force, Boolean.class);
        Object singleLineValue = findValue(singleLine, Boolean.class);

        boolean forced = forceValue != null && Boolean.parseBoolean(forceValue.toString());
        if (!compressionEnabled && !forced) {
            LOG.debug("Compression disabled globally, skipping: {}", truncateForLogging(body));
            return super.end(writer, body, true);
        }
        if (devMode && !forced) {
            LOG.debug("Avoids compressing output: {} in DevMode", truncateForLogging(body));
            return super.end(writer, body, true);
        }
        LOG.trace("Compresses: {}", truncateForLogging(body));
        boolean useSingleLine = singleLineValue instanceof Boolean single && single;
        String compressedBody = compressWhitespace(body, useSingleLine);
        LOG.trace("Compressed: {}", truncateForLogging(compressedBody));
        return super.end(writer, compressedBody, true);
    }

    @Override
    public boolean usesBody() {
        return true;
    }

    @StrutsTagAttribute(description = "Force output compression")
    public void setForce(String force) {
        this.force = force;
    }

    @StrutsTagAttribute(description = "Always compress to a single space instead of a line break")
    public void setSingleLine(String singleLine) {
        this.singleLine = singleLine;
    }

    /**
     * Truncates content for safe logging to prevent sensitive data exposure
     * and excessive log file growth.
     *
     * @param content the content to truncate
     * @return truncated content with length indicator if truncated, original content otherwise
     */
    private String truncateForLogging(String content) {
        if (content == null) {
            return null;
        }
        if (content.length() <= logMaxLength) {
            return content;
        }
        return content.substring(0, logMaxLength) + "... (truncated, length: " + content.length() + ")";
    }

    /**
     * Checks if the body content exceeds the maximum allowed size.
     *
     * @param body the body content to check
     * @return true if body exceeds maximum size, false otherwise
     */
    private boolean exceedsMaxSize(String body) {
        if (maxSize == null || body == null) {
            return false;
        }
        return body.length() > maxSize;
    }

    /**
     * Compresses whitespace in the input string.
     *
     * <p>This method normalizes line breaks (CR, LF, CRLF) to LF and collapses
     * consecutive whitespace characters according to the specified mode.</p>
     *
     * <p>Security note: This method includes safeguards against ReDoS attacks
     * by using simple, bounded regex patterns and early exit for very large inputs.</p>
     *
     * @param input      the input string to compress
     * @param singleLine if true, removes all line breaks and collapses to single spaces;
     *                   if false, preserves line structure with single line breaks
     * @return the compressed string with normalized whitespace
     */
    private String compressWhitespace(String input, boolean singleLine) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Early exit for very large inputs to prevent ReDoS and excessive processing
        // This is a secondary check; primary size check happens in end() method
        if (input.length() > 50_000_000) { // 50MB hard limit for regex operations
            LOG.warn("Input size: {} exceeds safe processing limit (50MB), returning original content", 
                     input.length());
            return input;
        }

        // Normalize all line breaks to \n (handles \r\n, \r, \n)
        // This pattern is safe: simple alternation with no quantifiers
        String normalized = input.replaceAll("\\r\\n|\\r", "\n");

        if (singleLine) {
            // Remove all line breaks and collapse whitespace to single space
            // Pattern is safe: simple character class with quantifier
            String compressed = normalized.replaceAll("\\s+", " ").strip();
            // Simple string replace (not regex) - safe
            return compressed.replace("> <", "><");
        } else {
            // Preserve line breaks but collapse other whitespace
            // Patterns are safe: bounded character classes with simple quantifiers
            return normalized
                    .replaceAll("[ \\t]+", " ")      // Collapse spaces/tabs to single space
                    .replaceAll("\\n+", "\n")       // Collapse multiple newlines to single
                    .replaceAll(" *\\n *", "\n")    // Remove spaces around newlines
                    .strip();                        // Remove leading/trailing whitespace
        }
    }
}
