<%@ taglib uri="/webwork" prefix="ww" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Available Skills</title></head>

<body>
<h1>Available Skills</h1>
<table>
    <tr>
        <th>Name</th><th>Description</th>
    </tr>
    <ww:iterator value="availableItems">
        <tr>
            <td><a href="<ww:url action="edit"><ww:param name="skillName" value="name"/></ww:url>"><ww:property value="name"/></a></td>
            <td><ww:property value="description"/></td>
        </tr>
    </ww:iterator>
</table>
<!-- Although namescape not correctly specified, the following link should find the right action -->
<p><a href="<ww:url action="edit" includeParams="none"/>">Create new Skill</a></p>
<p><a href="<ww:url action="showcase" namespace="/" includeParams="none"/>">Back to Showcase Startpage</a></p>
</body>
</html>
