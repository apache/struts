<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Showcase - Validation - Store Errors Across Request Example</title>
</head>
<body>
	<p>
	This is an example demonstrating the use of MessageStoreInterceptor.
	When this form is submited a redirect is issue both when there's a validation
	error or not. Normally, when a redirect is issue the action messages / errors and
	field errors stored in the action will be lost (due to an action lives 
	only as long as a request). With a MessageStoreInterceptor in place and 
	configured, the action errors / messages / field errors will be store and 
	remains retrieveable even after a redirect.
	</p>

    <table border="1">
        <tr><td>ActionMessages: </td><td></td><s:actionmessage/></tr>
       <tr><td>ActionErrors: </td><td><s:actionerror/></td></tr>
    </table>

    <p>
    <s:form action="submitApplication" namespace="/validation">
		<s:textfield name="name" label="Name" />
		<s:textfield name="age" label="Age" />
		<s:submit />
		<s:submit action="cancelApplication" value="%{'Cancel'}" />
	</s:form>
    </p>
    <p>
        Try submitting with an invalid age value,
        and note that the browser location changes,
        but validation messages are retained.
        Because of the redirect,
        the input values are not retained. 
    </p>

</body>
</html>

