<%@ taglib prefix="saf" uri="/struts-action" %>

<!-- START SNIPPET: basicValidation -->

<html>
<head>
    <title>Validation - Basic</title>
    <saf:head/>
</head>

<body>

<b>What is your favorite color?</b>
<p/>

<saf:form method="post">
    <saf:textfield label="Name" name="name"/>
    <saf:textfield label="Age" name="age"/>
    <saf:textfield label="Favorite color" name="answer"/>
    <saf:submit/>
</saf:form>

</body>
</html>

<!-- END SNIPPET: basicValidation -->

