<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<s:if test="currentEmployee!=null">
    <s:text var="title" name="item.edit"><s:param><s:text name="employee"/></s:param></s:text>
</s:if>
<s:else>
    <s:text var="title" name="item.create"><s:param><s:text name="employee"/></s:param></s:text>
</s:else>
<html>
<head>
    <title><s:property value="#title"/></title>
    <s:head/>
    <sx:head/>
</head>

<body>
<h1><s:property value="#title"/></h1>

<s:action var="skillAction" namespace="/skill" name="list"/>

<s:form name="editForm" action="save">
    <s:textfield label="Employee Id" name="currentEmployee.empId"/>
    <s:textfield label="%{getText('employee.firstName')}" name="currentEmployee.firstName"/>
    <s:textfield label="%{getText('employee.lastName')}" name="currentEmployee.lastName"/>
    <sx:datetimepicker label="Birthdate" name="currentEmployee.birthDate"/>
    <s:textfield label="Salary" name="currentEmployee.salary" value="%{getText('format.number',{currentEmployee.salary})}" />
    <s:checkbox fieldValue="true" label="Married" name="currentEmployee.married"/>
    <s:combobox list="availablePositions" label="Position" name="currentEmployee.position"/>
    <s:select list="#skillAction.availableItems" listKey="name" label="Main Skill"
               name="currentEmployee.mainSkill.name"/>
    <s:select list="#skillAction.availableItems" listKey="name" listValue="description" label="Other Skills"
               name="selectedSkills" multiple="true"/>
    <s:password label="Password" name="currentEmployee.password"/>
    <s:radio list="availableLevels" name="currentEmployee.level"/>
    <s:textarea label="Comment" name="currentEmployee.comment" cols="50" rows="3"/>
    <s:submit value="%{getText('save')}" />
</s:form>
<p><a href="<s:url action="list"/>"><s:text name="employee.backtolist"/></a></p>
</body>
</html>
