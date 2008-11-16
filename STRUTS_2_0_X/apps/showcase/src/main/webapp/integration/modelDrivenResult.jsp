<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>Showcase - Struts 1 Integration  Example</title>
<s:head/>
</head>
<body>
	<s:actionmessage />
    <s:label 
            label="Gangster Name" 
            name="name" /><br/>
    <s:label 
            label="Gangster Age"
            name="age" /><br/>
    <s:label
            label="Busted Before"
            name="bustedBefore" /><br/>
    <s:label
            label="Gangster Description"
            name="description" /><br/>

</body>
</html>
