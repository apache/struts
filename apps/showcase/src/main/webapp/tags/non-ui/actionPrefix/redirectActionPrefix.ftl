

<html>
<head>
	<title>Showcase - Tags - Non UI - Action Prefix (freemarker)</title>
</head>
<body>
	
	You have come to this page because you used an 'redirect-action' prefix.<p/>
	
	Because this is a 'redirect-action', the text will be lost, due to a redirection
	implies a new request being issued from the client.<p/>
	
	The text you've enter is ${text?default('')}<p/>
	
	<@s.url id="url" action="actionPrefixExampleUsingFreemarker" namespace="/tags/non-ui/actionPrefix" />
	<@s.a href="%{#url}">Back</@s.a>
	
</body>
</html>


