<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
<title>Showcase - Tag - Non UI Tag - AppendIterator Tag</title>
</head>
<body>

	<saf:actionerror/>
	<saf:actionmessage/>

	<saf:form action="submitAppendTagDemo" namespace="/tags/non-ui/appendIteratorTag" method="POST">
		<saf:textfield label="iterator 1 values (comma separated)" name="iteratorValue1" />
		<saf:textfield label="iterator 2 values (comma separated)" name="iteratorValue2" />
		<saf:submit />
	</saf:form>

</body>
</html>
