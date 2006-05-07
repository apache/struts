<%@taglib uri="/struts-action" prefix="saf" %>

<html>
<head>
<title>Showcase - Tag - Non Ui Tag - Iterator Generator Tag Demo</title>
<saf:head/>
</head>
<body>

	<saf:actionerror/>
	<saf:actionmessage/>
	
	<saf:form action="submitGeneratorTagDemo" namespace="/tags/non-ui/iteratorGeneratorTag" method="POST">
		<saf:textfield label="Value" name="value" />
		<saf:textfield label="Separator" name="separator" />
		<saf:textfield label="Count" name="count" />
		<saf:submit />
	</saf:form>

</body>
</html>
