<%@ taglib prefix="s" uri="/struts-tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Available Employees</title></head>

<body>
<h1>Available Employees</h1>
<table>
    <tr>
        <th>Id</th>
        <th>First Name</th>
        <th>Last Name</th>
    </tr>
    <s:iterator value="availableItems">
        <tr>
            <td><a href="<s:url action="edit-%{empId}" />"><s:property value="empId"/></a></td>
            <td><s:property value="firstName"/></td>
            <td><s:property value="lastName"/></td>
        </tr>
    </s:iterator>
</table>
<p><a href="<s:url action="edit-" includeParams="none"/>">Create new Employee</a></p>
<p><a href="<s:url action="showcase" namespace="/" includeParams="none"/>">Back to Showcase Startpage</a></p>
</body>
</html>
