<%@ taglib uri="/struts-action" prefix="saf" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<saf:if test="currentSkill!=null">
    <saf:set name="submitType" value="'update'"/>
    <saf:text id="title" name="item.edit"><saf:param><saf:text name="skill"/></saf:param></saf:text>
</saf:if>
<saf:else>
    <saf:set name="submitType" value="'create'"/>
    <saf:text id="title" name="item.create"><saf:param><saf:text name="skill"/></saf:param></saf:text>
</saf:else>
<html>
<head><title><saf:property value="#title"/></title></head>

<body>
<h1><saf:property value="#title"/></h1>

<saf:form action="save">
    <saf:textfield label="%{getText('skill.name')}" name="currentSkill.name"/>
    <saf:textfield label="%{getText('skill.description')}" name="currentSkill.description"/>
    <%--saf:submit name="%{#submitType}" value="%{getText('save')}" /--%>
    <saf:submit value="%{getText('save')}" />
</saf:form>
<p><a href="<saf:url action="list"/>"><saf:text name="skill.backtolist"/></a></p>
</body>
</html>
