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

		    <s:actionerror cssClass="alert alert-error"/>
		    <s:fielderror cssClass="alert alert-error"/>

		    <s:form action="submitAppendTagDemo" namespace="/tags/non-ui/appendIteratorTag" method="POST">
		        <s:textfield label="iterator 1 values (comma separated)" name="iteratorValue1" />
		        <s:textfield label="iterator 2 values (comma separated)" name="iteratorValue2" />
		        <s:submit cssClass="btn btn-primary"/>
		    </s:form>
	    </div>
    </div>
</div>
</body>
</html>
