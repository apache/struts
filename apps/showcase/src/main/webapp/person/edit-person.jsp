<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Edit Persons (batch-edit)</title>
</head>

<body>
<s:form action="edit-person" theme="simple" validate="false">

    <table>
        <tr>
            <th>ID</th>
            <th>First Name</th>
            <th>Last Name</th>
        </tr>
        <s:iterator var="p" value="persons">
            <tr>
                <td>
                    <s:property value="%{id}" />
                </td>
                <td>
                    <s:textfield label="First Name" name="persons(%{id}).name" value="%{name}" theme="simple" />
                </td>
                <td>
                    <s:textfield label="Last Name" name="persons(%{id}).lastName" value="%{lastName}" theme="simple"/>
                </td>
            </tr>
        </s:iterator>
    </table>

    <s:submit method="save" value="Save all persons"/>
</s:form>

<ul>
    <s:url id="newpersonurl" action="new-person" method="input" />
    <li><s:a href="%{newpersonurl}">Create</s:a> a new person</li>
    <s:url id="listpeopleurl" action="list-people" />
    <li><s:a href="%{listpeopleurl}">List</s:a> all people</li>
</ul>

</body>
</html>
