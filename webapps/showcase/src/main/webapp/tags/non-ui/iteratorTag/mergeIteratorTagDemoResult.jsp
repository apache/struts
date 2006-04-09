<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
<title>Showcase - Tags - Non UI Tag - MergeIterator Tag</title>
</head>
<body>

	<saf:generator id="iterator1" val="%{iteratorValue1}" separator="," />
	<saf:generator id="iterator2" val="%{iteratorValue2}" separator="," />
	
	<saf:merge id="mergedIterator">
		<saf:param value="%{#attr.iterator1}" />
		<saf:param value="%{#attr.iterator2}" />
	</saf:merge>
	
	<saf:iterator value="%{#mergedIterator}">
		<saf:property /><br/>
	</saf:iterator>

	<saf:url value="%{'/tags/non-ui/'}" id="url" /><saf:a href="%{#url}">Back To Non-UI Demo</saf:a>
	<saf:url value="%{'/'}" id="url" /><saf:a href="%{#url}">Back To Showcase</saf:a>

</body>
</html>
