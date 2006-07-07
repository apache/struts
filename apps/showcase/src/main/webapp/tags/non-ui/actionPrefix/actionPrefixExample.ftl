
<html>
	<head>
		<title>Showcase - Tags - Non UI - Action Prefix (Freemarker)</title>
	</head>
	<body>
		<b>Action Prefix</b><br/>
		By clicking on 'action prefix' button, the request will go to the action alias 'actionPrefix'
		instead of the normal 'submit' action alias. <p/><p/>
		
		<b>Method Prefix</b><br/>
		By clicking on the 'method prefix' button, the request will cause Struts to invoke 'submit' 
		action alias's 'alternateMethod' method instead of the default 'execute' method.<p/>
		
		<b>Redirect Prefix</b><br/>
		By clicking on the 'redirect prefix' button, the request will get redirected to www.google.com 
		instead<p/>
		
		<b>Redirect Action Prefix</b><br/>
		By clicking on the 'redirect-action prefix' button, the request will get redirected to 
		an action alias of 'redirectActionPrefix' instead of 'submit' action alias. Since this is a
		redirect (a new request is issue from the client), the text entered will be lost.<p/>
		
		
		<@saf.url id="url" action="viewSource" namespace="/tags/non-ui/actionPrefix" />
		The JSP code can be read <@saf.a href="%{#url}">here</@saf.a>.
		
	
		<@saf.form action="submit" namespace="/tags/non-ui/actionPrefix" method="POST">
			
			<@saf.textfield label="Enter Some Text" name="text" />
			
			<@saf.submit name="action:actionPrefix" value="%{'action prefix'}" />
			
			<@saf.submit name="method:alternateMethod" value="%{'method prefix'}" />
			
			<@saf.submit name="redirect:http://www.google.com" value="%{'redirect prefix'}" />
			
			<@saf.submit name="redirect-action:redirectActionPrefix" value="%{'redirect-action prefix'}" />
			
			<@saf.submit value="Normal Submit" />
		</@saf.form>
	</body>
</html>

