<%@taglib uri="/struts-action" prefix="saf" %>

<html>
<head>
<title>Showcase - Tags - Non UI Tags - SubsetTag Demo</title>
</head>
<body>

	<saf:actionerror/>
	<saf:actionmessage/>

	<saf:form action="submitSubsetTagDemo" namespace="/tags/non-ui/subsetIteratorTag" method="POST">
		<saf:textfield label="Iterator value (comma separated)" name="iteratorValue" />
		<saf:textfield label="Count" name="count" />
		<saf:textfield label="Start" name="start" />
		<saf:submit />
	</saf:form>

</body>
</html>
