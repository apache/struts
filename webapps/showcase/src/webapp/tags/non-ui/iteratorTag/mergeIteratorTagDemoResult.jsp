<%@taglib prefix="ww" uri="/webwork" %>

<html>
<head>
<title>Showcase - Tags - Non UI Tag - MergeIterator Tag</title>
</head>
<body>

	<ww:generator id="iterator1" val="%{iteratorValue1}" separator="," />
	<ww:generator id="iterator2" val="%{iteratorValue2}" separator="," />
	
	<ww:merge id="mergedIterator">
		<ww:param value="%{#attr.iterator1}" />
		<ww:param value="%{#attr.iterator2}" />
	</ww:merge>
	
	<ww:iterator value="%{#mergedIterator}">
		<ww:property /><br/>
	</ww:iterator>

	<ww:url value="%{'/tags/non-ui/'}" id="url" /><ww:a href="%{#url}">Back To Non-UI Demo</ww:a>
	<ww:url value="%{'/'}" id="url" /><ww:a href="%{#url}">Back To Showcase</ww:a>

</body>
</html>
