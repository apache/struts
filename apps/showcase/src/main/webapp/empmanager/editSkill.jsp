<%@ taglib prefix="s" uri="/struts-tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<s:if test="currentSkill!=null">
    <s:set name="submitType" value="'update'"/>
    <s:text id="title" name="item.edit"><s:param><s:text name="skill"/></s:param></s:text>
</s:if>
<s:else>
    <s:set name="submitType" value="'create'"/>
    <s:text id="title" name="item.create"><s:param><s:text name="skill"/></s:param></s:text>
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
