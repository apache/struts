<html>
<head>
    <title>New Person</title>
</head>

<body>
<@s.form action="newPerson">
    <@s.textfield label="First Name" name="person.name"/>
    <@s.textfield label="Last Name" name="person.lastName"/>
    <@s.submit value="Create person"/>
</@s.form>

<ul>
    <li><a href="editPerson.action">Edit people</a></li>
    <li><a href="listPeople.action">List</a> all people</li>
</ul>

</body>
</html>
