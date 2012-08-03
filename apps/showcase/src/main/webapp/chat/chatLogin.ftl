
<html>
	<head>
		<title>Showcase - Chat - Login</title>
		<@s.head />
	</head>
	<body>
	<@s.actionerror />
	<@s.actionmessage />
	<@s.fielderror />
	<@s.form action="login" namespace="/chat" method="POST">
		<@s.textfield name="name" label="Name" required="true" />
		<@s.submit/>
	</@s.form>
	</body>
</html>
