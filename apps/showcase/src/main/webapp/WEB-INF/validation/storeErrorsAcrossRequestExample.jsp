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
<html>
<head>
	<title>Struts2 Showcase - Validation - Store Errors Across Request Example</title>
	<s:head/>
</head>

<body>

<div class="page-header">
	<h1>Store Errors Across Request Example</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<p>
				This is an example demonstrating the use of MessageStoreInterceptor.
				When this form is submited a redirect is issue both when there's a validation
				error or not. Normally, when a redirect is issue the action messages / errors and
				field errors stored in the action will be lost (due to an action lives
				only as long as a request). With a MessageStoreInterceptor in place and
				configured, the action errors / messages / field errors will be store and
				remains retrieveable even after a redirect.
			</p>

			<table border="1">
				<tr><td>ActionMessages: </td><td></td><s:actionmessage/></tr>
				<tr><td>ActionErrors: </td><td><s:actionerror/></td></tr>
			</table>

			<p>
				<s:form action="submitApplication" namespace="/validation">
					<s:textfield name="name" label="Name" />
					<s:textfield name="age" label="Age" />
					<s:submit cssClass="btn btn-primary"/>
					<s:submit action="cancelApplication" value="%{'Cancel'}" cssClass="btn btn-danger"/>
				</s:form>
			</p>
			<p>
				Try submitting with an invalid age value,
				and note that the browser location changes,
				but validation messages are retained.
				Because of the redirect,
				the input values are not retained.
			</p>
		</div>
	</div>
</div>
</body>
</html>
