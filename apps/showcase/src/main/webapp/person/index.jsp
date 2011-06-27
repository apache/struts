<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Acme Corp</title>
</head>

<body>
<ul>
    <s:url id="newpersonurl" action="new-person" method="input" />
    <li><s:a href="%{newpersonurl}">Create</s:a> a new person</li>
    <s:url id="listpeopleurl" action="list-people" />
    <li><s:a href="%{listpeopleurl}">List</s:a> all people</li>
</ul>
</body>
</html>
