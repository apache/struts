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
	<title>Struts2 Showcase - Person Manager Example - New Person</title>
</head>
<body>
<div class="border-bottom pb-2 mb-3">
	<h1>New Person</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-3">
			<ul class="nav nav-tabs nav-stacked">
				<@s.url var="listpeopleurl" action="list-people" />
				<li><@s.a href="%{listpeopleurl}">List all people</@s.a></li>
				<@s.url var="editpersonurl" action="edit-person" />
				<li><@s.a href="%{editpersonurl}">Edit people</@s.a></li>
				<@s.url var="newpersonurl" action="new-person" />
				<li class="active"><@s.a href="%{newpersonurl}">Create a new person</@s.a></li>
			</ul>
		</div>
		<div class="col-md-9">
			<@s.actionerror cssClass="alert alert-danger"/>
			<@s.actionmessage cssClass="alert alert-info"/>
			<@s.fielderror  cssClass="alert alert-danger"/>

			<@s.form action="new-person" theme="simple">
				<legend>Create a new Person</legend>
				<div class="mb-3">
					<label class="form-label" for="name">First Name<span class="required">*</span></label>
					<@s.textfield id="name" name="person.name" placeholder="First Name" cssClass="form-control"/>
				</div>
				<div class="mb-3">
					<label class="form-label" for="lastName">Last Name<span class="required">*</span></label>
					<@s.textfield id="lastName" name="person.lastName" placeholder="Last Name" cssClass="form-control"/>
				</div>
			    <div class="mb-3">
				    <@s.submit value="Create person" cssClass="btn btn-primary"/>
			    </div>
			</@s.form>
		</div>
	</div>
</div>
</body>
</html>
