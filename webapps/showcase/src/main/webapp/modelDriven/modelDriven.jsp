<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
<title>Showcase - Model Driven Example</title>
</head>
<body>

<saf:form action="modelDrivenResult" method="POST" namespace="/modelDriven">
	
	<saf:textfield 
			label="Gangster Name"
			name="name" />
	<saf:textfield
			label="Gangster Age"
			name="age" />
	<saf:checkbox
			label="Gangster Busted Before"
			name="bustedBefore" />
	<saf:textarea
			cols="30"
			rows="5"
			label="Gangster Description"
			name="description" />			
	<saf:submit />
	
</saf:form>

</body>
</html>
