
<html>
<head>
	<title>Showcase - Tags - Non UI - Action Prefix (freemarker)</title>
</head>
<body>
	
	You have come to this page because you did a normal submit.<p/>
	
	The text you've enter is %{text}<p/>
	
	<@saf.url id="url" action="actionPrefixExampleUsingFreemarker" namespace="/tags/non-ui/prefix" />
	<@saf.a href="%{#url}">Back</@saf.a>
	
</body>
</html>

