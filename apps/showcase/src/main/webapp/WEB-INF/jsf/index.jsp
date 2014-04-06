<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - JSF Integration</title>
	<s:head/>
</head>

<body>

<div class="page-header">
	<h1>JavaServer Faces Integration</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<p>
				The following pages show how Struts and JSF components can work together,
				each doing what they do best.
			</p>

			<p>
			<ul>
				<li><s:url var="url" namespace="/jsf/employee" action="list"/><s:a
						href="%{url}">List available Employees</s:a></li>
				<li><s:url var="url" namespace="/jsf/employee" action="edit"/><s:a
						href="%{url}">Create/Edit Employee</s:a></li>
			</ul>
			</p>
		</div>
	</div>
</div>
</body>
</html>