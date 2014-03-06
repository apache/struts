<html>
	<head>
		<title>Struts2 Showcase - Chat - Login</title>
		<@s.head />
	</head>
	<body>
	<div class="page-header">
		<h1>Chat - Login</h1>
	</div>

	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span12">
			<@s.actionerror cssClass="alert alert-error"/>
			<@s.actionmessage cssClass="alert alert-info"/>
			<@s.fielderror  cssClass="alert alert-error"/>

			<@s.form action="login" namespace="/chat" method="POST">
				<@s.textfield name="name" label="Name" required="true" />
				<@s.submit cssClass="btn btn-primary"/>
			</@s.form>
			</div>
		</div>
	</div>
	</body>
</html>

