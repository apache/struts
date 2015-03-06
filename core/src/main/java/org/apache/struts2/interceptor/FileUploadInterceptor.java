/*
 * $Id$
 *
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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.util.ContentTypeMatcher;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <!-- START SNIPPET: description -->
 * <p/>
 * Interceptor that is based off of {@link MultiPartRequestWrapper}, which is automatically applied for any request that
 * includes a file. It adds the following parameters, where [File Name] is the name given to the file uploaded by the
 * HTML form:
 * <p/>
 * <ul>
 * <p/>
 * <li>[File Name] : File - the actual File</li>
 * <p/>
 * <li>[File Name]ContentType : String - the content type of the file</li>
 * <p/>
 * <li>[File Name]FileName : String - the actual name of the file uploaded (not the HTML name)</li>
 * <p/>
 * </ul>
 * <p/>
 * <p/> You can get access to these files by merely providing setters in your action that correspond to any of the three
 * patterns above, such as setDocument(File document), setDocumentContentType(String contentType), etc.
 * <br/>See the example code section.
 * <p/>
 * <p/> This interceptor will add several field errors, assuming that the action implements {@link ValidationAware}.
 * These error messages are based on several i18n values stored in struts-messages.properties, a default i18n file
 * processed for all i18n requests. You can override the text of these messages by providing text for the following
 * keys:
 * <p/>
 * <ul>
 * <p/>
 * <li>struts.messages.error.uploading - a general error that occurs when the file could not be uploaded</li>
 * <p/>
 * <li>struts.messages.error.file.too.large - occurs when the uploaded file is too large</li>
 * <p/>
 * <li>struts.messages.error.content.type.not.allowed - occurs when the uploaded file does not match the expected
 * content types specified</li>
 * <p/>
 * <li>struts.messages.error.file.extension.not.allowed - occurs when the uploaded file does not match the expected
 * file extensions specified</li>
 * <p/>
 * </ul>
 * <p/>
 * <!-- END SNIPPET: description -->
 * <p/>
 * <p/> <u>Interceptor parameters:</u>
 * <p/>
 * <!-- START SNIPPET: parameters -->
 * <p/>
 * <ul>
 * <p/>
 * <li>maximumSize (optional) - the maximum size (in bytes) that the interceptor will allow a file reference to be set
 * on the action. Note, this is <b>not</b> related to the various properties found in struts.properties.
 * Default to approximately 2MB.</li>
 * <p/>
 * <li>allowedTypes (optional) - a comma separated list of content types (ie: text/html) that the interceptor will allow
 * a file reference to be set on the action. If none is specified allow all types to be uploaded.</li>
 * <p/>
 * <li>allowedExtensions (optional) - a comma separated list of file extensions (ie: .html) that the interceptor will allow
 * a file reference to be set on the action. If none is specified allow all extensions to be uploaded.</li>
 * </ul>
 * <p/>
 * <p/>
 * <!-- END SNIPPET: parameters -->
 * <p/>
 * <p/> <u>Extending the interceptor:</u>
 * <p/>
 * <p/>
 * <p/>
 * <!-- START SNIPPET: extending -->
 * <p/>
 * You can extend this interceptor and override the acceptFile method to provide more control over which files
 * are supported and which are not.
 * <p/>
 * <!-- END SNIPPET: extending -->
 * <p/>
 * <p/> <u>Example code:</u>
 * <p/>
 * <pre>
 * <!-- START SNIPPET: example-configuration -->
 * &lt;action name="doUpload" class="com.example.UploadAction"&gt;
 *     &lt;interceptor-ref name="fileUpload"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.jsp&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example-configuration -->
 * </pre>
 * <p/>
 * <!-- START SNIPPET: multipart-note -->
 * <p/>
 * You must set the encoding to <code>multipart/form-data</code> in the form where the user selects the file to upload.
 * <p/>
 * <!-- END SNIPPET: multipart-note -->
 * <p/>
 * <pre>
 * <!-- START SNIPPET: example-form -->
 *   &lt;s:form action="doUpload" method="post" enctype="multipart/form-data"&gt;
 *       &lt;s:file name="upload" label="File"/&gt;
 *       &lt;s:submit/&gt;
 *   &lt;/s:form&gt;
 * <!-- END SNIPPET: example-form -->
 * </pre>
 * <p/>
 * And then in your action code you'll have access to the File object if you provide setters according to the
 * naming convention documented in the start.
 * <p/>
 * <pre>
 * <!-- START SNIPPET: example-action -->
 *    package com.example;
 *
 *    import java.io.File;
 *    import com.opensymphony.xwork2.ActionSupport;
 *
 *    public UploadAction extends ActionSupport {
 *       private File file;
 *       private String contentType;
 *       private String filename;
 *
 *       public void setUpload(File file) {
 *          this.file = file;
 *       }
 *
 *       public void setUploadContentType(String contentType) {
 *          this.contentType = contentType;
 *       }
 *
 *       public void setUploadFileName(String filename) {
 *          this.filename = filename;
 *       }
 *
 *       public String execute() {
 *          //...
 *          return SUCCESS;
 *       }
 *  }
 * <!-- END SNIPPET: example-action -->
 * </pre>
 */
public class FileUploadInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = -4764627478894962478L;

    protected static final Logger LOG = LoggerFactory.getLogger(FileUploadInterceptor.class);

    protected Long maximumSize;
    protected Set<String> allowedTypesSet = Collections.emptySet();
    protected Set<String> allowedExtensionsSet = Collections.emptySet();

    private ContentTypeMatcher matcher;
    private Container container;

    @Inject
    public void setMatcher(ContentTypeMatcher matcher) {
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

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.interceptor.Interceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
     */

    public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext ac = invocation.getInvocationContext();

        HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);

        if (!(request instanceof MultiPartRequestWrapper)) {
            if (LOG.isDebugEnabled()) {
                ActionProxy proxy = invocation.getProxy();
                LOG.debug(getTextMessage("struts.messages.bypass.request", new String[]{proxy.getNamespace(), proxy.getActionName()}));
            }

            return invocation.invoke();
        }

        ValidationAware validation = null;

        Object action = invocation.getAction();

        if (action instanceof ValidationAware) {
            validation = (ValidationAware) action;
        }

        MultiPartRequestWrapper multiWrapper = (MultiPartRequestWrapper) request;

        if (multiWrapper.hasErrors()) {
            for (String error : multiWrapper.getErrors()) {
                if (validation != null) {
                    validation.addActionError(error);
                }
            }
        }

        // bind allowed Files
        Enumeration fileParameterNames = multiWrapper.getFileParameterNames();
        while (fileParameterNames != null && fileParameterNames.hasMoreElements()) {
            // get the value of this input tag
            String inputName = (String) fileParameterNames.nextElement();

            // get the content type
            String[] contentType = multiWrapper.getContentTypes(inputName);

            if (isNonEmpty(contentType)) {
                // get the name of the file from the input tag
                String[] fileName = multiWrapper.getFileNames(inputName);

                if (isNonEmpty(fileName)) {
                    // get a File object for the uploaded File
                    File[] files = multiWrapper.getFiles(inputName);
                    if (files != null && files.length > 0) {
                        List<File> acceptedFiles = new ArrayList<File>(files.length);
                        List<String> acceptedContentTypes = new ArrayList<String>(files.length);
                        List<String> acceptedFileNames = new ArrayList<String>(files.length);
                        String contentTypeName = inputName + "ContentType";
                        String fileNameName = inputName + "FileName";

                        for (int index = 0; index < files.length; index++) {
                            if (acceptFile(action, files[index], fileName[index], contentType[index], inputName, validation)) {
                                acceptedFiles.add(files[index]);
                                acceptedContentTypes.add(contentType[index]);
                                acceptedFileNames.add(fileName[index]);
                            }
                        }

                        if (!acceptedFiles.isEmpty()) {
                            Map<String, Object> params = ac.getParameters();

                            params.put(inputName, acceptedFiles.toArray(new File[acceptedFiles.size()]));
                            params.put(contentTypeName, acceptedContentTypes.toArray(new String[acceptedContentTypes.size()]));
                            params.put(fileNameName, acceptedFileNames.toArray(new String[acceptedFileNames.size()]));
                        }
                    }
                } else {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn(getTextMessage(action, "struts.messages.invalid.file", new String[]{inputName}));
                    }
                }
            } else {
                if (LOG.isWarnEnabled()) {
                    LOG.warn(getTextMessage(action, "struts.messages.invalid.content.type", new String[]{inputName}));
                }
            }
        }

        // invoke action
        return invocation.invoke();
    }

    /**
     * Override for added functionality. Checks if the proposed file is acceptable based on contentType and size.
     *
     * @param action      - uploading action for message retrieval.
     * @param file        - proposed upload file.
     * @param contentType - contentType of the file.
     * @param inputName   - inputName of the file.
     * @param validation  - Non-null ValidationAware if the action implements ValidationAware, allowing for better
     *                    logging.
     * @return true if the proposed file is acceptable by contentType and size.
     */
    protected boolean acceptFile(Object action, File file, String filename, String contentType, String inputName, ValidationAware validation) {
        boolean fileIsAcceptable = false;

        // If it's null the upload failed
        if (file == null) {
            String errMsg = getTextMessage(action, "struts.messages.error.uploading", new String[]{inputName});
            if (validation != null) {
                validation.addFieldError(inputName, errMsg);
            }

            if (LOG.isWarnEnabled()) {
                LOG.warn(errMsg);
            }
        } else if (maximumSize != null && maximumSize < file.length()) {
            String errMsg = getTextMessage(action, "struts.messages.error.file.too.large", new String[]{inputName, filename, file.getName(), "" + file.length(), getMaximumSizeStr(action)});
            if (validation != null) {
                validation.addFieldError(inputName, errMsg);
            }

            if (LOG.isWarnEnabled()) {
                LOG.warn(errMsg);
            }
        } else if ((!allowedTypesSet.isEmpty()) && (!containsItem(allowedTypesSet, contentType))) {
            String errMsg = getTextMessage(action, "struts.messages.error.content.type.not.allowed", new String[]{inputName, filename, file.getName(), contentType});
            if (validation != null) {
                validation.addFieldError(inputName, errMsg);
            }

            if (LOG.isWarnEnabled()) {
                LOG.warn(errMsg);
            }
        } else if ((!allowedExtensionsSet.isEmpty()) && (!hasAllowedExtension(allowedExtensionsSet, filename))) {
            String errMsg = getTextMessage(action, "struts.messages.error.file.extension.not.allowed", new String[]{inputName, filename, file.getName(), contentType});
            if (validation != null) {
                validation.addFieldError(inputName, errMsg);
            }

            if (LOG.isWarnEnabled()) {
                LOG.warn(errMsg);
            }
        } else {
            fileIsAcceptable = true;
        }

        return fileIsAcceptable;
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
        return matcher.match(new HashMap<String, String>(), text, o);
    }

    private boolean isNonEmpty(Object[] objArray) {
        boolean result = false;
        for (int index = 0; index < objArray.length && !result; index++) {
            if (objArray[index] != null) {
                result = true;
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

    private TextProvider getTextProvider(Object action) {
        TextProviderFactory tpf = new TextProviderFactory();
        if (container != null) {
            container.inject(tpf);
        }
        LocaleProvider localeProvider = getLocaleProvider(action);
        return tpf.createInstance(action.getClass(), localeProvider);
    }

    private LocaleProvider getLocaleProvider(Object action) {
        LocaleProvider localeProvider;
        if (action instanceof LocaleProvider) {
            localeProvider = (LocaleProvider) action;
        } else {
            localeProvider = container.getInstance(LocaleProvider.class);
        }
        return localeProvider;
    }

}
