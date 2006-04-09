<%@taglib uri="/webwork" prefix="ww" %>

<html>
<head>
<title>Showcase - Tags - Non UI Tags - SubsetTag Demo</title>
</head>
<body>

	<ww:actionerror/>
	<ww:actionmessage/>

	<ww:form action="submitSubsetTagDemo" namespace="/tags/non-ui/subsetIteratorTag" method="POST">
		<ww:textfield label="Iterator value (comma separated)" name="iteratorValue" />
		<ww:textfield label="Count" name="count" />
		<ww:textfield label="Start" name="start" />
		<ww:submit />
	</ww:form>

</body>
</html>
