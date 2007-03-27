<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <s:if test="task=='Create'">
        <title><s:text name="registration.title.create"/></title>
    </s:if>
    <s:if test="task=='Edit'">
        <title><s:text name="registration.title.edit"/></title>
    </s:if>
    <link href="<s:url value="/css/mailreader.css"/>" rel="stylesheet"
          type="text/css"/>
</head>

<body onLoad="self.focus();document.Registration_save_username.focus()">

<s:actionerror/>
<s:form action="Registration_save" validate="false">
    <s:token />
    <s:hidden name="task"/>
    <s:if test="task == 'Create'">
        <s:textfield key="username"/>
    </s:if>
    <s:else>
        <s:label key="username"/>
        <s:hidden name="username"/>
    </s:else>

    <s:password key="password" showPassword="true"/>
    <s:password key="password2"/>
    <s:textfield key="user.fullName"/>
    <s:textfield key="user.fromAddress"/>
    <s:textfield key="user.replyToAddress"/>

    <s:if test="task == 'Create'">
        <s:submit key="button.save" action="Registration_save"/>
        <s:reset key="button.reset"/>
        <s:submit action="Welcome" key="button.cancel"
                    onclick="form.onsubmit=null"/>
    </s:if>
    <s:else>
        <s:submit key="button.save" action="Registration"/>
        <s:reset key="button.reset"/>
        <s:submit action="MainMenu" key="button.cancel"
                    onclick="form.onsubmit=null"/>
    </s:else>

</s:form>

<s:if test="task == 'Edit'">
    <div align="center">
        <h3><s:text name="heading.subscriptions"/></h3>
    </div>

    <table border="1" width="100%">

        <tr>
            <th align="center" width="30%">
                <s:text name="heading.host"/>
            </th>
            <th align="center" width="25%">
                <s:text name="heading.user"/>
            </th>
            <th align="center" width="10%">
                <s:text name="heading.type"/>
            </th>
            <th align="center" width="10%">
                <s:text name="heading.autoConnect"/>
            </th>
            <th align="center" width="15%">
                <s:text name="heading.action"/>
            </th>
        </tr>

        <s:iterator value="user.subscriptions">
            <tr>
                <td align="left">
                    <s:property value="host"/>
                </td>
                <td align="left">
                    <s:property value="username"/>
                </td>
                <td align="center">
                    <s:property value="type"/>
                </td>
                <td align="center">
                    <s:property value="autoConnect"/>
                </td>
                <td align="center">

                    <a href="<s:url action="Subscription_delete"><s:param name="host" value="host"/></s:url>">
                        <s:text name="registration.deleteSubscription"/>
                    </a>
                    &nbsp;
                    <a href="<s:url action="Subscription_edit"><s:param name="host" value="host"/></s:url>">
                        <s:text name="registration.editSubscription"/>
                    </a>

                </td>
            </tr>
        </s:iterator>

    </table>

    <a href="<s:url action="Subscription_input"/>"><s:text
            name="registration.addSubscription"/></a>

</s:if>

<jsp:include page="Footer.jsp"/>

</body>
</html>
