<%@ taglib prefix="ww" uri="/webwork" %>

<!-- START SNIPPET: clientCssValidation -->

<html>
<head>
    <title>Validation - Basic</title>
    <ww:head theme="css_xhtml"/>
</head>

<body>

<ww:form method="post" theme="css_xhtml" validate="true">
    <ww:textfield label="Name" name="name"/>
    <ww:textfield label="Age" name="age"/>
    <ww:textfield label="Favorite color" name="answer"/>
    <ww:submit/>
</ww:form>

</body>
</html>

<!--  END SNIPPET: clientCssValidation -->

