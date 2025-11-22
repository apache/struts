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
<%@ page
        language="java"
        contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html lang="en">
<head>
    <title>Struts2 Showcase - Dynamic File Upload Success</title>
</head>

<body>
<div class="page-header">
    <h1>File Upload Successful</h1>
    <p class="lead">Your file was validated and uploaded successfully</p>
</div>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <div class="alert alert-success">
                <strong>Success!</strong> Your file passed all validation checks.
            </div>

            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Upload Details</h3>
                </div>
                <div class="panel-body">
                    <dl class="dl-horizontal">
                        <dt>Upload Type:</dt>
                        <dd><s:property value="uploadType == 'image' ? 'Image' : 'Document'"/></dd>

                        <dt>Content Type:</dt>
                        <dd><code><s:property value="contentType"/></code></dd>

                        <dt>File Name:</dt>
                        <dd><s:property value="fileName"/></dd>

                        <dt>Original Name:</dt>
                        <dd><s:property value="originalName"/></dd>

                        <dt>File Size:</dt>
                        <dd><s:property value="uploadSize"/> bytes</dd>

                        <dt>Input Name:</dt>
                        <dd><s:property value="inputName"/></dd>

                        <dt>File Object:</dt>
                        <dd><code><s:property value="uploadedFile"/></code></dd>
                    </dl>
                </div>
            </div>

            <div class="panel panel-info">
                <div class="panel-heading">
                    <h3 class="panel-title">Validation Rules Applied</h3>
                </div>
                <div class="panel-body">
                    <dl class="dl-horizontal">
                        <dt>Allowed MIME Types:</dt>
                        <dd><code><s:property value="uploadConfig.allowedMimeTypes"/></code></dd>

                        <dt>Allowed Extensions:</dt>
                        <dd><code><s:property value="uploadConfig.allowedExtensions"/></code></dd>

                        <dt>Maximum Size:</dt>
                        <dd><s:property value="uploadConfig.maxFileSizeFormatted"/></dd>
                    </dl>
                    <p class="text-muted">
                        <small>
                            These validation rules were determined dynamically at runtime
                            using <code>WithLazyParams</code> and evaluated from the ValueStack.
                        </small>
                    </p>
                </div>
            </div>

            <div class="btn-group">
                <s:a action="dynamicUpload" cssClass="btn btn-primary">
                    <i class="glyphicon glyphicon-upload"></i> Upload Another File
                </s:a>
            </div>
        </div>
    </div>
</div>

</body>
</html>
