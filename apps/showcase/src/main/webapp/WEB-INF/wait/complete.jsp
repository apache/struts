<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Execute and Wait Examples - Complete</title>
</head>

<body>
<div class="page-header">
	<h1>The process is complete</h1>
</div>


<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12" style="text-align: center;">

			<b>We have processed your request.</b>
			<p/>
			Click here to <s:url var="back" value="/wait/index.html"/><s:a href="%{back}" cssClass="btn btn-link">return</s:a>.

		</div>
	</div>
</div>
</body>
</html>
