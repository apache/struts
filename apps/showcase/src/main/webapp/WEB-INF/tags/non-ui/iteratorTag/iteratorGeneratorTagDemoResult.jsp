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

		<s:generator val="%{value}" separator="%{separator}" count="%{count}">
            <s:iterator value="%{top}">
               <s:property /><br/>
            </s:iterator>
        </s:generator>


        <s:url var="url" action="showGeneratorTagDemo" namespace="/tags/non-ui/iteratorGeneratorTag" />
        <s:a href="%{#url}" cssClass="btn btn-info"><i class="icon icon-arrow-left"></i> Back To Input</s:a>
        </div>
    </div>
</div>
</body>
</html>
