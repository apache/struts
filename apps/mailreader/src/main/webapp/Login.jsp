<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><s:text name="login.title"/></title>
    <link href="<s:url value="/css/mailreader.css"/>" rel="stylesheet"
          type="text/css"/>
</head>

<body onLoad="self.focus();document.Login.username.focus()">

<s:actionerror />
<s:form action="Login" validate="true">
    <s:textfield key="username" />

    <s:password key="password" showPassword="true"/>

    <s:submit key="button.logon"/>

    <s:reset key="button.reset"/>

    <s:submit action="Login_cancel" key="button.cancel"
                onclick="form.onsubmit=null"/>
</s:form>

<jsp:include page="Footer.jsp"/>
</body>
</html>
