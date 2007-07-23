<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>Showcase - Tags - Non UI Tags</title>
</head>
<body>
<h1>Non UI Tags</h1>

<ul>
    <li><s:url id="url" action="showActionTagDemo" namespace="/tags/non-ui/actionTag"/><s:a href="%{url}">Action Tag</s:a></li>
    <li><s:url id="url" value="date.jsp" /><s:a href="%{url}">Date Tag</s:a></li>
    <li><s:url id="url" value="debug.jsp" /><s:a href="%{url}">Debug Tag</s:a></li>
    <li><s:url id="url" action="showGeneratorTagDemo" namespace="/tags/non-ui/iteratorGeneratorTag" /><s:a href="%{url}">Iterator Generator Tag</s:a></li>
    <li><s:url id="url" action="showAppendTagDemo" namespace="/tags/non-ui/appendIteratorTag" /><s:a href="%{#url}">Append Iterator Tag</s:a>
    <li><s:url id="url" action="showMergeTagDemo" namespace="/tags/non-ui/mergeIteratorTag" /><s:a href="%{#url}">Merge Iterator Demo</s:a>
    <li><s:url id="url" action="showSubsetTagDemo" namespace="/tags/non-ui/subsetIteratorTag" /><s:a href="%{#url}">Subset Tag</s:a>
    <li><s:url id="url" value="actionPrefix/index.jsp"/><s:a href="%{#url}">Action Prefix Example</s:a></li>
	<li><s:url id="url" action="testIfTagJsp" namespace="/tags/non-ui/ifTag"/><s:a href="%{#url}">If Tag (JSP)</s:a></li>
	<li><s:url id="url" action="testIfTagFreemarker" namespace="/tags/non-ui/ifTag"/><s:a href="%{#url}">If Tag (Freemarker)</s:a></li>
</ul>

</body>
</html>
