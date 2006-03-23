<html>
<head>
    <title>New Person</title>
</head>

<body>
<@ww.form action="newPerson">
    <@ww.textfield label="First Name" name="person.name"/>
    <@ww.textfield label="Last Name" name="person.lastName"/>
    <@ww.submit value="Create person"/>
</@ww.form>

<ul>
    <li><a href="editPerson.action">Edit people</a></li>
    <li><a href="listPeople.action">List</a> all people</li>
    <li><a href="jasperList.action">Jasper Report</a> all people as PDF export</li>
</ul>

</body>
</html>
