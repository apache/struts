<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
<title>Showcase - Tag - Non UI Tag - AppendIterator Tag</title>
</head>
<body>

	<saf:generator id="iterator1" separator="," val="%{iteratorValue1}" />
	<saf:generator id="iterator2" separator="," val="%{iteratorValue2}" />
	
	<saf:append id="appendedIterator">
		<saf:param value="%{#attr.iterator1}" />
		<saf:param value="%{#attr.iterator2}" />
	</saf:append>
	
	<saf:iterator value="#appendedIterator">
		<saf:property /><br/>
	</saf:iterator>
	
	<saf:url value="%{'/tags/non-ui/'}" id="url" /><saf:a href="%{#url}">Back To Non-UI Demo</saf:a>
	<saf:url value="%{'/'}" id="url" /><saf:a href="%{#url}">Back To Showcase</saf:a>

</body>
</html>
