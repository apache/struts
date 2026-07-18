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
<div class="border-bottom pb-2 mb-3">
    <h1>File Upload Successful</h1>
    <p class="lead">Your file was validated and uploaded successfully</p>
</div>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <div class="alert alert-success">
                <strong>Success!</strong> Your file passed all validation checks.
            </div>

            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Upload Details</h3>
                </div>
                <div class="card-body">
                    <dl class="row">
                        <dt class="col-sm-3">Upload Type:</dt>
                        <dd class="col-sm-9"><s:property value="uploadType == 'image' ? 'Image' : 'Document'"/></dd>

                        <dt class="col-sm-3">Content Type:</dt>
                        <dd class="col-sm-9"><code><s:property value="contentType"/></code></dd>

                        <dt class="col-sm-3">File Name:</dt>
                        <dd class="col-sm-9"><s:property value="fileName"/></dd>

                        <dt class="col-sm-3">Original Name:</dt>
                        <dd class="col-sm-9"><s:property value="originalName"/></dd>

                        <dt class="col-sm-3">File Size:</dt>
                        <dd class="col-sm-9"><s:property value="uploadSize"/> bytes</dd>

                        <dt class="col-sm-3">Input Name:</dt>
                        <dd class="col-sm-9"><s:property value="inputName"/></dd>

                        <dt class="col-sm-3">File Object:</dt>
                        <dd class="col-sm-9"><code><s:property value="uploadedFile"/></code></dd>
                    </dl>
                </div>
            </div>

            <div class="card border-info">
                <div class="card-header text-bg-info">
                    <h3 class="card-title">Validation Rules Applied</h3>
                </div>
                <div class="card-body">
                    <dl class="row">
                        <dt class="col-sm-3">Allowed MIME Types:</dt>
                        <dd class="col-sm-9"><code><s:property value="uploadConfig.allowedMimeTypes"/></code></dd>

                        <dt class="col-sm-3">Allowed Extensions:</dt>
                        <dd class="col-sm-9"><code><s:property value="uploadConfig.allowedExtensions"/></code></dd>

                        <dt class="col-sm-3">Maximum Size:</dt>
                        <dd class="col-sm-9"><s:property value="uploadConfig.maxFileSizeFormatted"/></dd>
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
                    <i class="bi bi-upload"></i> Upload Another File
                </s:a>
            </div>
        </div>
    </div>
</div>

</body>
</html>
