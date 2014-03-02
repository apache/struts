<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Execute and Wait Examples</title>
</head>

<body>
<div class="page-header">
	<h1>Execute and Wait Examples</h1>
</div>



<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12" style="text-align: center;">

			<p>
				These examples illustrate Struts build in support for execute and wait.
			<p/>
			<p>
				When you have a process that takes a long time your users can be impatient and starts to submit/click again.
				<br/> A good solution is to show the user a progress page (wait page) while the process takes it time.
			<p/>

			<br/>
			<br/><a href="example1.jsp">Example 1 (no delay)</a>
			<br/><a href="example2.jsp">Example 2 (with delay)</a>
			<br/><a href="example3.jsp">Example 3 (with longer check delay)</a>
		</div>
	</div>
</div>
</body>
</html>
