<%@taglib prefix="ww" uri="/webwork" %>

<html>
<head>
<title>Showcase - CRUD </title>
</head>
<body>
<h1> CRUD </h1>

<p>
	<ul>
		<li><ww:url id="url" namespace="/skill" action="list"/><ww:a href="%{url}">List available Skills</ww:a></li>
        <li><ww:url id="url" namespace="/skill" action="edit"/><ww:a href="%{url}">Create/Edit Skill</ww:a></li>
        <li><ww:url id="url" namespace="/employee" action="list"/><ww:a href="%{url}">List available Employees</ww:a></li>
        <li><ww:url id="url" namespace="/employee" action="edit"/><ww:a href="%{url}">Create/Edit Employee</ww:a></li>
	</ul>
</p>


</body>
</html>
