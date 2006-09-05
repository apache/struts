<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Logon</title>
</head>

<body>
<s:form action="Login">
    <s:textfield label="%{getText('username')}" name="username"/>
    <s:password label="%{getText('password')}" name="password" />
    <s:submit/>
</s:form>
</body>
</html>
