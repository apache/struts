<html>
<head>
    <title>All People</title>
</head>

<body>

There are ${peopleCount} people... 
<table>
    <tr>
        <th>ID</th>
        <th>Name</th>
    </tr>
<#list people as person>
    <tr>
        <td>${person.id?html}</td>
        <td>${person.name?html}</td>
        <td>${person.lastName?html}</td>
    </tr>
</#list>
</table>

<ul>
    <@s.url id="editpersonurl" action="edit-person" />
    <li><@s.a href="%{editpersonurl}">Edit people</@s.a></li>
    <@s.url id="newpersonurl" action="new-person" method="input" />
    <li><@s.a href="%{newpersonurl}">Create</@s.a> a new person</li>
</ul>
</body>
</html>
