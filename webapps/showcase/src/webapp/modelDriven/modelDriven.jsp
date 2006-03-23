<%@taglib prefix="ww" uri="/webwork" %>

<html>
<head>
<title>Showcase - Model Driven Example</title>
</head>
<body>

<ww:form action="modelDrivenResult" method="POST" namespace="/modelDriven">
	
	<ww:textfield 
			label="Gangster Name"
			name="name" />
	<ww:textfield
			label="Gangster Age"
			name="age" />
	<ww:checkbox
			label="Gangster Busted Before"
			name="bustedBefore" />
	<ww:textarea
			cols="30"
			rows="5"
			label="Gangster Description"
			name="description" />			
	<ww:submit />
	
</ww:form>

</body>
</html>
