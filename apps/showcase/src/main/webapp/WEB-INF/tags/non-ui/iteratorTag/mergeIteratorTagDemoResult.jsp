<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Non Ui Tag - MergeIterator Tag</title>
	<s:head/>
</head>
<body>
<div class="page-header">
	<h1>Non Ui Tag - MergeIterator Tag</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">
			<s:generator var="iterator1" val="%{iteratorValue1}" separator="," />
		    <s:generator var="iterator2" val="%{iteratorValue2}" separator="," />

		    <s:merge var="mergedIterator">
		        <s:param value="%{#attr.iterator1}" />
		        <s:param value="%{#attr.iterator2}" />
		    </s:merge>

		    <s:iterator value="%{#mergedIterator}">
		        <s:property /><br/>
		    </s:iterator>

			<s:url var="url" action="showMergeTagDemo" namespace="/tags/non-ui/mergeIteratorTag" />
			<s:a href="%{#url}" cssClass="btn btn-info"><i class="icon icon-arrow-left"></i> Back To Input</s:a>
		</div>
	</div>
</div>
</body>
</html>
