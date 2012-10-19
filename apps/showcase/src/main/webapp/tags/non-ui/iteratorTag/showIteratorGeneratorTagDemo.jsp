<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Non Ui Tag - Iterator Generator Tag Demo</title>
	<s:head/>
</head>
<body>
<div class="page-header">
	<h1>Non Ui Tag - Iterator Generator Tag Demo</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<s:actionerror cssClass="alert alert-error"/>
			<s:fielderror cssClass="alert alert-error"/>

			<s:form action="submitGeneratorTagDemo" namespace="/tags/non-ui/iteratorGeneratorTag" method="POST">
		        <s:textfield label="Value" name="value" />
		        <s:textfield label="Separator" name="separator" />
		        <s:textfield label="Count" name="count" />
		        <s:submit cssClass="btn btn-primary"/>
            </s:form>
		</div>
	</div>
</div>
</body>
</html>
