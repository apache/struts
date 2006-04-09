<%@taglib uri="/webwork" prefix="ww" %>

<html>
<head>
<title>Showcase - Tag - Non Ui Tag - Iterator Generator Tag Demo</title>
<ww:head/>
</head>
<body>

	<ww:actionerror/>
	<ww:actionmessage/>
	
	<ww:form action="submitGeneratorTagDemo" namespace="/tags/non-ui/iteratorGeneratorTag" method="POST">
		<ww:textfield label="Value" name="value" />
		<ww:textfield label="Separator" name="separator" />
		<ww:textfield label="Count" name="count" />
		<ww:submit />
	</ww:form>

</body>
</html>
