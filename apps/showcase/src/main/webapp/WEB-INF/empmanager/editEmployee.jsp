<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
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
	<sx:head/>
</head>
<body>
<div class="page-header">
	<h1><s:property value="#title"/></h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span3">
			<ul class="nav nav-tabs nav-stacked">
				<li><s:url var="url" namespace="/employee" action="list"/><s:a href="%{url}">List available Employees</s:a></li>
				<li class="active"><s:url var="url" namespace="/employee" action="edit"/><s:a href="%{url}">Create/Edit Employee</s:a></li>
				<li><s:url var="url" namespace="/skill" action="list"/><s:a href="%{url}">List available Skills</s:a></li>
				<li><s:url var="url" namespace="/skill" action="edit"/><s:a href="%{url}">Create/Edit Skill</s:a></li>
			</ul>
		</div>
		<div class="span9">

			<s:action var="skillAction" namespace="/skill" name="list"/>

			<s:form name="editForm" action="save">
				<s:textfield label="Employee Id" name="currentEmployee.empId"/>
				<s:textfield label="%{getText('employee.firstName')}" name="currentEmployee.firstName"/>
				<s:textfield label="%{getText('employee.lastName')}" name="currentEmployee.lastName"/>
				<sx:datetimepicker label="Birthdate" name="currentEmployee.birthDate"/>
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
