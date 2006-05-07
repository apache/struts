<%@taglib uri="/struts-action" prefix="saf" %>

<html>
<head>
<title>Showcase - Tags - Non UI Tag - MergeIterator Tag</title>
</head>
<body>

	<saf:form action="submitMergeTagDemo" namespace="/tags/non-ui/mergeIteratorTag" method="POST">
		<saf:textfield label="Iterator 1 Value (Comma Separated)" name="iteratorValue1" />
		<saf:textfield label="Iterator 2 Value (Comma Separated)" name="iteratorValue2" />
		<saf:submit />
	</saf:form>


</body>
</html>
