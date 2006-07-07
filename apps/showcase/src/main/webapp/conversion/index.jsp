<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Showcase - Conversion</title>
</head>
<body>

<ul>
	<li><saf:url id="url" action="enterPersonsInfo" namespace="/conversion" /><saf:a href="%{#url}">Populate into the Struts action class a List of Person.java Object</saf:a></li>
</ul>

</body>
</html>