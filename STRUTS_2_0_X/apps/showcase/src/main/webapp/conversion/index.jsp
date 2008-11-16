<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Showcase - Conversion</title>
</head>
<body>

<ul>
    <li>
    	<s:url id="url" action="enterPersonsInfo" namespace="/conversion" />
    	<s:a href="%{#url}">Populate into the Struts action class a List of Person.java Object</s:a>
    </li>
    <li>
    	<s:url id="url" action="enterAddressesInfo" namespace="/conversion" />
    	<s:a href="%{#url}">Populate into Struts action class a Set of Address.java Object</s:a>
    </li>
    <li>
    	<s:url id="url" action="enterOperationEnumInfo" namespace="/conversion" />
    	<s:a href="%{#url}">Populate into Struts action class a List of OperationEnum.java (Java5 Enum)</s:a> 
    </li>
</ul>

</body>
</html>