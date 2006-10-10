<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>Showcase - CRUD </title>
</head>
<body>
<h1> CRUD </h1>

<p>
    <ul>
        <li><s:url id="url" namespace="/skill" action="list"/><s:a href="%{url}">List available Skills</s:a></li>
        <li><s:url id="url" namespace="/skill" action="edit"/><s:a href="%{url}">Create/Edit Skill</s:a></li>
        <li><s:url id="url" namespace="/employee" action="list"/><s:a href="%{url}">List available Employees</s:a></li>
        <li><s:url id="url" namespace="/employee" action="edit"/><s:a href="%{url}">Create/Edit Employee</s:a></li>
    </ul>
</p>


</body>
</html>
