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
<%--
    successFieldValidatorsExample.jsp

    @author tm_jee
    @version $Date$ $Id$
--%>

<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Validation - Success Client Side Validators Example</title>
	<s:head/>
</head>
<body>

<div class="page-header">
	<h1>Success !</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

        <table class="table table-striped table-bordered table-hover table-condensed">
            <tr>
                <td>Required Validator Field:</td>
                <td><s:property value="requiredValidatorField" /></td>
            </tr>
            <tr>
                <td>Required String Validator Field:</td>
                <td><s:property value="requiredStringValidatorField" /></td>
            </tr>
            <tr>
                <td>Integer Validator Field: </td>
                <td><s:property value="integerValidatorField" /></td>
            </tr>
            <tr>
                <td>Date Validator Field: </td>
                <td><s:property value="dateValidatorField" /></td>
            </tr>
            <tr>
                <td>Email Validator Field: </td>
                <td><s:property value="emailValidatorField" /></td>
            </tr>
            <tr>
                <td>URL Validator Field: </td>
                <td><s:property value="urlValidatorField" /></td>
            </tr>
            <tr>
                <td>String Length Validator Field: </td>
                <td><s:property value="stringLengthValidatorField" /></td>
            </tr>
            <tr>
                <td>Regex Validator Field: <s:property value="regexValidatorField" /></td>
                <td>Field Expression Validator Field: <s:property value="fieldExpressionValidatorField" /></td>
            </tr>
        </table>
		</div>
	</div>
</div>
</body>
</html>
