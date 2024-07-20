<!--
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
-->
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>Struts2 Showcase - Html5 theme</title>
    <s:url var="bootstrapCss" value="/styles/bootstrap.css" encode="false" includeParams="none"/>
    <s:link theme="html5" href="%{bootstrapCss}"/>
    <s:url var="mainCss" value="/styles/main.css" encode="false" includeParams="none"/>
    <s:link href="%{mainCss}" />
    <s:head theme="html5"/>
</head>

<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <div class="hero-unit">
                <h1>Html 5 tags demo</h1>
                <p>All the tags on this page are from <i>html5</i> theme. <s:a theme="html5" action="showcase" namespace="/">Back</s:a> to main Showcase App page
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-2">
            <pre>&lt;s:a/&gt;</pre>
        </div>
        <div class="col-md-10">
            <s:a theme="html5" action="index">index</s:a>
        </div>
    </div>
    <div class="row">
        <div class="col-md-2">
            <pre>&lt;s:actionerror/&gt;</pre>
        </div>
        <div class="col-md-10">
            <s:actionerror theme="html5"/>
        </div>
    </div>
    <div class="row">
        <div class="col-md-2">
            <pre>&lt;s:actionmessage/&gt;</pre>
        </div>
        <div class="col-md-10">
            <s:actionmessage theme="html5"/>
        </div>
    </div>
</div>
</body>
</html>
