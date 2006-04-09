<%@ taglib prefix="saf" uri="/struts-action" %>

<!-- START SNIPPET: clientValidation -->

<html>
<head>
    <title>Validation - Basic</title>
    <saf:head/>
</head>

<body>

<saf:form method="post" validate="true">
    <saf:textfield label="Name" name="name"/>
    <saf:textfield label="Age" name="age"/>
    <saf:textfield label="Favorite color" name="answer"/>
    <saf:submit/>
</saf:form>

</body>
</html>

<!--  END SNIPPET: clientValidation -->

