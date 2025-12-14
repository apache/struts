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
<s:compress>
<html lang="en">
<head>
    <s:url var="bootstrapCss" value="/styles/bootstrap.css" encode="false" includeParams="none"/>
    <s:link theme="html5" href="%{bootstrapCss}"/>
    <s:url var="mainCss" value="/styles/main.css" encode="false" includeParams="none"/>
    <s:link theme="html5" href="%{mainCss}" />
    <s:head theme="html5"/>
    <title>Struts2 Showcase - Html5 theme</title>
</head>

<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <div class="hero-unit">
                <h1>Html 5 tags demo</h1>
                <p>All the tags on this page are from <i>html5</i> theme. <s:a theme="html5" action="showcase" namespace="/">Back</s:a> to main Showcase App page</p>
            </div>
        </div>
    </div>

    <!-- Section 1: Link Components -->
    <div class="row">
        <div class="col-md-12">
            <div class="page-header">
                <h2>Link Components</h2>
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

    <!-- Section 2: Error & Message Components -->
    <div class="row">
        <div class="col-md-12">
            <div class="page-header">
                <h2>Error &amp; Message Components</h2>
            </div>
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
    <div class="row">
        <div class="col-md-2">
            <pre>&lt;s:fielderror/&gt;</pre>
        </div>
        <div class="col-md-10">
            <s:fielderror theme="html5"/>
        </div>
    </div>

    <!-- Section 3: Form Components -->
    <div class="row">
        <div class="col-md-12">
            <div class="page-header">
                <h2>Form Components</h2>
            </div>
        </div>
    </div>
    <s:form theme="html5" action="index" method="post">
        <div class="row">
            <div class="col-md-2">
                <pre>&lt;s:form/&gt;</pre>
            </div>
            <div class="col-md-10">
                <p>Form wrapper (wraps all components below)</p>
            </div>
        </div>
        <div class="row">
            <div class="col-md-2">
                <pre>&lt;s:textfield/&gt;</pre>
            </div>
            <div class="col-md-10">
                <s:textfield theme="html5" label="Name" name="name" tooltip="Enter your name here"/>
            </div>
        </div>
        <div class="row">
            <div class="col-md-2">
                <pre>&lt;s:password/&gt;</pre>
            </div>
            <div class="col-md-10">
                <s:password theme="html5" label="Password" name="password"/>
            </div>
        </div>
        <div class="row">
            <div class="col-md-2">
                <pre>&lt;s:textarea/&gt;</pre>
            </div>
            <div class="col-md-10">
                <s:textarea theme="html5" label="Comments" name="comments" cols="40" rows="3"/>
            </div>
        </div>
        <div class="row">
            <div class="col-md-2">
                <pre>&lt;s:hidden/&gt;</pre>
            </div>
            <div class="col-md-10">
                <s:hidden theme="html5" name="hiddenValue" value="secret"/>
                <p><small>Hidden field with value="secret" (not visible)</small></p>
            </div>
        </div>
        <div class="row">
            <div class="col-md-2">
                <pre>&lt;s:checkbox/&gt;</pre>
            </div>
            <div class="col-md-10">
                <s:checkbox theme="html5" label="Accept Terms" name="terms"/>
            </div>
        </div>
        <div class="row">
            <div class="col-md-2">
                <pre>&lt;s:radio/&gt;</pre>
            </div>
            <div class="col-md-10">
                <s:radio theme="html5" label="Gender" list="{'Male', 'Female', 'Other'}" name="gender"/>
            </div>
        </div>
        <div class="row">
            <div class="col-md-2">
                <pre>&lt;s:select/&gt;</pre>
            </div>
            <div class="col-md-10">
                <s:select theme="html5" label="Country" list="{'USA', 'UK', 'Canada', 'Australia'}" name="country" emptyOption="true" headerKey="" headerValue="-- Please Select --"/>
            </div>
        </div>
        <div class="row">
            <div class="col-md-2">
                <pre>&lt;s:checkboxlist/&gt;</pre>
            </div>
            <div class="col-md-10">
                <s:checkboxlist theme="html5" label="Interests" list="{'Sports', 'Music', 'Reading', 'Travel'}" name="interests"/>
            </div>
        </div>
        <div class="row">
            <div class="col-md-2">
                <pre>&lt;s:file/&gt;</pre>
            </div>
            <div class="col-md-10">
                <s:file theme="html5" label="Upload File" name="upload"/>
            </div>
        </div>
        <div class="row">
            <div class="col-md-2">
                <pre>&lt;s:token/&gt;</pre>
            </div>
            <div class="col-md-10">
                <s:token theme="html5"/>
                <p><small>CSRF token (hidden, check page source)</small></p>
            </div>
        </div>
        <div class="row">
            <div class="col-md-2">
                <pre>&lt;s:submit/&gt;</pre>
            </div>
            <div class="col-md-10">
                <s:submit theme="html5" value="Submit" cssClass="btn btn-primary"/>
            </div>
        </div>
        <div class="row">
            <div class="col-md-2">
                <pre>&lt;s:reset/&gt;</pre>
            </div>
            <div class="col-md-10">
                <s:reset theme="html5" value="Reset" cssClass="btn btn-danger"/>
            </div>
        </div>
    </s:form>

    <!-- Section 4: Advanced Selection Components -->
    <div class="row">
        <div class="col-md-12">
            <div class="page-header">
                <h2>Advanced Selection Components</h2>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-2">
            <pre>&lt;s:combobox/&gt;</pre>
        </div>
        <div class="col-md-10">
            <s:combobox theme="html5" label="Favourite City" name="city" list="{'New York', 'London', 'Tokyo', 'Paris'}"/>
        </div>
    </div>

    <!-- Section 5: Utility & Display Components -->
    <div class="row">
        <div class="col-md-12">
            <div class="page-header">
                <h2>Utility &amp; Display Components</h2>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-2">
            <pre>&lt;s:label/&gt;</pre>
        </div>
        <div class="col-md-10">
            <s:label theme="html5" label="Display Label" name="displayValue" value="Read-only Value"/>
        </div>
    </div>
    <div class="row">
        <div class="col-md-2">
            <pre>&lt;s:script/&gt;</pre>
        </div>
        <div class="col-md-10">
            <s:script theme="html5">
                console.log('HTML5 theme script tag example');
            </s:script>
            <p><small>Script tag (check browser console for output)</small></p>
        </div>
    </div>
    <div class="row">
        <div class="col-md-2">
            <pre>&lt;s:debug/&gt;</pre>
        </div>
        <div class="col-md-10">
            <s:debug theme="html5"/>
        </div>
    </div>
    <div class="row">
        <div class="col-md-2">
            <pre>&lt;s:head/&gt;</pre>
        </div>
        <div class="col-md-10">
            <p><small>Already used in page &lt;head&gt; section (line 30)</small></p>
        </div>
    </div>
    <div class="row">
        <div class="col-md-2">
            <pre>&lt;s:link/&gt;</pre>
        </div>
        <div class="col-md-10">
            <p><small>Already used in page &lt;head&gt; section for CSS (lines 27, 29)</small></p>
        </div>
    </div>
</div>
</body>
</html>
</s:compress>