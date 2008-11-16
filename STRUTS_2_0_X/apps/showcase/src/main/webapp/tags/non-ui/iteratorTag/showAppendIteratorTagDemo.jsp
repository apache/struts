<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>Showcase - Tag - Non UI Tag - AppendIterator Tag</title>
</head>
<body>

    <s:actionerror/>
    <s:actionmessage/>

    <s:form action="submitAppendTagDemo" namespace="/tags/non-ui/appendIteratorTag" method="POST">
        <s:textfield label="iterator 1 values (comma separated)" name="iteratorValue1" />
        <s:textfield label="iterator 2 values (comma separated)" name="iteratorValue2" />
        <s:submit />
    </s:form>

</body>
</html>
