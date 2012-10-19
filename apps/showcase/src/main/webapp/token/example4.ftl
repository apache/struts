<html>
<head>
	<title>Struts2 Showcase - Token Examples - Example 4</title>
</head>

<body>
<div class="page-header">
	<h1>Token Examples - Example 4</h1>
</div>


<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<p>
				<b>Example 4:</b> This example illustrates a situation where you can transfer money from
				one account to another. We use the token to prevent double posts so the transfer only
				happens once. This page is rendered using freemarker. See the xwork-token.xml where
				we must also use the createSession interceptor to be sure that a HttpSession exists
				when freemarker renders this webpage, otherwise the @s.token tag causes an exception
				while rendering the page.

			<p/>

			<p>Balance of source account: <@s.property value="#session.balanceSource"/>
				<br/>Balance of destination account: <@s.property value="#session.balanceDestination"/>

			<p/>

		<@s.form action="transfer4">
			<@s.token/>
			<@s.textfield label="Amount" name="amount" required="true" value="400"/>
			<@s.submit value="Transfer money" cssClass="btn btn-primary"/>
		</@s.form>
		</div>
	</div>
</div>
</body>
</html>
