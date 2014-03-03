<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<s:if test="currentSkill!=null">
		<s:set name="submitType" value="'update'"/>
		<s:text id="title" name="item.edit"><s:param><s:text name="skill"/></s:param></s:text>
	</s:if>
	<s:else>
		<s:set name="submitType" value="'create'"/>
		<s:text var="title" name="item.create"><s:param><s:text name="skill"/></s:param></s:text>
	</s:else>
	<title>Struts2 Showcase - CRUD Example - <s:property value="#title"/></title>
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
				<li><s:url var="url" namespace="/employee" action="edit"/><s:a href="%{url}">Create/Edit Employee</s:a></li>
				<li><s:url var="url" namespace="/skill" action="list"/><s:a href="%{url}">List available Skills</s:a></li>
				<li class="active"><s:url var="url" namespace="/skill" action="edit"/><s:a href="%{url}">Create/Edit Skill</s:a></li>
			</ul>
		</div>
		<div class="span9">

			<s:form action="save">
				<s:textfield label="%{getText('skill.name')}" name="currentSkill.name"/>
				<s:textfield label="%{getText('skill.description')}" name="currentSkill.description"/>
				<%--s:submit name="%{#submitType}" value="%{getText('save')}" /--%>
				<s:submit value="%{getText('save')}" cssClass="btn btn-primary"/>
			</s:form>

		</div>
	</div>
</div>
</body>
</html>
