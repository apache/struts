<%@ taglib uri="/webwork" prefix="ww" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<ww:if test="currentSkill!=null">
    <ww:set name="submitType" value="'update'"/>
    <ww:text id="title" name="item.edit"><ww:param><ww:text name="skill"/></ww:param></ww:text>
</ww:if>
<ww:else>
    <ww:set name="submitType" value="'create'"/>
    <ww:text id="title" name="item.create"><ww:param><ww:text name="skill"/></ww:param></ww:text>
</ww:else>
<html>
<head><title><ww:property value="#title"/></title></head>

<body>
<h1><ww:property value="#title"/></h1>

<ww:form action="save">
    <ww:textfield label="%{getText('skill.name')}" name="currentSkill.name"/>
    <ww:textfield label="%{getText('skill.description')}" name="currentSkill.description"/>
    <%--ww:submit name="%{#submitType}" value="%{getText('save')}" /--%>
    <ww:submit value="%{getText('save')}" />
</ww:form>
<p><a href="<ww:url action="list"/>"><ww:text name="skill.backtolist"/></a></p>
</body>
</html>
