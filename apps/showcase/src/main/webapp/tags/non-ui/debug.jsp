<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Non UI Tags Example - Debug</title>
</head>

<body>
<div class="page-header">
	<h1>Debug Tag Usage</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<p/>
				This page shows a simple example of using the debug tag.  <br/>
				Just add <tt style="font-size: 12px; font-weight:bold;color: blue;">&lt;s:debug /&gt;</tt> to your JSP page
			    and you will see the debug link.
		    <p/>

			<p>
			    Just click on the Debug label to see the Struts ValueStack Debug information.
		    <p/>
		    <s:debug />
		</div>
	</div>
</div>
</body>
</html>