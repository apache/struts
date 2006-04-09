<%@ taglib prefix="ww" uri="/webwork" %>

<H2>Input your name</H2>
<ww:form action="processFormExampleEdit" method="POST">
	<ww:textfield label="First name" name="firstName" value="%{firstName}"/>
	<ww:textfield label="Last name" name="lastName" value="%{lastName}"/>
	<ww:submit value="Submit the form"/>
</ww:form>
