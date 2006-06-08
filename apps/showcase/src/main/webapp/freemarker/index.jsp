
<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
	<title>Showcase - Freemarker</title>
</head>
<body>
	
	<ul>
		<li>
			<saf:url id="url" action="customFreemarkerManagerDemo" namespace="/freemarker" />
			<saf:a href="%{#url}">Demo of usage of a Custom Freemarker Manager</saf:a>
		</li>
	</ul>

</body>
</html>


