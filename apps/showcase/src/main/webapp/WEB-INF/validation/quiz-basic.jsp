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

<!-- START SNIPPET: basicValidation -->

<html>
<head>
	<title>Struts2 Showcase - Validation - Basic</title>
	<s:head/>
</head>

<body>

<div class="page-header">
	<h1>Basic validation Example</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<p>
				<b>What is your favorite color?</b>

			<p/>

			<s:form method="post">
				<s:textfield label="Name" name="name"/>
				<s:textfield label="Age" name="age"/>
				<s:textfield label="Favorite color" name="answer"/>
				<s:submit cssClass="btn btn-primary"/>
			</s:form>
		</div>
	</div>
</div>
</body>
</html>
<!-- END SNIPPET: basicValidation -->

