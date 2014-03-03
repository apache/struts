<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
	<title>Struts2 Showcase - Conversion</title>
</head>
<body>
<div class="page-header">
	<h1>Conversion</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<ul>
			    <li>
			        <s:url var="url" action="enterPersonsInfo" namespace="/conversion" />
			        <s:a href="%{#url}">Populate into the Struts action class a List of Person.java Object</s:a>
			    </li>
			    <li>
			        <s:url var="url" action="enterAddressesInfo" namespace="/conversion" />
			        <s:a href="%{#url}">Populate into Struts action class a Set of Address.java Object</s:a>
			    </li>
			    <li>
			        <s:url var="url" action="enterOperationEnumInfo" namespace="/conversion" />
			        <s:a href="%{#url}">Populate into Struts action class a List of OperationEnum.java (Java5 Enum)</s:a>
			    </li>
			</ul>

		</div>
	</div>
</div>
</body>
</html>