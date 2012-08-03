<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>Showcase - Tags - Non UI Tag - MergeIterator Tag</title>
</head>
<body>

    <s:form action="submitMergeTagDemo" namespace="/tags/non-ui/mergeIteratorTag" method="POST">
        <s:textfield label="Iterator 1 Value (Comma Separated)" name="iteratorValue1" />
        <s:textfield label="Iterator 2 Value (Comma Separated)" name="iteratorValue2" />
        <s:submit />
    </s:form>


</body>
</html>
