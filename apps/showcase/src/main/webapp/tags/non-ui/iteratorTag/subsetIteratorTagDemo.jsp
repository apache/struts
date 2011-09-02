<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>Showcase - Tags - Non UI Tags - SubsetTag Demo</title>
</head>
<body>

    <s:actionerror/>
    <s:actionmessage/>

    <s:form action="submitSubsetTagDemo" namespace="/tags/non-ui/subsetIteratorTag" method="POST">
        <s:textfield label="Iterator value (comma separated)" name="iteratorValue" />
        <s:textfield label="Count" name="count" />
        <s:textfield label="Start" name="start" />
        <s:submit />
    </s:form>

</body>
</html>
