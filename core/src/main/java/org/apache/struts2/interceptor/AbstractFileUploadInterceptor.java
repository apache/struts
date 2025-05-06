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
package org.apache.struts2.interceptor;

import org.apache.struts2.locale.LocaleProvider;
import org.apache.struts2.locale.LocaleProviderFactory;
import org.apache.struts2.text.TextProvider;
import org.apache.struts2.text.TextProviderFactory;
import org.apache.struts2.inject.Container;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.util.TextParseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.LocalizedMessage;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.apache.struts2.util.ContentTypeMatcher;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractFileUploadInterceptor extends AbstractInterceptor {

    private static final Logger LOG = LogManager.getLogger(AbstractFileUploadInterceptor.class);

    public static final String STRUTS_MESSAGES_BYPASS_REQUEST_KEY = "struts.messages.bypass.request";
    public static final String STRUTS_MESSAGES_ERROR_UPLOADING_KEY = "struts.messages.error.uploading";
    public static final String STRUTS_MESSAGES_ERROR_FILE_TOO_LARGE_KEY = "struts.messages.error.file.too.large";
    public static final String STRUTS_MESSAGES_INVALID_FILE_KEY = "struts.messages.invalid.file";
    public static final String STRUTS_MESSAGES_INVALID_CONTENT_TYPE_KEY = "struts.messages.invalid.content.type";
    public static final String STRUTS_MESSAGES_ERROR_CONTENT_TYPE_NOT_ALLOWED_KEY = "struts.messages.error.content.type.not.allowed";
    public static final String STRUTS_MESSAGES_ERROR_FILE_EXTENSION_NOT_ALLOWED_KEY = "struts.messages.error.file.extension.not.allowed";

    private Long maximumSize;
    private Set<String> allowedTypesSet = Collections.emptySet();
    private Set<String> allowedExtensionsSet = Collections.emptySet();

    private ContentTypeMatcher<Object> matcher;
    private Container container;

    @Inject
    public void setMatcher(ContentTypeMatcher<Object> matcher) {
        this.matcher = matcher;
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    /**
     * Sets the allowed extensions
     *
     * @param allowedExtensions A comma-delimited list of extensions
     */
    public void setAllowedExtensions(String allowedExtensions) {
        allowedExtensionsSet = TextParseUtil.commaDelimitedStringToSet(allowedExtensions);
    }

    /**
     * Sets the allowed mimetypes
     *
     * @param allowedTypes A comma-delimited list of types
     */
    public void setAllowedTypes(String allowedTypes) {
        allowedTypesSet = TextParseUtil.commaDelimitedStringToSet(allowedTypes);
    }

    /**
     * Sets the maximum size of an uploaded file
     *
     * @param maximumSize The maximum size in bytes
     */
    public void setMaximumSize(Long maximumSize) {
        this.maximumSize = maximumSize;
    }

    /**
     * Override for added functionality. Checks if the proposed file is acceptable based on contentType and size.
     *
     * @param action           - uploading action for message retrieval.
     * @param file             - proposed upload file.
     * @param originalFilename - name of the file.
     * @param contentType      - contentType of the file.
     * @param inputName        - inputName of the file.
     * @return true if the proposed file is acceptable by contentType and size.
     */
    protected boolean acceptFile(Object action, UploadedFile file, String originalFilename, String contentType, String inputName) {
        Set<String> errorMessages = new HashSet<>();

        ValidationAware validation = null;
        if (action instanceof ValidationAware) {
            validation = (ValidationAware) action;
        }

        // If it's null the upload failed
        if (file == null || file.getContent() == null) {
            String errMsg = getTextMessage(action, STRUTS_MESSAGES_ERROR_UPLOADING_KEY, new String[]{inputName});
            if (validation != null) {
                validation.addFieldError(inputName, errMsg);
            }
            LOG.warn(errMsg);
            return false;
        }

        if (file.getContent() == null) {
            String errMsg = getTextMessage(action, STRUTS_MESSAGES_ERROR_UPLOADING_KEY, new String[]{originalFilename});
            errorMessages.add(errMsg);
            LOG.warn(errMsg);
        }
        if (maximumSize != null && maximumSize < file.length()) {
            String errMsg = getTextMessage(action, STRUTS_MESSAGES_ERROR_FILE_TOO_LARGE_KEY, new String[]{
                inputName, originalFilename, file.getName(), "" + file.length(), getMaximumSizeStr(action)
            });
            errorMessages.add(errMsg);
            LOG.warn(errMsg);
        }
        if ((!allowedTypesSet.isEmpty()) && (!containsItem(allowedTypesSet, contentType))) {
            String errMsg = getTextMessage(action, STRUTS_MESSAGES_ERROR_CONTENT_TYPE_NOT_ALLOWED_KEY, new String[]{
                inputName, originalFilename, file.getName(), contentType
            });
            errorMessages.add(errMsg);
            LOG.warn(errMsg);
        }
        if ((!allowedExtensionsSet.isEmpty()) && (!hasAllowedExtension(allowedExtensionsSet, originalFilename))) {
            String errMsg = getTextMessage(action, STRUTS_MESSAGES_ERROR_FILE_EXTENSION_NOT_ALLOWED_KEY, new String[]{
                inputName, originalFilename, file.getName(), contentType
            });
            errorMessages.add(errMsg);
            LOG.warn(errMsg);
        }
        if (validation != null) {
            for (String errorMsg : errorMessages) {
                validation.addFieldError(inputName, errorMsg);
            }
        }

        return errorMessages.isEmpty();
    }

    private String getMaximumSizeStr(Object action) {
        return NumberFormat.getNumberInstance(getLocaleProvider(action).getLocale()).format(maximumSize);
    }

    /**
     * @param extensionCollection - Collection of extensions (all lowercase).
     * @param filename            - filename to check.
     * @return true if the filename has an allowed extension, false otherwise.
     */
    private boolean hasAllowedExtension(Collection<String> extensionCollection, String filename) {
        if (filename == null) {
            return false;
        }

        String lowercaseFilename = filename.toLowerCase();
        for (String extension : extensionCollection) {
            if (lowercaseFilename.endsWith(extension)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param itemCollection - Collection of string items (all lowercase).
     * @param item           - Item to search for.
     * @return true if itemCollection contains the item, false otherwise.
     */
    private boolean containsItem(Collection<String> itemCollection, String item) {
        for (String pattern : itemCollection)
            if (matchesWildcard(pattern, item))
                return true;
        return false;
    }

    private boolean matchesWildcard(String pattern, String text) {
        Object o = matcher.compilePattern(pattern);
        return matcher.match(new HashMap<>(), text, o);
    }

    protected boolean isNonEmpty(Object[] objArray) {
        boolean result = false;
        for (Object o : objArray) {
            if (o != null) {
                result = true;
                break;
            }
        }
        return result;
    }

    protected String getTextMessage(String messageKey, String[] args) {
        return getTextMessage(this, messageKey, args);
    }

    protected String getTextMessage(Object action, String messageKey, String[] args) {
        if (action instanceof TextProvider) {
            return ((TextProvider) action).getText(messageKey, args);
        }
        return getTextProvider(action).getText(messageKey, args);
    }

    protected TextProvider getTextProvider(Object action) {
        TextProviderFactory tpf = container.getInstance(TextProviderFactory.class);
        return tpf.createInstance(action.getClass());
    }

    private LocaleProvider getLocaleProvider(Object action) {
        LocaleProvider localeProvider;
        if (action instanceof LocaleProvider) {
            localeProvider = (LocaleProvider) action;
        } else {
            LocaleProviderFactory localeProviderFactory = container.getInstance(LocaleProviderFactory.class);
            localeProvider = localeProviderFactory.createLocaleProvider();
        }
        return localeProvider;
    }

    protected void applyValidation(Object action, MultiPartRequestWrapper multiWrapper) {
        ValidationAware validation = null;
        if (action instanceof ValidationAware) {
            validation = (ValidationAware) action;
        }

        if (multiWrapper.hasErrors() && validation != null) {
            TextProvider textProvider = getTextProvider(action);
            for (LocalizedMessage error : multiWrapper.getErrors()) {
                String errorMessage;
                if (textProvider.hasKey(error.getTextKey())) {
                    errorMessage = textProvider.getText(error.getTextKey(), Arrays.asList(error.getArgs()));
                } else {
                    errorMessage = textProvider.getText(STRUTS_MESSAGES_ERROR_UPLOADING_KEY, error.getDefaultMessage());
                }
                validation.addActionError(errorMessage);
            }
        }
    }

}
