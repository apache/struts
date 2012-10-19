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

			<s:actionerror cssClass="alert alert-error"/>
			<s:fielderror cssClass="alert alert-error"/>

		    <s:form action="submitMergeTagDemo" namespace="/tags/non-ui/mergeIteratorTag" method="POST">
		        <s:textfield label="Iterator 1 Value (Comma Separated)" name="iteratorValue1" />
		        <s:textfield label="Iterator 2 Value (Comma Separated)" name="iteratorValue2" />
		        <s:submit cssClass="btn btn-primary"/>
		    </s:form>

		</div>
	</div>
</div>
</body>
</html>
