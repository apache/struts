<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Conversion - Populate Object into Struts' action List</title>
</head>
<body>
<div class="page-header">
	<h1>Conversion - Populate Object into Struts' action List</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<s:iterator value="persons" status="status">
                 <s:label label="%{'SET '+#status.index+' Name'}" value="%{name}" /><br/>
                 <s:label label="%{'SET '+#status.index+' Age'}" value="%{age}" /><br/>
			</s:iterator>
		</div>
	</div>
</div>
</body>
</html>

