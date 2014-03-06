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

			<s:actionerror cssClass="alert alert-error"/>
			<s:fielderror cssClass="alert alert-error"/>

		    <s:form action="submitSubsetTagDemo" namespace="/tags/non-ui/subsetIteratorTag" method="POST">
		        <s:textfield label="Iterator value (comma separated)" name="iteratorValue" />
		        <s:textfield label="Count" name="count" />
		        <s:textfield label="Start" name="start" />
		        <s:submit cssClass="btn btn-primary"/>
		    </s:form>
		</div>
	</div>
</div>
</body>
</html>
