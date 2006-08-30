<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
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

<body onLoad="self.focus();document.subscription_Update.subscription_Update_username.focus()">

<s:actionerror/>
<s:form id="subscription_Update" action="subscription/Update" validate="true">
    <s:token/>
    <s:hidden name="task"/>
    <s:label label="%{getText('username')}" name="user.username"/>

    <s:if test="task == 'Create'">
        <s:textfield label="%{getText('mailHostname')}" name="host"/>
    </s:if>
    <s:else>
        <s:label label="%{getText('mailHostname')}" name="host"/>
        <s:hidden name="host"/>
    </s:else>

    <s:if test="task == 'Delete'">
        <s:label label="%{getText('mailUsername')}"
                 name="subscription.username"/>
        <s:label label="%{getText('mailPassword')}"
                 name="subscription.password"/>
        <s:label label="%{getText('mailServerType')}"
                 name="subscription.type"/>
        <s:label label="%{getText('autoConnect')}"
                 name="subscription.autoConnect"/>
        <s:submit value="%{getText('button.confirm')}"/>
    </s:if>
    <s:else>
        <s:textfield label="%{getText('mailUsername')}"
                     name="subscription.username"/>
        <s:textfield label="%{getText('mailPassword')}"
                     name="subscription.password"/>
        <s:select label="%{getText('mailServerType')}"
                  name="subscription.type" list="types"/>
        <s:checkbox label="%{getText('autoConnect')}"
                    name="subscription.autoConnect"/>
        <s:submit value="%{getText('button.save')}"/>
        <s:reset value="%{getText('button.reset')}"/>
    </s:else>

    <s:submit action="registration/+Input"
              value="%{getText('button.cancel')}"
              onclick="form.onsubmit=null"/>
</s:form>

<jsp:include page="Footer.jsp"/>

</body>
</html>
