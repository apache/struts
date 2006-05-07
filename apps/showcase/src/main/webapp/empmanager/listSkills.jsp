<%@ taglib uri="/struts-action" prefix="saf" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Available Skills</title></head>

<body>
<h1>Available Skills</h1>
<table>
    <tr>
        <th>Name</th><th>Description</th>
    </tr>
    <saf:iterator value="availableItems">
        <tr>
            <td><a href="<saf:url action="edit"><saf:param name="skillName" value="name"/></saf:url>"><saf:property value="name"/></a></td>
            <td><saf:property value="description"/></td>
        </tr>
    </saf:iterator>
</table>
<!-- Although namescape not correctly specified, the following link should find the right action -->
<p><a href="<saf:url action="edit" includeParams="none"/>">Create new Skill</a></p>
<p><a href="<saf:url action="showcase" namespace="/" includeParams="none"/>">Back to Showcase Startpage</a></p>
</body>
</html>
