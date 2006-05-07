<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
<title>Showcase - Tags - Non UI Tags - Subset Tag</title>
</head>
<body>

	<saf:generator id="iterator" val="%{iteratorValue}" separator="," />

	<saf:subset count="%{count}" start="%{start}" source="%{#attr.iterator}" >
		<saf:iterator>
			<saf:property /><br/>
		</saf:iterator>
	</saf:subset>

	<saf:url value="%{'/tags/non-ui/'}" id="url" /><saf:a href="%{#url}">Back To Non-UI Demo</saf:a>
	<saf:url value="%{'/'}" id="url" /><saf:a href="%{#url}">Back To Showcase</saf:a>

</body>
</html>
