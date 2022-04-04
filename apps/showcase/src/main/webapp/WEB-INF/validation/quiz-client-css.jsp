<%@ taglib prefix="s" uri="/struts-tags" %>

<!-- START SNIPPET: clientCssValidation -->

<html>
<head>
	<title>Struts2 Showcase - Validation - Basic (CSS Theme)</title>
	<s:head theme="css_xhtml"/>
</head>

<body>

<div class="page-header">
	<h1>Basic validation Example (CSS Theme)</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<s:form method="post" theme="css_xhtml" validate="true">
				<s:textfield label="Name" name="name"/>
				<s:textfield label="Age" name="age"/>
				<s:textfield label="Favorite color" name="answer"/>
				<s:submit cssClass="btn btn-primary"/>
			</s:form>
		</div>
	</div>
</div>
</body>
</html>

<!-- END SNIPPET: clientCssValidation -->

