<#--
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
<html>
<head>
	<title>Struts2 Showcase - Person Manager Example - All People</title>
</head>
<body>
<div class="page-header">
	<h1>All People</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-3">
			<ul class="nav nav-tabs nav-stacked">
				<@s.url var="listpeopleurl" action="list-people" />
				<li class="active"><@s.a href="%{listpeopleurl}">List all people</@s.a></li>
				<@s.url var="editpersonurl" action="edit-person" />
				<li><@s.a href="%{editpersonurl}">Edit people</@s.a></li>
				<@s.url var="newpersonurl" action="new-person" method="input" />
				<li><@s.a href="%{newpersonurl}">Create a new person</@s.a></li>
			</ul>
		</div>
		<div class="col-md-9">

			<p>There are ${peopleCount} people...</p>

			<table class="table table-striped table-bordered table-hover table-condensed">
				<tr>
					<th>ID</th>
					<th>Name</th>
					<th>Last Name</th>
				</tr>
			<#list people as person>
				<tr>
					<td>${person.id?html}</td>
					<td>${person.name?html}</td>
					<td>${person.lastName?html}</td>
				</tr>
			</#list>
			</table>
		</div>
	</div>
</div>
</body>
</html>
