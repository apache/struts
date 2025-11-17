<!DOCTYPE html>
<!--
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/
-->
<%@ taglib prefix="s" uri="/struts-tags" %>
<html lang="en">
<head>
    <title>Struts2 Showcase - Dynamic File Upload Validation</title>
</head>

<body>
<div class="page-header">
    <h1>Dynamic File Upload Validation</h1>
    <p class="lead">Demonstrates WithLazyParams for runtime validation rules</p>
</div>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <div class="alert alert-info">
                <h4>About This Example</h4>
                <p>
                    This example demonstrates how to use <code>WithLazyParams</code> to configure
                    file upload validation rules dynamically at runtime. The validation parameters
                    (<code>allowedTypes</code>, <code>allowedExtensions</code>, <code>maximumSize</code>)
                    are evaluated from the ValueStack for each request, allowing different rules
                    based on action state, user permissions, or other runtime conditions.
                </p>
            </div>

            <div class="alert alert-success">
                <h4>Current Configuration</h4>
                <ul>
                    <li><strong>Upload Type:</strong> <s:property
                            value="uploadType == 'image' ? 'Image Upload' : 'Document Upload'"/></li>
                    <li><strong>Allowed Types:</strong> <code><s:property value="uploadConfig.allowedMimeTypes"/></code>
                    </li>
                    <li><strong>Allowed Extensions:</strong> <code><s:property
                            value="uploadConfig.allowedExtensions"/></code></li>
                    <li><strong>Maximum Size:</strong> <s:property value="uploadConfig.maxFileSizeFormatted"/></li>
                    <li><strong>Description:</strong> <s:property value="uploadConfig.description"/></li>
                </ul>
            </div>
        </div>
    </div>

    <s:actionerror cssClass="alert alert-danger"/>
    <s:fielderror cssClass="alert alert-warning"/>

    <div class="row">
        <div class="col-md-12">
            <s:form action="doDynamicUpload" method="POST" enctype="multipart/form-data" cssClass="form-vertical">
                <div class="form-group">
                    <label class="col-sm-2 control-label">Upload Type:</label>
                    <div class="col-sm-10">
                        <s:radio name="uploadType"
                                 list="#{'document':'Documents (PDF, Word) - up to 5MB', 'image':'Images (JPEG, PNG) - up to 2MB'}"/>
                    </div>
                </div>

                <s:file name="upload" label="Select File" cssClass="form-control"/>

                <div class="form-group">
                    <div class="col-sm-offset-2 col-sm-10">
                        <s:submit value="Upload File" cssClass="btn btn-primary"/>
                        <s:submit value="Refresh Rules" action="dynamicUpload" cssClass="btn btn-default"/>
                    </div>
                </div>
            </s:form>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <div class="well">
                <h4>How It Works</h4>
                <p>In <code>struts.xml</code>, the interceptor parameters use expressions:</p>
                <pre>&lt;interceptor-ref name="actionFileUpload"&gt;
    &lt;param name="allowedTypes"&gt;<strong>${uploadConfig.allowedMimeTypes}</strong>&lt;/param&gt;
    &lt;param name="allowedExtensions"&gt;<strong>${uploadConfig.allowedExtensions}</strong>&lt;/param&gt;
    &lt;param name="maximumSize"&gt;<strong>${uploadConfig.maxFileSize}</strong>&lt;/param&gt;
&lt;/interceptor-ref&gt;</pre>
                <p>
                    These expressions are evaluated at runtime against the ValueStack,
                    allowing the action to control validation rules dynamically in its
                    <code>prepare()</code> method.
                </p>
            </div>
        </div>
    </div>
</div>
</body>
</html>
