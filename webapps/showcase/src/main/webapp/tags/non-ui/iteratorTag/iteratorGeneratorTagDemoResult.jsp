<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
<title>Showcase - Tag - Non Ui Tag - Iterator Generator Tag Demo</title>
</head>
<body>

	<saf:generator val="%{value}" separator="%{separator}" count="%{count}">
		<saf:iterator value="%{top}">
			<saf:property /><br/>
		</saf:iterator>
	</saf:generator>

	<saf:url value="%{'/tags/non-ui/'}" id="url" /><saf:a href="%{#url}">Back To Non-UI Demo</saf:a>
	<saf:url value="%{'/'}" id="url" /><saf:a href="%{#url}">Back To Showcase</saf:a>

</body>
</html>
