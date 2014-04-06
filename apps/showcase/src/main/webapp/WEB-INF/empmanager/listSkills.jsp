<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - CRUD Example</title>
</head>
<body>
<div class="page-header">
	<h1>Available Skills</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span3">
			<ul class="nav nav-tabs nav-stacked">
				<li><s:url var="url" namespace="/employee" action="list"/><s:a href="%{url}">List available Employees</s:a></li>
				<li><s:url var="url" namespace="/employee" action="edit"/><s:a href="%{url}">Create/Edit Employee</s:a></li>
				<li class="active"><s:url var="url" namespace="/skill" action="list"/><s:a href="%{url}">List available Skills</s:a></li>
				<li><s:url var="url" namespace="/skill" action="edit"/><s:a href="%{url}">Create/Edit Skill</s:a></li>
			</ul>
		</div>
		<div class="span9">

			<table class="table table-striped table-bordered table-hover table-condensed">
				<tr>
					<th>Name</th><th>Description</th>
				</tr>
				<s:iterator value="availableItems">
					<tr>
						<td><a href="<s:url action="edit"><s:param name="skillName" value="name"/></s:url>"><s:property value="name"/></a></td>
						<td><s:property value="description"/></td>
					</tr>
				</s:iterator>
			</table>

			<a href="<s:url action="edit" includeParams="none"/>" class="btn btn-primary">Create new Skill</a>
		</div>
	</div>
</div>
</body>
</html>

