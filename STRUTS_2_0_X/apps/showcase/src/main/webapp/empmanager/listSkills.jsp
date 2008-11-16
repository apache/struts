<%@ taglib prefix="s" uri="/struts-tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Available Skills</title></head>

<body>
<h1>Available Skills</h1>
<table>
    <tr>
        <th>Name</th><th>Description</th>
    </tr>
    <s:iterator value="availableItems">
        <tr>
            <td><a href="<s:url action="edit"><s:param name="skillName" value="name"/></s:url>"><s:property value="name"/></a></td>
            <td><s:property value="description"/></td>
        </tr>
    </s:iterator>
</table>
<!-- Although namescape not correctly specified, the following link should find the right action -->
<p><a href="<s:url action="edit" includeParams="none"/>">Create new Skill</a></p>
<p><a href="<s:url action="showcase" namespace="/" includeParams="none"/>">Back to Showcase Startpage</a></p>
</body>
</html>
