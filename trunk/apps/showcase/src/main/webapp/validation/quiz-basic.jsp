<%@ taglib prefix="s" uri="/struts-tags" %>

<!-- START SNIPPET: basicValidation -->

<html>
<head>
    <title>Validation - Basic</title>
    <s:head/>
</head>

<body>

<b>What is your favorite color?</b>
<p/>

<s:form method="post">
    <s:textfield label="Name" name="name"/>
    <s:textfield label="Age" name="age"/>
    <s:textfield label="Favorite color" name="answer"/>
    <s:submit/>
</s:form>

</body>
</html>

<!-- END SNIPPET: basicValidation -->

