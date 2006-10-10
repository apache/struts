<%@ taglib prefix="s" uri="/struts-tags" %>

<!-- START SNIPPET: clientCssValidation -->

<html>
<head>
    <title>Validation - Basic</title>
    <s:head theme="css_xhtml"/>
</head>

<body>

<s:form method="post" theme="css_xhtml" validate="true">
    <s:textfield label="Name" name="name"/>
    <s:textfield label="Age" name="age"/>
    <s:textfield label="Favorite color" name="answer"/>
    <s:submit/>
</s:form>

</body>
</html>

<!--  END SNIPPET: clientCssValidation -->

