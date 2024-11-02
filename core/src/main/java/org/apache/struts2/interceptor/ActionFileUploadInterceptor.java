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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.action.UploadedFilesAware;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * <p>
 * Interceptor that is based off of {@link MultiPartRequestWrapper}, which is automatically applied for any request that
 * includes a file when the support for multi-part request is enabled,
 * see <a href="https://struts.apache.org/core-developers/file-upload.html#disabling-file-upload-support">Disabling file upload</a>.
 * </p>
 *
 * <p>
 * You can get access to these files by implementing {@link UploadedFilesAware} interface. The interceptor will then
 * call {@link UploadedFilesAware#withUploadedFiles(List)} when there are files which were accepted during the upload process.
 * </p>
 *
 * <p>
 * This interceptor will add several field errors, assuming that the action implements {@link ValidationAware}.
 * These error messages are based on several i18n values stored in struts-messages.properties, a default i18n file
 * processed for all i18n requests. You can override the text of these messages by providing text for the following
 * keys:
 * </p>
 *
 * <ul>
 * <li>struts.messages.error.uploading - a general error that occurs when the file could not be uploaded</li>
 * <li>struts.messages.error.file.too.large - occurs when the uploaded file is too large</li>
 * <li>struts.messages.error.content.type.not.allowed - occurs when the uploaded file does not match the expected
 * content types specified</li>
 * <li>struts.messages.error.file.extension.not.allowed - occurs when the uploaded file does not match the expected
 * file extensions specified</li>
 * </ul>
 *
 * <p>Interceptor parameters:</p>
 * <ul>
 * <li>maximumSize (optional) - the maximum size (in bytes) that the interceptor will allow a file reference to be set
 * on the action. Note, this is <b>not</b> related to the various properties found in struts.properties.
 * Default to approximately 2MB.</li>
 * <li>allowedTypes (optional) - a comma separated list of content types (ie: text/html) that the interceptor will allow
 * a file reference to be set on the action. If none is specified allow all types to be uploaded.</li>
 * <li>allowedExtensions (optional) - a comma separated list of file extensions (ie: .html) that the interceptor will allow
 * a file reference to be set on the action. If none is specified allow all extensions to be uploaded.</li>
 * </ul>
 *
 * <p>Example code:</p>
 *
 * <pre>
 * &lt;action name="doUpload" class="com.example.UploadAction"&gt;
 *     &lt;interceptor-ref name="actionFileUpload"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.jsp&lt;/result&gt;
 * &lt;/action&gt;
 * </pre>
 * <p>
 * <p>
 * You must set the encoding to <code>multipart/form-data</code> in the form where the user selects the file to upload.
 * </p>
 * <pre>
 *   &lt;s:form action="doUpload" method="post" enctype="multipart/form-data"&gt;
 *       &lt;s:file name="upload" label="File"/&gt;
 *       &lt;s:submit/&gt;
 *   &lt;/s:form&gt;
 * </pre>
 * <p>
 * And then in your action code you'll have access to the File object if you provide setters according to the
 * naming convention documented in the start.
 * </p>
 *
 * <pre>
 *  package com.example;
 *
 *  import java.io.File;
 *  import org.apache.struts2.ActionSupport;
 *  import org.apache.struts2.action.UploadedFilesAware;
 *
 *  public UploadAction extends ActionSupport implements UploadedFilesAware {
 *    private UploadedFile uploadedFile;
 *    private String contentType;
 *    private String fileName;
 *    private String originalName;
 *
 *    &#064;Override
 *    public void withUploadedFiles(List<UploadedFile> uploadedFiles) {
 *        if (!uploadedFiles.isEmpty() > 0) {
 *            this.uploadedFile = uploadedFiles.get(0);
 *            this.fileName = uploadedFile.getName();
 *            this.contentType = uploadedFile.getContentType();
 *            this.originalName = uploadedFile.getOriginalName();
 *        }
 *    }
 *
 *    public String execute() {
 *       //...
 *       return SUCCESS;
 *    }
 *  }
 * </pre>
 */
public class ActionFileUploadInterceptor extends AbstractFileUploadInterceptor {

    protected static final Logger LOG = LogManager.getLogger(ActionFileUploadInterceptor.class);

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = invocation.getInvocationContext().getServletRequest();
        MultiPartRequestWrapper multiWrapper = request instanceof HttpServletRequestWrapper wrapper
                ? findMultipartRequestWrapper(wrapper)
                : null;

        if (multiWrapper == null) {
            if (LOG.isDebugEnabled()) {
                ActionProxy proxy = invocation.getProxy();
                LOG.debug(getTextMessage(STRUTS_MESSAGES_BYPASS_REQUEST_KEY, new String[]{proxy.getNamespace(), proxy.getActionName()}));
            }
            return invocation.invoke();
        }

        if (!(invocation.getAction() instanceof UploadedFilesAware action)) {
            LOG.debug("Action: {} doesn't implement: {}, ignoring file upload",
                    invocation.getProxy().getActionName(),
                    UploadedFilesAware.class.getSimpleName());
            return invocation.invoke();
        }

        applyValidation(action, multiWrapper);

        // bind allowed Files
        Enumeration<String> fileParameterNames = multiWrapper.getFileParameterNames();
        List<UploadedFile> acceptedFiles = new ArrayList<>();

        while (fileParameterNames != null && fileParameterNames.hasMoreElements()) {
            // get the value of this input tag
            String inputName = fileParameterNames.nextElement();
            UploadedFile[] uploadedFiles = multiWrapper.getFiles(inputName);

            if (uploadedFiles == null || uploadedFiles.length == 0) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn(getTextMessage(action, STRUTS_MESSAGES_INVALID_FILE_KEY, new String[]{inputName}));
                }
            } else {
                for (UploadedFile uploadedFile : uploadedFiles) {
                    if (acceptFile(action, uploadedFile, uploadedFile.getOriginalName(), uploadedFile.getContentType(), inputName)) {
                        acceptedFiles.add(uploadedFile);
                    }
                }
            }
        }

        if (acceptedFiles.isEmpty()) {
            LOG.debug("No files have been uploaded/accepted");
        } else {
            LOG.debug("Passing: {} uploaded file(s) to action", acceptedFiles.size());
            action.withUploadedFiles(acceptedFiles);
        }

        // invoke action
        return invocation.invoke();
    }

    /**
     * Tries to find {@link MultiPartRequestWrapper} as the request can be already wrapped
     * with another {@link HttpServletRequestWrapper}.
     * If the {@link MultiPartRequestWrapper} cannot be found, null is returned instead.
     *
     * @param request current {@link HttpServletRequestWrapper}
     * @return {@link MultiPartRequestWrapper} or null
     * @since 7.0.0
     */
    protected MultiPartRequestWrapper findMultipartRequestWrapper(HttpServletRequestWrapper request) {
        if (request instanceof MultiPartRequestWrapper multiPartRequestWrapper) {
            LOG.debug("Found multipart request: {}", multiPartRequestWrapper.getClass().getSimpleName());
            return multiPartRequestWrapper;
        } else if (request.getRequest() instanceof HttpServletRequestWrapper wrappedRequest) {
            LOG.debug("Could not find multipart request wrapper, checking ancestor: {}",
                    wrappedRequest.getClass().getSimpleName());
            return findMultipartRequestWrapper(wrappedRequest);
        }
        return null;
    }

}

