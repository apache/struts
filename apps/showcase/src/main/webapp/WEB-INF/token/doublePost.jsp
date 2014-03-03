<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Token Examples - Double post</title>
</head>

<body>
<div class="page-header">
	<h1>Token Examples - Double post</h1>
</div>


<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<p class="alert alert-warning">
				Double post. Struts intercepted this request and prevents the action from executing again.
			</p>

			<p/>
			Click here to
			<s:url var="back" value="/token/index.html"/><s:a href="%{back}">return</s:a>.
		</div>
	</div>
</div>
</body>
</html>
