<%@taglib prefix="ww" uri="/webwork" %>

<html>
<head>
<title>Showcase - Tags - Non UI Tags - Subset Tag</title>
</head>
<body>

	<ww:generator id="iterator" val="%{iteratorValue}" separator="," />

	<ww:subset count="%{count}" start="%{start}" source="%{#attr.iterator}" >
		<ww:iterator>
			<ww:property /><br/>
		</ww:iterator>
	</ww:subset>

	<ww:url value="%{'/tags/non-ui/'}" id="url" /><ww:a href="%{#url}">Back To Non-UI Demo</ww:a>
	<ww:url value="%{'/'}" id="url" /><ww:a href="%{#url}">Back To Showcase</ww:a>

</body>
</html>
