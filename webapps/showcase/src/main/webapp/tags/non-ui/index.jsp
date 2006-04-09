<%@taglib prefix="ww" uri="/webwork" %>

<html>
<head>
<title>Showcase - Tags - Non UI Tags</title>
</head>
<body>
<h1>Non UI Tags</h1>

<ul>
	<li><ww:url id="url" action="showActionTagDemo" namespace="/tags/non-ui/actionTag"/><ww:a href="%{url}">Action Tag</ww:a></li>
	<li><ww:url id="url" value="date.jsp" /><ww:a href="%{url}">Date Tag</ww:a></li>
	<li><ww:url id="url" action="showGeneratorTagDemo" namespace="/tags/non-ui/iteratorGeneratorTag" /><ww:a href="%{url}">Iterator Generator Tag</ww:a></li>
	<li><ww:url id="url" action="showAppendTagDemo" namespace="/tags/non-ui/appendIteratorTag" /><ww:a href="%{#url}">Append Iterator Tag</ww:a>
	<li><ww:url id="url" action="showMergeTagDemo" namespace="/tags/non-ui/mergeIteratorTag" /><ww:a href="%{#url}">Merge Iterator Demo</ww:a>
	<li><ww:url id="url" action="showSubsetTagDemo" namespace="/tags/non-ui/subsetIteratorTag" /><ww:a href="%{#url}">Subset Tag</ww:a>
</ul>

</body>
</html>
