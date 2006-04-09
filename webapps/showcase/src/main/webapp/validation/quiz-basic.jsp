<%@ taglib prefix="ww" uri="/webwork" %>

<!-- START SNIPPET: basicValidation -->

<html>
<head>
    <title>Validation - Basic</title>
    <ww:head/>
</head>

<body>

<b>What is your favorite color?</b>
<p/>

<ww:form method="post">
    <ww:textfield label="Name" name="name"/>
    <ww:textfield label="Age" name="age"/>
    <ww:textfield label="Favorite color" name="answer"/>
    <ww:submit/>
</ww:form>

</body>
</html>

<!-- END SNIPPET: basicValidation -->

