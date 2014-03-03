<html>
<head>
	<title>Struts2 Showcase - Non UI Tags - Action Prefix (Freemarker)</title>
</head>
<body>
<div class="page-header">
	<h1>Non Ui Tag - Action Prefix (Freemarker)</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

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


			<@s.url id="url" action="viewSource" namespace="/tags/non-ui/actionPrefix" />
			The JSP code can be read <@s.a href="%{#url}">here</@s.a>.


			<@s.form action="submit" namespace="/tags/non-ui/actionPrefix" method="POST">

				<@s.textfield label="Enter Some Text" name="text" />

				<@s.submit action="actionPrefix" value="%{'action prefix'}" cssClass="btn" />

				<@s.submit method="alternateMethod" value="%{'method prefix'}" cssClass="btn" />

				<@s.submit value="Normal Submit" cssClass="btn" />

	            <@s.submit action="redirectActionPrefixAction" value="%{'redirectAction without prefix'}" cssClass="btn" />

	        </@s.form>
		</div>
	</div>
</div>
</body>
</html>

