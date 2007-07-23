<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>Showcase - JSF Integration</title>
</head>
<body>
<h1> JavaServer Faces Integration </h1>

<p>
The following pages show how Struts and JSF components can work together,
each doing what they do best.
</p>

<p>
    <ul>
        <li><s:url id="url" namespace="/jsf/employee" action="list"/><s:a href="%{url}">List available Employees</s:a></li>
        <li><s:url id="url" namespace="/jsf/employee" action="edit"/><s:a href="%{url}">Create/Edit Employee</s:a></li>
    </ul>
</p>


</body>
</html>
