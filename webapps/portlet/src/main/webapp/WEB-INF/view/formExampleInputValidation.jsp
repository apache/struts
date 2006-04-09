<%@ taglib prefix="ww" uri="/webwork" %>
<link rel="stylesheet" type="text/css" href="<ww:url value="/styles/styles.css"/>">
<H2>Input your name</H2>
<ww:form action="processValidationExample" method="POST">
	<ww:textfield label="First name" name="firstName" value="%{firstName}"/>
	<ww:textfield label="Last name" name="lastName" value="%{lastName}"/>
	<ww:submit value="Submit the form"/>
</ww:form>
