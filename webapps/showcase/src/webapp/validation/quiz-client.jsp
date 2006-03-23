<%@ taglib prefix="ww" uri="/webwork" %>

<!-- START SNIPPET: clientValidation -->

<html>
<head>
    <title>Validation - Basic</title>
    <ww:head/>
</head>

<body>

<ww:form method="post" validate="true">
    <ww:textfield label="Name" name="name"/>
    <ww:textfield label="Age" name="age"/>
    <ww:textfield label="Favorite color" name="answer"/>
    <ww:submit/>
</ww:form>

</body>
</html>

<!--  END SNIPPET: clientValidation -->

