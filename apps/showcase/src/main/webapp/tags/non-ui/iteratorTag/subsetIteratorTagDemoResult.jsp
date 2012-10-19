<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Non Ui Tag - SubsetTag Demo</title>
	<s:head/>
</head>
<body>
<div class="page-header">
	<h1>Non Ui Tag - SubsetTag Demo</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<s:generator var="iterator" val="%{iteratorValue}" separator="," />

		    <s:subset count="%{count}" start="%{start}" source="%{#attr.iterator}" >
		        <s:iterator>
		            <s:property /><br/>
		        </s:iterator>
		    </s:subset>

			<s:url var="url" action="showSubsetTagDemo" namespace="/tags/non-ui/subsetIteratorTag" />
			<s:a href="%{#url}" cssClass="btn btn-info"><i class="icon icon-arrow-left"></i> Back To Input</s:a>
		</div>
	</div>
</div>
</body>
</html>
