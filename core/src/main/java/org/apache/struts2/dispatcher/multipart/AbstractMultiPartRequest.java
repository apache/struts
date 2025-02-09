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
package org.apache.struts2.dispatcher.multipart;

import com.opensymphony.xwork2.LocaleProviderFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.security.DefaultExcludedPatternsChecker;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.LocalizedMessage;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.normalizeSpace;

/**
 * Abstract class with some helper methods, it should be used
 * when starting development of another implementation of {@link MultiPartRequest}
 */
public abstract class AbstractMultiPartRequest implements MultiPartRequest {

    protected static final String STRUTS_MESSAGES_UPLOAD_ERROR_ILLEGAL_CHARACTERS_FIELD = "struts.messages.upload.error.illegal.characters.field";
    protected static final String STRUTS_MESSAGES_UPLOAD_ERROR_ILLEGAL_CHARACTERS_NAME = "struts.messages.upload.error.illegal.characters.name";

    private static final Logger LOG = LogManager.getLogger(AbstractMultiPartRequest.class);

    private static final String EXCLUDED_FILE_PATTERN = "^(.*[<>&\"'|;\\\\/?*:]+.*|.*\\.\\..*)$";
    private static final String EXCLUDED_FILE_PATTERN_WITH_DMI_SUPPORT = "^(?!action:[^<>&\"'|;\\\\/?*:]+(![^<>&\"'|;\\\\/?*:]+)?$)(.*[<>&\"'|;\\\\/?*:]+.*|.*\\.\\..*)$\n";

    /**
     * Defines the internal buffer size used during streaming operations.
     */
    public static final int BUFFER_SIZE = 10240;

    /**
     * Internal list of raised errors to be passed to the Struts2 framework.
     */
    protected List<LocalizedMessage> errors = new ArrayList<>();

    /**
     * Specifies the maximum size of the entire request.
     */
    protected Long maxSize;

    /**
     * Specifies the maximum number of files in one request.
     */
    protected Long maxFiles;

    /**
     * Specifies the maximum length of a string parameter in a multipart request.
     */
    protected Long maxStringLength;

    /**
     * Specifies the maximum size per file in the request.
     */
    protected Long maxFileSize;

    /**
     * Specifies the buffer size to use during streaming.
     */
    protected int bufferSize = BUFFER_SIZE;

    protected String defaultEncoding;

    /**
     * Localization to be used regarding errors.
     */
    protected Locale defaultLocale = Locale.ENGLISH;

    private final ExcludedPatternsChecker patternsChecker;

    protected AbstractMultiPartRequest() {
        this(false);
    }

    protected AbstractMultiPartRequest(boolean dmiValue) {
        DefaultExcludedPatternsChecker patternsChecker = new DefaultExcludedPatternsChecker();
        patternsChecker.setAdditionalExcludePatterns(dmiValue ? EXCLUDED_FILE_PATTERN_WITH_DMI_SUPPORT : EXCLUDED_FILE_PATTERN);
        this.patternsChecker = patternsChecker;
    }

    /**
     * @param bufferSize Sets the buffer size to be used.
     */
    @Inject(value = StrutsConstants.STRUTS_MULTIPART_BUFFERSIZE, required = false)
    public void setBufferSize(String bufferSize) {
        this.bufferSize = Integer.parseInt(bufferSize);
    }

    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setDefaultEncoding(String enc) {
        this.defaultEncoding = enc;
    }

    /**
     * @param maxSize Injects the Struts multipart request maximum size.
     */
    @Inject(StrutsConstants.STRUTS_MULTIPART_MAXSIZE)
    public void setMaxSize(String maxSize) {
        this.maxSize = Long.parseLong(maxSize);
    }

    @Inject(StrutsConstants.STRUTS_MULTIPART_MAXFILES)
    public void setMaxFiles(String maxFiles) {
        this.maxFiles = Long.parseLong(maxFiles);
    }

    @Inject(value = StrutsConstants.STRUTS_MULTIPART_MAXFILESIZE, required = false)
    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = Long.parseLong(maxFileSize);
    }

    @Inject(StrutsConstants.STRUTS_MULTIPART_MAX_STRING_LENGTH)
    public void setMaxStringLength(String maxStringLength) {
        this.maxStringLength = Long.parseLong(maxStringLength);
    }

    @Inject
    public void setLocaleProviderFactory(LocaleProviderFactory localeProviderFactory) {
        defaultLocale = localeProviderFactory.createLocaleProvider().getLocale();
    }

    /**
     * @param request Inspect the servlet request and set the locale if one wasn't provided by
     *                the Struts2 framework.
     */
    protected void setLocale(HttpServletRequest request) {
        if (defaultLocale == null) {
            defaultLocale = request.getLocale();
        }
    }

    /**
     * Build error message.
     *
     * @param e    the Throwable/Exception
     * @param args arguments
     * @return error message
     */
    protected LocalizedMessage buildErrorMessage(Throwable e, Object[] args) {
        String errorKey = "struts.messages.upload.error." + e.getClass().getSimpleName();
        LOG.debug("Preparing error message for key: [{}]", errorKey);

        return new LocalizedMessage(this.getClass(), errorKey, e.getMessage(), args);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getErrors()
     */
    public List<LocalizedMessage> getErrors() {
        return errors;
    }

    /**
     * @param originalFileName file name
     * @return the canonical name based on the supplied filename
     */
    protected String getCanonicalName(final String originalFileName) {
        return FilenameUtils.getName(originalFileName);
    }

    /**
     * @param fileName file name to check
     * @return true if the file name is excluded
     */
    protected boolean isExcluded(String fileName) {
        return patternsChecker.isExcluded(fileName).isExcluded();
    }

    protected boolean isInvalidInput(String fieldName, String fileName) {
        // Skip file uploads that don't have a file name - meaning that no file was selected.
        if (fileName == null || fileName.trim().isEmpty()) {
            LOG.debug(() -> "No file has been uploaded for the field: " + normalizeSpace(fieldName));
            return true;
        }

        if (isExcluded(fileName)) {
            String normalizedFileName = normalizeSpace(fileName);
            LOG.debug("File name [{}] is not accepted", normalizedFileName);
            errors.add(new LocalizedMessage(getClass(), STRUTS_MESSAGES_UPLOAD_ERROR_ILLEGAL_CHARACTERS_NAME, null,
                    new String[]{normalizedFileName}));
            return true;
        }

        return isInvalidInput(fieldName);
    }

    protected boolean isInvalidInput(String fieldName) {
        if (isExcluded(fieldName)) {
            String normalizedFieldName = normalizeSpace(fieldName);
            LOG.debug("Form field [{}] is rejected!", normalizedFieldName);
            errors.add(new LocalizedMessage(getClass(), STRUTS_MESSAGES_UPLOAD_ERROR_ILLEGAL_CHARACTERS_FIELD, null,
                    new String[]{normalizedFieldName}));
            return true;
        }
        return false;
    }
}
