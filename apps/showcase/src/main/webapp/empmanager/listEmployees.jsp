<%@ taglib uri="/struts-action" prefix="saf" %>

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
    <saf:iterator value="availableItems">
        <tr>
            <td><a href="<saf:url action="edit"><saf:param name="empId" value="empId"/></saf:url>"><saf:property value="empId"/></a></td>
            <td><saf:property value="firstName"/></td>
            <td><saf:property value="lastName"/></td>
        </tr>
    </saf:iterator>
</table>
<p><a href="<saf:url action="edit" includeParams="none"/>">Create new Employee</a></p>
<p><a href="<saf:url action="showcase" namespace="/" includeParams="none"/>">Back to Showcase Startpage</a></p>
</body>
</html>
