<html>
<head>
    <title>New Person</title>
</head>

<body>
<@saf.form action="newPerson">
    <@saf.textfield label="First Name" name="person.name"/>
    <@saf.textfield label="Last Name" name="person.lastName"/>
    <@saf.submit value="Create person"/>
</@saf.form>

<ul>
    <li><a href="editPerson.action">Edit people</a></li>
    <li><a href="listPeople.action">List</a> all people</li>
</ul>

</body>
</html>
