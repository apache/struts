<%@ taglib uri="/struts-action" prefix="saf" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<saf:if test="currentEmployee!=null">
    <saf:text id="title" name="item.edit"><saf:param><saf:text name="employee"/></saf:param></saf:text>
</saf:if>
<saf:else>
    <saf:text id="title" name="item.create"><saf:param><saf:text name="employee"/></saf:param></saf:text>
</saf:else>
<html>
<head>
    <title><saf:property value="#title"/></title>
    <saf:head/>
</head>

<body>
<h1><saf:property value="#title"/></h1>

<saf:action id="skillAction" namespace="/skill" name="list"/>

<saf:form name="editForm" action="save">
    <saf:textfield label="Employee Id" name="currentEmployee.empId"/>
    <saf:textfield label="%{getText('employee.firstName')}" name="currentEmployee.firstName"/>
    <saf:textfield label="%{getText('employee.lastName')}" name="currentEmployee.lastName"/>
    <saf:datepicker label="Birthdate" name="currentEmployee.birthDate"/>
    <saf:textfield label="Salary" name="currentEmployee.salary"/>
    <saf:checkbox fieldValue="true" label="Married" name="currentEmployee.married"/>
    <saf:combobox list="availablePositions" label="Position" name="currentEmployee.position"/>
    <saf:select list="#skillAction.availableItems" listKey="name" label="Main Skill"
               name="currentEmployee.mainSkill.name"/>
    <saf:select list="#skillAction.availableItems" listKey="name" listValue="description" label="Other Skills"
               name="selectedSkills" multiple="true"/>
    <saf:password label="Password" name="currentEmployee.password"/>
    <saf:radio list="availableLevels" name="currentEmployee.level"/>
    <saf:textarea label="Comment" name="currentEmployee.comment" cols="50" rows="3"/>
    <saf:submit value="%{getText('save')}" />
</saf:form>
<p><a href="<saf:url action="list"/>"><saf:text name="employee.backtolist"/></a></p>
</body>
</html>
