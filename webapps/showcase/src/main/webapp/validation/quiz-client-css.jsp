<%@ taglib prefix="saf" uri="/struts-action" %>

<!-- START SNIPPET: clientCssValidation -->

<html>
<head>
    <title>Validation - Basic</title>
    <saf:head theme="css_xhtml"/>
</head>

<body>

<saf:form method="post" theme="css_xhtml" validate="true">
    <saf:textfield label="Name" name="name"/>
    <saf:textfield label="Age" name="age"/>
    <saf:textfield label="Favorite color" name="answer"/>
    <saf:submit/>
</saf:form>

</body>
</html>

<!--  END SNIPPET: clientCssValidation -->

