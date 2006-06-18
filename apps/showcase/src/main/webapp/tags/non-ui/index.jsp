<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
<title>Showcase - Tags - Non UI Tags</title>
</head>
<body>
<h1>Non UI Tags</h1>

<ul>
	<li><saf:url id="url" action="showActionTagDemo" namespace="/tags/non-ui/actionTag"/><saf:a href="%{url}">Action Tag</saf:a></li>
	<li><saf:url id="url" value="date.jsp" /><saf:a href="%{url}">Date Tag</saf:a></li>
	<li><saf:url id="url" action="showGeneratorTagDemo" namespace="/tags/non-ui/iteratorGeneratorTag" /><saf:a href="%{url}">Iterator Generator Tag</saf:a></li>
	<li><saf:url id="url" action="showAppendTagDemo" namespace="/tags/non-ui/appendIteratorTag" /><saf:a href="%{#url}">Append Iterator Tag</saf:a>
	<li><saf:url id="url" action="showMergeTagDemo" namespace="/tags/non-ui/mergeIteratorTag" /><saf:a href="%{#url}">Merge Iterator Demo</saf:a>
	<li><saf:url id="url" action="showSubsetTagDemo" namespace="/tags/non-ui/subsetIteratorTag" /><saf:a href="%{#url}">Subset Tag</saf:a>
	<li><saf:url id="url" value="actionPrefix/index.jsp"/><saf:a href="%{#url}">Action Prefix Example</saf:a></li>
</ul>

</body>
</html>
