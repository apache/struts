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
<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - CRUD Example</title>
</head>
<body>
<div class="page-header">
	<h1>Available Employees</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-3">
			<ul class="nav nav-tabs nav-stacked">
				<li class="active"><s:url var="url" namespace="/employee" action="list"/><s:a href="%{url}">List available Employees</s:a></li>
				<li><s:url var="url" namespace="/employee" action="edit"/><s:a href="%{url}">Create/Edit Employee</s:a></li>
				<li><s:url var="url" namespace="/skill" action="list"/><s:a href="%{url}">List available Skills</s:a></li>
				<li><s:url var="url" namespace="/skill" action="edit"/><s:a href="%{url}">Create/Edit Skill</s:a></li>
			</ul>
		</div>
		<div class="col-md-9">

				<table class="table table-striped table-bordered table-hover table-condensed">
					<tr>
						<th>Id</th>
						<th>First Name</th>
						<th>Last Name</th>
					</tr>
					<s:iterator value="availableItems">
						<tr>
							<td><a href="<s:url action="edit-%{empId}" />"><s:property value="empId"/></a></td>
							<td><s:property value="firstName"/></td>
							<td><s:property value="lastName"/></td>
						</tr>
					</s:iterator>

				</table>
				<a href="<s:url action="edit-" includeParams="none"/>" class="btn btn-primary">Create new Employee</a>
		</div>
	</div>
</div>
</body>
</html>
