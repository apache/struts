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
	<title>Struts2 Showcase - Execute and Wait Examples - Example 1</title>
</head>

<body>
<div class="page-header">
	<h1>Execute and Wait Examples - Example 1</h1>
</div>


<div class="container-fluid">
	<div class="row">
		<div class="col-md-12" style="text-align: center;">

			<p><b>Example 1:</b> In the form below enter how long time to simulate the process should take.</p>

			<s:form action="longProcess1">
				<s:textfield label="Time (millis)" name="time" required="true" value="7000"/>
				<s:submit value="submit" cssClass="btn btn-primary"/>
			</s:form>

		</div>
	</div>
</div>
</body>
</html>
