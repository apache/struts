<%@ taglib prefix="s" uri="/struts-tags" %>

<!-- START SNIPPET: ajaxValidation -->

<html>
<head>
    <title>Validation - Basic</title>
    <s:head theme="ajax"/>
</head>

<body>

<s:form method="post" validate="true" theme="ajax">
    <s:textfield label="Name" name="name"/>
    <s:textfield label="Age" name="age"/>
    <s:textfield label="Favorite color" name="answer"/>
    <s:submit/>
</s:form>

</body>
</html>

<!-- END SNIPPET: ajaxValidation -->
