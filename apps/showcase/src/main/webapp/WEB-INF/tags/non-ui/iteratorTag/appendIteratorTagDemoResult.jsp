<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Non Ui Tag - AppendIterator Tag</title>
	<s:head/>
</head>
<body>
<div class="page-header">
	<h1>Non Ui Tag - AppendIterator Tag Demo</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<s:generator var="iterator1" separator="," val="%{iteratorValue1}" />
		    <s:generator var="iterator2" separator="," val="%{iteratorValue2}" />

		    <s:append id="appendedIterator">
		        <s:param value="%{#attr.iterator1}" />
		        <s:param value="%{#attr.iterator2}" />
		    </s:append>

		    <s:iterator value="#appendedIterator">
		        <s:property /><br/>
		    </s:iterator>

			<s:url var="url" action="showAppendTagDemo" namespace="/tags/non-ui/appendIteratorTag" />
			<s:a href="%{#url}" cssClass="btn btn-info"><i class="icon icon-arrow-left"></i> Back To Input</s:a>
		</div>
	</div>
</div>
</body>
</html>
