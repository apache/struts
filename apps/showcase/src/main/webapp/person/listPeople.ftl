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
        <td>${person.id}</td>
        <td>${person.name}</td>
        <td>${person.lastName}</td>
    </tr>
</#list>
</table>

<ul>
    <li><a href="editPerson.action">Edit people</a></li>
    <li><a href="newPerson!input.action">Create</a> a new person</li>
</ul>
</body>
</html>
