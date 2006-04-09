<%@taglib prefix="ww" uri="/webwork" %>

<html>
<head>
<title>Showcase - Tag - Non UI Tag - AppendIterator Tag</title>
</head>
<body>

	<ww:actionerror/>
	<ww:actionmessage/>

	<ww:form action="submitAppendTagDemo" namespace="/tags/non-ui/appendIteratorTag" method="POST">
		<ww:textfield label="iterator 1 values (comma separated)" name="iteratorValue1" />
		<ww:textfield label="iterator 2 values (comma separated)" name="iteratorValue2" />
		<ww:submit />
	</ww:form>

</body>
</html>
