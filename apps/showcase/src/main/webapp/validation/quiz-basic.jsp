<%@ taglib prefix="s" uri="/struts-tags" %>

<!-- START SNIPPET: basicValidation -->

<html>
<head>
	<title>Struts2 Showcase - Validation - Basic</title>
	<s:head/>
</head>

<body>

<div class="page-header">
	<h1>Basic validation Example</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<p>
				<b>What is your favorite color?</b>

			<p/>

			<s:form method="post">
				<s:textfield label="Name" name="name"/>
				<s:textfield label="Age" name="age"/>
				<s:textfield label="Favorite color" name="answer"/>
				<s:submit cssClass="btn btn-primary"/>
			</s:form>


			<s:include value="footer.jsp"/>
		</div>
	</div>
</div>
</body>
</html>
<!-- END SNIPPET: basicValidation -->

