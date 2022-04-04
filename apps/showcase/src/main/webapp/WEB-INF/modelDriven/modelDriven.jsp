<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Model Driven Example</title>
	<s:head/>
</head>

<body>

<div class="page-header">
	<h1>Model Driven Example</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<s:form action="modelDrivenResult" method="POST" namespace="/modelDriven">

				<s:textfield
						label="Gangster Name"
						name="name"/>
				<s:textfield
						label="Gangster Age"
						name="age"/>
				<s:checkbox
						label="Gangster Busted Before"
						name="bustedBefore"/>
				<s:textarea
						cols="30"
						rows="5"
						label="Gangster Description"
						name="description"/>
				<s:submit cssClass="btn btn-primary"/>

			</s:form>

		</div>
	</div>
</div>
</body>
</html>