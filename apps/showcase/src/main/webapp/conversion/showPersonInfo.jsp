<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Showcase - Conversion - Populate Object into SAF Action List</title>
</head>
<body>

<saf:iterator value="persons" status="status">
	<saf:label label="%{#status.index+' Name'}" value="%{name}" /><br/>
	<saf:label label="%{#status.index+' Age'}" value="%{age}" /><br/>
</saf:iterator>

</body>
</html>

