<%@ taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
    <title>Edit Persons (batch-edit)</title>
</head>

<body>
<saf:form action="doEditPerson" theme="simple" validate="false">

    <table>
        <tr>
            <th>ID</th>
            <th>First Name</th>
            <th>Last Name</th>
        </tr>
        <saf:iterator id="p" value="persons">
            <tr>
                <td>
                    <saf:property value="%{id}" />
                </td>
                <td>
                    <saf:textfield label="First Name" name="persons(%{id}).name" value="%{name}" theme="simple" />
                </td>
                <td>
                    <saf:textfield label="Last Name" name="persons(%{id}).lastName" value="%{lastName}" theme="simple"/>
                </td>
            </tr>
        </saf:iterator>
    </table>

    <saf:submit value="Save all persons"/>
</saf:form>

<ul>
    <li><a href="newPerson!input.action">Create</a> a new person</li>
    <li><a href="listPeople.action">List</a> all people</li>
    <li><a href="jasperList.action">Jasper Report</a> all people as PDF export</li>
</ul>

</body>
</html>
