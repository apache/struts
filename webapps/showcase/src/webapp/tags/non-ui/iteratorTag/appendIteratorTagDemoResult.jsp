<%@taglib prefix="ww" uri="/webwork" %>

<html>
<head>
<title>Showcase - Tag - Non UI Tag - AppendIterator Tag</title>
</head>
<body>

	<ww:generator id="iterator1" separator="," val="%{iteratorValue1}" />
	<ww:generator id="iterator2" separator="," val="%{iteratorValue2}" />
	
	<ww:append id="appendedIterator">
		<ww:param value="%{#attr.iterator1}" />
		<ww:param value="%{#attr.iterator2}" />
	</ww:append>
	
	<ww:iterator value="#appendedIterator">
		<ww:property /><br/>
	</ww:iterator>
	
	<ww:url value="%{'/tags/non-ui/'}" id="url" /><ww:a href="%{#url}">Back To Non-UI Demo</ww:a>
	<ww:url value="%{'/'}" id="url" /><ww:a href="%{#url}">Back To Showcase</ww:a>

</body>
</html>
