<%@ taglib prefix="ww" uri="/webwork" %>

<!-- START SNIPPET: ajaxValidation -->

<html>
<head>
    <title>Validation - Basic</title>
    <ww:head theme="ajax"/>
</head>

<body>

<ww:form method="post" validate="true" theme="ajax">
    <ww:textfield label="Name" name="name"/>
    <ww:textfield label="Age" name="age"/>
    <ww:textfield label="Favorite color" name="answer"/>
    <ww:submit/>
</ww:form>

</body>
</html>

<!-- END SNIPPET: ajaxValidation -->
