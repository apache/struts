<html>
<head>
    <title>New Person</title>
</head>

<body>
<@s.form action="new-person">
    <@s.textfield label="First Name" name="person.name"/>
    <@s.textfield label="Last Name" name="person.lastName"/>
    <@s.submit value="Create person"/>
</@s.form>

<ul>
    <li><a href="edit-person.action">Edit people</a></li>
    <li><a href="list-people.action">List</a> all people</li>
</ul>

</body>
</html>
