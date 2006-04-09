<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
<title>Showcase - CRUD </title>
</head>
<body>
<h1> CRUD </h1>

<p>
	<ul>
		<li><saf:url id="url" namespace="/skill" action="list"/><saf:a href="%{url}">List available Skills</saf:a></li>
        <li><saf:url id="url" namespace="/skill" action="edit"/><saf:a href="%{url}">Create/Edit Skill</saf:a></li>
        <li><saf:url id="url" namespace="/employee" action="list"/><saf:a href="%{url}">List available Employees</saf:a></li>
        <li><saf:url id="url" namespace="/employee" action="edit"/><saf:a href="%{url}">Create/Edit Employee</saf:a></li>
	</ul>
</p>


</body>
</html>
