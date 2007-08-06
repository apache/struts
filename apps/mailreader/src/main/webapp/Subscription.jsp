<%@ page contentType="text/html; charset=UTF-8" %>
<%--
/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
--%>
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
    <link href="<s:url value="/css/mailreader.css" includeParams="none"/>" rel="stylesheet"
          type="text/css"/>
</head>

<body onLoad="self.focus();document.Subscription.username.focus()">

<s:actionerror/>
<s:form action="Subscription_save" validate="true">
    <s:token />
    <s:hidden name="task"/>
    <s:label key="username" name="user.username"/>

    <s:if test="task == 'Create'">
        <s:textfield key="host"/>
    </s:if>
    <s:else>
        <s:label key="host"/>
        <s:hidden name="host"/>
    </s:else>

    <s:if test="task == 'Delete'">
        <s:label key="subscription.username"/>
        <s:label key="subscription.password"/>
        <s:label key="subscription.type"/>
        <s:label key="subscription.autoConnect"/>
        <s:submit key="button.confirm"/>
    </s:if>
    <s:else>
        <s:textfield key="subscription.username"/>
        <s:textfield key="subscription.password"/>
        <s:select key="subscription.type" list="types"/>
        <s:checkbox key="subscription.autoConnect"/>
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
