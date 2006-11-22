<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <s:if test="task=='Create'">
        <title><s:text name="subscription.title.create"/></title>
    </s:if>
    <s:if test="task=='Edit'">
        <title><s:text name="subscription.title.edit"/></title>
    </s:if>
    <s:if test="task=='Delete'">
        <title><s:text name="subscription.title.delete"/></title>
    </s:if>
    <link href="<s:url value="/css/mailreader.css"/>" rel="stylesheet"
          type="text/css"/>
</head>

<body onLoad="self.focus();document.Subscription.username.focus()">

<s:actionerror/>
<s:form action="Subscription_save" validate="true">
    <s:token />
    <s:hidden name="task"/>
    <s:label key="username" name="user.username"/>

    <s:if test="task == 'Create'">
        <s:textfield key="mailHostname" name="host"/>
    </s:if>
    <s:else>
        <s:label key="mailHostname" name="host"/>
        <s:hidden name="host"/>
    </s:else>

    <s:if test="task == 'Delete'">
        <s:label key="mailUsername"
                   name="subscription.username"/>
        <s:label key="mailPassword"
                   name="subscription.password"/>
        <s:label key="mailServerType"
                   name="subscription.type"/>
        <s:label key="autoConnect"
                   name="subscription.autoConnect"/>
        <s:submit key="button.confirm"/>
    </s:if>
    <s:else>
        <s:textfield key="mailUsername"
                       name="subscription.username"/>
        <s:textfield key="mailPassword"
                       name="subscription.password"/>
        <s:select key="mailServerType"
                    name="subscription.type" list="types"/>
        <s:checkbox key="autoConnect"
                      name="subscription.autoConnect"/>
        <s:submit key="button.save"/>
        <s:reset key="button.reset"/>
    </s:else>

    <s:submit action="Registration_input"
                key="button.cancel"
                onclick="form.onsubmit=null"/>
</s:form>

<jsp:include page="Footer.jsp"/>

</body>
</html>
