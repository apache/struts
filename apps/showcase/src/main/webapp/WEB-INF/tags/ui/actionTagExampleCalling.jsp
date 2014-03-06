<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - UI Tags - Action Tag</title>
	<s:head/>
</head>
<body>
<div class="page-header">
	<h1>UI Tags - Action Tag</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">
			  <b>Example 1:</b>
			  This example calls an action and includes the output on the page
			  <p id="example1" class="well">
			    <s:action namespace="/tags/ui" name="actionTagExample" executeResult="true"/>
			  </p>
		</div>
	</div>
</div>
</body>
</html>