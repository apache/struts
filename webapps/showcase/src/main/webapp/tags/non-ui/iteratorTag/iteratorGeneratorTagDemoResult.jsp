<%@taglib prefix="ww" uri="/webwork" %>

<html>
<head>
<title>Showcase - Tag - Non Ui Tag - Iterator Generator Tag Demo</title>
</head>
<body>

	<ww:generator val="%{value}" separator="%{separator}" count="%{count}">
		<ww:iterator value="%{top}">
			<ww:property /><br/>
		</ww:iterator>
	</ww:generator>

	<ww:url value="%{'/tags/non-ui/'}" id="url" /><ww:a href="%{#url}">Back To Non-UI Demo</ww:a>
	<ww:url value="%{'/'}" id="url" /><ww:a href="%{#url}">Back To Showcase</ww:a>

</body>
</html>
