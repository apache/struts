<%@taglib uri="/webwork" prefix="ww" %>

<html>
<head>
<title>Showcase - Tags - Non UI Tag - MergeIterator Tag</title>
</head>
<body>

	<ww:form action="submitMergeTagDemo" namespace="/tags/non-ui/mergeIteratorTag" method="POST">
		<ww:textfield label="Iterator 1 Value (Comma Separated)" name="iteratorValue1" />
		<ww:textfield label="Iterator 2 Value (Comma Separated)" name="iteratorValue2" />
		<ww:submit />
	</ww:form>


</body>
</html>
