<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><s:text name="logon.title"/></title>
    <link href="<s:url value="/css/mailreader.css"/>" rel="stylesheet"
          type="text/css"/>
</head>

<body onLoad="self.focus();document.registration/Retrieve.username.focus()">

<s:actionerror/>
<s:url id="Retrieve" action="registration/Retrieve" />
<s:form action="registration/Retrieve" validate="true">
    <s:textfield label="%{getText('username')}" name="username"/>

    <s:password label="%{getText('password')}" name="password" showPassword="true"/>

    <s:submit value="%{getText('button.save')}"/>

    <s:reset value="%{getText('button.reset')}"/>

    <s:submit action="Welcome" value="%{getText('button.cancel')}"
              onclick="form.onsubmit=null"/>
</s:form>

<jsp:include page="../Footer.jsp"/>
</body>
</html>
