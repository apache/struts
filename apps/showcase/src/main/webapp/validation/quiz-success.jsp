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
	<div class="row-fluid">
		<div class="span12">

			Thank you, <b><s:property value="name"/></b>. Your answer has been submitted as:

			<b><s:property value="answer"/></b>

			<s:include value="footer.jsp"/>
		</div>
	</div>
</div>
</body>
</html>
