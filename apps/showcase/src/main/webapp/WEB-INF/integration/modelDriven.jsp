<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Struts1 Integration</title>
	<s:head/>
</head>

<body>

<div class="page-header">
	<h1>Struts1 Integration</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">


			<s:form action="saveGangster" namespace="/integration">

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