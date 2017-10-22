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
	<s:if test="currentEmployee!=null">
		<s:text var="title" name="item.edit"><s:param><s:text name="employee"/></s:param></s:text>
	</s:if>
	<s:else>
		<s:text var="title" name="item.create"><s:param><s:text name="employee"/></s:param></s:text>
	</s:else>
	<title>Struts2 Showcase - CRUD Example - <s:property value="#title"/></title>
	<s:head/>
</head>
<body>
<div class="page-header">
	<h1><s:property value="#title"/></h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-3">
			<ul class="nav nav-tabs nav-stacked">
				<li><s:url var="url" namespace="/employee" action="list"/><s:a href="%{url}">List available Employees</s:a></li>
				<li class="active"><s:url var="url" namespace="/employee" action="edit"/><s:a href="%{url}">Create/Edit Employee</s:a></li>
				<li><s:url var="url" namespace="/skill" action="list"/><s:a href="%{url}">List available Skills</s:a></li>
				<li><s:url var="url" namespace="/skill" action="edit"/><s:a href="%{url}">Create/Edit Skill</s:a></li>
			</ul>
		</div>
		<div class="col-md-9">

			<s:action var="skillAction" namespace="/skill" name="list"/>

			<s:form name="editForm" action="save">
				<s:textfield label="Employee Id" name="currentEmployee.empId"/>
				<s:textfield label="%{getText('employee.firstName')}" name="currentEmployee.firstName"/>
				<s:textfield label="%{getText('employee.lastName')}" name="currentEmployee.lastName"/>
				<s:textfield type="date" label="Birthdate" name="currentEmployee.birthDate"/>
				<s:textfield label="Salary" name="currentEmployee.salary" value="%{getText('format.number',{currentEmployee.salary})}" />
				<s:checkbox fieldValue="true" label="Married" name="currentEmployee.married"/>
				<s:combobox list="availablePositions" label="Position" name="currentEmployee.position"/>
				<s:select list="#skillAction.availableItems" listKey="name" label="Main Skill"
				          name="currentEmployee.mainSkill.name"/>
				<s:select list="#skillAction.availableItems" listKey="name" listValue="description" label="Other Skills"
				          name="selectedSkills" multiple="true"/>
				<s:password label="Password" name="currentEmployee.password"/>
				<s:radio list="availableLevels" name="currentEmployee.level"/>
				<s:textarea label="Comment" name="currentEmployee.comment" cols="50" rows="3"/>
				<s:submit value="%{getText('save')}"  cssClass="btn btn-primary"/>
			</s:form>

		</div>
	</div>
</div>
</body>
</html>
