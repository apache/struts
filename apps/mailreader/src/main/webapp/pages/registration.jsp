<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
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

<body onLoad="self.focus();document.form1.username.focus()">

<s:actionerror/>
<s:form id="form1" action="registration/Update" validate="true">
    <s:token/>
    <s:hidden name="task"/>
    <s:if test="task == 'Create'">
        <s:textfield label="%{getText('username')}" name="username"/>
    </s:if>
    <s:else>
        <s:label label="%{getText('username')}" name="username"/>
        <s:hidden name="username"/>
    </s:else>

    <s:password label="%{getText('password')}" name="password"/>

    <s:password label="%{getText('password2')}" name="password2"/>

    <s:textfield label="%{getText('fullName')}"
                 name="user.fullName"/>

    <s:textfield label="%{getText('fromAddress')}"
                 name="user.fromAddress"/>

    <s:textfield label="%{getText('replyToAddress')}"
                 name="user.replyToAddress"/>

    <s:if test="task == 'Create'">
        <s:submit value="%{getText('button.save')}" action="registration/Create"/>

        <s:reset value="%{getText('button.reset')}"/>

        <s:submit action="Welcome" value="%{getText('button.cancel')}"
                  onclick="form.onsubmit=null"/>
    </s:if>
    <s:else>
        <s:submit value="%{getText('button.save')}"/>

        <s:reset value="%{getText('button.reset')}"/>

        <s:submit value="%{getText('button.cancel')}"
                  action="MainMenu" onclick="form.onsubmit=null"/>
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
                    <s:url id="Delete" action="subscription/vDelete" >
                        <s:param name="host" value="host"/>
                    </s:url>
                    <s:a href="%{Delete}">
                        <s:text name="registration.deleteSubscription"/>
                    </s:a>
                    &nbsp;
                    <s:url id="Edit" action="subscription/vEdit">
                        <s:param name="host" value="host"/>
                    </s:url>
                    <s:a href="%{Edit}">
                        <s:text name="registration.editSubscription"/>
                    </s:a>
                </td>
            </tr>
        </s:iterator>

    </table>

    <s:url id="Input" action="subscription/vInput" />
    <s:a href="%{Input}"><s:text name="registration.addSubscription"/></s:a>

</s:if>

<jsp:include page="Footer.jsp"/>

</body>
</html>
