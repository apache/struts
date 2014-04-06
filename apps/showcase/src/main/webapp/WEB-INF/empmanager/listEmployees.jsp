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
	<div class="row-fluid">
		<div class="span3">
			<ul class="nav nav-tabs nav-stacked">
				<li class="active"><s:url var="url" namespace="/employee" action="list"/><s:a href="%{url}">List available Employees</s:a></li>
				<li><s:url var="url" namespace="/employee" action="edit"/><s:a href="%{url}">Create/Edit Employee</s:a></li>
				<li><s:url var="url" namespace="/skill" action="list"/><s:a href="%{url}">List available Skills</s:a></li>
				<li><s:url var="url" namespace="/skill" action="edit"/><s:a href="%{url}">Create/Edit Skill</s:a></li>
			</ul>
		</div>
		<div class="span9">

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
