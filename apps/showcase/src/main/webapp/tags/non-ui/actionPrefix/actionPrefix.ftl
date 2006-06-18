

<html>
<head>
	<title>Showcase - Tags - Non UI - Action Prefix (freemarker)</title>
</head>
<body>
	
	You have come to this page because you used an 'action' prefix.<p/>
	
	The text you've enter is ${text?default('')}<p/>
	
	<@saf.url id="url" action="actionPrefixExampleUsingFreemarker" namespace="/tags/non-ui/actionPrefix" />
	<@saf.a href="%{#url}">Back</@saf.a>
	
</body>
</html>


