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
    <@s.url id="editpersonurl" action="edit-person" />
    <li><@s.a href="%{editpersonurl}">Edit people</@s.a></li>
    <@s.url id="listpeopleurl" action="list-people" />
    <li><@s.a href="%{listpeopleurl}">List</@s.a> all people</li>
</ul>

</body>
</html>
