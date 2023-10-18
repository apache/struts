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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.Parameter;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;

import jakarta.servlet.http.HttpServletRequest;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <!-- START SNIPPET: description -->
 * <p>
 * Interceptor that is based off of {@link MultiPartRequestWrapper}, which is automatically applied for any request that
 * includes a file. It adds the following parameters, where [File Name] is the name given to the file uploaded by the
 * HTML form:
 * </p>
 * <ul>
 *
 * <li>[File Name] : File - the actual File</li>
 *
 * <li>[File Name]ContentType : String - the content type of the file</li>
 *
 * <li>[File Name]FileName : String - the actual name of the file uploaded (not the HTML name)</li>
 *
 * </ul>
 *
 * <p>You can get access to these files by merely providing setters in your action that correspond to any of the three
 * patterns above, such as setDocument(File document), setDocumentContentType(String contentType), etc.
 * <br>See the example code section.
 * </p>
 *
 * <p> This interceptor will add several field errors, assuming that the action implements {@link ValidationAware}.
 * These error messages are based on several i18n values stored in struts-messages.properties, a default i18n file
 * processed for all i18n requests. You can override the text of these messages by providing text for the following
 * keys:
 * </p>
 *
 * <ul>
 *
 * <li>struts.messages.error.uploading - a general error that occurs when the file could not be uploaded</li>
 *
 * <li>struts.messages.error.file.too.large - occurs when the uploaded file is too large</li>
 *
 * <li>struts.messages.error.content.type.not.allowed - occurs when the uploaded file does not match the expected
 * content types specified</li>
 *
 * <li>struts.messages.error.file.extension.not.allowed - occurs when the uploaded file does not match the expected
 * file extensions specified</li>
 *
 * </ul>
 * <p>
 * <!-- END SNIPPET: description -->
 *
 * <p><u>Interceptor parameters:</u></p>
 * <p>
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>maximumSize (optional) - the maximum size (in bytes) that the interceptor will allow a file reference to be set
 * on the action. Note, this is <b>not</b> related to the various properties found in struts.properties.
 * Default to approximately 2MB.</li>
 *
 * <li>allowedTypes (optional) - a comma separated list of content types (ie: text/html) that the interceptor will allow
 * a file reference to be set on the action. If none is specified allow all types to be uploaded.</li>
 *
 * <li>allowedExtensions (optional) - a comma separated list of file extensions (ie: .html) that the interceptor will allow
 * a file reference to be set on the action. If none is specified allow all extensions to be uploaded.</li>
 * </ul>
 * <p>
 * <p>
 * <!-- END SNIPPET: parameters -->
 *
 * <p><u>Extending the interceptor:</u></p>
 * <p>
 * <p>
 * <p>
 * <!-- START SNIPPET: extending -->
 * <p>
 * You can extend this interceptor and override the acceptFile method to provide more control over which files
 * are supported and which are not.
 * </p>
 * <!-- END SNIPPET: extending -->
 *
 * <p><u>Example code:</u></p>
 *
 * <pre>
 * <!-- START SNIPPET: example-configuration -->
 * &lt;action name="doUpload" class="com.example.UploadAction"&gt;
 *     &lt;interceptor-ref name="fileUpload"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.jsp&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example-configuration -->
 * </pre>
 * <p>
 * <!-- START SNIPPET: multipart-note -->
 * <p>
 * You must set the encoding to <code>multipart/form-data</code> in the form where the user selects the file to upload.
 * </p>
 * <!-- END SNIPPET: multipart-note -->
 *
 * <pre>
 * <!-- START SNIPPET: example-form -->
 *   &lt;s:form action="doUpload" method="post" enctype="multipart/form-data"&gt;
 *       &lt;s:file name="upload" label="File"/&gt;
 *       &lt;s:submit/&gt;
 *   &lt;/s:form&gt;
 * <!-- END SNIPPET: example-form -->
 * </pre>
 * <p>
 * And then in your action code you'll have access to the File object if you provide setters according to the
 * naming convention documented in the start.
 * </p>
 *
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
 *
 * @deprecated since Struts 6.4.0, use {@link ActionFileUploadInterceptor} instead
 */
@Deprecated
public class FileUploadInterceptor extends AbstractFileUploadInterceptor {

    private static final long serialVersionUID = -4764627478894962478L;

    protected static final Logger LOG = LogManager.getLogger(FileUploadInterceptor.class);

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.interceptor.Interceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
     */

    public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext ac = invocation.getInvocationContext();

        HttpServletRequest request = ac.getServletRequest();

        if (!(request instanceof MultiPartRequestWrapper)) {
            if (LOG.isDebugEnabled()) {
                ActionProxy proxy = invocation.getProxy();
                LOG.debug(getTextMessage(STRUTS_MESSAGES_BYPASS_REQUEST_KEY, new String[]{proxy.getNamespace(), proxy.getActionName()}));
            }

            return invocation.invoke();
        }

        Object action = invocation.getAction();
        MultiPartRequestWrapper multiWrapper = (MultiPartRequestWrapper) request;

        applyValidation(action, multiWrapper);

        // bind allowed Files
        Enumeration<String> fileParameterNames = multiWrapper.getFileParameterNames();
        while (fileParameterNames != null && fileParameterNames.hasMoreElements()) {
            // get the value of this input tag
            String inputName = fileParameterNames.nextElement();

            // get the content type
            String[] contentType = multiWrapper.getContentTypes(inputName);

            if (isNonEmpty(contentType)) {
                // get the name of the file from the input tag
                String[] fileName = multiWrapper.getFileNames(inputName);

                if (isNonEmpty(fileName)) {
                    // get a File object for the uploaded File
                    UploadedFile[] files = multiWrapper.getFiles(inputName);
                    if (files != null && files.length > 0) {
                        List<UploadedFile> acceptedFiles = new ArrayList<>(files.length);
                        List<String> acceptedContentTypes = new ArrayList<>(files.length);
                        List<String> acceptedFileNames = new ArrayList<>(files.length);
                        String contentTypeName = inputName + "ContentType";
                        String fileNameName = inputName + "FileName";

                        for (int index = 0; index < files.length; index++) {
                            if (acceptFile(action, files[index], fileName[index], contentType[index], inputName)) {
                                acceptedFiles.add(files[index]);
                                acceptedContentTypes.add(contentType[index]);
                                acceptedFileNames.add(fileName[index]);
                            }
                        }

                        if (!acceptedFiles.isEmpty()) {
                            Map<String, Parameter> newParams = new HashMap<>();
                            newParams.put(inputName, new Parameter.File(inputName, acceptedFiles.toArray(new UploadedFile[0])));
                            newParams.put(contentTypeName, new Parameter.File(contentTypeName, acceptedContentTypes.toArray(new String[0])));
                            newParams.put(fileNameName, new Parameter.File(fileNameName, acceptedFileNames.toArray(new String[0])));
                            ac.getParameters().appendAll(newParams);
                        }
                    }
                } else {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn(getTextMessage(action, STRUTS_MESSAGES_INVALID_FILE_KEY, new String[]{inputName}));
                    }
                }
            } else {
                if (LOG.isWarnEnabled()) {
                    LOG.warn(getTextMessage(action, STRUTS_MESSAGES_INVALID_CONTENT_TYPE_KEY, new String[]{inputName}));
                }
            }
        }

        // invoke action
        return invocation.invoke();
    }

}
