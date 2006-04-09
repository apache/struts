<%@ taglib prefix="saf" uri="/struts-action" %>

<H2>Input your name</H2>
<saf:form action="processFormExample" method="POST">
	<saf:textfield label="First name" name="firstName" value="%{firstName}"/>
	<saf:textfield label="Last name" name="lastName" value="%{lastName}"/>
	<saf:submit value="Submit the form"/>
</saf:form>
