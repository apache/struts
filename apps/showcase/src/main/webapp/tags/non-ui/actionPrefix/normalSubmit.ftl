
<html>
<head>
	<title>Showcase - Tags - Non UI - Action Prefix (freemarker)</title>
</head>
<body>
	
	You have come to this page because you did a normal submit.<p/>
	
	The text you've enter is %{text}<p/>
	
	<@s.url id="url" action="actionPrefixExampleUsingFreemarker" namespace="/tags/non-ui/prefix" />
	<@s.a href="%{#url}">Back</@s.a>
	
</body>
</html>

