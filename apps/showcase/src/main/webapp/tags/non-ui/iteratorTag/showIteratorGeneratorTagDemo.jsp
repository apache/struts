<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>Showcase - Tag - Non Ui Tag - Iterator Generator Tag Demo</title>
<s:head/>
</head>
<body>

    <s:actionerror/>
    <s:actionmessage/>
    
    <s:form action="submitGeneratorTagDemo" namespace="/tags/non-ui/iteratorGeneratorTag" method="POST">
        <s:textfield label="Value" name="value" />
        <s:textfield label="Separator" name="separator" />
        <s:textfield label="Count" name="count" />
        <s:submit />
    </s:form>

</body>
</html>
