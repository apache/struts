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
        description = "Compress wrapped content\n\n<p><b>Security:</b> The compress tag includes built-in protections against DoS attacks and sensitive data exposure. Large content exceeding the configured maximum size (default 10MB) will be skipped without compression. Log messages are automatically truncated to prevent sensitive data from appearing in logs.</p>")
public class Compress extends Component {

    private static final Logger LOG = LogManager.getLogger(Compress.class);

    private String force;
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
        try {
            this.maxSize = Long.parseLong(maxSize.trim());
        } catch (NumberFormatException e) {
            this.maxSize = null;
        }
    }

    @Inject(value = StrutsConstants.STRUTS_COMPRESS_LOG_MAX_LENGTH, required = false)
    public void setLogMaxLength(String logMaxLength) {
        try {
            this.logMaxLength = Integer.parseInt(logMaxLength.trim());
        } catch (NumberFormatException e) {
            this.logMaxLength = 200;
        }
    }

    @Override
    public boolean end(Writer writer, String body) {
        // Check size limit before processing
        if (exceedsMaxSize(body) && compressionEnabled) {
            LOG.warn("Body size: {} exceeds maximum allowed size: {}, skipping compression", body.length(), maxSize);
            return super.end(writer, body, true);
        }

        Object forceValue = findValue(force, Boolean.class);

        boolean forced = forceValue instanceof Boolean forcedValue && forcedValue;

        if (!compressionEnabled && !forced) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Compression disabled globally, skipping: {}", truncateForLogging(body));
            }
            return super.end(writer, body, true);
        }
        if (devMode && !forced) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Avoids compressing output: {} in DevMode", truncateForLogging(body));
            }
            return super.end(writer, body, true);
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Compresses: {}", truncateForLogging(body));
        }
        String compressedBody = compressWhitespace(body);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Compressed: {}", truncateForLogging(compressedBody));
        }
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

    private String truncateForLogging(String content) {
        if (content == null) {
            return null;
        }
        if (content.length() <= logMaxLength) {
            return content;
        }
        return content.substring(0, logMaxLength) + "... (truncated, length: " + content.length() + ")";
    }

    private boolean exceedsMaxSize(String body) {
        if (maxSize == null || body == null) {
            return false;
        }
        return body.length() > maxSize;
    }

    private String compressWhitespace(String input) {
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

        // Simple compression: trim and remove whitespace between tags
        return input.trim().replaceAll(">\\s+<", "><");
    }
}
