
<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
<title>Showcase - Tags - Non-Ui - Action Prefix</title>
</head>
<body>
	<ul><saf:url id="url" action="actionPrefixExampleUsingFreemarker" namespace="/tags/non-ui/actionPrefix" />
	<saf:a href="%{#url}">Action Prefix Example (Freemarker)</saf:a></ul>
</body>
</html>

