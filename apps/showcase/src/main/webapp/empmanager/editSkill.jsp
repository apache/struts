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
<%@ taglib prefix="s" uri="/struts-tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<s:if test="currentSkill!=null">
    <s:set name="submitType" value="'update'"/>
    <s:text id="title" name="item.edit"><s:param><s:text name="skill"/></s:param></s:text>
</s:if>
<s:else>
    <s:set name="submitType" value="'create'"/>
    <s:text var="title" name="item.create"><s:param><s:text name="skill"/></s:param></s:text>
</s:else>
<html>
<head><title><s:property value="#title"/></title></head>

<body>
<h1><s:property value="#title"/></h1>

<s:form action="save">
    <s:textfield label="%{getText('skill.name')}" name="currentSkill.name"/>
    <s:textfield label="%{getText('skill.description')}" name="currentSkill.description"/>
    <%--s:submit name="%{#submitType}" value="%{getText('save')}" /--%>
    <s:submit value="%{getText('save')}" />
</s:form>
<p><a href="<s:url action="list"/>"><s:text name="skill.backtolist"/></a></p>
</body>
</html>
