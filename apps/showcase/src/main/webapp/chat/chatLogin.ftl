
<html>
	<head>
		<title>Showcase - Chat - Login</title>
		<@saf.head theme="ajax" />
	</head>
	<body>
	<@saf.actionerror />
	<@saf.actionmessage />
	<@saf.fielderror />
	<@saf.form action="login" namespace="/chat" method="POST">
		<@saf.textfield name="name" label="Name" required="true" />
		<@saf.submit/>
	</@saf.form>
	</body>
</html>
