<%@ taglib prefix="s" uri="/struts-tags" %>

<h2>Your name:</h2>

<s:form action="index">
	<table>
		<s:textfield name="firstName" label="First name"/>
		<s:textfield name="lastName" label="Last name"/>
		<s:submit value="Update"/>
	</table>
</s:form>

<s:actionmessage/>