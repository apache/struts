<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Struts1 Integration - Result</title>
	<style type="text/css">
		.label {
			background-color: #ffffff;
			color: #000000;
			text-shadow: none;
			font-weight: bold;
		}
	</style>
</head>

<body>

<div class="page-header">
	<h1>Struts1 Integration - Result</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<s:actionmessage cssClass="alert alert-info"/>
			<s:label
					label="Gangster Name"
					name="name" /><br/>
			<s:label
					label="Gangster Age"
					name="age" /><br/>
			<s:label
					label="Busted Before"
					name="bustedBefore" /><br/>
			<s:label
					label="Gangster Description"
					name="description" /><br/>

		</div>
	</div>
</div>
</body>
</html>
