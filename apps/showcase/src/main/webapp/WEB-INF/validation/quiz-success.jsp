<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Validation - Quiz submitted!</title>
	<s:head/>
</head>

<body>

<div class="page-header">
	<h1>Quiz submitted!</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			Thank you, <b><s:property value="name"/></b>. Your answer has been submitted as:

			<b><s:property value="answer"/></b>
		</div>
	</div>
</div>
</body>
</html>
